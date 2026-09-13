package it.uniroma3.siw.festivalprof.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.festivalprof.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Credentials findByUsername(String username);
    boolean existsByUsername(String username);
}
