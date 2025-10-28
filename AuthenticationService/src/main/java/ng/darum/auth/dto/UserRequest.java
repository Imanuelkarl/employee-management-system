package ng.darum.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String status;
    private String department;
}
