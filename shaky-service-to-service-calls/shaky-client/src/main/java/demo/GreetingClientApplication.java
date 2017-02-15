package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@EnableRetry
@EnableCircuitBreaker
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class GreetingClientApplication {

	public static void main(
			String args[]) {
		SpringApplication
				.run(
						GreetingClientApplication.class,
						args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
