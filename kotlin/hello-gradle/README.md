# hello-gradle

Example of setting up a kotlin project using Gradle.

 - `src/main/kotlin`: kotlin source code.

 - `build.gradle`: Gradle build script that uses
    Gradle 3.5+ "Plugin DSL" syntax to include and
    configure kotlin gradle plugin.

 - `build-traditional.gradle`: Gradle build script
    that uses the "old" plugin configuration method. 

Run:

 - `gradle run` to execute the application directly
 
 - `gradle build` to create a fat jar.  The jar
   is created at `build/libs/hello-gradle.jar`.

 - `gradle clean`: to clean up build artifacts. NOTE:
   this seems to do nothing right now.
