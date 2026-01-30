package mx.com.seccionamarilla.bcm.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIErrorDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3293328784262848122L;
	private int status;
	private String error;
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp = LocalDateTime.now();

	public APIErrorDTO(int status, String error, String message) {
		this.status = status;
		this.error = error;
		this.message = message;
	}
}
