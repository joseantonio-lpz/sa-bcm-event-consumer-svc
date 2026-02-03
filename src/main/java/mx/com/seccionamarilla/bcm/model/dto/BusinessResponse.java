package mx.com.seccionamarilla.bcm.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class BusinessResponse {
	private String businessId;
	private List<Business> businesses;
}
