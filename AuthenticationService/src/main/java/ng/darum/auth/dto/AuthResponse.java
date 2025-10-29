package ng.darum.auth.dto;

import lombok.Builder;
import lombok.Data;
import ng.darum.auth.enums.Role;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private Role role;
    private String token;
}
