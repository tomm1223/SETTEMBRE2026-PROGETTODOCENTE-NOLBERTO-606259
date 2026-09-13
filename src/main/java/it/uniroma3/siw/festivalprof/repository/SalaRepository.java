package it.uniroma3.siw.festivalprof.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.festivalprof.model.Sala;

public interface SalaRepository extends CrudRepository<Sala, Long> {
    List<Sala> findAll();
    boolean existsByNome(String nome);
}
