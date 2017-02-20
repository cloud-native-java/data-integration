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
 * we could use @Component. but an
 * Aggregate lets us `apply` which first
 * invokes an event sourcing handler. in
 * this case the event sourcing handler
 * is an ideal place to put any state
 * between objects in the aggregate
 * hierarchy (like whether the parent
 * complaint is closed or not).
 *
 * when a command is sent on the
 * commandGateway, its
 * targetAggregateIdentifier is used to
 * route the command to the right
 * command handler methods on an
 * aggregate. when those handlers are
 * involved, they publish events which
 * are then handled by
 * EventSourcingHandlers on the same
 * aggregate. The EventSourcingHandlers
 * then restore any state it has gleemed
 * from the event (for example, is the
 * complaint closed or not).
 */

@Aggregate
public class ComplaintAggregate {

 private Log log = LogFactory.getLog(getClass());

 @AggregateIdentifier
 private String complaintId;

 private boolean closed;

 // this is required because Spring
 // manufactures the aggregage for Axon,
 // and of course
 // now Spring tries to satisfy
 // parameters in the constructor that
 // have no arguments.
 public ComplaintAggregate() {
 }

 @CommandHandler
 public ComplaintAggregate(FileComplaintCommand c) {
  Assert.hasLength(c.getCompany());
  Assert.hasLength(c.getDescription());
  apply(new ComplaintFiledEvent(c.getId(), c.getCompany(), c.getDescription()));
 }

 @CommandHandler
 public void resolveComplaint(CloseComplaintCommand ccc) {
  if (notClosed()) {
   apply(new ComplaintClosedEvent(this.complaintId));
  }
 }

 @CommandHandler
 public void addComment(AddCommentCommand c) {
  Assert.hasLength(c.getComment());
  Assert.hasLength(c.getCommentId());
  Assert.hasLength(c.getComplaintId());
  Assert.hasLength(c.getUser());
  Assert.notNull(c.getWhen());
  Assert.isTrue(notClosed());
  apply(new CommentAddedEvent(c.getComplaintId(), c.getCommentId(),
   c.getComment(), c.getUser(), c.getWhen()));
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

 private boolean notClosed() {
  return !this.closed;
 }
}
