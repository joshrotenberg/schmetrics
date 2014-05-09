(ns schmetrics.histogram
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry 
                                         histogram context]])
  (:require [schmetrics.json :refer [get-mapper]])
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
  [histogram-name]
  (histogram (get-registry) histogram-name))

(defn update 
  "Update histogram-name with the value in to-update."
  [histogram-name to-update]
  (let [histogram (retrieve-histogram histogram-name)]
    (.update histogram to-update)))

(defn read
  "Read the current value of histogram-name."
  [histogram-name]
  (merge
   {:name (keyword histogram-name)}
   (read-metric (retrieve-histogram histogram-name))))

(defn json
  "Returns the histogram as a json string."
  [histogram-name]
  (.writeValueAsString (get-mapper) (retrieve-histogram histogram-name)))
