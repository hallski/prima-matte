(ns prima-matte.test.matrix
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [prima-matte.matrix :as m]))

(deftest can-calculate-something
  (is (= [3 5 7]
         (m/calculate-row + 2 [1 3 5]))))
