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

(def conn (jdbc/get-datasource db-entry))

(defn all-jobs
  "retrieve all the jobs"
  []
  (jdbc/execute! conn
                 (sql/format {:select [:*]
                              :from [:jobs]})))

(defn exec
  "Invoke me with clojure -X orangeskylab.trading/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with clojure -M -m orangeskylab.trading"
  [& args]
  (println "-main with" args))
