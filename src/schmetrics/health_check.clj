(ns schmetrics.health-check
  (:require [schmetrics.json :refer [get-mapper]])
  (:import [com.codahale.metrics.health HealthCheckRegistry HealthCheck 
            HealthCheck$Result]))

(defonce context (atom {:registry (HealthCheckRegistry.)}))

(defn get-registry
  "Returns the HealthCheckRegistry used to register all health checks."
  []
  (get @context :registry))

(defn register
  "Register a health check in the registry."
  [health-check-name health-check]
  (if (fn? health-check)
    (.register (get-registry) (name health-check-name) 
               (proxy [com.codahale.metrics.health.HealthCheck] []
                 (check [] (health-check))))
    (.register (get-registry) (name health-check-name) health-check)))

(defn unregister 
  "Unregisters the named health check."
  [health-check-name]
  (.unregister (get-registry) (name health-check-name)))

(defn get-names
  "Get the names of registered health checks."
  []
  (into [] (map keyword (.getNames (get-registry)))))

(defn- result-to-map
  "Returns a mapified version of the HealthCheck$Result instance."
  [entry]
  {(keyword (key entry)) 
   (dissoc (bean (val entry)) :class)})

(defn run-health-check
  "Runs the named health check."
  [health-check-name]
  (dissoc (bean (.runHealthCheck (get-registry) (name health-check-name))) :class))

(defn run-health-checks
  "Runs the registered health checks and return a map of their results."
  []
  (into {} (map result-to-map (.runHealthChecks (get-registry)))))

(defn healthy
  "Wraps the HealthCheck.Result healthy static method. Call with no arguments to simply respond healthy with no 
message, or one or more args to return a formatted string message."
  ([]
     (HealthCheck$Result/healthy))
  ([str]
     (HealthCheck$Result/healthy str))
  ([format & args]
     (HealthCheck$Result/healthy format (into-array Object args))))

(defn unhealthy
  "Wraps the HealthCheck.Result unhealthy static method. Call with one argument to either pass in an exception or a string 
message, or two or more arguments to return a formatted string message."
  ([arg]
     (HealthCheck$Result/unhealthy arg))
  ([format & args]
     (HealthCheck$Result/unhealthy format (into-array Object args))))

(defn json
  []
  (.writeValueAsString (get-mapper) (run-health-checks)))
