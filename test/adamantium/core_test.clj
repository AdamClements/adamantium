(ns adamantium.core-test
  (:use clojure.test
        adamantium.core
        midje.sweet))

; Crud. They totally do affect one another, and I don't know how to do anything about that.
; Suggestions welcome.
#_(fact "Test cases have no affect on one another"
      (do 
        (defn testfn [] 2)
        (with-redefs [testfn nil]
          (defresilient testfn 3 [] 4))
        (testfn)) => 2)

(fact "Calling a function which has been defined once in a way that will fail, returns the default
       and printing something (hopefully useful) to the console)"
      (def boom (Exception. "BOOM"))
      (do 
        (defresilient asplosion 42 [] (throw boom))
        (asplosion)) => 42
      (provided (println anything) => nil))

(fact "Calling a resilient function does in fact give us the latest version of that function"
      (do
        (defresilient stuff "default" [] "one")
        (defresilient stuff "default" [] "two")
        (stuff)) => "two")

(fact "Arguments are maintained in resilient functions"
      (do
        (defresilient resilient-div nil [n d] (/ n d))
        (resilient-div 8 2)) => 4
      (resilient-div 8 0) => nil)