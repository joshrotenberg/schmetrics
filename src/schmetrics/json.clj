(ns schmetrics.json
  (:import [com.codahale.metrics MetricRegistry Metric]
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
  (to-stream [this s] "write the json value to the given stream.")
  (to-file [this f] "write the json value to the given file.")
  (to-generator [this g] "write the json value to the given generator."))

(extend-type Metric
  AsJson
  (as-string [this] (.writeValueAsString (get-mapper) this))
  (as-bytes [this] (.writeValueAsBytes (get-mapper) this))
  (to-stream [this s] (.writeValue (get-mapper) s this))
  (to-file [this f] (.writeValue (get-mapper) f this))
  (to-generator [this g] (.writeValue (get-mapper) g this)))

(extend-type MetricRegistry
  AsJson
  (as-string [this] (.writeValueAsString (get-mapper) this))
  (as-bytes [this] (.writeValueAsBytes (get-mapper) this))
  (to-stream [this s] (.writeValue (get-mapper) s this))
  (to-file [this f] (.writeValue (get-mapper) f this))
  (to-generator [this g] (.writeValue (get-mapper) g this)))

(defmacro defjson-fn 
  [name doc json-fn get-fn]
  (let [fname (symbol name)]
  `(defn ~fname
     ~doc
     [key#]
     (~json-fn (~get-fn key#)))))
     
