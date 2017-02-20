package complaints.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentQueryObjectRepository extends JpaRepository<CommentQueryObject, String> {
}
