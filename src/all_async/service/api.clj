(ns all-async.service.api
  (:require
   [manifold.deferred :as deferred]
   [yada.yada :refer [yada resource]]
   [all-async.service.resources
    [random :as random]
    [callback :as callback]
    [core-async :as core-async]
    [promise :as promise]
    [monad :as monad]]))

(defn api-routes
  []
  ["/api/" [["reliable-random-number" :reliable-random-number]
            ["unreliable-random-number" :unreliable-random-number]
            ["slow-random-number" :slow-random-number]

            ["reliable-random-letter" :reliable-random-letter]
            ["unreliable-random-letter" :unreliable-random-letter]

            ["callback" :callback]
            ["callback-unreliable" :callback-unreliable]

            ["core-async" :core-async]
            ["core-async-unreliable" :core-async-unreliable]

            ["promise" :promise]
            ["promise-unreliable" :promise-unreliable]
            ["promise-unreliable-flow" :promise-unreliable-flow]

            ["monad" :monad]
            ["monad-unreliable" :monad-unreliable]
            ["monad-unreliable-log" :monad-unreliable-log]
            ["monad-slow" :monad-slow]
            ["applicative-slow" :applicative-slow]]])

(defn api-handlers
  []
  {:reliable-random-number (random/reliable-random-number-resource)
   :unreliable-random-number (random/unreliable-random-number-resource)
   :slow-random-number (random/slow-random-number-resource)

   :reliable-random-letter (random/reliable-random-letter-resource)
   :unreliable-random-letter (random/unreliable-random-letter-resource)

   :callback (callback/callback-resource)
   :callback-unreliable (callback/callback-unreliable-resource)

   :core-async (core-async/core-async-resource)
   :core-async-unreliable (core-async/core-async-unreliable-resource)

   :promise (promise/promise-resource)
   :promise-unreliable (promise/promise-unreliable-resource)
   :promise-unreliable-flow (promise/promise-unreliable-flow-resource)

   :monad (monad/monad-resource)
   :monad-unreliable (monad/monad-unreliable-resource)
   :monad-unreliable-log (monad/monad-unreliable-log-resource)
   :monad-slow (monad/monad-slow-resource)
   :applicative-slow (monad/applicative-slow-resource)})
