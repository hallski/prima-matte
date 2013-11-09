(ns prima-matte.matrix
  (:use [clojure.string :only [join split]]
        [domina.xpath :only [xpath]])
  (:require-macros [hiccups.core :as h])
  (:require [domina :as dom]
            [domina.events :as event]
            [hiccups.runtime :as hiccupsrt]))

(def current-operator (atom +))

(def operators {"+" +
                "-" -
                "*" *
                "/" /})

(defn produce-row [func n others]
  (map #(func n %) others))

(defn numbers-from-input-field [input-field]
  (map js/parseInt (split (dom/value input-field) #"\s+")))

(defn make-row [value]
  (h/html [:tr.number_row [:th value]
       (map #(h/html [:td %])
            (produce-row @current-operator value (numbers-from-input-field (dom/by-id "top_values"))))]))

(defn update-input []
  (let [tbody (dom/by-id "results_table")]
    (dom/destroy! (dom/children tbody))
    (dom/append! tbody
                 (h/html 
                   [:tr [:td.hidden] (join (map #(h/html [:th %])
                                           (numbers-from-input-field (dom/by-id "top_values")))) 
                          ]))
    (dom/append! tbody
                 (join (map make-row (numbers-from-input-field (dom/by-id "left_values")))))))

(defn update-operator [evt]
  (reset! current-operator (operators (dom/text (:target evt))))
  (update-input))

(defn ^:export init [] 
  (event/listen! (dom/by-id "top_values") :keyup update-input)
  (event/listen! (dom/by-id "left_values") :keyup update-input)
  (doseq [button-id ["plus_btn" "minus_btn" "mult_btn" "div_btn"]]
    (event/listen! (dom/by-id button-id) :click update-operator)))

;; Todo
;; - Refactor update-input
;; - Refactor make-roww
;; - Support operator
