package complaints.command;

import complaints.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

// <1>
@Aggregate
public class ComplaintAggregate {

 // <2>
 @AggregateIdentifier
 private String complaintId;

 private boolean closed;

 public ComplaintAggregate() {
 }

 // <3>
 @CommandHandler
 public ComplaintAggregate(FileComplaintCommand c) {
  Assert.hasLength(c.getCompany());
  Assert.hasLength(c.getDescription());
  apply(new ComplaintFiledEvent(c.getId(), c.getCompany(), c.getDescription()));
 }

 // <4>
 @CommandHandler
 public void resolveComplaint(CloseComplaintCommand ccc) {
  if (!this.closed) {
   apply(new ComplaintClosedEvent(this.complaintId));
  }
 }

 // <5>
 @CommandHandler
 public void addComment(AddCommentCommand c) {
  Assert.hasLength(c.getComment());
  Assert.hasLength(c.getCommentId());
  Assert.hasLength(c.getComplaintId());
  Assert.hasLength(c.getUser());
  Assert.notNull(c.getWhen());
  Assert.isT rue(!this.closed);
  apply(new CommentAddedEvent(c.getComplaintId(), c.getCommentId(),
   c.getComment(), c.getUser(), c.getWhen()));
 }

 // <6>
 @EventSourcingHandler
 protected void on(ComplaintFiledEvent cfe) {
  this.complaintId = cfe.getId();
  this.closed = false;
 }

 // <7>
 @EventSourcingHandler
 protected void on(ComplaintClosedEvent cce) {
  this.closed = true;
 }

}
