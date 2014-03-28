(ns schmetrics.meter
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry meter context]])
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
  [n]
  (meter (get-registry) n))

(defn mark
  ([name n]
     (let [meter (retrieve-meter name)]
       (.mark meter n)))
  ([name]
     (mark name 1)))

(defn read
  [name]
  (merge 
   {:name (keyword name)}
   (read-metric (retrieve-meter name))))
   
