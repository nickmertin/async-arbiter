(ns ca.nickmertin.async-arbiter-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go timeout <! <!!]]
            [ca.nickmertin.async-arbiter :refer :all]
            [ca.nickmertin.async-arbiter.protocols :as proto]))

(deftest simple-arbiter-test
  (let [a (simple-arbiter)]
    (testing "Standalone actions."
      (is (with-lock!! a [] true) "Locking no keys.")
      (is (with-lock!! a [:a] true) "Locking single key.")
      (is (with-lock!! a [:a :b] true) "Locking multiple keys."))
    (testing "Concurrent actions."
      (-> (go
            (let [token-a (<! (proto/lock! a [:a]))
                  token-b (<! (proto/lock! a [:b]))]
              (proto/unlock! a token-a)
              (proto/unlock! a token-b)
              true))
          <!!
          (is "Non-conflicting concurrent actions."))
      (-> (go
            (let [x (atom false)]
              (<! (with-lock! a [:a]
                    (let [ret (go (with-lock! a [:a] @x))]
                      (<! (timeout 100))
                      (reset! x true)
                      ret)))))
          <!!
          (is "Forced conflict using with-lock!, atom and go."))
      (-> (go
            (let [x (atom false)]
              (<! (with-lock! a [:a :b]
                    (let [ret (go (with-lock! a [:a :c] @x))]
                      (<! (timeout 100))
                      (reset! x true)
                      ret)))))
          <!!
          (is "Non-total conflict using with-lock!, atom and go.")))))
