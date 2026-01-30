package mx.com.seccionamarilla.bcm.config.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.model.dto.KafkaTaskRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import mx.com.seccionamarilla.bcm.service.ITaskService;
import mx.com.seccionamarilla.bcm.util.PayloadUtil;

@Slf4j
@Service
public class EventConsumer {

	private final IProcessMessagesService processMessagesService;
	private final ITaskService taskService;

	public EventConsumer(IProcessMessagesService processMessagesService, ITaskService taskService) {
		this.processMessagesService = processMessagesService;
		this.taskService = taskService;
	}

	@KafkaListener(id = "BCMEventTaskSTART", topics = "wfm.task_assigned", groupId = "dev.bcm_event.task_start")
	public void bcmAssignUser(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK ASSIGN================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		ProcessedMessage pm = insertConsumeEventLog(rec);
		KafkaTaskRequest kTaskRequest = PayloadUtil.toKafkaTaskRequest(payload);
		taskService.updateFlowTask(kTaskRequest, pm, "START");
	}

	@KafkaListener(id = "BCMEventTaskCO", topics = "wfm.task_completed", groupId = "dev.bcm_event.task_complete")
	public void bcmComplete(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK COMPLETE================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		ProcessedMessage pm = insertConsumeEventLog(rec);
		KafkaTaskRequest kTaskRequest = PayloadUtil.toKafkaTaskRequest(payload);
		taskService.updateFlowTask(kTaskRequest, pm, "COMPLETE");
	}

	@KafkaListener(id = "BCMEventTaskReject", topics = "wfm.task_rejected", groupId = "dev.bcm_event.task_reject")
	public void bcmReject(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK REJECT================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		ProcessedMessage pm = insertConsumeEventLog(rec);
		KafkaTaskRequest kTaskRequest = PayloadUtil.toKafkaTaskRequest(payload);
		taskService.updateFlowTask(kTaskRequest, pm, "REJECT");
	}

	/**
	 * @param rec Parametro recibido de Kafka que contiene los datos del topic
	 *            consumido.
	 * @return Map<String, Object> payload se regresa los datos a procesar.
	 */
	private Map<String, Object> printPayloadLogInfoFromTopic(ConsumerRecord<String, Map<String, Object>> rec) {
		// Payload
		Map<String, Object> payload = rec.value();
		log.info("📥 Mensaje del topic '{}'", rec.topic());
		log.info("📥 Headers del topic '{}'", rec.headers());
		log.info("   ➡ Payload: {}", payload);

		// Headers
		rec.headers().forEach(header -> log.debug("   ➡ Header: {} = {}", header.key(), new String(header.value())));

		// Metadatos adicionales
		log.debug("   ➡ Partition: {}", rec.partition());
		log.debug("   ➡ Offset: {}", rec.offset());
		log.info("   ➡ Timestamp: {}", rec.timestamp());
		return payload;
	}

	private ProcessedMessage insertConsumeEventLog(ConsumerRecord<String, Map<String, Object>> rec) {
		ProcessedMessage pm = new ProcessedMessage();
		pm.setProcessedMessagesId(rec.timestamp());
		pm.setTopicName(rec.topic());
		pm.setPayload(rec.value().toString());
		// se llama el insert de RECIBIDO a la tabla de log
		processMessagesService.insert(pm);
		return pm;
	}
}
