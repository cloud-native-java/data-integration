package complaints;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

// <1>
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseComplaintCommand {

 @TargetAggregateIdentifier
 private String complaintId;
}
