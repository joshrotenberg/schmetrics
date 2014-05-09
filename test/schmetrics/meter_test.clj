(ns schmetrics.meter-test
  (:require [clojure.test :refer :all]
            [schmetrics.meter :as meter])
  (:require [cheshire.core :refer [parse-string]]))

(deftest meter-test
  (testing "meter creation"
    (meter/mark :test-meter)
    (meter/mark :test-meter 100)
    (let [r (meter/read :test-meter)]
      (is (= 101 (:count r)))
      (is (= :test-meter (:name r))))))


(deftest meter-json-test
  (testing "meter json"
    (let [meter (meter/mark :test-meter-json 42)
          json (meter/json :test-meter-json)]
      (is (= (:count (parse-string json true))
             (:count (meter/read :test-meter-json)))))))

