(ns chanty.handler.message
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [chanty.boundary.db.message :as db.message]))

(defmethod ig/init-key ::fetch [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result}]
    [::response/ok (db.message/fetch db id)]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ from to body] :ataraxy/result}]
    (db.message/create db from to body)
    [::response/ok (db.message/fetch db from)]))
