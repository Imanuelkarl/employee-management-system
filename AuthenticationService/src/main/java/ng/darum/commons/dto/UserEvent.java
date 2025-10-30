package ng.darum.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.darum.auth.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private Long id;
    private String email;
    private Role role;
    private String password;
}
