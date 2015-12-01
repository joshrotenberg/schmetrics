(ns schmetrics.counter-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :refer [get-registry counter remove-metric]]
            [schmetrics.counter :as counter]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

(deftest counter-test
  (testing "registry protocol"
    (let [r (get-registry)]
      (is (= com.codahale.metrics.Counter (type (counter r :test-counter))))))
  (testing "counter inc"
    (counter/inc :test-counter)
    (counter/inc :test-counter 2)
    (let [r (counter/read :test-counter)]
      (is (= 3 (:count r)))
      (is (= :test-counter (:name r)))))
  (testing "counter dec"
    (counter/inc :test-counter2 20)
    (counter/dec :test-counter2 19)
    (let [r (counter/read :test-counter2)]
      (is (= 1 (:count r)))
      (is (= :test-counter2 (:name r)))))
  (is (= true (remove-metric :test-counter)))
  (is (= true (remove-metric :test-counter2))))

(deftest counter-test-json
  (testing "counter json"
    (let [counter (counter/inc :test-counter-json 42)
          json (json/as-string (counter/get-counter :test-counter-json))]
      (is (= (:count (parse-string json true))
             (:count (counter/read :test-counter-json)))))
    (is (= true (remove-metric :test-counter-json)))))
