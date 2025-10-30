package ng.darum.employee.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.default.admin")
public class DefaultAdminConfig {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String departmentName;
    private String departmentDescription;
}
