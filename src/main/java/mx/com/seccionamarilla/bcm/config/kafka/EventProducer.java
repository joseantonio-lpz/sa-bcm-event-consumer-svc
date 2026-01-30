package mx.com.seccionamarilla.bcm.config.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.com.seccionamarilla.bcm.model.dto.EventMessage;

@Service
@RequiredArgsConstructor
public class EventProducer {
	private final KafkaTemplate<String, EventMessage> kafkaTemplate;

	public void sendEvent(String topic, EventMessage event) {
		kafkaTemplate.send(topic, event.getId(), event);
	}
}
