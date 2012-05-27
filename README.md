# AdamAntium

(TODO add a picture of Adam Ant)

People make mistakes. Especially when learning. Interactive programming is fantastic for learning, instant feedback is good! Killing your lovely interactive app because you forgot the arity of the color function is bad.

The idea behind adamantium is to give you a second chance. Adamantium is kind, suggests that perhaps you want to try again, without exploding in your face.

Currently the defresilient macro works like defn except it takes an additional argument before the argument bindings. If your function throws an exception, we'll see if any of the older versions of the function work or failing that just return the default, print the stacktrace, and you can have another attempt.

## Usage

Import adamantium.core and then use the defresilient macro to define functions that you wish to be resilient. Note that this is a ridiculous example. A better idea would be something along the lines of a quil draw function.

	(defresilient division 0 [a b] (/ a b))
	
	(division 8 4) => 2
	(division 8 0) => 0  ; And prints an appropriate stacktrace
	(division 8 4) => 0  ; This function is now redefined, so your console doesn't fill 
	                     ; with stacktraces, obscuring what actually went wrong
	                     
## Future development
* (DONE) Make it so that each function redefinition adds to the head of a list of possible definitions for that function. On failure, it will take the last known good function and pop the bad function off the top of the list, all the way back to the default
* Figure out how this translates to production code (sed -i '/defresilient/defn/g' doesn't really cut it but we don't really want to be swallowing errors)
* Keep track of which functions are the latest version and which have fallen back, allow the user to query this in the REPL and/or make a little interactive utility which will show it on screen the whole time
* Consider performance/overhead and how that might be improved upon

## License

Copyright © 2012

Distributed under the Eclipse Public License
