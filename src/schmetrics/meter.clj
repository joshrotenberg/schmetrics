(ns schmetrics.meter
  (:refer-clojure :exclude [read])
  (:require [schmetrics.core :refer [context read-metric]])
  (:import [com.codahale.metrics Meter]))

(defn- retrieve-meter
  [n]
  (let [registry (get @context :registry)]
    (.meter registry (name n))))

(defn mark
  ([name n]
     (let [registry (get @context :registry)
           meter (retrieve-meter name)]
       (.mark meter n)))
  ([name]
     (mark name 1)))

(defn read
  [name]
  (merge 
   {:name (keyword name)}
   (read-metric (retrieve-meter name))))
   
