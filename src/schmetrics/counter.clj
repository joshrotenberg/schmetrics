(ns schmetrics.counter
  (:refer-clojure :exclude [inc dec read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry 
                                         counter context]])
  (:require [schmetrics.json :refer [get-mapper]])
  (:import [com.codahale.metrics Counter]))

(extend-protocol ReadMetric
  Counter
  (read-metric [this] {:count (.getCount this)}))

(defn ^Counter get-counter
  "Get the counter object from the registry."
  [counter-name]
  (counter (get-registry) counter-name))

(defn inc 
  "Increment the counter named counter-name by to-inc, or by 1 if not specified."
  ([counter-name to-inc] 
     (let [counter (get-counter counter-name)]
       (.inc counter to-inc)))
  ([counter-name] 
     (inc counter-name 1)))

(defn dec
  "Decrement the counter named counter-name by to-dec, or by 1 if not specified."
  ([counter-name to-dec]
     (let [counter (get-counter counter-name)]
       (.dec counter to-dec)))
  ([counter-name] 
     (dec counter-name 1)))

(defn read
  "Returns the current count for counter-name."
  [counter-name]
  (merge 
   {:name (keyword counter-name)}
   (read-metric (get-counter counter-name))))

