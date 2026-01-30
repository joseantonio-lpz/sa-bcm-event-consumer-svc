package mx.com.seccionamarilla.bcm.dao.impl;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import mx.com.seccionamarilla.bcm.dao.ISAOLBCDao;

@Repository
public class SAOLBCDaoImpl implements ISAOLBCDao {

	private final SimpleJdbcCall spEvtEntityCreated;
	private final SimpleJdbcCall spEvtTaskRejected;
	private final JdbcTemplate jdbcTemplate;

	public SAOLBCDaoImpl(@Qualifier("primaryJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate=jdbcTemplate;
		this.spEvtEntityCreated = new SimpleJdbcCall(jdbcTemplate).withCatalogName("VEM_PQ_INGRESO_WFM") // paquete
				.withProcedureName("SP_EVT_ENTITY_CREATED").withoutProcedureColumnMetaDataAccess() // 🔴 CLAVE
				.declareParameters(new SqlParameter("P_FOLIO", Types.NUMERIC),
						new SqlParameter("P_ADN_ENTITY_ID", Types.NUMERIC),
						new SqlParameter("P_EVENT_DATE", Types.TIMESTAMP));

		this.spEvtTaskRejected = new SimpleJdbcCall(jdbcTemplate).withCatalogName("VEM_PQ_INGRESO_WFM")
				.withProcedureName("SP_EVT_TASK_REJECTED").withoutProcedureColumnMetaDataAccess()
				.declareParameters(new SqlParameter("P_ADN_ENTITY_ID", Types.NUMERIC),
						new SqlParameter("P_EVENT_DATE", Types.TIMESTAMP));

	}

	@Override
	public void taskInsert(String productCode, String entityId, String externalId) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("P_FOLIO", Long.valueOf(externalId))
				.addValue("P_ADN_ENTITY_ID", Long.valueOf(entityId))
				// si mandas null → Oracle usa SYSDATE
				.addValue("P_EVENT_DATE", timestamp);
		spEvtEntityCreated.execute(params);
	}

	@Override
	public void taskUpdate(String entityId, String taskCode, String flowCode, String date, String userId,
			String rejectReason) {
		LocalDateTime ldt = LocalDateTime.parse(date);

		Timestamp ts = Timestamp.valueOf(ldt);
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("P_ADN_ENTITY_ID", entityId)
				.addValue("P_EVENT_DATE", ts);
		spEvtTaskRejected.execute(params);

	}

	@Override
	public String actualizarBusinessStatus(Long business) {
		return jdbcTemplate.execute(
		        "{ call VEM_PQ_FORMULARIOUNICO_JSON.SP_ACTUALIZA_BUSINESS_STATUS(?, ?) }",
		        (CallableStatementCallback<String>) cs -> {
		            // IN
		            cs.setLong(1, business);
		            // OUT
		            cs.registerOutParameter(2, Types.VARCHAR);
		            cs.execute();
		            return cs.getString(2);
		        }
		    );
	}

}
