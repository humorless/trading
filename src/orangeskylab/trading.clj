(ns orangeskylab.trading
  "The trading system does the following things:

   1. Get the jobs from queue every x seconds.
   2. According the job, send/cancel the order.
   3. Mark the job as done in queue.
  
  The trading system needs to satisfy two requirements:
  (a) There is timeout mechanism in send/canel order.
  (b) Each key is used exclusively at any time. "
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [aero.core :as aero]))

(def config (aero/read-config "config.edn"))

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
      (#(do (clojure.pprint/pprint %)
            (jdbc/execute! jdbc-txds %)) query))))

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
