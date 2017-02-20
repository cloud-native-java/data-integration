package complaints;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/complaints",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
class ComplaintsRestController {

	private final CommandGateway cg;

	@Autowired
	ComplaintsRestController(CommandGateway cg) {
		this.cg = cg;
	}

	@PostMapping
	ResponseEntity<?> createComplaint(@RequestBody Map<String, String> body) {

		FileComplaintCommand complaint = new FileComplaintCommand(
				body.get("id"), body.get("company"), body.get("description"));

		this.cg.send(complaint);

		UriComponents uriComponents = UriComponentsBuilder
				.newInstance()
				.path("/complaints/{id}")
				.build()
				.expand(Collections.singletonMap("id", complaint.getId()));

		URI uri = uriComponents.toUri();

		return ResponseEntity.created(uri).build();
	}

	@PostMapping("/{complaintId}/comments")
	ResponseEntity<?> createComplaintComment(
			@PathVariable String complaintId,
			@RequestBody Map<String, Object> body) {

		AddCommentCommand command = new AddCommentCommand(
				complaintId,
				UUID.randomUUID().toString(),
				String.class.cast(body.get("comment")),
				String.class.cast(body.get("user")),
				new Date(Long.class.cast(body.get("when"))));
		this.cg.send(command);
		UriComponents uriComponents = UriComponentsBuilder
				.newInstance()
				.path("/complaints/{complaintId}/comments/{commentId}")
				.build()
				.expand(complaintId, command.getCommentId());
		URI uri = uriComponents.toUri();
		return ResponseEntity.created(uri).build();
	}
}
