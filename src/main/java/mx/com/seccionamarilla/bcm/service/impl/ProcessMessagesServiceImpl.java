package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.dao.IProcessedMessageDao;
import mx.com.seccionamarilla.bcm.exception.InternalServiceException;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;

@Slf4j
@Service
public class ProcessMessagesServiceImpl implements IProcessMessagesService {

	private IProcessedMessageDao processedMessageDao;

	public ProcessMessagesServiceImpl(IProcessedMessageDao processedMessageDao) {
		this.processedMessageDao = processedMessageDao;
	}

	@Override
	public int insert(ProcessedMessage pm) {
		try {
			return processedMessageDao.insert(pm);
		} catch (Exception e) {
			log.error("error insert sevice log  {} ", e.getLocalizedMessage());
			throw new InternalServiceException("error insert sevice log", e);
		}
	}

	@Override
	public int updateStatusOut(Long id, String statusOut, String errorOut, String msgId, String msgValue,
			String msgCode,String msgOut) {
		try {
			String error = null;
			if (errorOut != null && errorOut.length() > 1000) {
				error = errorOut.substring(0, 1000);
			}else {
				error = errorOut;
			}
			return processedMessageDao.updateStatusOut(id, statusOut, error, msgId, msgValue, msgCode, msgOut);
		} catch (Exception e) {
			log.error("error update sevice log  {} ", e.getLocalizedMessage());
			throw new InternalServiceException("error update sevice log", e);
		}
	}
}
