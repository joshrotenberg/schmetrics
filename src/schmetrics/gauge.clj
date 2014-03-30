(ns schmetrics.gauge
  (:refer-clojure :exclude [read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry gauge context]])
  (:import com.codahale.metrics.Gauge))

(extend-type Gauge
  ReadMetric
  (read-metric [this] {:value (.getValue this)}))

(defn- retrieve-gauge
  [gauge-name]
  (gauge (get-registry) gauge-name))

(defn register 
  "Register the function gauge-fn named by the gauge gauge-name. When read, the function's value will
   be returned."
  [gauge-name gauge-fn]
  (.register (get-registry) (name gauge-name)
             (proxy [com.codahale.metrics.Gauge] []
               (getValue [] (gauge-fn)))))

(defn read
  "Read the current value for gauge-name."
  [gauge-name]
  (merge
   {:name (keyword gauge-name)}
   (read-metric (retrieve-gauge gauge-name))))
