package mx.com.seccionamarilla.bcm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
	@JsonProperty("task_id")
	@NotNull
	private Long taskId;
	@JsonProperty("user_id")
	@NotNull
	private Long userId;
	@JsonProperty("comments")
	private String comments;
}
