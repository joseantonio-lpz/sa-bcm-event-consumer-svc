package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.service.IKafkaListenerManager;

@Slf4j
@Service
public class KafkaListenerManagerImpl implements IKafkaListenerManager {
	private final KafkaListenerEndpointRegistry registry;

	public KafkaListenerManagerImpl(KafkaListenerEndpointRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void stopListener(String listenerId) {
		if (registry.getListenerContainer(listenerId) != null) {
			registry.getListenerContainer(listenerId).stop();
			log.debug("< Listener detenido: " + listenerId);
		}
	}

	@Override
	public void startListener(String listenerId) {
		if (registry.getListenerContainer(listenerId) != null) {
			registry.getListenerContainer(listenerId).start();
			log.debug("> Listener iniciado: " + listenerId);
		}
	}

}
