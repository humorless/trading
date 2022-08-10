(ns orangeskylab.trading
  "The trading system does the following things:

   1. Get the jobs from queue every x seconds.
   2. According the job, send/cancel the order.
   3. Mark the job as done in queue.
  
  The trading system needs to satisfy two requirements:
  (a) There is timeout mechanism in send/canel order.
  (b) Each key is used exclusively at any time. "
  (:require [aero.core :as aero]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [hato.client :as hc]))

;; Read the config file
(def config (aero/read-config "config.edn"))

;; Handle the send/cancel order (http request)
(comment
  (def c (hc/build-http-client {:connect-timeout 2000
                                :redirect-policy :always}))
  (hc/get "https://httpbin.org/get"))

;; initialize the DB connection
(def db-entry {:jdbcUrl (:database-url config)
               :user (:user config)
               :password (:password config)})

(def db-ds (jdbc/get-datasource db-entry))

(defn all-jobs
  "retrieve all the jobs"
  [ds]
  (jdbc/execute! ds
                 (sql/format {:select [:*]
                              :from [:jobs]})))

(defn process-job
  "process job"
  [ds key-id]
  (jdbc/with-transaction [jdbc-txds ds]
    (let [query (sql/format
                 {:select [:*]
                  :from [:jobs]
                  :where [:and
                          [:= :key_id key-id]
                          [:= :done false]]
                  :order-by [[:updated]]
                  :limit 1
                  :for [:update :skip-locked]})]
      (let [return (#(do (clojure.pprint/pprint %)
                         (jdbc/execute! jdbc-txds %)) query)
            [{:jobs/keys [id type key_id input_json priority done updated] :as row} _] return
            command (sql/format
                     {:update :jobs
                      :set {:done true}
                      :where [:= :id id]})]
        (prn row)
        (prn id type key_id input_json)
        ;; handle the http request here
        ;; Run the update command if http request is successful
        (#(do (clojure.pprint/pprint %)
              (jdbc/execute! jdbc-txds %)) command)))))

(comment
  (all-jobs db-ds)
  (process-job db-ds 13))

(defn exec
  "Invoke me with clojure -X orangeskylab.trading/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with clojure -M -m orangeskylab.trading"
  [& args]
  (println "-main with" args))
