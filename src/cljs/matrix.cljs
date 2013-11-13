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

(defn numbers-from-input-field [input-field-id]
  (filter #(not (js/isNaN %)) (map js/parseInt (split (dom/value (dom/by-id input-field-id)) #"\s+"))))

(defn make-row [value]
  (h/html [:tr.number_row [:th value]
          (map #(h/html [:td %])
               (calculate-row @current-operator value (numbers-from-input-field "top_values")))]))

(defn update-table []
  (let [tbody (dom/by-id "results_table")
        col-values (numbers-from-input-field "top_values")
        row-values (numbers-from-input-field "left_values")]
    (dom/destroy! (dom/children tbody))
    (when (and (> (count col-values) 0)
             (> (count row-values) 0))
      (dom/append! tbody
                   (h/html
                   [:tr [:td.hidden] (join (map #(h/html [:th %])
                                           (numbers-from-input-field "top_values")))]))
      (dom/append! tbody
                   (join (map make-row (numbers-from-input-field "left_values")))))))

(defn set-operator! [operator]
  (reset! current-operator (:func operator))
  (dom/remove-class! (dom/by-class "operator_button") "selected")
  (dom/add-class! (dom/by-id (:css-id operator)) "selected")
  (update-table))

(defn update-operator [evt]
  (set-operator! (operators (dom/text (:target evt)))))

(defn ^:export init [] 
  (event/listen! (dom/by-id "top_values") :keyup update-table)
  (event/listen! (dom/by-id "left_values") :keyup update-table)
  (doseq [button-id ["plus_btn" "minus_btn" "mult_btn" "div_btn"]]
    (event/listen! (dom/by-id button-id) :click update-operator))
  (set-operator! (operators "+")))

;; TODO:
;; - Add testing
