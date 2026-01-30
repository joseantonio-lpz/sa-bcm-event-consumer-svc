package mx.com.seccionamarilla.bcm.dao.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import mx.com.seccionamarilla.bcm.dao.IProcessedMessageDao;
import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

@Repository
public class ProcessedMessageDaoImpl implements IProcessedMessageDao {

	private final JdbcTemplate jdbcTemplate;

	public ProcessedMessageDaoImpl(@Qualifier("primaryJdbcTemplate")JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int insert(ProcessedMessage pm) {
		String sql = """
				MERGE INTO KAFKA_WFM_PROCESSED_MESSAGES t
				USING (
				    SELECT ? AS PROCESSED_MESSAGES_ID,
				           ? AS TOPIC_NAME,
				           ? AS PAYLOAD
				    FROM dual
				) s
				ON (t.PROCESSED_MESSAGES_ID = s.PROCESSED_MESSAGES_ID)
				WHEN NOT MATCHED THEN
				INSERT (
				    PROCESSED_MESSAGES_ID,
				    TOPIC_NAME,
				    PAYLOAD
				)
				VALUES (
				    s.PROCESSED_MESSAGES_ID,
				    s.TOPIC_NAME,
				    s.PAYLOAD
				)
				""";

		return jdbcTemplate.update(sql, 
				pm.getProcessedMessagesId(), 
				pm.getTopicName(), 
				pm.getPayload());
	}

	@Override
	public int updateStatusOut(Long id, String statusOut, String errorOut, String msgId, String msgValue,
			String msgCode, String msgOut) {
		String sql = "UPDATE KAFKA_WFM_PROCESSED_MESSAGES SET STATUS_OUT = ?, ERROR_OUT = ?, PROCESSED_AT = current_timestamp, MSG_ID = ?, MSG_ID_VALUE = ?, MSG_CODE = ?, MSG_OUT = ? WHERE PROCESSED_MESSAGES_ID = ?";
		return jdbcTemplate.update(sql, statusOut, errorOut, msgId, msgValue, msgCode, msgOut, id);
	}

}
