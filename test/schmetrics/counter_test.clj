(ns schmetrics.counter-test
  (:require [clojure.test :refer :all]
            [schmetrics.counter :as counter]))

(deftest counter-test
  (testing "counter inc"
    (counter/inc :foo)
    (counter/inc "foo" 2)
    (let [r (counter/read :foo)]
      (is (= com.codahale.metrics.Counter (:type r)))
      (is (= 3 (:count r)))
      (is (= :foo (:name r)))
      ))
  (testing "counter dec"
    (counter/inc :bar 20)
    (counter/dec :bar 19)
    (let [r (counter/read :bar)]
      (is (= com.codahale.metrics.Counter (:type r)))
      (is (= 1 (:count r)))
      (is (= :bar (:name r)))
      )))


