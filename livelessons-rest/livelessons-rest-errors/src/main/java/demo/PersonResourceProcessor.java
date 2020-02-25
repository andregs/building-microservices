package demo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@Component
public class PersonResourceProcessor implements RepresentationModelProcessor<EntityModel<Person>> {

	@Override
	public EntityModel<Person> process(EntityModel<Person> resource) {
		String id = Long.toString(resource.getContent().getId());
		UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/people/{id}/photo").buildAndExpand(id);
		String uri = uriComponents.toUriString();
		resource.add(new Link(uri, "photo"));
		return resource;
	}

}
