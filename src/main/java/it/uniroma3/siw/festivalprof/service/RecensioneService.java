package it.uniroma3.siw.festivalprof.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Recensione;
import it.uniroma3.siw.festivalprof.model.User;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;
import it.uniroma3.siw.festivalprof.repository.RecensioneRepository;
import it.uniroma3.siw.festivalprof.repository.UserRepository;

@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public RecensioneService(RecensioneRepository recensioneRepository,
                             FilmRepository filmRepository,
                             UserRepository userRepository) {
        this.recensioneRepository = recensioneRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Recensione> getRecensioniByFilm(Film film) {
        return this.recensioneRepository.findByFilmIdWithUtente(film.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Recensione> findById(Long id) {
        return this.recensioneRepository.findById(id);
    }

    @Transactional
    public Recensione salvaRecensione(Recensione recensione, User utente, Film film) throws Exception {
        // Verifica se l'utente ha già recensito il film
        if (recensioneRepository.existsByFilmAndUtente(film, utente)) {
            throw new IllegalStateException("Hai già inserito una recensione per questo film!");
        }

        recensione.setUtente(utente);
        recensione.setFilm(film);
        recensione.setData(LocalDateTime.now());

        return recensioneRepository.save(recensione);
    }

    @Transactional
    public Recensione modificaRecensione(Long recensioneId, String nuovoTitolo, String nuovoTesto, Integer nuovoVoto, User utente) throws Exception {
        Recensione existing = recensioneRepository.findById(recensioneId)
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata"));

        // Verifica autorizzazione: solo l'autore può modificare
        if (!existing.getUtente().getId().equals(utente.getId())) {
            throw new SecurityException("Non sei autorizzato a modificare questa recensione!");
        }

        existing.setTitolo(nuovoTitolo);
        existing.setTesto(nuovoTesto);
        existing.setVoto(nuovoVoto);
        existing.setData(LocalDateTime.now());

        return recensioneRepository.save(existing);
    }

    @Transactional
    public void eliminaRecensione(Long recensioneId, User utente) throws Exception {
        Recensione existing = recensioneRepository.findById(recensioneId)
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata"));

        // Verifica autorizzazione: solo l'autore o un admin (gestito a livello controller/security) può eliminare
        if (!existing.getUtente().getId().equals(utente.getId())) {
            throw new SecurityException("Non sei autorizzato a eliminare questa recensione!");
        }

        recensioneRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public boolean haGiaRecensito(Film film, User utente) {
        if (film == null || utente == null) return false;
        return recensioneRepository.existsByFilmAndUtente(film, utente);
    }
}
