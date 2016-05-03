(defproject all-async ""

  :description "all-async"
  :url "https://github.com/mccraigmccraig/all-async"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.reader "0.9.2"]
                 [jarohen/embed-nrepl "0.1.7"]

                 [jarohen/bounce "0.0.1-alpha1"]

                 [jarohen/bounce.aleph "0.0.1-alpha1"]
                 [aleph "0.4.1"]
                 [bidi "2.0.9"]
                 [yada "1.1.11" :exclusions [bidi]]
                 [org.clojure/core.async "0.2.374"]
                 [funcool/cats "1.2.1"]

                 [ring/ring-core "1.3.2"]
                 [hiccup "1.0.5"]
                 [garden "1.2.1"]
                 [ring-middleware-format "0.5.0" :exclusions [ring]]

                 [org.webjars/jquery "2.1.4"]
                 [org.webjars/bootstrap "3.3.5"]

                 [jarohen/bounce.figwheel "0.0.1-alpha1"]

                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.9"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.1"]
                 [org.apache.logging.log4j/log4j-core "2.1"]]

  :exclusions [org.clojure/clojure
               org.clojure/clojurescript]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.7.228"]
                                  [jarohen/bounce.mux "0.0.1-alpha2"]
                                  [lein-figwheel "0.5.0-2"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [reagent "0.5.1"]]}}

  :auto-clean false

  :filespecs [{:type :path, :path "target/cljs/build/mains"}]

  :aliases {"dev" ["run" "-m" "all-async.service.main"]
            "build" ["do"
                     "clean"
                     ["run" "-m" "all-async.service.main/build!"]
                     "uberjar"]})
