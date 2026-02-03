package mx.com.seccionamarilla.bcm.config.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.model.dto.KafkaSubmitRequest;
import mx.com.seccionamarilla.bcm.model.dto.KafkaWfmBcmRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IBcmOlbcService;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import mx.com.seccionamarilla.bcm.util.PayloadUtil;

@Slf4j
@Service
public class EventConsumer {
	private static final String STR_SAOLBC_PRODUCT_CODE = "SAOLBC";
	private static final String STR_VAR_PRODUCT_CODE = "ProductCode";
	private final IProcessMessagesService processMessagesService;
	private final IBcmOlbcService bcmOlbcService;

	public EventConsumer(IProcessMessagesService processMessagesService, IBcmOlbcService bcmOlbcService) {
		this.processMessagesService = processMessagesService;
		this.bcmOlbcService = bcmOlbcService;
	}

	@KafkaListener(id = "BCMEventAssigned", topics = "wfm.task_assigned", groupId = "dev.bcm_event.task_start")
	public void bcmAssignUser(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK ASSIGN================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		if (PayloadUtil.getString(payload, STR_VAR_PRODUCT_CODE).equalsIgnoreCase(STR_SAOLBC_PRODUCT_CODE)) {
			ProcessedMessage pm = insertConsumeEventLog(rec);
			KafkaWfmBcmRequest kTaskRequest = PayloadUtil.toKafkaWfmBcmRequest(payload);
			bcmOlbcService.updateFlowTask(kTaskRequest, pm, "START");
		}
	}

	@KafkaListener(id = "BCMEventCompleted", topics = "wfm.task_completed", groupId = "dev.bcm_event.task_complete")
	public void bcmComplete(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK COMPLETE================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);

		if (PayloadUtil.getString(payload, STR_VAR_PRODUCT_CODE).equalsIgnoreCase(STR_SAOLBC_PRODUCT_CODE)) {
			ProcessedMessage pm = insertConsumeEventLog(rec);
			KafkaWfmBcmRequest kTaskRequest = PayloadUtil.toKafkaWfmBcmRequest(payload);
			bcmOlbcService.updateFlowTask(kTaskRequest, pm, "COMPLETE");
		}

	}

	@KafkaListener(id = "BCMEventStarted", topics = "wfm.task_started", groupId = "dev.bcm_event.task_started")
	public void bcmReject(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK STARTED================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		if (PayloadUtil.getString(payload, STR_VAR_PRODUCT_CODE).equalsIgnoreCase(STR_SAOLBC_PRODUCT_CODE)) {
			ProcessedMessage pm = insertConsumeEventLog(rec);
			// externalid igual al idcontrato
			KafkaWfmBcmRequest kTaskRequest = PayloadUtil.toKafkaWfmBcmRequest(payload);
			bcmOlbcService.updateFlowTask(kTaskRequest, pm, "REJECT");
		}
	}

	@KafkaListener(id = "BCMEventSubmit", topics = "wfm.form_submit", groupId = "dev.bcm_event.form_submit")
	public void bcmSubmit(ConsumerRecord<String, Map<String, Object>> rec) {
		log.info(" ➡ =============================TASK SUBMIT================================= ➡ ");
		Map<String, Object> payload = printPayloadLogInfoFromTopic(rec);
		if (PayloadUtil.getString(payload, "FormCode").equalsIgnoreCase("FORM_ECOSISTEMA_DIGITAL")) {
			ProcessedMessage pm = insertConsumeEventLog(rec);
			// externalid igual al idcontrato
			KafkaSubmitRequest kTaskRequest = PayloadUtil.toKafkaSubmitRequest(payload);
			bcmOlbcService.updateBusiness(kTaskRequest, pm);
		}
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
