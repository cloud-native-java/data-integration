package complaints.command;

import complaints.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


/***
 *
 * i could use @Component.
 * Aggregate lets me `apply`. First apply invokes the event sourcing handler `on(CFE)`.
 *
 */

@Aggregate
public class ComplaintAggregate {

	private Log log = LogFactory.getLog(getClass());

	@AggregateIdentifier
	private String complaintId;

	private boolean closed;

	public ComplaintAggregate(){}


	@CommandHandler
	public ComplaintAggregate(FileComplaintCommand complaint) {
		Assert.hasLength(complaint.getCompany());
		Assert.hasLength(complaint.getDescription());
		apply(new ComplaintFiledEvent(complaint.getId(),
				complaint.getCompany(), complaint.getDescription()));
	}

	@CommandHandler
	public void resolveComplaint(CloseComplaintCommand ccc) {
		if (notClosed()) {
			apply(new ComplaintClosedEvent(this.complaintId));
		}
	}

	private boolean notClosed() {
		return !this.closed;
	}

	@CommandHandler
	public void addComment(AddCommentCommand comment) {
		log.info("adding comment " + comment.toString());
		Assert.hasLength(comment.getComment());
		Assert.hasLength(comment.getCommentId());
		Assert.hasLength(comment.getComplaintId());
		Assert.hasLength(comment.getUser());
		Assert.notNull(comment.getWhen());
		Assert.isTrue(notClosed());
		apply(new CommentAddedEvent(comment.getComplaintId(),
				comment.getCommentId(), comment.getComment(),
				comment.getUser(), comment.getWhen()));
	}

	@EventSourcingHandler
	public void on(ComplaintClosedEvent cce) {
		this.closed = true;
	}

	@EventSourcingHandler
	protected void on(ComplaintFiledEvent cfe) {
		this.complaintId = cfe.getId();
		this.closed = false;
	}
}
