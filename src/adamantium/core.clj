(ns adamantium.core
  (:use [clojure.stacktrace]))

(defmacro defresilient
  "A wrapper around a function with a resilient binding which
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

(defmacro defnr
  "Either defines or redefines a function. When redefining, if the new
   function fails, it will roll back to a previous incarnation"
  [name & definition]
  `(cond (:rollbacks (meta (resolve '~name)))
         (swap! (:rollbacks (meta (resolve '~name))) conj (fn ~@definition))
        
         (resolve '~name)
         (if-let [orig-fn# name]
           (defn ~name 
             {:rollbacks (atom [orig-fn# (fn ~@definition)])}
             [& args#]
             (let [rollbacks# (:rollbacks (meta (resolve '~name)))
                   latest-fn# (last @rollbacks#)]
               (try (apply latest-fn# args#)
                 (catch Exception exp#
                   (do
                     (print-stack-trace exp#)
                     (swap! rollbacks# pop)
                     (apply ~name args#)))))))
  
         :else
         (def ~name (fn ~@definition))))