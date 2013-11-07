(ns prima-matte.matrix
  (:use [clojure.string :only [join split]]
        [domina.xpath :only [xpath]])
  (:require [domina :as dom]
            [domina.events :as event]))

(defn produce-row [func n others]
  (map #(func n %) others))

(defn numbers-from-input-field [input-field]
  (split (dom/value input-field) #"\s+"))

(defn make-html-tag [tag value]
  (str "<" tag ">" value "</" tag ">"))

(defn make-row [value]
  (str "<tr class='number_row'><th>" value "</th>"
       (map (partial make-html-tag "td") 
            (produce-row - value (numbers-from-input-field (dom/by-id "top_values"))))
       "</tr>"))

(defn update-input []
  (let [tbody (dom/by-id "results_table")]
    (dom/destroy! (dom/children tbody))
    (dom/append! tbody
                 (str "<tr><td class='hidden'></td>" (join (map (partial make-html-tag "th") (numbers-from-input-field (dom/by-id "top_values")))) "</tr>"))
    (dom/append! tbody
                 (join (map make-row (numbers-from-input-field (dom/by-id "left_values")))))))

(defn ^:export init [] 
  (event/listen! (dom/by-id "top_values") :keyup update-input)
  (event/listen! (dom/by-id "left_values") :keyup update-input))

