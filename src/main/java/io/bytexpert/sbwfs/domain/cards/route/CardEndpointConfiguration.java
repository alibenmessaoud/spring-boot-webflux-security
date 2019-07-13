package io.bytexpert.sbwfs.domain.cards.route;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Log4j2
@Configuration
class CardEndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> cardRoutes(CardHandler handler) {

        RouterFunction<ServerResponse> route = RouterFunctions
                .route(RequestPredicates.GET("/cards/{name}"), handler::getByName);

        route
                .andRoute(RequestPredicates.POST("/cards"), handler::create);

        return route;

    }


}