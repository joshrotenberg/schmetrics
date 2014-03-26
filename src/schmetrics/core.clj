(ns schmetrics.core
  (:import [com.codahale.metrics MetricRegistry Counter Gauge Meter Histogram Timer]))


(defonce context (atom {:registry (MetricRegistry.)
                        :counters {}
                        }))

(defn metric-name
  ([n]
     (MetricRegistry/name "" (into-array String [n])))
  ([ns n]
     (MetricRegistry/name ns (into-array String [n]))))

(defn- merge-meta [o m]
  (merge {:type (type o) :read-time (java.util.Date.)} m))
     
(defprotocol ReadMetric
  (read-metric [this] "Read the various values of a metric and return a map"))

(extend-protocol ReadMetric
  Counter
  (read-metric [this] (merge-meta this {:count (.getCount this)}))
  Gauge
  (read-metric [this] (merge-meta this {:value (.getValue this)}))
  Meter
  (read-metric [this] (merge-meta this {:count (.getCount this)
                                        :fifteen-minute-rate (.getFifteenMinuteRate this)
                                        :five-minute-rate (.getFiveMinuteRate this)
                                        :one-minute-rate (.getOneMinuteRate this)
                                        :mean-rate (.getMeanRate this)
                                        }))
  Histogram
  (read-metric [this] 
    (let [snapshot (.getSnapshot this)]
      (merge-meta this {:count (.getCount this)
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
  Timer
  (read-metric [this]
    (let [snapshot (.getSnapshot this)]
      (merge-meta this {:count (.getCount this)}))))
