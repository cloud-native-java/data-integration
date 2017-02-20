package complaints.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintQueryObject {

	@Id
	private String id;

	private String complaint;
	private String company;

	@OneToMany(mappedBy="complaint")
	private Set<CommentQueryObject> comments = new HashSet<>();
	private boolean closed ;

}
