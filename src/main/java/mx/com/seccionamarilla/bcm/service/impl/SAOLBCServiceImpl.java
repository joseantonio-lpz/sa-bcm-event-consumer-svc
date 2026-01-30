package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.dao.ISAOLBCDao;
import mx.com.seccionamarilla.bcm.exception.InternalServiceException;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import mx.com.seccionamarilla.bcm.service.ISAOLBCService;

@Slf4j
@Service
public class SAOLBCServiceImpl implements ISAOLBCService {
	private final IProcessMessagesService processMessagesService;
	private final ISAOLBCDao saolbcDao;

	public SAOLBCServiceImpl(ISAOLBCDao saolbcDao, IProcessMessagesService processMessagesService) {
		this.saolbcDao = saolbcDao;
		this.processMessagesService = processMessagesService;
	}

	@Override
	public void insertIngreso(ProcessedMessage pm, String productCode, String entityId, String externalId) {
		try {
			saolbcDao.taskInsert(productCode, entityId, externalId);
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null, "FOLIO", externalId,
					productCode, null);
		} catch (Exception e) {
			log.error("error task insert  {} ", e.getLocalizedMessage());
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(), "FOLIO",
					externalId, productCode, null);
			throw new InternalServiceException("error task insert", e);
		}
	}

	@Override
	public void updateContrato(ProcessedMessage pm, String productCode, String entityId, String taskCode,
			String flowCode, String date, String userId, String rejectReason) {
		try {
			saolbcDao.taskUpdate(entityId, taskCode, flowCode, date, userId, rejectReason);
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "PROCESADO", null, "ENTITY_ID",
					entityId, productCode, null);
		} catch (Exception e) {
			log.error("error task update  {} ", e.getLocalizedMessage());
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(), "ENTITY_ID",
					entityId, productCode, null);
			throw new InternalServiceException("error task update", e);
		}
	}

	@Override
	public String updatePinbox(ProcessedMessage pm, String responseId, String externalId, String formCode,
			String token) {
		try {
			return saolbcDao.actualizarBusinessStatus(Long.valueOf(externalId));
		} catch (Exception e) {
			log.error("error PINBOX notification service {} ", e.getLocalizedMessage());
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(), "EXTERNAL_ID",
					externalId, formCode, null);
			throw new InternalServiceException("error PINBOX notification service", e);
		}
	}

}
