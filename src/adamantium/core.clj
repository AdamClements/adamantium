(ns adamantium.core
  (:use [clojure.stacktrace]))

(defmacro defresilient
  "Use in place of defn; replaces the function with a resilient binding which
   catches exceptions, prints them to the console and instead of throwing, 
   rolls back to a previous incarnation"
  [name default & definition]
  `(let [f# (fn ~@definition)]
     (if-let [existing-rollbacks# (:rollbacks (meta (resolve '~name)))]
       (swap! existing-rollbacks# conj f#)
       (defn ~name {:rollbacks (atom [(fn [& whatever#] ~default) f#])}
         [& args#]
         (let [rollback-data# (:rollbacks (meta (resolve '~name)))
               latest-fn#     (last @rollback-data#)]
           (try (apply latest-fn# args#)
             (catch Exception exp#
               (do 
                 (print-stack-trace exp#)
                 (swap! rollback-data# pop)
                 (apply ~name args#)))))))))
