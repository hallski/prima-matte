(defproject prima-matte "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1847"]
                 [domina "1.0.3-SNAPSHOT"] 
                 [hiccups "0.2.0"]]
  :plugins [[lein-cljsbuild "0.3.4"]]

  :cljsbuild {:builds [{:source-paths ["src/cljs"] 
                        :compiler {:output-to "resources/prima_matte.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
