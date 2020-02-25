package demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/* browse this localhost:8080 app and see the metadata generated, then try to upload/access a person's photo */
@SpringBootApplication
public class Application {

    @Bean
    CommandLineRunner commandLineRunner(PersonRepository personRepository) {
        return args -> {
            Arrays.asList("Phil", "Josh")
					.forEach(name -> personRepository.save(
							new Person(name, (name + "@email.com").toLowerCase())));

            personRepository.findAll().forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
