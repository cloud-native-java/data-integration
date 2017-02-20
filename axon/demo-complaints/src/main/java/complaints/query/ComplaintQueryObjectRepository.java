package complaints.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintQueryObjectRepository extends
 JpaRepository<ComplaintQueryObject, String> {

}
