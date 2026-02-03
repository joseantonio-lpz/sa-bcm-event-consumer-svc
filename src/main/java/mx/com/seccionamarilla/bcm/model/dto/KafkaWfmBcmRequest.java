package mx.com.seccionamarilla.bcm.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaWfmBcmRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540786349772218808L;
	private String entityId;
	private String externalId;
	private String productCode;
	private String taskCode;
	private String flowCode;
	private String flowInstanceId;
	private String date;
	private String userId;
}
