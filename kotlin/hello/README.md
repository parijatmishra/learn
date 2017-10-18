# hello

Example of how to use the kotlin compiler directly to
compile and run kotlin code.

 - `hello.kt`: code containing a main method (kotlin
   generates the `HelloKt` module from the file
   name).

 - `build.sh`: compile the `hello.kt` file and builds
   a jar capable of being run directly by the JVM (by
   including the kotlin runtime).

 - `run.sh`: runs the above jar, using just the JRE.

 - `clean.sh`: removes build artifacts (the created
   jar).
