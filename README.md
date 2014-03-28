# schmetrics

A simple Clojure wrapper around Coda Hale's metrics (3.x) library. Support for Gauges, Counters, Meters, Timers and Histograms.

## Usage

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
  
;; ...

```
## Overview

`schmetrics` is intended to be a thin Clojure wrapper atop Coda Hale's [metrics](http://metrics.codahale.com/) library for measuring the behavior of various aspects of an application. The Java APIs are fairly straightforward and can fairly easily be used directly via Clojure's interop, but this library ties them up and makes them a little more Clojure-y, managing some of the state behind the scenes and exposing only what's necessary to get the job done. Comments, bugs and patches are welcome. 

This project came after a quick solo brainstorming of something useful and entertaining to write during Clojure West 2014. I've been using metrics on the Java side for a few weeks and just wanted to see how it might look in Clojure. So yeah.

## Metrics

`schmetrics` has a fairly regular API with a few exceptions noted inline below in the docs. You create a unique metric on the fly by calling it's specific namespaced function with a keyword or string, and then when you want to know the value of the metric, you call it's read function, which will return a map containing the metric specific values and some meta data.

### Gauges

Gauges measure a value at a given point in time. In `schmetrics`, you are essentially creating a closure around some value that may be harder to reach later on, and you then have the ability to read its value at a later point. The metrics example shows a Gauge closing over the size of a queue in a queue manager class' constructor. Because we have functions as first class values in Clojure, this may not really need an equivalent, but its there for completeness and might be useful in other situations:

```clojure
(require '[schmetrics.gauge :as gauge])

;; register a gauge with a unique name and a function that takes no arguments ...
(gauge/register :my-gauge #(+ 1 2))

;; when you read the gauge at some later point, the function will be called and it's value returned
(gauge/read :my-gauge) 
{:value 3, ;; this was the result of our gauge function above 
 :name :my-gauge}

```

### Counters

A Counter ... counts. Up or down.

```clojure
(require '[schmetrics.counter :as counter])

(counter/inc :my-counter 1)
(counter/inc :my-counter 2)
(counter/read :my-counter)
{:count 3, 
 :name :my-counter}
(counter/dec :my-counter 1)
{:count 2, 
 :name :my-counter}
```

### Meters

Meters measure the rate of an even over time, i.e. a load average.

```clojure
(require '[schmetrics.meter :as meter])
(meter/mark :my-meter 2)
(meter/mark :my-meter 3)
(meter/mark :my-meter 2)
(meter/mark :my-meter 4)
(meter/read :my-meter)
{:mean-rate 0.8747603265851388, 
 :one-minute-rate 1.3520266487775938, 
 :five-minute-rate 1.3900828722929706, 
 :fifteen-minute-rate 1.396675908802938, 
 :count 11, 
 :name :my-meter}
```

### Histograms

```clojure
```

### Timers

```clojure
```

## License

Copyright © 2014 Josh Rotenberg

Distributed under the Apache License 2.0

