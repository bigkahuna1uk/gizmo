(ns clojurewerkz.gizmo.service-test
  (:require [net.cgrand.enlive-html :as html])
  (:use clojure.test
        clojurewerkz.gizmo.service))

(deftest defservice-start-test
  (let [res (atom nil)]
    (defservice defservice-test-1
      :config {:config :config}
      :start (fn [cfg] (reset! res cfg)))
    (start! defservice-test-1)
    (Thread/sleep 10)
    (is (= {:config :config} @res))))

(deftest defservice-is-alive-with-exception
  (let [res (atom nil)]
    (defservice defservice-test-2
      :config nil
      :start (fn [cfg] (throw (Exception. "couldn't start"))))
    (start! defservice-test-2 {:config :config})
    (Thread/sleep 10)
    (is (not (alive? defservice-test-2)))
    (is (= nil @res))))

(deftest defservice-is-alive-with-long-running
  (let [res (atom nil)]
    (defservice defservice-test-2
      :config nil
      :start (fn [cfg] (Thread/sleep 100)))
    (start! defservice-test-2 {:config :config})
    (Thread/sleep 10)
    (is (alive? defservice-test-2))
    (is (= nil @res))))

(deftest defservice-start-config-override-test
  (let [res (atom nil)]
    (defservice defservice-test-3
      :config nil
      :start (fn [cfg] (reset! res cfg)))
    (start! defservice-test-3 {:config :config})
    (Thread/sleep 10)
    (is (= {:config :config} @res))))

(deftest defservice-stop-test
  (let [res (atom nil)
        ext-alive (atom true)]
    (defservice defservice-test-4
      :config {:config :config}
      :start (fn [cfg] (loop []
                        (when @ext-alive
                          (recur))))
      :stop (fn [] (reset! ext-alive false)))
    (start! defservice-test-4)
    (Thread/sleep 10)
    (is (alive? defservice-test-4))
    (stop! defservice-test-4)
    (Thread/sleep 10)
    (is (not (alive? defservice-test-4)))))

(deftest all-services-test
  (defservice all-services-test-1)
  (is (= all-services-test-1 (:all-services-test-1 (all-services)))))