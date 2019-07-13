package io.bytexpert.sbwfs.security.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
class UserEndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/authorize"), handler::authorize)
                .andRoute(RequestPredicates.GET("/users/{login}"), handler::getByLogin);
    }


}