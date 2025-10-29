package ng.darum.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.darum.auth.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String status;
    private String department;
}
