package ng.darum.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.darum.employee.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private Long id;
    private String email;
    private Role role;
    private String password;
}
