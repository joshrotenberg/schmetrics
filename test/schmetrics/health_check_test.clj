(ns schmetrics.health-check-test
  (:require [clojure.test :refer :all]
            [schmetrics.health-check :as health-check])
  (:import [com.codahale.metrics.health HealthCheck HealthCheck$Result]))

(deftest health-check-test
  (testing "health check creation"
    (health-check/register :proxy-healthy-noarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (HealthCheck$Result/healthy))))
    (health-check/register :proxy-healthy-strarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (HealthCheck$Result/healthy "aww yeah"))))
    (health-check/register :proxy-healthy-formatarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] 
                               (HealthCheck$Result/healthy "aww %s %d" (into-array Object ["yeah" 42])))))
    
    (health-check/register :proxy-unhealthy-strarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (HealthCheck$Result/unhealthy "darn"))))
    (health-check/register :proxy-unhealthy-formatarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (HealthCheck$Result/unhealthy "oh no %s %d" (into-array Object ["bummer" 42])))))
    (health-check/register :proxy-unhealthy-exceptionarg 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (HealthCheck$Result/unhealthy (Exception. "weak")))))
    (health-check/register :proxy-unhealthy-exceptionthrown 
                           (proxy [com.codahale.metrics.health.HealthCheck] []
                             (check [] (throw (Exception. "doof")))))
    

    (health-check/register :fn-healthy-noarg #(health-check/healthy))
    (health-check/register :fn-healthy-strarg #(health-check/healthy "check it"))
    (health-check/register :fn-healthy-formatarg #(health-check/healthy "get %s %d" "cool" 42))

    (health-check/register :fn-unhealthy-strarg #(health-check/unhealthy "not so great"))
    (health-check/register :fn-unhealthy-formatarg #(health-check/unhealthy "very %s %d" "bad" 42))
    (health-check/register :fn-unhealthy-exceptionarg #(health-check/unhealthy (Exception. "dude this sucks")))
    (health-check/register :fn-unhealthy-exceptionthrown #(health-check/unhealthy (throw (Exception. "i know right?"))))

    (let [health-checks (health-check/run-health-checks)]
      (is (= (:proxy-healthy-noarg health-checks)
             {:error nil, :healthy true, :message nil}))
      (is (= (:proxy-healthy-strarg health-checks)
             {:error nil, :healthy true, :message "aww yeah"}))
      (is (= (:proxy-unhealthy-formatarg health-checks)
             {:error nil, :healthy false, :message "oh no bummer 42"}))

      (is (= (:proxy-unhealthy-strarg health-checks)
             {:error nil, :healthy false, :message "darn"}))
      (is (= (:proxy-unhealthy-formatarg health-checks)
             {:error nil, :healthy false, :message "oh no bummer 42"}))
      (is (= (dissoc (:proxy-unhealthy-exceptionarg health-checks) :error)
             {:healthy false, :message "weak"}))
      (is (= (.getMessage (get-in health-checks [:proxy-unhealthy-exceptionarg :error]))
             (get-in health-checks [:proxy-unhealthy-exceptionarg :message])))
      (is (= (dissoc (:proxy-unhealthy-exceptionthrown health-checks) :error)
             {:healthy false, :message "doof"}))
      (is (= (.getMessage (get-in health-checks [:proxy-unhealthy-exceptionthrown :error]))
             (get-in health-checks [:proxy-unhealthy-exceptionthrown :message])))

      (is (= (:fn-healthy-noarg health-checks)
             {:error nil, :healthy true, :message nil}))
      (is (= (:fn-healthy-strarg health-checks)
             {:error nil, :healthy true, :message "check it"}))
      (is (= (:fn-healthy-formatarg health-checks) 
             {:error nil, :healthy true, :message "get cool 42"}))

      (is (= (:fn-unhealthy-strarg health-checks)
             {:error nil, :healthy false, :message "not so great"}))
      (is (= (:fn-unhealthy-formatarg health-checks)
             {:error nil, :healthy false, :message "very bad 42"}))
      (is (= (dissoc (:fn-unhealthy-exceptionarg health-checks) :error)
             {:healthy false, :message "dude this sucks"}))
      (is (= (.getMessage (get-in health-checks [:fn-unhealthy-exceptionarg :error]))
             (get-in health-checks [:fn-unhealthy-exceptionarg :message])))
      (is (= (dissoc (:fn-unhealthy-exceptionthrown health-checks) :error)
             {:healthy false, :message "i know right?"}))
      (is (= (.getMessage (get-in health-checks [:fn-unhealthy-exceptionthrown :error]))
             (get-in health-checks [:fn-unhealthy-exceptionthrown :message]))))))

  
