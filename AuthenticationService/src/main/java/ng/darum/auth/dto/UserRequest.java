package ng.darum.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.darum.auth.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String email;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String status;
    private String department;
}
