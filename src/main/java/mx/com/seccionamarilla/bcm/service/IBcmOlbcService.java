package mx.com.seccionamarilla.bcm.service;

import mx.com.seccionamarilla.bcm.model.dto.KafkaSubmitRequest;
import mx.com.seccionamarilla.bcm.model.dto.KafkaWfmBcmRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public interface IBcmOlbcService {
	void updateFlowTask(KafkaWfmBcmRequest kTaskRequest, ProcessedMessage pm, String taskStatus);

	void updateBusiness(KafkaSubmitRequest businessRequest, ProcessedMessage pm);
}
