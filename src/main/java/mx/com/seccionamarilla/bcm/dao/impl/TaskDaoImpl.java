package mx.com.seccionamarilla.bcm.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.dao.ITaskDao;
import mx.com.seccionamarilla.bcm.model.entity.TaskEntity;
import mx.com.seccionamarilla.bcm.model.entity.TaskFlowInstanceDetail;

@RequiredArgsConstructor
@Slf4j
@Repository
public class TaskDaoImpl implements ITaskDao {
	
	@Qualifier("primaryJdbcTemplate")
	private JdbcTemplate primaryJdbcTemplate;
	private SimpleJdbcCall newFlowTask;
	public static final String PARAM_ADN_ENTITY_ID = "pa_adn_entity_id";
	
	@Override
	public TaskFlowInstanceDetail getTaskFlowInstanceDetail(Long taskId) {
		Map<String, Object> inParams = new HashMap<>();
		inParams.put("p_task_id", taskId);
		primaryJdbcTemplate.setQueryTimeout(300);
		Map<String, Object> result = new SimpleJdbcCall(primaryJdbcTemplate)
				.withCatalogName("pck_prodapps_v2")
				.withFunctionName("get_detailtask_flowinstance")
				.withoutProcedureColumnMetaDataAccess() // evita
																										// que
																										// Spring
																										// intente
																										// leer
																										// metadatos
				.declareParameters(
						new SqlOutParameter("RETURN", Types.REF_CURSOR, this::mapRowDetaiTaskFlowInstance),
						new SqlParameter("p_task_id", Types.VARCHAR))
				.execute(inParams);// accountid, product_code, descriptiontask, taskcode,
									// taskdescription

		@SuppressWarnings("unchecked")
		List<TaskFlowInstanceDetail> list = (List<TaskFlowInstanceDetail>) result.get("RETURN");

		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	private TaskFlowInstanceDetail mapRowDetaiTaskFlowInstance(ResultSet rs, int rowNum) throws SQLException {
		TaskFlowInstanceDetail detail = new TaskFlowInstanceDetail();
		detail.setTaskStatus(rs.getString("TASK_STATUS"));
		detail.setTaskName(rs.getString("TASK_NAME"));
		detail.setTaskCode(rs.getString("TASK_CODE"));
		detail.setFlowName(rs.getString("FLOW_NAME"));
		detail.setAdvertiserName(rs.getString("ACCOUNT_NAME"));
		detail.setProductName(rs.getString("PRODUCT_NAME"));
		detail.setProductId(rs.getLong("ADN_PRODUCT_ID"));
		detail.setEntityId(rs.getLong("ADN_ENTITY_ID"));
		detail.setAssigneeUser(rs.getLong("ASSIGNEE_USER"));
		detail.setAdvertiserId(rs.getLong("ADN_ACCOUNT_ID"));
		detail.setTaskId(rs.getLong("TASK_ID"));
		detail.setEstatus(rs.getString("flow_status"));
		detail.setRechazable(rs.getLong("RECHAZABLE"));
		detail.setFlowId(rs.getLong("FLOW_ID"));
		detail.setSinergiaProductId(rs.getLong("PRODUCT_ID"));
		detail.setSubscriptionId(rs.getLong("SUSCRIPTION_ID"));
		return detail;
	}

	@Override
	public Long updateFlowTask(TaskEntity data) {
		log.info("Entrando al DAO newFlowTask");

        Map<String, Object> params = new HashMap<>();
        params.put("pa_adn_product_id", data.getAdnProductId());
        params.put("pa_flow_code", data.getFlowCode());
        params.put("pa_task_code", data.getTaskCode());
        params.put("pa_flow_status", data.getFlowStatus());
        params.put("pa_task_status", data.getTaskStatus());
        params.put("pa_activity_code", data.getActivityCode());
        params.put("pa_activity_date", Timestamp.valueOf(LocalDateTime.now()));
        params.put("pa_assignee_user", data.getAssigneeUser());
        params.put("pa_ctcr_sys_user_id", data.getCtcrSysUserId());
        params.put("pa_error_desc", data.getError_desc()); // null es válido
        if (data.getAdnEntityId().longValue() == 0) {
                params.put(PARAM_ADN_ENTITY_ID, null);
        } else {
                params.put(PARAM_ADN_ENTITY_ID, data.getAdnEntityId());
        }
        primaryJdbcTemplate.setQueryTimeout(300);
        newFlowTask = new SimpleJdbcCall(primaryJdbcTemplate)
                        .withCatalogName("wfm_pq_dml_transactions")
                        .withFunctionName("fn_new_flow_task")
                        .declareParameters(
                                        new SqlOutParameter("RETURN_VALUE", Types.NUMERIC),
                                        new SqlParameter("pa_adn_product_id", Types.NUMERIC),
                                        new SqlParameter("pa_flow_code", Types.VARCHAR),
                                        new SqlParameter("pa_task_code", Types.VARCHAR),
                                        new SqlParameter("pa_flow_status", Types.VARCHAR),
                                        new SqlParameter("pa_task_status", Types.VARCHAR),
                                        new SqlParameter("pa_activity_code", Types.VARCHAR),
                                        new SqlParameter("pa_activity_date", Types.DATE),
                                        new SqlParameter("pa_assignee_user", Types.NUMERIC),
                                        new SqlParameter("pa_ctcr_sys_user_id", Types.NUMERIC),
                                        new SqlParameter("pa_error_desc", Types.VARCHAR),
                                        new SqlParameter(PARAM_ADN_ENTITY_ID, Types.NUMERIC))
                        .withoutProcedureColumnMetaDataAccess();

        Map<String, Object> result = newFlowTask.execute(params);
        BigDecimal retorno = (BigDecimal) result.get("RETURN_VALUE");
        return retorno != null ? retorno.longValue() : null;
	}
	
	
	@Override
	public int updateAssignUserAndTaskStatus(String assigneeUser, String taskStatus, String systemUser,
			Long flowInstanceId) {
		String sql = "UPDATE wfm_flow_instance SET assignee_user = ?, task_status = ?, ctcr_sys_upd_dt = SYSDATE, ctcr_sys_user_id = ? WHERE flow_instance_id = ?";
		return primaryJdbcTemplate.update(sql, assigneeUser, taskStatus, systemUser, flowInstanceId);
	}
}
