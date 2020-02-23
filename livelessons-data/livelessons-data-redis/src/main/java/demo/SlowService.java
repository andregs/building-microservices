package demo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SlowService {

    @Cacheable("slow")
    public String executeCached(String arg) {
        return execute(arg);
    }

    public String executeNotCached(String arg) {
    	return execute(arg);
	}

	private String execute(String arg) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ignored) {
		}
		return UUID.randomUUID().toString();
	}

}
