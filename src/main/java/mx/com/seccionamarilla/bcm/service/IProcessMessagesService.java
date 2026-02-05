package mx.com.seccionamarilla.bcm.service;

import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public interface IProcessMessagesService {
	public int insert(ProcessedMessage pm);

	void insertCompleto(ProcessedMessage pm, Long id);
	
	public int updateStatusOut(Long id, String statusOut, String errorOut, String msgId, String msgValue,
			String msgCode,String msgOut);
}
