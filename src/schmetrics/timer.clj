(ns schmetrics.timer
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry timer context]])
  (:import [com.codahale.metrics Timer]))

(defonce timer-context (atom {}))

(extend-type Timer
  ReadMetric
  (read-metric [this] 
    (let [snapshot (.getSnapshot this)]
      {:mean-rate (.getMeanRate this)
       :one-minute-rate (.getOneMinuteRate this)
       :five-minute-rate (.getFiveMinuteRate this)
       :fiteen-minute-rate (.getFifteenMinuteRate this)
       :count (.getCount this)
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


(defn- retrieve-timer
  [n]
  (timer (get-registry) n))

(defn start
  [n]
  (let [timer (retrieve-timer n)
        ctx (.time timer)]
    (swap! timer-context assoc (keyword n) ctx))
  nil)

(defn stop
  [n]
  (let [timer (retrieve-timer n)
        ctx (get @timer-context (keyword n))]
    (.stop ctx)))

(defn read
  [n]
  (merge 
   {:name (keyword n)}
   (read-metric (retrieve-timer n))))
        
    
    
    
    
    
