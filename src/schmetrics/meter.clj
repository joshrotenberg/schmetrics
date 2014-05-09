(ns schmetrics.meter
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry meter context]])
  (:require [schmetrics.json :refer [get-mapper]])
  (:import [com.codahale.metrics Meter]))

(extend-type Meter
  ReadMetric
  (read-metric [this] {:count (.getCount this)
                       :fifteen-minute-rate (.getFifteenMinuteRate this)
                       :five-minute-rate (.getFiveMinuteRate this)
                       :one-minute-rate (.getOneMinuteRate this)
                       :mean-rate (.getMeanRate this)
                       }))

(defn- retrieve-meter
  [meter-name]
  (meter (get-registry) meter-name))

(defn mark
  "Mark meter-name with to-mark, or by 1 if not specified."
  ([meter-name to-mark]
     (let [meter (retrieve-meter meter-name)]
       (.mark meter to-mark)))
  ([meter-name]
     (mark meter-name 1)))

(defn read
  "Read the current value of meter-name."
  [meter-name]
  (merge 
   {:name (keyword meter-name)}
   (read-metric (retrieve-meter meter-name))))
   
(defn json 
  "Returns the meter as a json string."
  [meter-name]
  (.writeValueAsString (get-mapper) (retrieve-meter meter-name)))
