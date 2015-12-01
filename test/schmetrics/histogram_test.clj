(ns schmetrics.histogram-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :refer [get-registry histogram remove-metric]]
            [schmetrics.histogram :as histogram]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

(deftest histogram-test
  (testing "registry protocol"
    (let [r (get-registry)]
      (is (= com.codahale.metrics.Histogram (type (histogram r :test-histogram-protocol)))))
    (is (= true (remove-metric :test-histogram-protocol))))
  (testing "histogram update"
    (doseq [n [10 20 30 40 50]]
      (histogram/update :test-histogram n))
    (let [r (histogram/read :test-histogram)]
      (is (= :test-histogram (:name r)))
      (is (= 5 (:count r)))
      (is (= 50 (:max r)))
      (is (= 10 (:min r)))
      (is (= 30.0 (:mean r)))
      (is (= 30.0 (:median r)))
      (is (= 40.0 (:75th-percentile r)))
      (is (= 50.0 (:95th-percentile r)))
      (is (= 50.0 (:98th-percentile r)))
      (is (= 50.0 (:99th-percentile r)))
      (is (= 50.0 (:999th-percentile r))))
    (is (= true (remove-metric :test-histogram)))))

(deftest histogram-test-json
  (testing "histogram json"
    (let [histogram (histogram/update :test-histogram-json 42)
          json (json/as-string (histogram/get-histogram :test-histogram-json))]
      (is (= (:count (parse-string json true))
             (:count (histogram/read :test-histogram-json)))))
    (is (= true (remove-metric :test-histogram-json)))))
