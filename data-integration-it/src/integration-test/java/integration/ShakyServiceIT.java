package integration;

import cnj.CloudFoundryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.stream.Stream;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShakyServiceIT.Config.class)
public class ShakyServiceIT {

	@SpringBootApplication
	public static class Config {
	}

	@Autowired
	private CloudFoundryService cloudFoundryService;

	private File shakyClient, shakyService;

	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CloudFoundryService service;


	private final RestTemplate restTemplate = new RestTemplateBuilder()
			.build();

	@Autowired
	private CloudFoundryOperations cf;

	private void installApplications() throws Throwable {
		File project = new File(".");
		this.shakyClient = new File(project, "../shaky-service-to-service-calls/shaky-client/manifest.yml");
		this.shakyService = new File(project, "../shaky-service-to-service-calls/shaky-service/manifest.yml");
		Assert.assertTrue(this.shakyClient.exists());
		Assert.assertTrue(this.shakyService.exists());

		this.service.pushApplicationAndCreateUserDefinedServiceUsingManifest(
				this.shakyService);
		this.service.pushApplicationUsingManifest(this.shakyClient);
	}

	@Before
	public void before() throws Throwable {
		this.installApplications();
	}

	@Test
	public void deployServiceAndClient() throws Throwable {

		String clientUrl = this.cloudFoundryService.urlForApplication("shaky-client");

		String param = "World";
		Stream.of("hystrix", "retry").forEach(type -> {
			String fullUrl = clientUrl + "/" + type + "/hi/" + param;
			this.log.info("full URL: " + fullUrl);
			ResponseEntity<String> responseEntity =
					this.restTemplate.getForEntity(fullUrl, String.class);
			log.info("called " + fullUrl + " and received " + responseEntity.getBody());
			Assert.assertEquals("Hello, " + param, responseEntity.getBody());
		});

		this.cf.applications().stop(StopApplicationRequest.builder().name("shaky-service").build()).block();

		Stream.of("hystrix", "retry").forEach(type -> {
			String fullUrl = clientUrl + "" + type + "/hi/World";
			this.log.info("full URL: " + fullUrl);
			ResponseEntity<String> responseEntity =
					this.restTemplate.getForEntity(fullUrl, String.class);
			log.info("called " + fullUrl + " and received " + responseEntity.getBody());
			Assert.assertEquals("OHAI", responseEntity.getBody());
		});

	}
}
