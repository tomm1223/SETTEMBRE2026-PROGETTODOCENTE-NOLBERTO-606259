package it.uniroma3.siw.festivalprof.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festivalprof.model.Festival;

public interface FestivalRepository extends CrudRepository<Festival, Long> {
    List<Festival> findAll();
    boolean existsByNomeAndAnno(String nome, Integer anno);

    // Query per recuperare un festival con i suoi film e relativi registi (JOIN FETCH)
    @Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.films m LEFT JOIN FETCH m.regista WHERE f.id = :id")
    Optional<Festival> findByIdWithFilmsAndRegisti(@Param("id") Long id);

    // Query per recuperare un festival con le proiezioni, film e sale (JOIN FETCH)
    @Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.proiezioni p LEFT JOIN FETCH p.film LEFT JOIN FETCH p.sala WHERE f.id = :id")
    Optional<Festival> findByIdWithProiezioniDetails(@Param("id") Long id);

    // Esempio con EntityGraph per caricare un festival con film e registi
    @EntityGraph(attributePaths = {"films", "films.regista"})
    @Query("SELECT f FROM Festival f WHERE f.id = :id")
    Optional<Festival> findByIdWithEntityGraph(@Param("id") Long id);
}
