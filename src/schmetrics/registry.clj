(ns schmetrics.registry
  (:import [com.codahale.metrics MetricRegistry Counter Gauge 
            Meter Histogram Timer]))

(defonce context (atom {:registry (MetricRegistry.)}))

(defn ^MetricRegistry get-registry 
  "Returns the MetricRegistry used to register all metrics."
  []
  (get @context :registry))

(defn remove-metric
  "Removes the metric from the registry."
  [metric-name]
  (.remove (get-registry) (name metric-name)))

(defn get-metric-names
  "Get a vector of all the registered metric names."
  []
  (into [] (map keyword (.getNames (get-registry)))))

(defn- keywordize-keys
  [m]
  (zipmap (map keyword (keys m)) (vals m)))

(defn get-gauges
  "Returns a map of all the currently registered gauges."
  []
  (keywordize-keys (.getGauges (get-registry))))

(defn get-counters
  "Returns a map of all the currently registered counters."
  []
  (keywordize-keys (.getCounters (get-registry))))

(defn get-histograms
  "Returns a map of all the currently registered histograms."
  []
  (keywordize-keys (.getHistograms (get-registry))))

(defn get-meters
  "Returns a map of all the currently registered meters."
  []
  (keywordize-keys (.getMeters (get-registry))))

(defn get-timers
  "Returns a map of all the currently registered timers."
  []
  (keywordize-keys (.getTimers (get-registry))))

(defn get-metrics
  "Returns a map of all the currently registered metrics."
  []
  (keywordize-keys (.getMetrics (get-registry))))

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
  (timer [this n]  (.timer this (name n))))
