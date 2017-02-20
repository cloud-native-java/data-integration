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
import java.util.concurrent.CompletableFuture;

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
	CompletableFuture<ResponseEntity<?>> createComplaint(@RequestBody Map<String, String> body) {

		FileComplaintCommand complaint = new FileComplaintCommand(
				UUID.randomUUID().toString(), body.get("company"), body.get("description"));

		return this.cg.send(complaint).thenApply(complaintId -> {
			UriComponents uriComponents = UriComponentsBuilder
					.newInstance()
					.path("/complaints/{id}")
					.build()
					.expand(Collections.singletonMap("id", complaint.getId()));
			URI uri = uriComponents.toUri();
			return ResponseEntity.created(uri).build();
		});

	}


	@DeleteMapping("/{complaintId}")
	void close(@PathVariable String complaintId) {
		CloseComplaintCommand closeComplaintCommand = new CloseComplaintCommand(complaintId);
		this.cg.send(closeComplaintCommand);
	}

	@PostMapping("/{complaintId}/comments")
	ResponseEntity<?> createComplaintComment(
			@PathVariable String complaintId,
			@RequestBody Map<String, Object> body) {

		Long when = Long.class.cast(body.getOrDefault("when", new Date().getTime()));

		AddCommentCommand command = new AddCommentCommand(
				complaintId,
				UUID.randomUUID().toString(),
				String.class.cast(body.get("comment")),
				String.class.cast(body.get("user")),
				new Date(when));

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
