(ns schmetrics.counter
  (:refer-clojure :exclude [inc dec read])
  (:require [schmetrics.registry :refer [ReadMetric read-metric get-registry 
                                         counter context]])
  (:import [com.codahale.metrics Counter]))

(extend-protocol ReadMetric
  Counter
  (read-metric [this] {:count (.getCount this)}))

(defn- retrieve-counter
  [n]
  (counter (get-registry) n))

(defn inc 
  ([name n] 
     (let [counter (retrieve-counter name)]
       (.inc counter n)))
  ([name] 
     (inc name 1)))

(defn dec
  ([name n]
     (let [counter (retrieve-counter name)]
       (.dec counter n)))
  ([name] 
     (dec name 1)))

(defn read
  [name]
  (merge 
   {:name (keyword name)}
   (read-metric (retrieve-counter name))))
