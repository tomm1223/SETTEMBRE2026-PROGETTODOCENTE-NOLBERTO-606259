package it.uniroma3.siw.festivalprof.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Regista;

public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findAll();
    List<Film> findByRegista(Regista regista);
    boolean existsByTitoloAndAnno(String titolo, Integer anno);

    // Query standard per trovare film di un festival (senza fetch join -> causa N+1 sui registi se usati)
    @Query("SELECT DISTINCT f FROM Film f JOIN f.festivals fest WHERE fest.id = :festivalId")
    List<Film> findFilmsByFestivalIdLazy(@Param("festivalId") Long festivalId);

    // Query con JOIN FETCH per caricare in una sola SQL query i film con il loro regista
    @Query("SELECT DISTINCT f FROM Film f LEFT JOIN FETCH f.regista JOIN f.festivals fest WHERE fest.id = :festivalId")
    List<Film> findFilmsByFestivalIdWithRegistaFetch(@Param("festivalId") Long festivalId);

    // Query con EntityGraph
    @EntityGraph(attributePaths = {"regista"})
    @Query("SELECT DISTINCT f FROM Film f JOIN f.festivals fest WHERE fest.id = :festivalId")
    List<Film> findFilmsByFestivalIdWithEntityGraph(@Param("festivalId") Long festivalId);

    @Query("SELECT DISTINCT f FROM Film f LEFT JOIN FETCH f.regista LEFT JOIN FETCH f.festivals LEFT JOIN FETCH f.proiezioni WHERE f.id = :id")
    Optional<Film> findByIdWithDetails(@Param("id") Long id);
}
