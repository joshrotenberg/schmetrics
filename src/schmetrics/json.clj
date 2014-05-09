(ns schmetrics.json
  (:import com.codahale.metrics.json.MetricsModule
           com.fasterxml.jackson.databind.ObjectMapper
           java.util.concurrent.TimeUnit))

(defonce context (atom {:mapper (doto (ObjectMapper.) 
                                  (.registerModule 
                                   (MetricsModule. TimeUnit/SECONDS 
                                                   TimeUnit/MILLISECONDS 
                                                   false)))}))

(defn get-mapper
  "Return the ObjectMapper used for converting metrics to json."
  []
  (get @context :mapper))

