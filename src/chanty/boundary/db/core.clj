(ns chanty.boundary.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [clojure.string :refer [join]]))

(defn select [{db :spec} sql-map]
  (->> sql-map
       sql/format
       (jdbc/query db)))

(defn select-one [{db :spec} sql-map]
  (->> sql-map
       sql/format
       (jdbc/query db)
       first))

(defn insert! [{db :spec} table row-map]
  (->> row-map
       (jdbc/insert! db table)
       first))

(defn join-comma [seq]
  (join ", " seq))

(defn upsert! [{db :spec} table row-map]
  (let [row (seq row-map)
        columns (map #(-> % first name) row)
        values (map #(-> % second) row)
        q (str "insert or replace into " (name table)
               "(" (join-comma columns) ")"
               " values(" (join-comma (repeat (count values) "?")) ")")]
    (prn q)
    (jdbc/execute! db (cons q values))))
