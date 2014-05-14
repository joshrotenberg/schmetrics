(ns schmetrics.timer
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry timer context]])
  (:require [schmetrics.json :refer [get-mapper]])
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

(defn get-timer
  "Get the timer object from the registry."
  [timer-name]
  (timer (get-registry) timer-name))

(defn start
  "Starts a named timer. Returns nil."
  [timer-name]
  (let [timer (get-timer timer-name)
        ctx (.time timer)]
    (swap! timer-context assoc (keyword timer-name) ctx))
  nil)

(defn stop
  "Stops a named timer. Returns the elapsed time since the timer was started."
  [timer-name]
  (let [timer (get-timer timer-name)
        ctx (get @timer-context (keyword timer-name))]
    (.stop ctx)))

(defn read
  "Read the values of a (previously run) timer."
  [timer-name]
  (merge 
   {:name (keyword timer-name)}
   (read-metric (get-timer timer-name))))

(defmacro with-timer [timer-name & body]
  "Runs the body with the named timer. Returns the elapsed time in nanoseconds. The 
   other timer values can be read with timer/read)."
  `(do
     (start ~timer-name)
     ~@body
     (stop ~timer-name)))
