package io.bytexpert.sbwfs.security.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
}
