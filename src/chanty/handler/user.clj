(ns chanty.handler.user
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [chanty.boundary.db.user :as db.user]))

(defmethod ig/init-key ::fetch [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result}]
    [::response/ok (db.user/fetch db id)]))

(defmethod ig/init-key ::signup [_ {:keys [db]}]
  (fn [{[_ id nickname password] :ataraxy/result}]
    (prn id nickname)
    [::response/ok (db.user/signup db id nickname password)]))

(defmethod ig/init-key ::login [_ {:keys [db]}]
  (fn [{[_ id password] :ataraxy/result}]
    (prn id)
    [::response/ok (db.user/login db id password)]))
