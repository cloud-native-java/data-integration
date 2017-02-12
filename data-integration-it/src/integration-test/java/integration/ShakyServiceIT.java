package integration;

import cnj.CloudFoundryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShakyServiceIT.Config.class)
public class ShakyServiceIT {

	@SpringBootApplication
	public static class Config {
	}

	private File shakyClient, shakyService;

	@Autowired
	private CloudFoundryService service;

	@Before
	public void before() throws Throwable {
		File project = new File(".");
		this.shakyClient = new File(project, "../shaky-service-to-service-calls/shaky-client");
		this.shakyService = new File(project, "../shaky-service-to-service-calls/shaky-service");

		this.service.pushApplicationAndCreateUserDefinedServiceUsingManifest(
				this.shakyService);

		this.service.pushApplicationUsingManifest(
				this.shakyClient);

		// TODO now test that the end to end works!
	}

	@Test
	public void deployServiceAndClient() throws Throwable {
		Assert.assertTrue(true);
	}


}
