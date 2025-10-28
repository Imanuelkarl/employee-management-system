package ng.darum.employee.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerResponse<T> {
	private String status;
	private String message;
	private T data;

}
