package ng.darum.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String status;
    private String department;
}
