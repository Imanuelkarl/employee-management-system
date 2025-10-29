package ng.darum.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.darum.employee.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
