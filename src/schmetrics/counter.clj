(ns schmetrics.counter
  (:refer-clojure :exclude [inc dec read])
  (:require [schmetrics.core :refer [context metric-name read-metric]])
  (:import [com.codahale.metrics Counter]))

(defn- retrieve-counter
  [n]
  (let [registry (get @context :registry)]
    (.counter registry (name n))))

(defn inc 
   ([name n] 
     (let [registry (get @context :registry)
            counter (retrieve-counter name)]
        (.inc counter n)))
    ([name] 
      (inc name 1)))

(defn dec
  ([name n]
     (let [registry (get @context :registry)
           counter (retrieve-counter name)]
       (.dec counter n)))
  ([name] 
     (dec name 1)))

(defn read
  [name]
  (merge 
   {:name (keyword name)}
   (read-metric (retrieve-counter name))))
