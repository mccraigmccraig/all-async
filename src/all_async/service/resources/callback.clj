(ns all-async.service.resources.callback
  (:require
   [cheshire.core :refer [generate-string parse-string]]
   [aleph.http :as http]
   [manifold.deferred :as d]
   [yada.yada :refer [yada resource]]))

(defn callback-handler
  [handler]
  (fn [ctx]
    (let [r (d/deferred)
          on-success (fn [v] (d/success! r (generate-string v)))
          on-error (fn [e] (d/error! r e))]
      (handler on-success on-error)
      r)))

(defn http-get-with-callbacks
  [url on-success on-error]
  (let [r (http/get url)]
    (d/on-realized r
                   #(on-success (-> % :body slurp parse-string))
                   on-error)
    nil))

;; ;;;;;;;;;;;;;;;;;;;;

(defn reliable-upstream-handler
  [on-success on-error]
  (http-get-with-callbacks
   "http://localhost:3000/api/reliable-random-number"
   (fn [v]
     (on-success [:ok v]))
   (fn [e]
     (on-success [:fail (.getMessage e)]))))

(defn callback-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (callback-handler reliable-upstream-handler)}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn unreliable-upstream-handler
  [on-success on-error]
  (http-get-with-callbacks
   "http://localhost:3000/api/unreliable-random-number"
   (fn [v]
     (http-get-with-callbacks
      "http://localhost:3000/api/unreliable-random-letter"
      (fn [v2] (on-success [:ok v v2]))
      (fn [e] (on-success [:fail (.getMessage e)]))))
   (fn [e] (on-success [:fail (.getMessage e)]))))

(defn callback-unreliable-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (callback-handler unreliable-upstream-handler)}}})))
