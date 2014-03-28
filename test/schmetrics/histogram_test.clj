(ns schmetrics.histogram-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :refer [get-registry histogram]]
            [schmetrics.histogram :as histogram]))

(deftest histogram-test
  (testing "registry protocol"
    (let [r (get-registry)]
      (is (= com.codahale.metrics.Histogram (type (histogram r :foo))))))
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
      (is (= 45.0 (:75th-percentile r)))
      (is (= 50.0 (:95th-percentile r)))
      (is (= 50.0 (:98th-percentile r)))
      (is (= 50.0 (:99th-percentile r)))
      (is (= 50.0 (:999th-percentile r))))))
