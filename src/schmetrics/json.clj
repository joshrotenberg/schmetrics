(ns schmetrics.json
  (:import [com.codahale.metrics.json MetricsModule HealthCheckModule]
           com.fasterxml.jackson.databind.ObjectMapper
           java.util.concurrent.TimeUnit))

(defonce context (atom {:mapper (doto (ObjectMapper.) 
                                  (.registerModule 
                                   (MetricsModule. TimeUnit/SECONDS 
                                                   TimeUnit/MILLISECONDS 
                                                   false)))
                        :health-check-mapper (doto (ObjectMapper.)
                                               (.registerModule
                                                (HealthCheckModule.)))}))

(defprotocol AsJson
  (as-string [this] "get the stringified json.")
  (as-bytes [this] "get the json value as a byte array.")
  (as-stream [this s] "get the json value on the given stream."))

(defn get-mapper
  "Return the ObjectMapper used for converting metrics to json."
  []
  (get @context :mapper))

(defn get-healthcheck-mapper 
  []
  (get @context :health-check-mapper))

(defn as-string
  "Return a json representation of the metric or health check."
  [o]
  (prn (type o))
  (.writeValueAsString (get-mapper) o))
