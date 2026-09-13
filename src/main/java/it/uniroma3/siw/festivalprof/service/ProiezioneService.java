package it.uniroma3.siw.festivalprof.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.model.Sala;
import it.uniroma3.siw.festivalprof.model.StatoProiezione;
import it.uniroma3.siw.festivalprof.repository.FestivalRepository;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;
import it.uniroma3.siw.festivalprof.repository.ProiezioneRepository;
import it.uniroma3.siw.festivalprof.repository.SalaRepository;

@Service
public class ProiezioneService {

    private final ProiezioneRepository proiezioneRepository;
    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;
    private final SalaRepository salaRepository;

    public ProiezioneService(ProiezioneRepository proiezioneRepository,
                             FestivalRepository festivalRepository,
                             FilmRepository filmRepository,
                             SalaRepository salaRepository) {
        this.proiezioneRepository = proiezioneRepository;
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
        this.salaRepository = salaRepository;
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findAllWithDetails() {
        return this.proiezioneRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Optional<Proiezione> findById(Long id) {
        return this.proiezioneRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFestivalId(Long festivalId) {
        return this.proiezioneRepository.findByFestivalIdWithDetails(festivalId);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFilmId(Long filmId) {
        return this.proiezioneRepository.findByFilmIdWithDetails(filmId);
    }

    /**
     * Operazione Atomica Multi-Entità (Sezione 7 della traccia).
     * Programma una proiezione coordinando Festival, Film e Sala e verificando la disponibilità della Sala.
     */
    @Transactional(rollbackFor = Exception.class)
    public Proiezione programmaProiezione(Long festivalId, Long filmId, Long salaId, LocalDate data, LocalTime ora) throws Exception {
        // 1. Recupero del Festival
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("Festival non trovato con ID: " + festivalId));

        // 2. Recupero del Film
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film non trovato con ID: " + filmId));

        // 3. Recupero della Sala
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala non trovata con ID: " + salaId));

        // Verifica consistenza date festival
        if (data.isBefore(festival.getDataInizio()) || data.isAfter(festival.getDataFine())) {
            throw new IllegalStateException("La data della proiezione (" + data + ") deve essere compresa tra " 
                    + festival.getDataInizio() + " e " + festival.getDataFine() + " (Date del Festival)");
        }

        // 4. Verifica della disponibilità della sala (Assenza di sovrapposizioni temporali)
        if (!isSalaDisponibile(sala, data, ora, film.getDurata(), null)) {
            throw new IllegalStateException("La sala '" + sala.getNome() + "' è già occupata nell'intervallo temporale selezionato!");
        }

        // 5. Creazione della Proiezione
        Proiezione proiezione = new Proiezione();
        proiezione.setFestival(festival);
        proiezione.setFilm(film);
        proiezione.setSala(sala);
        proiezione.setData(data);
        proiezione.setOra(ora);
        proiezione.setStato(StatoProiezione.SCHEDULED);

        // Assicura che il film sia associato al festival se non lo era già
        if (!festival.getFilms().contains(film)) {
            festival.getFilms().add(film);
            film.getFestivals().add(festival);
            festivalRepository.save(festival);
        }

        // 6. Salvataggio atomico della proiezione
        return proiezioneRepository.save(proiezione);
    }

    @Transactional(rollbackFor = Exception.class)
    public Proiezione modificaProiezione(Long id, StatoProiezione nuovoStato, LocalDate nuovaData, LocalTime nuovaOra, Long nuovaSalaId) throws Exception {
        Proiezione p = proiezioneRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Proiezione non trovata con ID: " + id));

        if (nuovaSalaId != null && nuovaData != null && nuovaOra != null) {
            Sala sala = salaRepository.findById(nuovaSalaId)
                    .orElseThrow(() -> new IllegalArgumentException("Sala non trovata con ID: " + nuovaSalaId));

            if (!isSalaDisponibile(sala, nuovaData, nuovaOra, p.getFilm().getDurata(), p.getId())) {
                throw new IllegalStateException("La sala '" + sala.getNome() + "' è già occupata nell'intervallo temporale selezionato!");
            }
            p.setSala(sala);
            p.setData(nuovaData);
            p.setOra(nuovaOra);
        }

        if (nuovoStato != null) {
            p.setStato(nuovoStato);
        }

        return proiezioneRepository.save(p);
    }

    @Transactional
    public void deleteById(Long id) {
        proiezioneRepository.deleteById(id);
    }

    /**
     * Algoritmo di controllo sovrapposizione orari in una sala per una data specifica.
     */
    @Transactional(readOnly = true)
    public boolean isSalaDisponibile(Sala sala, LocalDate data, LocalTime oraInizio, Integer durataMinuti, Long proiezioneIdEsclusa) {
        List<Proiezione> proiezioniGiorno = proiezioneRepository.findProiezioniInSalaEData(sala, data, proiezioneIdEsclusa);
        LocalTime nuovaOraFine = oraInizio.plusMinutes(durataMinuti != null ? durataMinuti : 120);

        for (Proiezione esistente : proiezioniGiorno) {
            LocalTime esistenteInizio = esistente.getOra();
            LocalTime esistenteFine = esistente.getOraFine();

            // Sovrapposizione temporale: nuovaInizio < esistenteFine AND nuovaFine > esistenteInizio
            if (oraInizio.isBefore(esistenteFine) && nuovaOraFine.isAfter(esistenteInizio)) {
                return false;
            }
        }
        return true;
    }
}
