(ns schmetrics.json
  (:import [com.codahale.metrics Metric]
           [com.codahale.metrics.health HealthCheck$Result]
           [com.codahale.metrics.json MetricsModule HealthCheckModule]
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

(defn get-mapper
  "Return the ObjectMapper used for converting metrics to json."
  []
  (get @context :mapper))

(defn get-healthcheck-mapper 
  []
  (get @context :health-check-mapper))

(defprotocol AsJson
  (as-string [this] "get the stringified json.")
  (as-bytes [this] "get the json value as a byte array.")
  (as-stream [this s] "get the json value on the given stream."))

(extend-type Metric
  AsJson
  (as-string [this] (.writeValueAsString (get-mapper) this))
  (as-bytes [this] (.writeValueAsBytes (get-mapper) this))
  )

(extend-type HealthCheck$Result
  AsJson
  (as-string [this] (.writeValueAsString (get-healthcheck-mapper) this)))

(defmacro defjson-fn 
  [name doc json-fn get-fn]
  (let [fname (symbol name)]
  `(defn ~fname
     ~doc
     [key#]
     (~json-fn (~get-fn key#)))))
     
