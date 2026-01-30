package mx.com.seccionamarilla.bcm.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.com.seccionamarilla.bcm.config.kafka.EventProducer;
import mx.com.seccionamarilla.bcm.model.dto.EventMessage;
import mx.com.seccionamarilla.bcm.service.IKafkaService;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements IKafkaService {

	private final EventProducer eventProducer;
	
	@Override
	public void doBusinessAndSendEvent(String topicName, String payload) {
		 EventMessage event = new EventMessage(
	                UUID.randomUUID().toString(),
	                "BUSINESS_EVENT",
	                payload
	        );
	        eventProducer.sendEvent(topicName, event);
	}

}
