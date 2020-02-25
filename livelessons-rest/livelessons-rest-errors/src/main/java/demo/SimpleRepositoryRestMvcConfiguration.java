package demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class SimpleRepositoryRestMvcConfiguration implements RepositoryRestConfigurer {

	@Override
	public void configureRepositoryRestConfiguration(
			RepositoryRestConfiguration config) {
		config.exposeIdsFor(Person.class);
	}

}
