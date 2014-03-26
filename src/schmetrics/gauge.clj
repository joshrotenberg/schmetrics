(ns schmetrics.gauge
  (:refer-clojure :exclude [read])
  (:require [schmetrics.core :refer [context read-metric]])
  (:import com.codahale.metrics.Gauge))

(defn retrieve-gauge
  [n]
  (let [registry (get @context :registry)
        gauges (.getGauges registry)]
    (.get gauges (name n))))

(defn register [n f]
  (let [registry (get @context :registry)]
    (.register registry (name n)
               (proxy [com.codahale.metrics.Gauge] []
                 (getValue [] (f))))))
        
(defn read
  [name]
  (merge
   {:name (keyword name)}
   (read-metric (retrieve-gauge name))))
