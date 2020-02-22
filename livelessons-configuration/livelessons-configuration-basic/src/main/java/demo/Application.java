package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

// -D and env vars work too!
// e.g. java `-Dconfiguration.projectName=FOO -jar .\target\livelessons-configuration-basic-1.0.0-SNAPSHOT.jar
// e.g. $env:CONFIGURATION_PROJECTNAME = 'NEWNAME'; mvn spring-boot:run

// localhost:8080/actuator/configprops to see the values of all properties, including our custom ones

@SpringBootApplication
@EnableConfigurationProperties
public class Application {

	@Value("${configuration.projectName}")
	void setProjectName(String projectName) {
		System.out.println("setting project name: " + projectName);
	}

	@Autowired
	void setEnvironment(Environment env) {
		System.out.println(
				"setting environment: " + env.getProperty("configuration.projectName"));
	}

	@Autowired
	void setConfigurationProjectProperties(ConfigurationProjectProperties cp) {
		System.out.println(
				"configurationProjectProperties.projectName = " + cp.getProjectName());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

}
