(ns schmetrics.gauge
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry gauge context]])
  (:import com.codahale.metrics.Gauge))

(extend-type Gauge
  ReadMetric
  (read-metric [this] {:value (.getValue this)}))

(defn retrieve-gauge
  [n]
  (gauge (get-registry) n))

(defn register [n f]
  (.register (get-registry) (name n)
             (proxy [com.codahale.metrics.Gauge] []
               (getValue [] (f)))))
        
(defn read
  [name]
  (merge
   {:name (keyword name)}
   (read-metric (retrieve-gauge name))))
