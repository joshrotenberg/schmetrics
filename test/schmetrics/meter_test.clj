(ns schmetrics.meter-test
  (:require [clojure.test :refer :all]
            [schmetrics.meter :as meter]))


(deftest meter-test
  (testing "meter creation"
    (meter/mark :test-meter)
    (meter/mark :test-meter 100)
    (let [r (meter/read :test-meter)]
      (prn r)
      (is (isa? (:type r) com.codahale.metrics.Meter))
      (is (= 101 (:count r)))
      (is (= :test-meter (:name r))))))

