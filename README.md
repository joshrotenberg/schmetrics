# schmetrics

[![Build Status](https://travis-ci.org/joshrotenberg/schmetrics.svg?branch=master)](https://travis-ci.org/joshrotenberg/schmetrics)

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

With leiningen
[![Clojars Project](http://clojars.org/schmetrics/latest-version.svg)](http://clojars.org/schmetrics)

or maven
```xml
<dependency>
  <groupId>schmetrics</groupId>
  <artifactId>schmetrics</artifactId>
  <version>0.2.2</version>
</dependency>
```

`schmetrics` is intended to be a thin Clojure wrapper atop Coda Hale's [metrics](http://metrics.codahale.com/) library for measuring the behavior of various aspects of an application. The Java APIs are fairly straightforward and can fairly easily be used directly via Clojure's interop, but this library ties them up and makes them a little more Clojure-y, managing some of the state behind the scenes and exposing only what's necessary to get the job done. Comments, bugs and patches are welcome. 

This project came after a quick solo brainstorming of something useful and entertaining to write during Clojure West 2014. I've been using metrics on the Java side for a few weeks and just wanted to see how it might look in Clojure. So yeah.

## Metrics

`schmetrics` has a fairly regular API with a few exceptions noted inline below in the docs. You create a unique metric on the fly by calling its specific namespaced function with a keyword or string (such as `inc` for a counter), and then when you want to know the value of the metric, you call its `read` function, which will return a map containing the metric specific values and some meta data.

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

Meters measure the rate of an event over time, i.e. a load average.

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

Histograms measure the statistical distribution in a stream of data.

```clojure
(require '[schmetrics.histogram :as histogram])

(histogram/update :my-histogram 10)
(histogram/update :my-histogram 20)
(histogram/update :my-histogram 30)
(histogram/update :my-histogram 40)
(histogram/update :my-histogram 50)
(histogram/read :my-histogram)
{:75th-percentile 45.0, 
 :99th-percentile 50.0, 
 :stddev 15.811388300841896, 
 :mean 30.0, 
 :name :my-histogram, 
 :median 30.0, 
 :count 5, 
 :999th-percentile 50.0, 
 :max 50, :min 10, 
 :98th-percentile 50.0, 
 :95th-percentile 50.0}
```

### Timers

```clojure
(require '[schmetrics.timer :as timer])

(timer/start :my-timer)
(Thread/sleep 2000)
(timer/stop :my-timer)
2003765859 ;; returns the elapsed time since start was called
(timer/read :my-timer)
{:75th-percentile 2.003765859E9, 
 :99th-percentile 2.003765859E9, 
 :five-minute-rate 0.1750346638085895, 
 :stddev 0.0, 
 :mean 2.003765859E9, 
 :one-minute-rate 0.10268342380651845, 
 :name :my-timer, 
 :median 2.003765859E9, 
 :count 1, 
 :999th-percentile 2.003765859E9, 
 :max 2003765859, 
 :mean-rate 0.021349204399746108, 
 :min 2003765859, 
 :98th-percentile 2.003765859E9, 
 :fiteen-minute-rate 0.19130574782060583, 
 :95th-percentile 2.003765859E9}
```

## Registry

`schmetrics` pretty much hides the `metrics` registry from normal usage, but there are a few things you might need:

```clojure
(require '[schmetrics.registry :as registry]
         '[schmetrics.counter :as counter])
(counter/inc :my-counter 22)
(counter/read :my-counter0
{:count 22, :name :my-counter}
(registry/remove-metric :my-counter)
(counter/read :my-counter)
{:count 0, :name :my-counter}
;; note that since registration of a metric is implicit, remove essentially resets a metric, because the next time you call it it will
;; automatically re-register
(registry/get-metric-names)
[:my-counter]
(registery/get-metrics)
{:my-counter #<Counter com.codahale.metrics.Counter@7f04eeb6>}
(counter/inc :my-other-counter)
(registry/get-counters)
{:my-other-counter #<Counter com.codahale.metrics.Counter@733636ed>, :my-counter #<Counter com.codahale.metrics.Counter@7f04eeb6>}
;; and so on for the other metric types
```

## JSON

It's easy enough to turn Clojure data structures into JSON, for sure, but `schmetrics` includes support for the `metrics-json` JSONification if you want it:

```clojure
(require '[schmetrics.counter :as counter] 
	 '[schmetrics.json :as json])
(counter/inc :my-counter 22)
(counter/read :my-counter)
{:count 22, :name :my-counter}
(json/as-string (counter/get-counter :my-counter))
"{\"count\":22}"
```

If you JSONify the registry itself, all of your metrics will be included:

```clojure
(require '[schmetrics.registry :as registry]
         '[schmetrics.counter :as counter]
         '[schmetrics.histogram :as histogram]
	 '[schmetrics.json :as json])
(counter/inc :my-counter 22)
(histogram/update :my-histogram 42)
(json/as-string (registry/get-registry))
"{\"version\":\"3.0.0\",\"gauges\":{},\"counters\":{\"my-counter\":{\"count\":22}},\"histograms\":{\"my-histogram\":{\"count\":1,\"max\":42,\"mean\":42.0,\"min\":42,\"p50\":42.0,\"p75\":42.0,\"p95\":42.0,\"p98\":42.0,\"p99\":42.0,\"p999\":42.0,\"stddev\":0.0}},\"meters\":{},\"\
timers\":{}}"
```

## History

* Version 0.2.1 - 05/14/14 - repush with doc updates
* Version 0.2.0 - 05/14/14 - updates to json api, initial health check api, add more registry functionality
* Version 0.1.0 - 05/09/14 - initial version pushed to clojars

## TODO

* initial support for health checks is in, need to clean up and document
* support multiple registries

## License

Copyright © 2014 Josh Rotenberg

Distributed under the Apache License 2.0

