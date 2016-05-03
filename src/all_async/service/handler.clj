(ns all-async.service.handler
  (:require [all-async.service.css :as css]
            [bidi.bidi :as bidi]
            [bidi.ring :as br]
            [bounce.core :as bc]
            [bounce.figwheel :as cljs]
            [hiccup.page :refer [html5 include-css include-js]]
            [ring.util.response :refer [response content-type]]
            [all-async.service.api :as api]))

(def site-routes
  ["" {"/" {:get :page-handler}
       "/css" {"/site.css" {:get :site-css}}
       "/webjars" (br/resources {:prefix "META-INF/resources/webjars"})}])

(defn page-handler [req]
  (-> (response
       (html5
        [:head
         [:title "all-async - CLJS Single Page Web Application"]

         (include-js "/webjars/jquery/2.1.4/jquery.min.js")
         (include-js "/webjars/bootstrap/3.3.5/js/bootstrap.min.js")
         (include-css "/webjars/bootstrap/3.3.5/css/bootstrap.min.css")

         (include-js (cljs/path-for-js (bc/ask :cljs-compiler)))
         (include-css (bidi/path-for site-routes :site-css :request-method :get))]

        [:body]))

      (content-type "text/html")))

(defn site-handlers []
  {:page-handler page-handler
   :site-css (fn [req]
               (-> (response (css/site-css))
                   (content-type "text/css")))})

(defn make-handler []

  (br/make-handler ["" [(api/api-routes)
                        site-routes
                        (cljs/bidi-routes (bc/ask :cljs-compiler))]]

                   (some-fn
                    (site-handlers)
                    (api/api-handlers)

                    #(when (fn? %) %)

                    (constantly {:status 404
                                 :body "Not found."}))))
