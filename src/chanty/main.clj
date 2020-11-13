(ns chanty.main
  (:gen-class)
  (:require [duct.core :as duct]))

(duct/load-hierarchy)

(def custom-readers
  {'chanty/regex re-pattern})

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]]
    (-> (duct/resource "chanty/config.edn")
        (duct/read-config)
        (duct/read-config custom-readers)
        (duct/exec-config profiles keys))))
