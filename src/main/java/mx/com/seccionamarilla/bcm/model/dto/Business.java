package mx.com.seccionamarilla.bcm.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Business {
	private Long businessId;
	private Integer businessVersion;
	private String commercialName;
	private String categoryCode;
	private String townCode;
	private Object externalData;
	private String state;
	private LocalDateTime lastUpdate;
	private Object accountId;
	private String businessStatus;
	private Integer currentConfigVersionId;
	private String creationSource;
	private String creationReference;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime businessUpdatedAt;
	private String businessUpdatedBy;
	private String businessSourceApp;
	private Long legacyAdvertiserId;
	private String creationExternalDataJson;
	private BusinessVersionDetail businessVersionDetail;
}
