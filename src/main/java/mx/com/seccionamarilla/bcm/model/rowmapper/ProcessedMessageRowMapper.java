package mx.com.seccionamarilla.bcm.model.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import mx.com.seccionamarilla.bcm.model.entity.ProcessedMessage;

public class ProcessedMessageRowMapper implements RowMapper<ProcessedMessage> {

	@Override
	public ProcessedMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessedMessage pm = new ProcessedMessage();
		pm.setProcessedMessagesId(rs.getLong("PROCESSED_MESSAGES_ID"));
		pm.setTopicName(rs.getString("TOPIC_NAME"));
		pm.setPayload(rs.getString("PAYLOAD"));
		pm.setStatusIn(rs.getString("STATUS_IN"));
		pm.setStatusOut(rs.getString("STATUS_OUT"));
		pm.setMsgId(rs.getString("MSG_ID"));
		pm.setMsgIdValue(rs.getString("MSG_ID_VALUE"));
		pm.setMsgCode(rs.getString("MSG_CODE"));
		pm.setErrorOut(rs.getString("ERROR_OUT"));
		pm.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		pm.setProcessedAt(rs.getTimestamp("PROCESSED_AT"));
		return pm;
	}

}
