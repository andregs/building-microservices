package demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/* Same as `livelessons-rest-basic` but with better error handling.
 * See controller class and its advice. Please also note the changes in pom.xml (there's now a starter for HATEOAS) */
@SpringBootApplication
public class Application {

	@Bean
	public CommandLineRunner commandLineRunner(PersonRepository personRepository) {
		return args -> {
			Arrays.asList("Phil", "Josh").forEach(name -> personRepository
					.save(new Person(name, (name + "@email.com").toLowerCase())));
			personRepository.findAll().forEach(System.out::println);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
