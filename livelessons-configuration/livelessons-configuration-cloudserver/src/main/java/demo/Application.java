package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class Application {

	// start this app and browse to http://localhost:8888/config-client/master
	// you'll see it pulls properties from https://github.com/joshlong/microservices-lab-configuration/blob/master/config-client.properties

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
