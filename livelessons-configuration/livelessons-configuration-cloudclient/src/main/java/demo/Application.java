package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application {

	// browse to http://localhost:8080/project-name to see the value we fetch from the config server

	// if we edit the property value in https://github.com/joshlong/microservices-lab-configuration/blob/master/config-client.properties
	// and refresh the config SERVER page http://localhost:8888/config-client/master, we'll see the new value is there, we don't need to restart the config server app

	// but if we refresh the CLIENT page http://localhost:8080/project-name we don't see the new value yet,
	// we can refresh the config by executing `curl http://localhost:8080/actuator/refresh -Method Post -ContentType "application/json"`

	@Autowired
	void setEnvironment(Environment e) {
		System.out.println(e.getProperty("configuration.projectName"));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
