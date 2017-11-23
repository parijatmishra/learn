package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot Dependency-injection, Configuration, and Web-App initiatilization
 */
@SpringBootApplication
public class GreetingApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GreetingApplication.class, args);
    }
}
