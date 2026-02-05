package mx.com.seccionamarilla.bcm.model.dto;

import lombok.Data;

@Data
public class BusinessReference {
	private Long businessId;
	private Integer versionNumber;
	private String commercialName;
	private String categoryCode;
	private String townCode;
	private String externalData; // es JSON en string
	private Object externalReferences;
	private String state;
	private Object lastUpdate;
}
