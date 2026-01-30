package mx.com.seccionamarilla.bcm.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5207886078820183694L;
	private String id;
	private String type;
	private String payload;
}
