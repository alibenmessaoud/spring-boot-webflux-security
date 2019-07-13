package io.bytexpert.sbwfs.domain.cards;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Log4j2
@Component
class CardDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CardRepository repository;

    public CardDataInitializer(CardRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        repository
                .deleteAll()
                .thenMany(
                        Flux
                                .just("1", "2", "3", "4")
                                .map(name -> {
                                    Card card = new Card();
                                    card.setName(name);
                                    return card;
                                })
                                .flatMap(repository::save)
                )
                .thenMany(repository.findAll())
                .subscribe(card -> log.info("saving " + card.toString()));
    }
}