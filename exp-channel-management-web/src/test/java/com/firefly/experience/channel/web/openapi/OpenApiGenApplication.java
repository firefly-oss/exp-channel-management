package com.firefly.experience.channel.web.openapi;

import org.fireflyframework.web.openapi.EnableOpenApiGen;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Minimal Spring Boot application used only during the Maven build to expose
 * the OpenAPI spec via Springdoc.
 */
@EnableOpenApiGen
@ComponentScan(basePackages = "com.firefly.experience.channel.web.controllers")
public class OpenApiGenApplication {

    /**
     * Starts the minimal Spring Boot context used to generate the OpenAPI specification at build time.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SpringApplication.run(OpenApiGenApplication.class, args);
    }
}
