package ng.darum.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Auth Service API").version("1.0"))
                // IMPORTANT: This points to the gateway route that fronts auth-service
                .addServersItem(new Server().url("http://localhost:8080/auth"));
    }
}
