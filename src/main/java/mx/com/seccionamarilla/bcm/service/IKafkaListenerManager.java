package mx.com.seccionamarilla.bcm.service;

public interface IKafkaListenerManager {
	public void stopListener(String listenerId);

	public void startListener(String listenerId);
}
