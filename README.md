# AdamAntium

(TODO add a picture of Adam Ant)

People make mistakes. Especially when learning. Interactive programming is fantastic for learning, instant feedback is good! Killing your lovely interactive app because you forgot the arity of the color function is bad.

The idea behind adamantium is to give you a second chance. To be kind, suggest that perhaps you want to try again, without exploding in your face.

Currently the defresilient macro works like defn except it takes an additional argument before the argument bindings. If your function throws an exception, we'll redefine the function as something which just returns the default, print the stacktrace, and you can have another attempt.

## Usage

Import adamantium.core and then use the defresilient macro to define functions that you wish to be resilient

(defresilient division 0 [a b] (/ a b))

(division 8 4) => 2
(division 8 0) => 0  ; And prints an appropriate stacktrace
(division 8 4) => 0  ; This function is now redefined, so your console doesn't fill with stacktraces, obscuring what actually went wrong

## License

Copyright © 2012

Distributed under the Eclipse Public License
