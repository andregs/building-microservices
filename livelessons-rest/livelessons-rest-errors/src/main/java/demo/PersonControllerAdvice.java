package demo;

import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Optional;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
@ResponseBody
public class PersonControllerAdvice {

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(FileNotFoundException.class)
	public VndErrors fileNotFoundException(FileNotFoundException ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(PersonNotFoundException.class)
	public VndErrors personNotFoundException(PersonNotFoundException e) {
		return this.error(e, e.getPersonId() + "");
	}

	private <E extends Exception> VndErrors error(E e, String logref) {
		String msg = Optional.of(e.getMessage()).orElse(e.getClass().getSimpleName());
		return new VndErrors(logref, msg);
	}

}
