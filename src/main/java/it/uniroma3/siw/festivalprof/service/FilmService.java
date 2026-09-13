package it.uniroma3.siw.festivalprof.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Transactional(readOnly = true)
    public List<Film> findAll() {
        return this.filmRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Film> findById(Long id) {
        return this.filmRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Film> findByIdWithDetails(Long id) {
        return this.filmRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<Film> findByFestivalId(Long festivalId) {
        return this.filmRepository.findFilmsByFestivalIdWithRegistaFetch(festivalId);
    }

    @Transactional(readOnly = true)
    public List<Film> findByRegista(Regista regista) {
        return this.filmRepository.findByRegista(regista);
    }

    @Transactional
    public Film save(Film film) {
        return this.filmRepository.save(film);
    }

    @Transactional
    public void deleteById(Long id) {
        this.filmRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByTitoloAndAnno(String titolo, Integer anno) {
        return this.filmRepository.existsByTitoloAndAnno(titolo, anno);
    }
}
