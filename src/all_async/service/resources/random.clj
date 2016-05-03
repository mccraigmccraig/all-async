(ns all-async.service.resources.random
  (:require
   [clojure.tools.logging :refer [debug info]]
   [cheshire.core :refer [generate-string]]
   [manifold.deferred :as d]
   [manifold.time :as mt]
   [yada.yada :refer [yada resource]]))

(defn unreliable-handler
  [handler]
  (fn [ctx]
    (info "unreliable")
    (if (> (rand) 0.25)
      (handler ctx)
      (d/error-deferred
       (ex-info "i'm a teapot"
                {:status 418
                 :yada.core/http-response true})))))

(defn slow-handler
  [handler]
  (fn [ctx]
    (info "slow")
    (let [r (handler ctx)
          d (d/deferred)]
      (mt/in 500 (fn [] (d/success! d r)))
      d)))

(defn random-number-handler
  [ctx]
  (let [r (rand-int 100)]
    (info "random-number" r)
    (generate-string r)))

(defn reliable-random-number-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response random-number-handler}}})))

(defn unreliable-random-number-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (unreliable-handler random-number-handler)}}})))

(defn slow-random-number-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (slow-handler random-number-handler)}}})))

(defn random-letter-handler
  [ctx]
  (let [r (str (char (+ (int \A)
                        (rand-int 26))))]
    (info "random-letter" r)
    (generate-string r)))

(defn reliable-random-letter-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response random-letter-handler}}})))

(defn unreliable-random-letter-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (unreliable-handler random-letter-handler)}}})))
