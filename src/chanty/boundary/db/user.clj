(ns chanty.boundary.db.user
  (:require [duct.database.sql]
            [honeysql.core :as sql]
            [chanty.boundary.db.core :as db])
  (:import [org.sqlite SQLiteException]))

(defprotocol Users
  (create [db nickname])
  (fetch [db id]))

(defn gen-id []
  (rand-int 100000))

(extend-protocol Users
  duct.database.sql.Boundary
  (create [db nickname]
    (loop [id (gen-id)]
      (let [user-row {:id id :nickname nickname}
            user (try
                   (db/insert! db :users user-row)
                   user-row
                   (catch SQLiteException _ nil))]
        (if user
          user
          (recur (gen-id))))))
  (fetch [db id]
    (db/select-one db (sql/build :select :*
                                 :from :users
                                 :where [:= :id id]))))