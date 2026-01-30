package mx.com.seccionamarilla.bcm.dao;

import mx.com.seccionamarilla.bcm.model.entity.TaskEntity;
import mx.com.seccionamarilla.bcm.model.entity.TaskFlowInstanceDetail;

public interface ITaskDao {
	public TaskFlowInstanceDetail getTaskFlowInstanceDetail(Long taskId);

	public Long updateFlowTask(TaskEntity data);

	public int updateAssignUserAndTaskStatus(String assigneeUser, String taskStatus, String systemUser,
			Long flowInstanceId);
}
