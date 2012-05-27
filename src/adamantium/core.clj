(ns adamantium.core)

;(defonce resilient-fn-store (atom {}))

#_(defn add-alternative-fn [name func]
  (swap! resilient-fn-store assoc name (conj (name @resilient-fn-store) func)))

#_(defn pop-alternative-fn [name default]
  (let [fn-list (name @resilient-fn-store)
        new-list (rest fn-list)]
    (swap! resilient-fn-store assoc name new-list)
    (peek (name @resilient-fn-store))))

(defmacro defresilient
  "Use in place of defn; replaces the function with a resilient binding which
   catches exceptions, prints them to the console and instead of throwing, 
   rolls back to a previous incarnation"
  [name & definition]
  `(do
     (let [f# (fn ~@definition)]
       (if-let [existing-rollbacks# (:rollbacks (meta (resolve '~name)))]
         (swap! existing-rollbacks# conj f#)        ; This is already a resilient function, just add to the list of fns
         (do                                                      ; Otherwise we have to create the wrapper
           (defn ~name {:rollbacks (atom [(fn [& whatever#] nil) f#])}
             [& args#]
             (let [rollback-data# (:rollbacks (meta (resolve '~name)))
                   latest-fn#     (last @rollback-data#)]
               (try (apply latest-fn# args#)
                 (catch Exception exp#
                   (do 
                     (.printStackTrace exp#)
                     (swap! rollback-data# pop)
                     (apply ~name args#)))))))))))

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

#_(defmacro defresilient
  "Use in place of defn; replaces the function with a resilient binding which
   catches exceptions, prints them to the console and instead of throwing, 
   rebinds the function to one which returns a safe default"
  [name default & definition]
  `(do
     (def ~name)
     (let [f# (fn ~@definition)]
       (defn ~name [& args#]
         (try
           (apply f# args#)
           (catch Exception e#
             (println (.getStackTrace e#)) ; This uses println instead of .printStackTrace so we can test it
                                           ; easily with midje (doesn't support mocking java calls yet)
             (defn ~name [& args#] (pop-alternative-fn '~name ~default))
             (~name args#))))
       (add-alternative-fn '~name ~name)
       )))
