(ns schmetrics.timer-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :refer [get-registry timer]]
            [schmetrics.timer :as timer]))

(deftest timer-test
  (testing "registry protocol"
    (let [r (get-registry)]
      (is (= com.codahale.metrics.Timer (type (timer r :test-timer))))))
  (testing "timer"
    (is (= 0 (:count (timer/read :test-timer))))
    (timer/start :test-timer)
    (Thread/sleep 2000)
    (timer/stop :test-timer)
    (is (= 1 (:count (timer/read :test-timer))))
    (is (= (:min (timer/read :test-timer)) (:max (timer/read :test-timer))))
    (timer/start :test-timer)
    (Thread/sleep 2000)
    (timer/stop :test-timer)
    (is (= 2 (:count (timer/read :test-timer))))
    (is (< (:min (timer/read :test-timer)) (:max (timer/read :test-timer))))))
