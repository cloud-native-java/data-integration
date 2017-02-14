package integration;

import cnj.CloudFoundryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShakyServiceIT.Config.class)
public class ShakyServiceIT {

	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private CloudFoundryService cloudFoundryService;

	private File shakyClient, shakyService;
	@Autowired
	private CloudFoundryService cfs;
	@Autowired
	private RestTemplate rt;
	@Autowired
	private CloudFoundryOperations cfo;

	private void installApplications() throws Throwable {
		File project = new File(".");
		this.shakyClient = new File(project, "../shaky-service-to-service-calls/shaky-client/manifest.yml");
		this.shakyService = new File(project, "../shaky-service-to-service-calls/shaky-service/manifest.yml");
		Assert.assertTrue(this.shakyClient.exists());
		Assert.assertTrue(this.shakyService.exists());

		this.cfs.pushApplicationAndCreateUserDefinedServiceUsingManifest(
				this.shakyService);
		this.cfs.pushApplicationUsingManifest(this.shakyClient);
	}

	@Before
	public void before() throws Throwable {
		this.installApplications();
	}

	@After
	public void after() throws Throwable {
		Stream.of(this.shakyClient, this.shakyService).forEach(file ->
				this.cfs.applicationManifestFrom(file).forEach((jar, manifest) -> {
					this.cfs.destroyApplicationIfExists(manifest.getName());
				}));

		this.cfs.applicationManifestFrom(this.shakyService).forEach((jar, manifest) -> {
			this.cfs.destroyServiceIfExists(manifest.getName());
		});
		this.cfs.destroyOrphanedRoutes();
	}

	@Test
	public void deployServiceAndClient() throws Throwable {

		// the client has the recovery logic
		String clientUrl = this.cloudFoundryService.urlForApplication("shaky-client");
		String param = "World";
		call(clientUrl, param, "Hello, " + param + "!");

		// client recovery logic trips when the service is down
		this.cfo.applications().stop(StopApplicationRequest.builder().name("shaky-service").build()).block();
		call(clientUrl, param, "OHAI");
	}

	private void call(String clientUrl, String param, String expected) {
		Stream.of("hystrix", "retry").forEach(type -> {
			String fullUrl = clientUrl + "/" + type + "/hi/" + param;
			ResponseEntity<String> responseEntity =
					this.rt.getForEntity(fullUrl, String.class);
			log.info("called " + fullUrl + " and received " + responseEntity.getBody());
			Assert.assertEquals(expected, responseEntity.getBody());
		});
	}

	@SpringBootApplication
	public static class Config {

		@Bean
		RestTemplate restTemplate() {
			return new RestTemplateBuilder()
					.build();
		}
	}

}
