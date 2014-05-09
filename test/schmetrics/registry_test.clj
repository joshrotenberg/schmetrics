(ns schmetrics.registry-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :as registry]
            [schmetrics.counter :as counter])
  (:require [cheshire.core :refer [parse-string]]))

(deftest registry-test
  (testing "registry"
    (let [r (registry/get-registry)
          counter (counter/inc :registry-test-counter 42)
          json (registry/json)]
      (is (= com.codahale.metrics.MetricRegistry (type r)))
      (is (= (:count (counter/read :registry-test-counter))
             (-> (parse-string json true) :counters :registry-test-counter :count))))))

            
