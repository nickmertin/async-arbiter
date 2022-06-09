(ns ca.nickmertin.async-arbiter
  "Provides an async-enabled key-based mutual exclusion facility."
  (:require [clojure.core.async :refer [<! #?(:clj <!!)]]
            [ca.nickmertin.async-arbiter.internal :as internal]
            [ca.nickmertin.async-arbiter.protocols :as proto]))

(defn simple-arbiter
  "Creates a simple arbiter which handles multiple keys by locking in sequential order according to a comparator (by default, clojure.core/compare)."
  ([] (simple-arbiter compare))
  ([comparator]
   (internal/make-simple-arbiter comparator)))

(defmacro with-lock!
  "Locks the given keys, executes the body, and ensures that the keys are unlocked after. Must be called inside a (go ...) block. Parks while waiting for the keys to be locked."
  [a keys & body]
  `(let [a# ~a
         handle# (<! (proto/lock! a# ~keys))]
     (try
       ~(list* `do body)
       (finally
         (proto/unlock! a# handle#)))))

#?(:clj (defmacro with-lock!!
          "Locks the given keys, executes the body, and ensures that the keys are unlocked after. Not intended for use in (go ...) blocks. Blocks while waiting for the keys to be locked."
          [a keys & body]
          `(let [a# ~a
                 handle# (<!! (proto/lock! a# ~keys))]
             (try
               ~(list* `do body)
               (finally
                 (proto/unlock! a# handle#))))))

(defn stop!
  "Shuts down an arbiter."
  [a]
  (proto/stop! a))
