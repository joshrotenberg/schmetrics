(ns schmetrics.health-check
  (:import [com.codahale.metrics.health HealthCheckRegistry HealthCheck HealthCheck$Result]))

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

(defn- result-to-map
  "Returns a mapified version of the HealthCheck$Result instance."
  [entry]
  {(keyword (key entry)) 
   (dissoc (bean (val entry)) :class)})

(defn run-health-checks
  "Runs the registered health checks and return a map of their results."
  []
  (let [f (first (.runHealthChecks (get-registry)))]
    (into {} (map result-to-map (.runHealthChecks (get-registry))))))

(defn healthy
  ([]
     (HealthCheck$Result/healthy))
  ([str]
     (HealthCheck$Result/healthy str))
  ([format & args]
     (HealthCheck$Result/healthy format (into-array Object args))))

(defn unhealthy
  ([arg]
     (HealthCheck$Result/unhealthy arg))
  ([format & args]
     (HealthCheck$Result/unhealthy format (into-array Object args))))
