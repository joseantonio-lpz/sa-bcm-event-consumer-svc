package mx.com.seccionamarilla.bcm.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.dao.ITaskDao;
import mx.com.seccionamarilla.bcm.exception.InternalServiceException;
import mx.com.seccionamarilla.bcm.model.dto.KafkaTaskRequest;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;
import mx.com.seccionamarilla.bcm.model.entity.TaskEntity;
import mx.com.seccionamarilla.bcm.model.entity.TaskFlowInstanceDetail;
import mx.com.seccionamarilla.bcm.service.IProcessMessagesService;
import mx.com.seccionamarilla.bcm.service.ITaskService;

@RequiredArgsConstructor
@Slf4j
@Repository
public class TaskServiceImpl implements ITaskService {

	private final IProcessMessagesService processMessagesService;
	private final ITaskDao taskDao;
	private static final String VAR_FLOW_INSTANCE_ID = "FLOW_INSTANCE_ID";

	@Override
	public void updateFlowTask(KafkaTaskRequest kTaskRequest, ProcessedMessage pm, String taskStatus) {
		TaskEntity data = new TaskEntity();
		try {
			String strVarProcesado = "NO_ENCONTRADO";
			TaskFlowInstanceDetail taskFlowIns = taskDao
					.getTaskFlowInstanceDetail(Long.valueOf(kTaskRequest.getFlowInstanceId()));
			// valida si encontro la tarea
			if (taskFlowIns != null) {
				switch (taskStatus) {
				case "COMPLETE":
					data.setTaskStatus("CO");
					break;
				case "REJECT":
					data.setFlowStatus("RE");
					data.setTaskStatus("RJ");
					break;
				case "START":
					data.setTaskStatus("ST");
					break;
				default:
				}
				data.setActivityCode(taskStatus);
				data.setTaskCode(taskFlowIns.getTaskCode());
				data.setAdnEntityId(taskFlowIns.getEntityId() != null ? taskFlowIns.getEntityId() : 0L);
				data.setAdnProductId(taskFlowIns.getProductId() != null ? taskFlowIns.getProductId() : 0L);
				data.setAssigneeUser(Long.valueOf(kTaskRequest.getUserId()));
				data.setCtcrSysUserId(Long.valueOf(kTaskRequest.getUserId()));

				int rows = taskDao.updateAssignUserAndTaskStatus(kTaskRequest.getUserId(), data.getTaskStatus(),
						kTaskRequest.getUserId(), Long.valueOf(kTaskRequest.getFlowInstanceId()));
				if (rows > 0) {
					strVarProcesado = "PROCESADO";
				}
			}
			// No se actualizó nada → revisar criterios
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), strVarProcesado, null,
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(), null);
		} catch (DataAccessException dae) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", dae.getMessage(),
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(),
					dae.getLocalizedMessage());
			log.error("Ocurrio el siguiente error en el servicio guardar new_flow_task: ", dae.getMessage(), dae);
			throw new InternalServiceException("Error al guardar new_flow_task en la base de datos: ", dae);
		} catch (Exception e) {
			processMessagesService.updateStatusOut(pm.getProcessedMessagesId(), "ERROR", e.getMessage(),
					VAR_FLOW_INSTANCE_ID, kTaskRequest.getFlowInstanceId(), kTaskRequest.getProductCode(),
					e.getLocalizedMessage());
			log.error("Error inesperado en newFlowTask: {}", e.getMessage(), e);
			throw new InternalServiceException("Error inesperado al guardar new_flow_task: " + e.getMessage(), e);
		}

	}

}
