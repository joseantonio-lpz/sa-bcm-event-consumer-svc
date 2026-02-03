package mx.com.seccionamarilla.bcm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaSubmitRequest {
	private String responseId;
	private String externalId;
	private String formCode;
	private String token;
}
