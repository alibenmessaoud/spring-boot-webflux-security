package io.bytexpert.sbwfs.domain.profile.route;

import io.bytexpert.sbwfs.common.exception.NotFoundException;
import io.bytexpert.sbwfs.domain.profile.Profile;
import io.bytexpert.sbwfs.domain.profile.ProfileRepository;
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
class ProfileHandler {

    private final ProfileRepository profileRepository;

    @Autowired
    ProfileHandler(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }


    Mono<ServerResponse> getByName(ServerRequest r) {
        Mono<Profile> login = this.profileRepository.findByName(r.pathVariable("name")).switchIfEmpty(Mono.error(new NotFoundException("No profile found")));
        return defaultReadResponse(login);
    }

    Mono<ServerResponse> create(ServerRequest request) {
        Flux<Profile> flux = request
                .bodyToFlux(Profile.class)
                .flatMap(this.profileRepository::save);

        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Profile> profiles) {
        return Mono
                .from(profiles)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/profiles/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<Profile> profiles) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(profiles, Profile.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}