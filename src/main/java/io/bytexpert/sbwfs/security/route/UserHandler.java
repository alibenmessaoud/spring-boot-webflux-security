package io.bytexpert.sbwfs.security.route;

import io.bytexpert.sbwfs.common.exception.BadRequestException;
import io.bytexpert.sbwfs.common.exception.NotFoundException;
import io.bytexpert.sbwfs.security.jwt.JWTReactiveAuthenticationManager;
import io.bytexpert.sbwfs.security.jwt.TokenProvider;
import io.bytexpert.sbwfs.security.repository.User;
import io.bytexpert.sbwfs.security.repository.UserRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Validator;

@Component
public class UserHandler {

    private final TokenProvider tokenProvider;
    private final JWTReactiveAuthenticationManager jwtAuthenticationManager;
    private final Validator validator;
    private final UserRepository userRepository;

    public UserHandler(TokenProvider tokenProvider, JWTReactiveAuthenticationManager jwtAuthenticationManager, Validator validator, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> authorize(ServerRequest serverRequest) {
        return authorize3(serverRequest);
    }

    Mono<ServerResponse> getByLogin(ServerRequest r) {
        Mono<User> login = this.userRepository.findByLogin(login(r)).switchIfEmpty(Mono.error(new NotFoundException("No user found"))).map(user -> {
            user.setPassword(null);
            user.setId(null);
            return user;
        });
        return defaultReadResponse(login);
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> users) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(users, User.class);
    }

    private static String login(ServerRequest r) {
        return r.pathVariable("login");
    }

    public Mono<ServerResponse> authorize0(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(LoginRequest.class)
                .flatMap(loginRequest -> {
                    if (validator.validate(loginRequest).isEmpty())
                        return processLoginRequest(loginRequest);
                    else
                        throw new BadRequestException("Unprocessed entity");
                });
    }

    private Mono<ServerResponse> processLoginRequest(LoginRequest loginRequest) {
        Mono<LoginResponse> loginResponseMono = this.jwtAuthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))
                .doOnSuccess(ReactiveSecurityContextHolder::withAuthentication)
                .map(authentication -> new LoginResponse(tokenProvider.createToken(authentication)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(loginResponseMono, LoginResponse.class);
    }

    public Mono<ServerResponse> authorize2(ServerRequest serverRequest) {

        Mono<LoginResponse> loginResponseMono = serverRequest.bodyToMono(LoginRequest.class)
                .map(loginRequest -> this.jwtAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .flatMap(authenticationMono -> authenticationMono)
                .doOnSuccess(ReactiveSecurityContextHolder::withAuthentication)
                .map(authentication -> new LoginResponse(tokenProvider.createToken(authentication)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(loginResponseMono, LoginResponse.class);

    }

    public Mono<ServerResponse> authorize3(ServerRequest serverRequest) {

        Mono<LoginResponse> loginResponseMono = serverRequest
                .bodyToMono(LoginRequest.class)
                .filter(loginRequest -> validator.validate(loginRequest).isEmpty())
                .map(loginRequest -> this.jwtAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .flatMap(authenticationMono -> authenticationMono)
                .subscribeOn(Schedulers.elastic())
                .doOnSuccess(ReactiveSecurityContextHolder::withAuthentication)
                .map(authentication -> new LoginResponse(tokenProvider.createToken(authentication)))
                .switchIfEmpty(Mono.error(new BadRequestException("Bad request")));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(loginResponseMono, LoginResponse.class);
    }
}
