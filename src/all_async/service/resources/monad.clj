(ns all-async.service.resources.monad
  (:require
   [clojure.tools.logging :refer [debug info]]
   [cheshire.core :as cheshire :refer [parse-string]]
   [clj-time.core :as t]
   [cats
    [core :refer [mlet alet return lift]]
    [context :refer [with-context]]]
   [cats.labs
    [manifold :refer [deferred-context]]
    [writer :as writer]]

   [aleph.http :as http]
   [manifold.deferred :as d]
   [yada.yada :refer [yada resource]]))

(defn generate-string
  [obj]
  (cheshire/generate-string obj {:pretty true}))

(defn http-get-promise-monad
  [url]
  (with-context deferred-context
    (mlet [r (http/get url)
           :let [v (-> r :body slurp parse-string)]]
      (return v))))

(defn encode-error-handler
  [handler]
  (fn [ctx]
    (let [dv (handler ctx)]
      (-> dv
          (d/chain (fn [v] [:ok v]))
          (d/catch Exception (fn [e] [:fail (.getMessage e)]))))))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;

(defn monad-handler
  [ctx]
  (with-context deferred-context
    (mlet [r1 (http-get-promise-monad
               "http://localhost:3000/api/reliable-random-number")
           r2 (http-get-promise-monad
               "http://localhost:3000/api/reliable-random-letter")]
      (return [r1 r2]))))

(defn monad-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response monad-handler}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;

(defn monad-unreliable-handler
  [ctx]
  (with-context deferred-context
    (mlet [r1 (http-get-promise-monad
               "http://localhost:3000/api/unreliable-random-number")
           r2 (http-get-promise-monad
               "http://localhost:3000/api/unreliable-random-letter")]
      (return [r1 r2]))))

(defn monad-unreliable-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (encode-error-handler
                                monad-unreliable-handler)}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;

(def deferred-writer-context (writer/writer-t deferred-context))

(defn http-get-log-promise
  [url]
  (with-context deferred-writer-context
    (mlet [:let [st (t/now)]

           r (lift (http/get url))

           :let [et (t/now)
                 d (t/in-millis (t/interval st et))

                 v (-> r :body slurp parse-string)]

           _ (writer/tell {:url url :duration d})]
      (return v))))

(defn encode-error-log-handler
  [handler]
  (fn [ctx]
    (let [dv (handler ctx)]
      (-> dv
          (d/chain (fn [[v log]]
                     (let [d (->> log
                                  (map :duration)
                                  (filter identity)
                                  (reduce +))]
                       (generate-string
                        [:ok v d log]))))
          (d/catch Exception
              (fn [e] [:fail (.getMessage e)]))))))

(defn monad-unreliable-log-handler
  [ctx]
  (with-context deferred-writer-context
    (mlet [r1 (http-get-log-promise
               "http://localhost:3000/api/unreliable-random-number")
           r2 (http-get-log-promise
               "http://localhost:3000/api/unreliable-random-letter")]
      (return [r1 r2]))))

(defn monad-unreliable-log-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (encode-error-log-handler
                                monad-unreliable-log-handler)}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn timer-handler
  [handler]
  (fn [ctx]
    (let [st (t/now)
          r (handler ctx)]
      (-> r
          (d/chain (fn [v]
                     (let [et (t/now)
                           d (t/in-millis (t/interval st et))]
                       [:ok v [d :millis]])))))))

(defn monad-slow-handler
  [ctx]
  (with-context deferred-context
    (mlet [r1 (http-get-promise-monad
               "http://localhost:3000/api/slow-random-number")
           r2 (http-get-promise-monad
               "http://localhost:3000/api/slow-random-number")]
      (return [r1 r2]))))

(defn monad-slow-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (timer-handler
                                monad-slow-handler)}}})))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn applicative-slow-handler
  [ctx]
  (with-context deferred-context
    (alet [r1 (http-get-promise-monad
               "http://localhost:3000/api/slow-random-number")
           r2 (http-get-promise-monad
               "http://localhost:3000/api/slow-random-number")]
      [r1 r2])))

(defn applicative-slow-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (timer-handler
                                applicative-slow-handler)}}})))
