package it.uniroma3.siw.festivalprof.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.festivalprof.model.Regista;

public interface RegistaRepository extends CrudRepository<Regista, Long> {
    List<Regista> findAll();
    boolean existsByNomeAndCognomeAndDataNascita(String nome, String cognome, LocalDate dataNascita);
}
