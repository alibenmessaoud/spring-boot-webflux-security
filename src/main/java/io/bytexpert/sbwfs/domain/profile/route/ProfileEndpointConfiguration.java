package io.bytexpert.sbwfs.domain.profile.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
class ProfileEndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(ProfileHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/profiles/{name}"), handler::getByName)
                .andRoute(RequestPredicates.POST("/profiles"), handler::create);
    }


}