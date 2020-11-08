(ns chanty.handler.user
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [chanty.boundary.db.user :as db.user]))

(defmethod ig/init-key ::fetch [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result}]
    [::response/ok (db.user/fetch db id)]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ nickname] :ataraxy/result}]
    [::response/ok (db.user/create db nickname)]))
