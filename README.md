# schmetrics

A simple Clojure wrapper around Coda Hale's metrics (3.x) library. Support for Gauges, Counters, Meters, Timers and Histograms.

## Usage

### Counters

```clojure
(ns my.thing
 (:require [schmetrics.counter :as counter]))

(defn my-func
  []
  ;; do some stuff
  (counter/inc :my-func-counter))

(dotimes [x 123] (my-func))
(let [r (counter/read :my-func-counter)]
  (:count r)) ;; 123

```

## License

Copyright © 2014 Josh Rotenberg

Distributed under the Apache License 2.0

