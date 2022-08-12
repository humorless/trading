(ns orangeskylab.trading
  "The trading system does the following things:

   1. Get all the keys and create a job processing thread for each key
   2. Inside every job processing thread:
      a. read the job from the `jobs` queue
      b. send/cancel the order (http request)
      c. mark the job as done if the http request is successful
  
  The trading system needs to satisfy two requirements:
  (a) There is timeout mechanism in send/canel order.
  (b) Each key is used exclusively at any time. "
  (:require [aero.core :as aero]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [hato.client :as hc]
            [long-thread.core :as long-thread]
            [lambdaisland.glogc :as log]
            [clojure.data.json :as json]))

(comment
  ;; JSON experiment
  (json/read-str
   "{\"a\":13, \"b\":27}"
   :key-fn keyword))

(def config
  "read the config file"
  (aero/read-config "config.edn"))

;; Handle the send/cancel order (http request)
(comment
  (def c (hc/build-http-client {:connect-timeout 2000
                                :redirect-policy :always}))
  (hc/get "https://httpbin.org/get"))

;; Communicate with the DB
(def db-entry {:jdbcUrl (:database-url config)
               :user (:user config)
               :password (:password config)})

(def db-ds (jdbc/get-datasource db-entry))

(defn dump-table
  "retrieve all the records from table"
  [ds table-keyword]
  (jdbc/execute! ds
                 (sql/format {:select [:*]
                              :from [table-keyword]})))

(defn process-job
  "process job according for specific `key-id`"
  [ds key-id]
  (jdbc/with-transaction [jdbc-txds ds]
    (let [query (sql/format
                 {:select [:*]
                  :from [:jobs]
                  :where [:and
                          [:= :key_id key-id]
                          [:= :done false]]
                  :order-by [[:priority :desc] [:updated]]
                  :limit 1
                  :for [:update :skip-locked]})]
      (let [return (#(do (log/debug :process-job {:sql %})
                         (jdbc/execute! jdbc-txds %)) query)
            [{:jobs/keys [id type key_id input_json priority done updated] :as row} _] return
            command (sql/format
                     {:update :jobs
                      :set {:done true}
                      :where [:= :id id]})]
        ;; TODO
        ;; * early return when the query results is empty
        (log/trace :process-job row)
        (log/debug :process-job {:id id :type type :key_id key_id :input_json input_json})
        ;; TODO
        ;; 1. Run send/cancel order here
        ;; 2. Run the update command if send/cancel order successful
        (#(do (log/debug :process-job {:sql %})
              (jdbc/execute! jdbc-txds %)) command)))))

(comment
  (dump-table db-ds :jobs)
  (dump-table db-ds :keys)
  (process-job db-ds 13))

;; Control the thread
(defn thread-body
  "run the process-job with certain key foreverly"
  [key-id]
  (long-thread/until-interrupted
   (while true
     (log/trace :thread-body {:key-id key-id})
     (process-job db-ds key-id)
     ;; sleep for 5000 milliseconds
     (Thread/sleep 5000))))

(defn create-thread
  "create the thread with key name and key id"
  [{:keys/keys [id name]}]
  (log/trace :create-thread {:key-id id
                             :key-name name})
  (long-thread/create name (partial thread-body id)))

(defn init
  "For every key, create a thread to keep consuming the jobs of it"
  []
  (let [key-records (dump-table db-ds :keys)
        thread-list (map create-thread key-records)]
    (while true
      (Thread/sleep 1000)
      ;; check if any thread is stopped
      (run! (fn [thread]
              (log/trace :init {:check-if-thread-terminated? (.getState thread)})
              ;; TODO
              ;; When the thread is not alive, restart it.
              )
            thread-list))))

(comment
  (def eee (let [t (long-thread/create "uniq-name-hhh" (do
                                                         (log/trace :h {:a 1})
                                                         (throw (Exception. "my exception message"))
                                                         (Thread/sleep 5000)))]
             (prn "here: " (.getState t))
             t))
  (long-thread/threads-by-name "uniq-name-hhh")
  (long-thread/running-threads)
  (prn "there: " (.getState eee))
  ;; init all the threads
  (init))

(defn exec
  "Invoke me with clojure -X orangeskylab.trading/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with clojure -M -m orangeskylab.trading"
  [& args]
  (println "-main with" args))
