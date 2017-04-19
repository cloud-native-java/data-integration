package complaints.query;

import complaints.CommentAddedEvent;
import complaints.ComplaintClosedEvent;
import complaints.ComplaintFiledEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ComplaintEventProcessor {

 private final ComplaintQueryObjectRepository complaints;

 private final CommentQueryObjectRepository comments;

 // <1>
 @Autowired
 ComplaintEventProcessor(ComplaintQueryObjectRepository complaints,
  CommentQueryObjectRepository comments) {
  this.complaints = complaints;
  this.comments = comments;
 }

 @EventHandler
 public void on(ComplaintFiledEvent cfe) {
  ComplaintQueryObject complaint = new ComplaintQueryObject(cfe.getId(),
   cfe.getComplaint(), cfe.getCompany(), Collections.emptySet(), false);
  this.complaints.save(complaint);
 }

 @EventHandler
 public void on(CommentAddedEvent cae) {
  ComplaintQueryObject complaint = this.complaints
   .findOne(cae.getComplaintId());
  CommentQueryObject comment = new CommentQueryObject(complaint,
   cae.getCommentId(), cae.getComment(), cae.getUser(), cae.getWhen());
  this.comments.save(comment);
 }

 @EventHandler
 public void on(ComplaintClosedEvent cce) {
  ComplaintQueryObject complaintQueryObject = this.complaints.findOne(cce
   .getComplaintId());
  complaintQueryObject.setClosed(true);
  this.complaints.save(complaintQueryObject);
 }
}
