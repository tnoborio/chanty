(ns chanty.handler.message
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [chanty.boundary.db.message :as db.message]
            [org.httpkit.server :as s]
            [org.httpkit.timer :refer [schedule-task]]
            [clojure.core.async :as async]))

(defonce message-chans (atom {}))

(defmethod ig/init-key ::fetch [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result}]
    [::response/ok (db.message/fetch db id)]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ from to body] :ataraxy/result}]
    (db.message/create db from to body)
    (doseq [chan (keys @message-chans)]
      (s/send! chan {:status 200 :body "done"}))
    [::response/ok (db.message/fetch db from)]))

(defmethod ig/init-key ::polling [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result :as req}]
    (s/with-channel req chan
      (swap! message-chans #(assoc % chan req))
      (s/on-close chan #(swap! message-chans dissoc chan))
      (schedule-task 3000 
                     (s/send! chan {:status 200 :body "hoge"})))))
