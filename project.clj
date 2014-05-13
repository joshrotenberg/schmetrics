(defproject schmetrics "0.1.0"
  :description "Clojure Bindings for metrics"
  :url "https://github.com/joshrotenberg/schmetrics"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.codahale.metrics/metrics-core "3.0.2"]
                 [com.codahale.metrics/metrics-json "3.0.2"]
                 [com.codahale.metrics/metrics-healthchecks "3.0.2"]
                 [cheshire "5.3.1" :scope "test"]]
  :plugins [[codox "0.8.0"]]
  :codox {:src-dir-uri "http://github.com/joshrotenberg/schmetrics/blob/master/"
          :src-linenum-anchor-prefix "L"})




