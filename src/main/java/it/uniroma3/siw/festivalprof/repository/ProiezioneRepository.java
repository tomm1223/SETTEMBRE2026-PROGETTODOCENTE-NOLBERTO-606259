package it.uniroma3.siw.festivalprof.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.model.Sala;

public interface ProiezioneRepository extends CrudRepository<Proiezione, Long> {
    List<Proiezione> findAll();
    List<Proiezione> findByFestival(Festival festival);
    List<Proiezione> findByFilm(Film film);
    List<Proiezione> findBySala(Sala sala);
    List<Proiezione> findByData(LocalDate data);

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala JOIN FETCH p.festival WHERE p.festival.id = :festivalId ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findByFestivalIdWithDetails(@Param("festivalId") Long festivalId);

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala JOIN FETCH p.festival WHERE p.film.id = :filmId ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findByFilmIdWithDetails(@Param("filmId") Long filmId);

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala JOIN FETCH p.festival ORDER BY p.data ASC, p.ora ASC")
    List<Proiezione> findAllWithDetails();

    // Query per verificare proiezioni esistenti nella stessa sala e nello stesso giorno (escludendo quelle annullate)
    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film WHERE p.sala = :sala AND p.data = :data AND p.stato != 'CANCELLED' AND (:id IS NULL OR p.id != :id)")
    List<Proiezione> findProiezioniInSalaEData(@Param("sala") Sala sala, @Param("data") LocalDate data, @Param("id") Long id);

    @Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala JOIN FETCH p.festival WHERE p.id = :id")
    Optional<Proiezione> findByIdWithDetails(@Param("id") Long id);
}
