package mx.com.seccionamarilla.bcm.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class BusinessReferenceResponse {
	private String legacyAdvertiserId;
	private List<BusinessReference> businesses;
}
