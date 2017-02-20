package complaints;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/complaints", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class ComplaintsRestController {

	private final CommandGateway cg;

	@Autowired
	ComplaintsRestController(CommandGateway cg) {
		this.cg = cg;
	}

	@PostMapping
	CompletableFuture<ResponseEntity<?>> createComplaint(
			@RequestBody Map<String, String> body) {

		String id = UUID.randomUUID().toString();
		FileComplaintCommand complaint = new FileComplaintCommand(id,
				body.get("company"), body.get("description"));

		return this.cg.send(complaint).thenApply(
				complaintId -> {
					URI uri = uri("/complaints/{id}",
							Collections.singletonMap("id", complaint.getId()));
					return ResponseEntity.created(uri).build();
				});
	}

	@DeleteMapping("/{complaintId}")
	CompletableFuture<ResponseEntity<?>> closeComplaint(
			@PathVariable String complaintId) {
		CloseComplaintCommand closeComplaintCommand = new CloseComplaintCommand(
				complaintId);
		return this.cg.send(closeComplaintCommand)
				.thenApply(none -> ResponseEntity.notFound().build());
	}

	@PostMapping("/{complaintId}/comments")
	CompletableFuture<ResponseEntity<?>> createComment(
			@PathVariable String complaintId, @RequestBody Map<String, Object> body) {

		Long when = Long.class.cast(body.getOrDefault("when", new Date().getTime()));

		AddCommentCommand command = new AddCommentCommand(complaintId, UUID
				.randomUUID().toString(), String.class.cast(body.get("comment")),
				String.class.cast(body.get("user")), new Date(when));

		return this.cg.send(command).thenApply(commentId -> {

			Map<String, String> parms = new HashMap<>();
			parms.put("complaintId", complaintId);
			parms.put("commentId", command.getCommentId());

			URI uri = uri("/complaints/{complaintId}/comments/{commentId}", parms);

			return ResponseEntity.created(uri).build();
		});
	}

	private static URI uri(String uri, Map<String, String> template) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().path(uri)
				.build().expand(template);
		return uriComponents.toUri();
	}
}
