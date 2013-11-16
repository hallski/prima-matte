(ns prima-matte.matrix
  (:use [clojure.string :only [join split]]
        [domina.xpath :only [xpath]])
  (:require-macros [hiccups.core :as h])
  (:require [domina :as dom]
            [domina.events :as event]
            [hiccups.runtime :as hiccupsrt]))

(def current-operator (atom +))

(def operators {"+" {:func + :css-id "plus_btn"}
                "-" {:func - :css-id "minus_btn"}
                "*" {:func * :css-id "mult_btn"}
                "/" {:func / :css-id "div_btn"}})

(defn calculate-row [func n others]
  (map #(func n %) others))

(defn numbers-from-string [str]
  (filter #(not (js/isNaN %)) (map js/parseInt (split str #"\s+"))))

(defn get-value [id]
  "Gets the value from a dom node with id"
  (dom/value (dom/by-id id)))

(defn make-row [top-values value]
  (h/html [:tr.number_row [:th value]
          (map #(h/html [:td %])
               (calculate-row @current-operator value top-values))]))

(defn update-table! []
  (let [tbody (dom/by-id "results_table")
        col-values (numbers-from-string (get-value "top_values"))
        row-values (numbers-from-string (get-value "left_values"))]
    (dom/destroy! (dom/children tbody))
    (when (and (> (count col-values) 0)
             (> (count row-values) 0))
      (dom/append! tbody
                   (h/html
                   [:tr [:td.hidden] (join (map #(h/html [:th %]) col-values))]))
      (dom/append! tbody
                   (join (map (partial make-row col-values) row-values))))))

(defn set-operator! [operator]
  (reset! current-operator (:func operator))
  (dom/remove-class! (dom/by-class "operator_button") "selected")
  (dom/add-class! (dom/by-id (:css-id operator)) "selected")
  (update-table!))

(defn update-operator! [evt]
  (set-operator! (operators (dom/text (:target evt)))))

(defn ^:export init [] 
  (event/listen! (dom/by-id "top_values") :keyup update-table!)
  (event/listen! (dom/by-id "left_values") :keyup update-table!)
  (doseq [button-id ["plus_btn" "minus_btn" "mult_btn" "div_btn"]]
    (event/listen! (dom/by-id button-id) :click update-operator!))
  (set-operator! (operators "+")))
