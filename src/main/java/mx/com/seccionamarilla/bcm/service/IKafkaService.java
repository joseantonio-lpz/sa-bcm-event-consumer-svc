package mx.com.seccionamarilla.bcm.service;

public interface IKafkaService {
	public void doBusinessAndSendEvent(String topicName, String payload);
}
