(ns schmetrics.registry-test
  (:require [clojure.test :refer :all]
            [schmetrics.registry :as registry]
            [schmetrics.counter :as counter]
            [schmetrics.json :as json])
  (:require [cheshire.core :refer [parse-string]]))

(deftest registry-test
  (testing "registry"
    (let [r (registry/get-registry)
          counter (counter/inc :registry-test-counter 42)
          json (json/as-string r)
          tmp-file (java.io.File/createTempFile "registry" ".json")]
      (= (String. (json/as-bytes r))
         (json/as-string r))
      (with-open [file (clojure.java.io/writer tmp-file)]
        (json/to-file r file)
        (= (json/as-string r)
           (slurp tmp-file)))
      (is (= com.codahale.metrics.MetricRegistry (type r)))
      (is (not= nil (some #{:registry-test-counter} (registry/get-metric-names))))
      (is (= (:count (counter/read :registry-test-counter))
             (-> (parse-string json true) :counters :registry-test-counter :count)))
      (is (= true (registry/remove-metric :registry-test-counter)))
      (is (= false (registry/remove-metric :registry-test-counter))))))

            
