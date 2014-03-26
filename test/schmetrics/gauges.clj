(ns schmetrics.gauge-test
  (:require [clojure.test :refer :all]
            [schmetrics.gauge :as gauge]))


(deftest gauge-test
  (testing "gauge read"
    (println (gauge/retrieve-gauge "foo"))))
