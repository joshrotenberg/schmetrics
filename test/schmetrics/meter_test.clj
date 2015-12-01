(ns schmetrics.meter-test
  (:require [clojure.test :refer :all]
            [schmetrics.meter :as meter]
            [schmetrics.registry :refer [get-registry meter remove-metric]]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

(deftest meter-test
  (testing "registry protocol"
    (let [r (get-registry)]
      (is (= com.codahale.metrics.Meter (type (meter r :test-meter))))))
  (testing "meter creation"
    (meter/mark :test-meter)
    (meter/mark :test-meter 100)
    (= (meter/get-meter "test-meter")
       (meter/get-meter :test-meter))
    (let [r (meter/read :test-meter)]
      (is (= 101 (:count r)))
      (is (= :test-meter (:name r))))
    (is (= true (remove-metric :test-meter)))))

(deftest meter-json-test
  (testing "meter json"
    (let [meter (meter/mark :test-meter-json 42)
          string-json (json/as-string (meter/get-meter :test-meter-json))
          bytes-json (json/as-bytes (meter/get-meter :test-meter-json))]
      (is (= (:count (parse-string string-json true))
             (:count (meter/read :test-meter-json))))
      (is (= (:count (parse-string (String. bytes-json)))
             (:count (parse-string string-json)))))
    (is (= true (remove-metric :test-meter-json)))))


