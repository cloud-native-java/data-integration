package complaints;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CommentAddedEvent {
	private String complaintId,  commentId, comment, user;
	private Date when;
}
