(ns schmetrics.gauge-test
  (:require [clojure.test :refer :all]
            [schmetrics.gauge :as gauge]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

(deftest gauge-test
  (testing "gauge creation"
    (gauge/register :test-gauge #(+ 1 2))
    (let [r (gauge/read :test-gauge)]
      (is (= :test-gauge (:name r)))
      (is (= 3 (:value r)))))
  (testing "gauge read"
    (gauge/register "other-gauge" #(java.util.Date.))
    (let [r1 (gauge/read :other-gauge)
          _ (Thread/sleep 1001)
          r2 (gauge/read :other-gauge)]
      (is (.before (:value r1) (:value r2))))))

(deftest gauge-json-test
  (testing "gauge json"
    (gauge/register :test-gauge-json #(+ 40 2))
    (let [gauge (gauge/read :test-gauge-json)
          json (json/as-string (gauge/get-gauge :test-gauge-json))]
      (is (= (:value (parse-string json true))
             (:value (gauge/read :test-gauge-json)))))))

