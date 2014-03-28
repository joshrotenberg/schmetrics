(ns schmetrics.registry
  (:import [com.codahale.metrics MetricRegistry Counter Gauge Meter Histogram Timer]))

(defonce context (atom {:registry (MetricRegistry.)
                        :counters {}
                        }))

(defn get-registry 
  []
  (get @context :registry))

(defprotocol ReadMetric
  (read-metric [this] "Read the various values of a metric and return a map"))

(defprotocol LookupMetric
  (counter [this n] "register/lookup a counter from the registry")
  (gauge [this n] "register/lookup a gauge from the registry")
  (meter [this n] "register/lookup a meter from the registry")
  (histogram [this n] "register/lookup a histogram from the registry")
  (timer [this n] "register/lookup a timer from the registry"))

  
(extend-protocol LookupMetric
  MetricRegistry
  (histogram [this n] (.histogram this (name n)))
  (gauge [this n] 
    (let [gauges (.getGauges (get-registry))]
      (.get gauges (name n))))
  (meter [this n] (.meter this (name n)))
  (counter [this n] (.counter this (name n)))
  (timer [this n] 
    (let [t (.timer this (name n))]
      (swap! context assoc-in [:timer n] t)
      t)))
