(ns schmetrics.json
  (:require [schmetrics.registry :as registry])
  (:import [com.codahale.metrics MetricRegistry Metric]
           [com.codahale.metrics.health HealthCheck$Result]
           [com.codahale.metrics.json MetricsModule HealthCheckModule]
           [com.fasterxml.jackson.databind ObjectMapper]
           java.util.concurrent.TimeUnit))

(defonce context (atom {:mapper (doto (ObjectMapper.) 
                                  (.registerModule 
                                   (MetricsModule. TimeUnit/SECONDS 
                                                   TimeUnit/MILLISECONDS 
                                                   false)))
                        :health-check-mapper (doto (ObjectMapper.)
                                               (.registerModule
                                                (HealthCheckModule.)))}))

(defn ^ObjectMapper get-mapper
  "Return the ObjectMapper used for converting metrics to json."
  []
  (get @context :mapper))

(defn ^ObjectMapper get-healthcheck-mapper 
  []
  (get @context :health-check-mapper))

(defprotocol AsJson
  (as-string [this] "get the stringified json.")
  (as-bytes [this] "get the json value as a byte array.")
  (to-stream [this s] "write the json value to the given stream.")
  (to-file [this f] "write the json value to the given file.")
  (to-writer [this w] "write the json value to the given writer.")
  (to-generator [this g] "write the json value to the given generator."))

(extend-type Metric
  AsJson
  (as-string [this] (.writeValueAsString (get-mapper) this))
  (as-bytes [this] (.writeValueAsBytes (get-mapper) this))
  (to-stream [this ^java.io.OutputStream s] (.writeValue (get-mapper) s this))
  (to-file [this ^java.io.File f] (.writeValue (get-mapper) f this))
  (to-writer [this ^java.io.Writer w] (.writeValue (get-mapper) w this))
  (to-generator [this ^com.fasterxml.jackson.core.JsonGenerator g] (.writeValue (get-mapper) g this)))

(extend-type MetricRegistry
  AsJson
  (as-string [this] (.writeValueAsString (get-mapper) this))
  (as-bytes [this] (.writeValueAsBytes (get-mapper) this))
  (to-stream [this ^java.io.OutputStream s] (.writeValue (get-mapper) s this))
  (to-file [this ^java.io.File f] (.writeValue (get-mapper) f this))
  (to-writer [this ^java.io.Writer w] (.writeValue (get-mapper) w this))
  (to-generator [this ^com.fasterxml.jackson.core.JsonGenerator g] (.writeValue (get-mapper) g this)))

(defmacro defjson-fn 
  [name doc json-fn get-fn]
  (let [fname (symbol name)]
  `(defn ~fname
     ~doc
     [key#]
     (~json-fn (~get-fn key#)))))
     
