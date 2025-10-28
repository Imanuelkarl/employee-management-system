package ng.darum.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRequest {
	private String firstName;
	private String lastName;
	private String employeeId;
	private Long userId;
	private String status;
	private String department;

}
