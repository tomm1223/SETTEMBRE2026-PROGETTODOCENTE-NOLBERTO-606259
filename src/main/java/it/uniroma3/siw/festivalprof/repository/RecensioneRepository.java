package it.uniroma3.siw.festivalprof.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Recensione;
import it.uniroma3.siw.festivalprof.model.User;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
    List<Recensione> findByFilmOrderByDataDesc(Film film);
    List<Recensione> findByUtente(User utente);

    @Query("SELECT r FROM Recensione r JOIN FETCH r.utente WHERE r.film.id = :filmId ORDER BY r.data DESC")
    List<Recensione> findByFilmIdWithUtente(@Param("filmId") Long filmId);

    Optional<Recensione> findByFilmAndUtente(Film film, User utente);
    boolean existsByFilmAndUtente(Film film, User utente);
}
