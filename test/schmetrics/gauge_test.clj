(ns schmetrics.gauge-test
  (:require [clojure.test :refer :all]
            [schmetrics.gauge :as gauge]))

(deftest gauge-test
  (testing "gauge creation"
    (gauge/register "gauge-foo" #(+ 1 2))
    (let [r (gauge/read "gauge-foo")]
      (is (= :gauge-foo (:name r)))
      (is (= 3 (:value r)))))
  (testing "gauge read"
    (gauge/register "what" #(java.util.Date.))
    (let [r1 (gauge/read :what)
          _ (Thread/sleep 1001)
          r2 (gauge/read :what)]
      (is (.before (:value r1) (:value r2))))))
