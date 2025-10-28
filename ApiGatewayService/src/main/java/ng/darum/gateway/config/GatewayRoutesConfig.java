package ng.darum.gateway.config;

/*import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration*/
public class GatewayRoutesConfig {

  /*  @Bean
    public RouterFunction<ServerResponse> customRoutes() {

        // @formatter:off
        return route("auth-service")
                .route(path("/api/auth/**"), http())
                .before(uri("http://auth-service"))
                .before(rewritePath("/api/auth/(?<segment>.*)", "/${segment}"))
                .build()
                .and(route("employee-service")
                        .route(path("/api/employee/**"), http())
                        .before(uri("lb://employee-service"))
                        .before(rewritePath("/api/employee/(?<segment>.*)", "/${segment}"))
                        .build())
                .and(route("department-service")
                        .route(path("/api/department/**"), http())
                        .before(uri("lb://employee-service"))
                        .before(rewritePath("/api/department/(?<segment>.*)", "/${segment}"))
                        .build());
        // @formatter:on
    }*/
}
