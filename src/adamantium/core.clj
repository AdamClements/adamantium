(ns adamantium.core
  (:use [clojure.stacktrace]))

(defmacro defresilient
  "Use in place of defn; replaces the function with a resilient binding which
   catches exceptions, prints them to the console and instead of throwing, 
   rolls back to a previous incarnation"
  [name default & definition]
  `(let [f# (fn ~@definition)]
     (if-let [existing-rollbacks# (:rollbacks (meta (resolve '~name)))]
       (swap! existing-rollbacks# conj f#)        ; This is already a resilient function, just add to the list of fns
       (do                                                      ; Otherwise we have to create the wrapper
         (defn ~name {:rollbacks (atom [(fn [& whatever#] ~default) f#])}
           [& args#]
           (let [rollback-data# (:rollbacks (meta (resolve '~name)))
                 latest-fn#     (last @rollback-data#)]
             (try (apply latest-fn# args#)
               (catch Exception exp#
                 (do 
                   (print-stack-trace exp#)
                   (swap! rollback-data# pop)
                   (apply ~name args#))))))))))

#_(defn example [a b] (/ a b))

#_(defn example-resilient {:rollbacks (atom [(fn [&whatever] 0)
                                           (fn [a b] (/ a b)) 
                                           (fn [a b] (/ a (+ 1 b))) 
                                           (fn [a b] (/ a (+ 2 b))) ])} 
  [& args]
  (let [rollback-data (:rollbacks (meta (resolve 'example-resilient)))
        latest-fn     (last @rollback-data)]
    (try (apply latest-fn args)
      (catch Exception exp
        (do 
          (.printStackTrace exp)
          (swap! rollback-data pop)
          (apply example-resilient args))))))