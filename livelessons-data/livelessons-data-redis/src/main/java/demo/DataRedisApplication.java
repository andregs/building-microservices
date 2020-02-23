package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DataRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataRedisApplication.class, args);
    }

}
