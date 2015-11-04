package com.spazomatic.jobyjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableAutoConfiguration(exclude = {MongoConfig.class})
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.spazomatic.jobyjob")

//public class JobyjobApplication  {
public class JobyjobApplication  extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JobyjobApplication.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(JobyjobApplication.class, args);
    }
}
