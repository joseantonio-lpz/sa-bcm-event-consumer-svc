package mx.com.seccionamarilla.bcm.util;

import java.util.Map;
import java.util.Optional;

import mx.com.seccionamarilla.bcm.model.dto.KafkaSubmitRequest;
import mx.com.seccionamarilla.bcm.model.dto.KafkaWfmBcmRequest;

public final class PayloadUtil {

	private PayloadUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String getString(Map<String, Object> payload, String key) {
		return Optional.ofNullable(payload.get(key))
				.map(Object::toString)
				.orElse(null);
	}

	public static KafkaSubmitRequest toKafkaSubmitRequest(Map<String, Object> payload) {
		KafkaSubmitRequest request = new KafkaSubmitRequest();
		request.setResponseId (getString(payload, "ResponseId"));
		request.setExternalId(getString(payload, "ExternalId"));
		request.setFormCode(getString(payload, "FormCode"));
		request.setToken(getString(payload, "Token"));
		return request;
	}

	public static KafkaWfmBcmRequest toKafkaWfmBcmRequest(Map<String, Object> payload) {
		KafkaWfmBcmRequest request = new KafkaWfmBcmRequest();
		request.setEntityId(getString(payload, "EntityId"));
		request.setExternalId(getString(payload, "ExternalId"));
		request.setProductCode(getString(payload, "ProductCode"));
		request.setTaskCode(getString(payload, "TaskCode"));
		request.setFlowCode(getString(payload, "FlowCode"));
		request.setFlowInstanceId(getString(payload, "FlowInstanceId"));
		request.setDate(getString(payload, "Date"));
		request.setUserId(getString(payload, "UserId"));
		return request;
	}
}
