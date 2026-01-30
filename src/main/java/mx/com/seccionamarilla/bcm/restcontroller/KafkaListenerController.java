package mx.com.seccionamarilla.bcm.restcontroller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import mx.com.seccionamarilla.bcm.service.IKafkaListenerManager;

@RestController
@RequestMapping("/listeners")
@AllArgsConstructor
public class KafkaListenerController {
	private final IKafkaListenerManager manager;

	@PostMapping("/start/{id}")
	public String start(@PathVariable String id) {
		manager.startListener(id);
		return "Listener " + id + " iniciado";
	}

	@PostMapping("/stop/{id}")
	public String stop(@PathVariable String id) {
		manager.stopListener(id);
		return "Listener " + id + " detenido";
	}
}
