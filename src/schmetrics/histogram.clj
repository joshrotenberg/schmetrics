(ns schmetrics.histogram
  (:refer-clojure :exclude [read])
  (:require [schmetrics.core :refer [context read-metric]])
  (:import [com.codahale.metrics Histogram]))

(defn- retrieve-histogram
  [n]
  (let [registry (get @context :registry)]
    (.histogram registry (name n))))

(defn update 
  [name n]
  (let [registry (get @context :registry)
        histogram (retrieve-histogram name)]
    (.update histogram n)))

(defn read
  [name]
  (merge
   {:name (keyword name)}
   (read-metric (retrieve-histogram name))))
