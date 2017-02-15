package demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Component
public class RetryableGreetingClient
		implements GreetingClient {

	private final RestTemplate restTemplate;

	private final String serviceUri;

	private Log log = LogFactory
			.getLog(getClass());

	@Autowired
	public RetryableGreetingClient(
			RestTemplate restTemplate,
			@Value("${greeting-service.uri}") String domain) {
		this.restTemplate = restTemplate;
		this.serviceUri = domain;
	}

	// <1>
	@Retryable(include = Exception.class, maxAttempts = 4, backoff = @Backoff(multiplier = 5))
	@Override
	public String greet(
			String name) {
		long time = System
				.currentTimeMillis();

		Date now = new Date(time);

		this.log
				.info("attempting to call the greeting-service "
						+ time
						+ "/"
						+ now.toString());

		ParameterizedTypeReference<Map<String, String>> ptr = new ParameterizedTypeReference<Map<String, String>>() {
		};

		return this.restTemplate
				.exchange(
						this.serviceUri
								+ "/hi/"
								+ name,
						HttpMethod.GET,
						null, ptr, name)
				.getBody()
				.get("greeting");
	}

	// <2>
	@Recover
	public String recoverForGreeting(
			Exception e) {
		return "OHAI";
	}

}
