(ns chanty.boundary.db.message
  (:require [duct.database.sql]
            [honeysql.core :as sql]
            [chanty.boundary.db.core :as db]))

(defprotocol Messages
  (create [db from to message])
  (fetch [db id]))

(defn fetch-messages [db user-id]
  (let [contents
        (->> (sql/build :select :contents
                        :from :messages
                        :where [:= :user_id user-id])
             (db/select-one db)
             :contents)]
    (read-string (or contents "[]"))))

(defn create-message [db user-id message]
  (let [messages (fetch-messages db user-id)
        messages (vec (take 200 (conj messages message)))]
    (db/upsert! db :messages {:user_id user-id :contents (str messages)})))

(extend-protocol Messages
  duct.database.sql.Boundary
  (create [db from to body]
    (let [message {:from from :to to :body body}]
      (prn (= from to) from to)
      (if (= from to)
        (create-message db from message)
        (do
          (create-message db from message)
          (create-message db to message)))))
  (fetch [db id] (fetch-messages db id)))
