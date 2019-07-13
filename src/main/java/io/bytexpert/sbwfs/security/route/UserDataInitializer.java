package io.bytexpert.sbwfs.security.route;

import io.bytexpert.sbwfs.security.repository.Authority;
import io.bytexpert.sbwfs.security.repository.User;
import io.bytexpert.sbwfs.security.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;

import static io.bytexpert.sbwfs.security.AuthoritiesConstants.ADMIN;
import static io.bytexpert.sbwfs.security.AuthoritiesConstants.USER;

@Log4j2
@Component
class UserDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        Authority userAuthority = new Authority();
        userAuthority.setName(USER);

        Authority adminAuthority = new Authority();
        adminAuthority.setName(ADMIN);

        Map<String, HashSet<Authority>> authorityMapPerUser = new HashMap<>();
        authorityMapPerUser.put("user", new HashSet<>(Collections.singletonList(userAuthority)));
        authorityMapPerUser.put("admin", new HashSet<>(Collections.singletonList(adminAuthority)));
        authorityMapPerUser.put("uadmin", new HashSet<>(Arrays.asList(userAuthority, adminAuthority)));

        repository
                .deleteAll()
                .thenMany(
                        Flux.fromIterable(new ArrayList<>(authorityMapPerUser.keySet()))
                                .map(name -> new User(null, name, passwordEncoder.encode("password"), authorityMapPerUser.get(name)))
                                .flatMap(repository::save)
                )
                .thenMany(repository.findAll())
                .subscribe(user -> log.info("saving " + user.toString()));

    }
}