(defproject schmetrics "0.3.0"
  :description "Clojure Bindings for metrics"
  :url "https://github.com/joshrotenberg/schmetrics"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [io.dropwizard.metrics/metrics-core "3.1.2"]
                 [io.dropwizard.metrics/metrics-json "3.1.2"]
                 [io.dropwizard.metrics/metrics-healthchecks "3.1.2"]]
  :plugins [[lein-codox "0.9.0"]]
  :profiles {:test {:dependencies [[cheshire "5.3.1"]]
                    :plugins [[lein-cloverage "1.0.6"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :dev [:1.7 :test]
             }
  :codox {:src-dir-uri "http://github.com/joshrotenberg/schmetrics/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :aliases {"test-all"   ["with-profile" "+1.5:+1.6:+1.7" "test"]})




