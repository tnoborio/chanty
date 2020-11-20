(ns chanty.boundary.db.user
  (:require [duct.database.sql]
            [honeysql.core :as sql]
            [chanty.boundary.db.core :as db]
            [crypto.password.bcrypt :refer [encrypt check]])
  (:import [org.sqlite SQLiteException]))

(defn- find-by-id [db id]
  (let [query (sql/build :select :*
                         :from :users
                         :where [:= :id id])]

    (db/select-one db query)))

(defprotocol Users
  (signup [db id nickname password])
  (login [db id password])
  (fetch [db id]))

(extend-protocol Users
  duct.database.sql.Boundary
  (signup [db id nickname password]
    (loop []
      (let [user-row {:id id :nickname nickname :password (encrypt password)}
            _ user-row
            user (try
                   (db/insert! db :users user-row)
                   user-row
                   (catch SQLiteException _ nil))]
        (if user
          user
          (recur)))))
  (login [db id password]
    (when-let [user (find-by-id db id)]
      (let [{encrypted-password :password} user]
        (if (check password encrypted-password)
          (assoc (dissoc user :password) :success true)
          {:success false}))))
  (fetch [db id]
    (-> (find-by-id db id)
        (dissoc :password))))
