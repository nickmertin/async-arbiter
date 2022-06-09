(ns ca.nickmertin.async-arbiter.protocols
  "Defines the protocol implemented by arbiters.")

(defprotocol Arbiter
  "An async-enabled key-based mutual exclusion facility."
  (lock! [this keys] "Locks the given keys, returning a channel which produces a lock handle when the locking is complete. unlock! must be called with the handle to unlock the keys.")
  (unlock! [this handle] "Unlocks the keys that were locked with lock!.")
  (stop! [this] "Shuts down the arbiter."))

