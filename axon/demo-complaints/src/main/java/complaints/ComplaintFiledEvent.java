package complaints;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComplaintFiledEvent {

	private final String id;

	private final String company;

	private final String complaint;
}
