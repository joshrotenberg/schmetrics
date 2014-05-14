(ns schmetrics.meter
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry meter context]])
  (:require [schmetrics.json :as json])
  (:import [com.codahale.metrics Meter]))

(extend-type Meter
  ReadMetric
  (read-metric [this] {:count (.getCount this)
                       :fifteen-minute-rate (.getFifteenMinuteRate this)
                       :five-minute-rate (.getFiveMinuteRate this)
                       :one-minute-rate (.getOneMinuteRate this)
                       :mean-rate (.getMeanRate this)
                       }))

(defn get-meter
  "Get the meter object from the registry."
  [meter-name]
  (meter (get-registry) meter-name))

(defn mark
  "Mark meter-name with to-mark, or by 1 if not specified."
  ([meter-name to-mark]
     (let [meter (get-meter meter-name)]
       (.mark meter to-mark)))
  ([meter-name]
     (mark meter-name 1)))

(defn read
  "Read the current value of meter-name."
  [meter-name]
  (merge 
   {:name (keyword meter-name)}
   (read-metric (get-meter meter-name))))

