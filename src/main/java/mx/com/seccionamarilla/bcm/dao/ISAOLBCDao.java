package mx.com.seccionamarilla.bcm.dao;

public interface ISAOLBCDao {
	public void taskInsert(String productCode, String entityId, String externalId);
	public void taskUpdate(String entityId, String taskCode, String flowCode, String date,
			String userId, String rejectReason);
	public String actualizarBusinessStatus(Long business);
}
