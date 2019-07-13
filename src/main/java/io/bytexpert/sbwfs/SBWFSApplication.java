package io.bytexpert.sbwfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
public class SBWFSApplication {

    public static void main(String[] args) {
        SpringApplication.run(SBWFSApplication.class, args);
    }
}
