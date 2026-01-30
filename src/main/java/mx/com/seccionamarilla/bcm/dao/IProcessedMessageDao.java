package mx.com.seccionamarilla.bcm.dao;

import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public interface IProcessedMessageDao {
	public int insert(ProcessedMessage pm);

	public int updateStatusOut(Long id, String statusOut, String errorOut, String msgId, String msgValue,
			String msgCode,String msgOut);
}
