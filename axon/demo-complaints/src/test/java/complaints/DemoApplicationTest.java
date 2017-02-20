package complaints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = DemoApplication.class, webEnvironment = MOCK)
public class DemoApplicationTest {

	private Log log = LogFactory.getLog(getClass());

	private String complaintJson, commentJson;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String complaintId = UUID.randomUUID().toString();

	@Before
	public void setUp() throws Throwable {

		Map<String, Object> map;

		map = new HashMap<>();
		map.put("id", this.complaintId);
		map.put("description", "Why WebLogic, why?");
		map.put("company", "Oracle");
		this.complaintJson = this.objectMapper.writeValueAsString(map);

		map = new HashMap<>();
		map.put("comment", "we looked into this, and we can't delete WebLogic from the universe.");
		map.put("user", "le");
		map.put("when", new Date());
		this.commentJson = this.objectMapper.writeValueAsString(map);
	}


	private void createComplaint() throws Throwable {
		this.log.debug("complaintJson: " + this.complaintJson);
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/complaints")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.complaintJson))
				.andExpect(header().stringValues("Location", "/complaints/" + this.complaintId))
				.andExpect(status().isCreated());
	}

	@Test
	public void complaint() throws Throwable {
		this.createComplaint();
	}

	@Test
	public void comment() throws Throwable {

		this.log.debug("commentJson: " + this.commentJson);

		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/complaints/" + this.complaintId + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.commentJson))
				.andExpect(status().isCreated());

	}


}