package mx.com.seccionamarilla.bcm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFlowInstanceDetail {
	private String taskCode;
	private Long entityId;
	private Long productId;
	private Long assigneeUser;
	private String taskName;
	private String advertiserName;
	private String productName;
	private Long advertiserId;
	private String taskStatus;
	private boolean canUserTakeAction;
	private boolean isUserValid;
	private Long taskId;
	private String estatus;
	private Long rechazable;
	private Long flowId;
	private Long subscriptionId;
	private Boolean isSinergia;
	private Long sinergiaProductId;
	private String flowName;
}
