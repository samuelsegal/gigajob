package com.spazomatic.jobyjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.spazomatic.jobyjob.conf.ElasticsearchConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = {ElasticsearchConfiguration.class})
@ComponentScan(basePackages = "com.spazomatic.jobyjob")

public class JobyjobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobyjobApplication.class, args);
    }
}
