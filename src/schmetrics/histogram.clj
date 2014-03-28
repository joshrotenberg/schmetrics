(ns schmetrics.histogram
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry 
                                         histogram context]])
  (:import [com.codahale.metrics Histogram]))

(extend-type Histogram
  ReadMetric
  (read-metric [this] 
    (let [snapshot (.getSnapshot this)]
      {:count (.getCount this)
       :min (.getMin snapshot)
       :max (.getMax snapshot)
       :mean (.getMean snapshot)
       :stddev (.getStdDev snapshot)
       :median (.getMedian snapshot)
       :75th-percentile (.get75thPercentile snapshot)
       :95th-percentile (.get95thPercentile snapshot)
       :98th-percentile (.get98thPercentile snapshot)
       :99th-percentile (.get99thPercentile snapshot)
       :999th-percentile (.get999thPercentile snapshot)})))

(defn- retrieve-histogram
  [n]
  (histogram (get-registry) n))

(defn update 
  [name n]
  (let [histogram (retrieve-histogram name)]
    (.update histogram n)))

(defn read
  [name]
  (merge
   {:name (keyword name)}
   (read-metric (retrieve-histogram name))))

