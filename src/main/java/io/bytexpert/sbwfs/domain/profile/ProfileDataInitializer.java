package io.bytexpert.sbwfs.domain.profile;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Log4j2
@Component
class ProfileDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ProfileRepository repository;

    public ProfileDataInitializer(ProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        repository
                .deleteAll()
                .thenMany(
                        Flux
                                .just("A", "B", "C", "D")
                                .map(name -> {
                                    Profile profile = new Profile();
                                    profile.setName(name);
                                    profile.setCountry("US");
                                    return profile;
                                })
                                .flatMap(repository::save)
                )
                .thenMany(repository.findAll())
                .subscribe(profile -> log.info("saving " + profile.toString()));
    }
}