package complaints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class ComplaintAggregate {

	private static Log log = LogFactory.getLog(ComplaintAggregate.class.getName());

	@AggregateIdentifier
	private String complaintId;

	@CommandHandler
	public ComplaintAggregate(FileComplaintCommand complaint) {
		Assert.hasLength(complaint.getCompany());
		Assert.hasLength(complaint.getDescription());
		apply(new ComplaintFiledEvent(complaint.getId(),
				complaint.getCompany(), complaint.getDescription()));
	}

	@CommandHandler
	public void addComment(AddCommentCommand comment) {
		log.info("adding comment " + comment.toString());
		Assert.hasLength(comment.getComment());
		Assert.hasLength(comment.getCommentId());
		Assert.hasLength(comment.getComplaintId());
		Assert.hasLength(comment.getUser());
		Assert.notNull(comment.getWhen());

		apply(new CommentAddedEvent(comment.getComplaintId(),
				comment.getCommentId(), comment.getComment(),
				comment.getUser(), comment.getWhen()));
	}

	@EventSourcingHandler
	protected void on(ComplaintFiledEvent event) {
		this.complaintId = event.getId();
	}
}
