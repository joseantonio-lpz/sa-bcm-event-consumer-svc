package mx.com.seccionamarilla.bcm.service;

import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public interface ISAOLBCService {

	/**
	 * @param productCode
	 * @param entityId
	 * @param externalId
	 */
	public void insertIngreso(ProcessedMessage pm, String productCode, String entityId, String externalId);

	public void updateContrato(ProcessedMessage pm, String productCode, String entityId, String taskCode,
			String flowCode, String date, String userId, String rejectReason);

	public String updatePinbox(ProcessedMessage pm, String responseId, String externalId, String formCode,
			String token);
}
