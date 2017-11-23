# A very simple RESTful Web Service using Spring Boot

This is basically implementing the ["Building a RESTful Web
Service"](https://spring.io/guides/gs/rest-service/) tutorial from Spring
Boot.

This project implements a service that will accept HTTP GET requests at:

```
http://localhost:8080/greeting
```

and respond with a JSON representation of a greeting:

```json
{"id":1,"content":"Hello, World!"}
```

You can customize the greeting with an optional name parameter in the query string:

```
http://localhost:8080/greeting?name=User
```

The name parameter value overrides the default value of "World" and is reflected in the response:

```json
{"id":1,"content":"Hello, User!"}
```

## Build an executable JAR

You will need Apache maven installed.  Build the project with:
```
$ mvn clean package
```

You can run the JAR file:
```
java -jar target/spring-boot-rest-greeting-1.0.jar
```

(Change the version number in the command to match the value specified in
`project.version` in `pom.xml`.)

Logging output is displayed.  The service should be up and running in a few seconds.


## Test the service

Now that the service is up, visit
[http://localhost:8080/greeting](http://localhost:8080/greeting) where you
see:

```json
{"id":1,"content":"Hello, World!"}
```

Provide a name query string parameter with
[http://localhost:8080/greeting?name=User](http://localhost:8080/greeting?name=User).
Notice how the value of the content attribute changes from "Hello, World!" to
"Hello User!":

```json
{"id":2,"content":"Hello, User!"}
```

Notice how the `id` attribute has changed from `1` to `2`.  This proves that
the same `GreetingController` instance is being used across requests.

