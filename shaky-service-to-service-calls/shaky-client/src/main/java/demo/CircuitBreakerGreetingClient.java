package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Component
public class CircuitBreakerGreetingClient implements GreetingClient {

	private final RestTemplate restTemplate;
	private final String serviceUri;
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	public CircuitBreakerGreetingClient(RestTemplate restTemplate,
			@Value("${greeting-service.uri}") String uri) {
		this.restTemplate = restTemplate;
		this.serviceUri = uri;
	}

	@Override
	@HystrixCommand(fallbackMethod = "fallback")
	public String greet(String name) {
		long time = System.currentTimeMillis();
		Date now = new Date(time);
		this.log.info("attempting to call " + "the greeting-service " + time + "/"
				+ now.toString());

		ParameterizedTypeReference<Map<String, String>> ptr = new ParameterizedTypeReference<Map<String, String>>() {
		};

		return this.restTemplate
				.exchange(this.serviceUri + "/hi/" + name, HttpMethod.GET, null, ptr, name)
				.getBody().get("greeting");
	}

	public String fallback(String name) {
		return "OHAI";
	}

}
