package ng.darum.employee.dto;

import lombok.Builder;
import lombok.Data;
import ng.darum.employee.enums.Role;

@Data
@Builder
public class EmployeeRequest {
	private String email;
	private String password;
	private Role role;
	private String firstName;
	private String lastName;
	private String employeeId;
	private String status;
	private Long departmentId;

}
