package complaints;

import complaints.query.CommentQueryObject;
import complaints.query.CommentQueryObjectRepository;
import complaints.query.ComplaintQueryObject;
import complaints.query.ComplaintQueryObjectRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@SpringBootApplication
public class DemoApplication {

	public static void main(String args[]) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private static String id() {
		return UUID.randomUUID().toString();
	}


	//	@Bean
	CommandLineRunner demo(
			PlatformTransactionManager transactionManager,
			ComplaintQueryObjectRepository complaintQueryObjectRepository,
			CommentQueryObjectRepository commentQueryObjectRepository) {
		return args -> {

			Log log = LogFactory.getLog(getClass());

			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

			ComplaintQueryObject result = transactionTemplate.execute(tx -> {
				ComplaintQueryObject complaint = new ComplaintQueryObject(id(), "WebLogic? why", "oracle", new HashSet<>(), false);
				CommentQueryObject comment2 = new CommentQueryObject(
						complaint, id(), "Please. Remove this from the world.", "rj", new Date());
				CommentQueryObject comment1 = new CommentQueryObject(
						complaint, id(), "We can't discontinue WebLogic", "le", new Date());
				complaintQueryObjectRepository.save(complaint);
				List<CommentQueryObject> complaintComments = Arrays.asList(comment1, comment2);
				complaintComments.forEach(commentQueryObjectRepository::save);
				complaint.getComments().addAll(complaintComments);
				return complaint;
			});

			transactionTemplate.execute(tx -> {
				commentQueryObjectRepository.findAll().forEach(log::info);
				complaintQueryObjectRepository.findAll().forEach(log::info);
				return null;
			});

			log.info(result);
		};
	}
}
