package mx.com.seccionamarilla.bcm.model.entity;

import java.sql.Timestamp;

import lombok.Data;


@Data
public class ProcessedMessage {
	private Long processedMessagesId;
    private String topicName;
    private String payload;          // CLOB
    private String statusIn;
    private String statusOut;
    private String msgId;
    private String msgIdValue;
    private String msgCode;
    private String errorOut;
    private String msgOut;
    private Timestamp createdAt;
    private Timestamp processedAt;
}
