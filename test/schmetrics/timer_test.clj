(ns schmetrics.timer-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :refer [get-registry timer]]
            [schmetrics.timer :as timer]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

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

(deftest timer-test-json
  (testing "timer json"
    (timer/start :test-timer-json)
    (Thread/sleep 2000)
    (timer/stop :test-timer-json)
    (let [timer (timer/read :test-timer-json)
          json (json/as-string (timer/get-timer :test-timer-json))]
      (is (= (:count timer) 
             (:count (parse-string json true)))))))

