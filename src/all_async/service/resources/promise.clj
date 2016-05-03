(ns all-async.service.resources.promise
  (:require
   [cheshire.core :refer [generate-string parse-string]]
   [aleph.http :as http]
   [manifold.deferred :as d]
   [yada.yada :refer [yada resource]]))

(defn http-get-promise
  [url]
  (let [dr (http/get url)]
    (d/chain dr
             (fn [v]
               (-> v :body slurp parse-string)))))

(defn encode-error-handler
  [handler]
  (fn [ctx]
    (let [dv (handler ctx)]
      (-> dv
          (d/chain (fn [v] [:ok v]))
          (d/catch Exception (fn [e] [:fail (.getMessage e)]))))))

;; ;;;;;;;;;;;;;;;;;;;;;;;

(defn promise-handler
  [ctx]
  (let [r1 (http-get-promise "http://localhost:3000/api/reliable-random-number")
        r2 (http-get-promise "http://localhost:3000/api/reliable-random-letter")
        comb (d/zip r1 r2)]
    (d/chain comb
             (fn [[v1 v2]]
               [v1 v2]))))

(defn promise-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response promise-handler}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;

(defn promise-unreliable-handler
  [ctx]
  (let [r1 (http-get-promise "http://localhost:3000/api/unreliable-random-number")
        r2 (http-get-promise "http://localhost:3000/api/unreliable-random-letter")
        comb (d/zip r1 r2)]
    (d/chain comb
             (fn [[v1 v2]]
               [v1 v2]))))

(defn promise-unreliable-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (encode-error-handler promise-unreliable-handler)}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn promise-unreliable-flow-handler
  [ctx]
  (d/let-flow [v1 (http-get-promise "http://localhost:3000/api/unreliable-random-number")
               v2 (http-get-promise "http://localhost:3000/api/unreliable-random-letter")]
    [v1 v2]))

(defn promise-unreliable-flow-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (encode-error-handler promise-unreliable-flow-handler)}}})))
