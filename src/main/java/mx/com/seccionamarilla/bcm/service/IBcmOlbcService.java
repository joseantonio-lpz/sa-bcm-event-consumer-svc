package mx.com.seccionamarilla.bcm.service;

import mx.com.seccionamarilla.bcm.model.dto.KafkaTaskRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public interface IBcmOlbcService {
	void updateFlowTask(KafkaTaskRequest kTaskRequest, ProcessedMessage pm, String taskStatus);
}
