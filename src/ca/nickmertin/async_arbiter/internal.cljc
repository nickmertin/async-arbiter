(ns ^:no-doc ca.nickmertin.async-arbiter.internal
  (:require [clojure.core.async :refer [chan promise-chan go-loop close! put! <! >!]]
            [ca.nickmertin.async-arbiter.protocols :refer [Arbiter]])
  (:import [clojure.lang PersistentQueue]))

(deftype SimpleArbiter
    [ch comparator]
  Arbiter
  (lock! [this keys]
    (if (empty? keys)
      (let [ready (promise-chan)]
        (put! ready {::keys []})
        ready)
      (let [step (chan)
            all-keys (seq (into (sorted-set-by comparator) keys))]
        (go-loop [keys all-keys]
          (if-let [key (first keys)]
            (do
              (>! ch [:lock [key step]])
              (<! step)
              (recur (next keys)))
            (do
              (close! step)
              {::keys all-keys}))))))
  (unlock! [this {keys ::keys}]
    (if-not (empty? keys)
      (put! ch [:unlock keys])))
  (stop! [this]
    (close! ch)))

(defn make-simple-arbiter [comparator]
  (let [ch (chan)]
    (go-loop [queues {}]
      (if-let [[action arg] (<! ch)]
        (-> (case action
              :lock
              (let [[key ready] arg]
                (update queues key
                        #(if (nil? %)
                           (do
                             (put! ready key)
                             PersistentQueue/EMPTY)
                           (conj % ready))))
              :unlock
              (doseq [key arg]
                (let [queue (get queues key)]
                  (if (empty? queue)
                    (dissoc queues key)
                    (do
                      (put! (peek queue) key)
                      (assoc queues key (pop queue)))))))
            recur)))
    (->SimpleArbiter ch comparator)))

