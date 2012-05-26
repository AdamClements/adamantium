(ns adamantium.core)

#_(defn temper 
  "Takes a function and returns a resilient version of it"
  [name default fn args]
  (try (apply fn args)
    (catch Exception e 
      (.printStackTrace e) 
      (def '~name (fn args default))
      default)))

(defmacro defresilient
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
             (defn ~name [& args#] ~default)
             ~default))))))
