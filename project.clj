(defproject schmetrics "0.2.2"
  :description "Clojure Bindings for metrics"
  :url "https://github.com/joshrotenberg/schmetrics"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [io.dropwizard.metrics/metrics-core "3.1.2"]
                 [io.dropwizard.metrics/metrics-json "3.1.2"]
                 [io.dropwizard.metrics/metrics-healthchecks "3.1.2"]
                 [cheshire "5.3.1" :scope "test"]]
  :plugins [[codox "0.8.0"]]
  :profiles {:dev
             {:plugins [[lein-cloverage "1.0.6"]]}}
  :codox {:src-dir-uri "http://github.com/joshrotenberg/schmetrics/blob/master/"
          :src-linenum-anchor-prefix "L"})




