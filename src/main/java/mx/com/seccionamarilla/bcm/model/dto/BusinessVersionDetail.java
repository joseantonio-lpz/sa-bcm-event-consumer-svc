package mx.com.seccionamarilla.bcm.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BusinessVersionDetail {
	private Integer businessVersionId;
	private Integer versionNumber;
	private String scope;
	private String state;
	private Integer basedOnVersionId;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
	private String sourceApp;
}
