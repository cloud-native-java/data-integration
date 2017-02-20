package complaints.query;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "complaint")
@EqualsAndHashCode(exclude = "complaint")
public class CommentQueryObject {

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "COMPLAINT_ID")
 private ComplaintQueryObject complaint;

 @Id
 private String id;

 private String comment, user;

 private Date when;
}
