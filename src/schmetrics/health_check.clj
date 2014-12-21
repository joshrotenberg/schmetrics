(ns schmetrics.health-check
  (:require [schmetrics.json :refer [AsJson get-healthcheck-mapper]])
  (:import [com.codahale.metrics.health HealthCheckRegistry HealthCheck 
            HealthCheck$Result]))

(defonce context (atom {:default-registry (HealthCheckRegistry.)}))

(defn ^HealthCheckRegistry get-registry
  "Returns the HealthCheckRegistry used to register all health checks."
  ([]
     (get @context :default-registry))
  ([registry-name]
     (let [r (get @context (keyword registry-name))]
       (when (nil? r)
         (swap! context assoc (keyword registry-name) (HealthCheckRegistry.)))
       (get @context (keyword registry-name)))))

(defn register
  "Register a health check in the registry."
  [health-check-name health-check & rest]
  (if (fn? health-check)
    (.register (get-registry) (name health-check-name) 
               (proxy [com.codahale.metrics.health.HealthCheck] []
                 (check [] (health-check))))
    (.register (get-registry) (name health-check-name) health-check)))

(defn unregister 
  "Unregisters the named health check."
  [health-check-name]
  (.unregister (get-registry) (name health-check-name)))

(defn get-healthcheck-names
  "Get the names of registered health checks."
  []
  (mapv keyword (.getNames (get-registry))))

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
  "Returns or writes the health check as json, depending on the input. With no arguments, returns the metrics-json health check value
   as a string. Use :as :bytes for a byte array instead (or :as :string if you want to be explicit). If given a single value argument, 
   takes one of the following types; java.io.File, com.fasterxml.jackson.core.JsonGenerator, java.io.OutputStream, or java.io.Writer, 
   and writes the json represntation to that object."
  ([health-check-name]
     (let [hc (.runHealthCheck (get-registry) (name health-check-name))]
       (.writeValueAsString (get-healthcheck-mapper) hc)))
  ([health-check-name & rest]
     (let [hc (.runHealthCheck (get-registry) (name health-check-name))]
       (if (= 2 (count rest)) ;; handle :as <type>
         (let [{:keys [as]} rest]
           (condp = as
             :string (.writeValueAsString (get-healthcheck-mapper) hc)
             :bytes (.writeValueAsBytes (get-healthcheck-mapper) hc)
             (.writeValueAsString (get-healthcheck-mapper) hc)))
         ;; XXX should probably check the type here. see
         ;; http://fasterxml.github.io/jackson-databind/javadoc/2.0.0/com/fasterxml/jackson/databind/ObjectMapper.html
         ;; for the options for writeValue
         (.writeValue (get-healthcheck-mapper) (first rest) hc)))))

