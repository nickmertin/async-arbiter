# async-arbiter

A Clojure(Script) library that provides an async-enabled key-based mutual
exclusion facility. Useful for ensuring atomic access to resources which don't
have atomicity features with minimal impact on concurrent access. For example,
do operations on a file system by using the path of the directory (or
directories) that you need to have atomic access to as key(s).

## Usage

```clojure
(require '[ca.nickmertin/async-arbiter :refer [simple-arbiter with-lock!]]
         '[clojure.core.async :refer [go]])

(def a (simple-arbiter))

(go
  ; Lock any number of keys
  (with-lock! a [:key1 :key2]
    ; Access some resource here.
    (println "locked!"))

  ; Use any collection expression to produce the list of keys
  (with-lock! a (map keyword #{"key1" "key2"})
    (println "locked again!")))
```

## License

Copyright © 2022 Nick Mertin

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
