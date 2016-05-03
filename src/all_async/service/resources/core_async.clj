(ns all-async.service.resources.core-async
  (:require
   [clojure.tools.logging :refer [debug info]]
   [cheshire.core :refer [generate-string parse-string]]
   [aleph.http :as http]
   [manifold.deferred :as d]
   [clojure.core.async :as a :refer [chan put! <! go]]
   [yada.yada :refer [yada resource]]))

(defn async-handler
  [handler]
  (fn [ctx]
    (let [d (d/deferred)]
      (go
        (let [v (<! (handler ctx))]
          (if-not (instance?  Throwable v)
            (d/success! d v)
            (d/error! v))))
      d)))

(defn http-get-with-core-async
  [url]
  (let [dr (http/get url)
        ch (chan)]
    (d/on-realized dr
                   (fn [r]
                     (let [v (-> r :body slurp parse-string)]
                       (put! ch v)))
                   (fn [e]
                     (put! ch e)))
    ch))

;; ;;;;;;;;;;;;;;;;;;;;

(defn reliable-upstream-handler
  [ctx]
  (go
    (let [rn (<! (http-get-with-core-async
                  "http://localhost:3000/api/reliable-random-number"))
          rl (<! (http-get-with-core-async
                  "http://localhost:3000/api/reliable-random-letter"))]
      [:ok [rn rl]])))

(defn core-async-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (async-handler reliable-upstream-handler)}}})))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn unreliable-upstream-handler
  [ctx]
  (go
    (let [rn (<! (http-get-with-core-async
                  "http://localhost:3000/api/unreliable-random-number"))
          rl (<! (http-get-with-core-async
                  "http://localhost:3000/api/unreliable-random-letter"))]
      (cond
        (not (or (instance? Exception rn)
                 (instance? Exception rl)))
        [:ok [rn rl]]

        :else
        [:fail (if (instance? Exception rn)
                 (.getMessage rn)
                 (.getMessage rl))]))))

(defn core-async-unreliable-resource
  []
  (yada
   (resource
    {:methods {:get {:produces #{"application/json"}
                     :response (async-handler unreliable-upstream-handler)}}})))
