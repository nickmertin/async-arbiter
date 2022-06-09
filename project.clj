(defproject ca.nickmertin/async-arbiter "0.1.1-RC2"
  :description "A Clojure(Script) library that provides an async-enabled key-based mutual exclusion facility."
  :url "https://github.com/nickmertin/async-arbiter"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories {"releases" {:url "https://repo.clojars.org" :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.5.648"]]
  :repl-options {:init-ns ca.nickmertin.async-arbiter})
