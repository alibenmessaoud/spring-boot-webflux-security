package io.bytexpert.sbwfs.domain.cards.route;

import io.bytexpert.sbwfs.domain.cards.Card;
import io.bytexpert.sbwfs.domain.cards.CardRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
class CardHandler {

    private final CardRepository cardRepository;

    @Autowired
    CardHandler(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    Mono<ServerResponse> getByName(ServerRequest r) {
        Mono<Card> login = this.cardRepository.findByName(r.pathVariable("name"));
        return defaultReadResponse(login);
    }

    Mono<ServerResponse> create(ServerRequest request) {
        Flux<Card> flux = request
                .bodyToFlux(Card.class)
                .flatMap(this.cardRepository::save);
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Card> cards) {
        return Mono
                .from(cards)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/cards/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<Card> cards) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(cards, Card.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}