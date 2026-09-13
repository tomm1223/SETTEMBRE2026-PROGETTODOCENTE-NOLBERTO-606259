package it.uniroma3.siw.festivalprof.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.repository.FestivalRepository;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;

@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;

    public FestivalService(FestivalRepository festivalRepository, FilmRepository filmRepository) {
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
    }

    @Transactional(readOnly = true)
    public List<Festival> findAll() {
        return this.festivalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Festival> findById(Long id) {
        return this.festivalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Festival> findByIdWithFilmsAndRegisti(Long id) {
        return this.festivalRepository.findByIdWithFilmsAndRegisti(id);
    }

    @Transactional(readOnly = true)
    public Optional<Festival> findByIdWithProiezioniDetails(Long id) {
        return this.festivalRepository.findByIdWithProiezioniDetails(id);
    }

    @Transactional
    public Festival save(Festival festival) {
        return this.festivalRepository.save(festival);
    }

    @Transactional
    public void deleteById(Long id) {
        this.festivalRepository.deleteById(id);
    }

    @Transactional
    public boolean addFilmToFestival(Long festivalId, Long filmId) {
        Optional<Festival> festOpt = this.festivalRepository.findById(festivalId);
        Optional<Film> filmOpt = this.filmRepository.findById(filmId);

        if (festOpt.isPresent() && filmOpt.isPresent()) {
            Festival festival = festOpt.get();
            Film film = filmOpt.get();
            festival.getFilms().add(film);      //aggiorna l'owner  
            film.getFestivals().add(festival);  //mantiene la coerenza in memoria
            this.festivalRepository.save(festival);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean removeFilmFromFestival(Long festivalId, Long filmId) {
        Optional<Festival> festOpt = this.festivalRepository.findById(festivalId);
        Optional<Film> filmOpt = this.filmRepository.findById(filmId);

        if (festOpt.isPresent() && filmOpt.isPresent()) {
            Festival festival = festOpt.get();
            Film film = filmOpt.get();
            festival.getFilms().remove(film);
            film.getFestivals().remove(festival);
            this.festivalRepository.save(festival);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean existsByNomeAndAnno(String nome, Integer anno) {
        return this.festivalRepository.existsByNomeAndAnno(nome, anno);
    }
}
