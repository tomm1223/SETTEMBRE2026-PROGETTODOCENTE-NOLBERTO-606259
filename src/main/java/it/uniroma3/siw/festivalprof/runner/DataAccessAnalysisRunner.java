package it.uniroma3.siw.festivalprof.runner;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.repository.FestivalRepository;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class DataAccessAnalysisRunner {

    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DataAccessAnalysisRunner(FestivalRepository festivalRepository, FilmRepository filmRepository) {
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
    }

    /**
     * Esegue il benchmark confrontando l'accesso LAZY (che soffre del problema N+1)
     * rispetto a JOIN FETCH (1 sola query SQL ottimizzata).
     */
    @Transactional(readOnly = true)
    public String eseguiConfrontoFetch(Long festivalId) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n===========================================================\n");
        sb.append("=== ANALISI SPERIMENTALE ACCESSO AI DATI (JPA / HIBERNATE) ===\n");
        sb.append("Caso d'uso: Caricamento film di un festival e dei relativi registi\n");
        sb.append("===========================================================\n");

        Festival festival = festivalRepository.findById(festivalId).orElse(null);
        if (festival == null) {
            return "Festival non trovato!";
        }

        // --- STRATEGIA 1: Accesso tramite associazioni LAZY (Problema N+1) ---
        entityManager.clear(); // Pulizia della cache di primo livello (Persistence Context)
        long startLazy = System.currentTimeMillis();
        
        List<Film> filmLazy = filmRepository.findFilmsByFestivalIdLazy(festivalId);
        int queryLazyCount = 1; // 1 query per i film
        for (Film f : filmLazy) {
            // L'accesso al regista di ciascun film scatena una query SQL aggiuntiva (Lazy Loading)
            if (f.getRegista() != null) {
                String nomeRegista = f.getRegista().getNomeCompleto();
                queryLazyCount++;
            }
        }
        long endLazy = System.currentTimeMillis();
        long tempoLazy = endLazy - startLazy;

        sb.append("\nStrategia 1: LAZY (Iterazione e Lazy Initialization - N+1 Query)\n");
        sb.append("Film caricati: ").append(filmLazy.size()).append("\n");
        sb.append("Query SQL stimate: ").append(queryLazyCount).append("\n");
        sb.append("Tempo di esecuzione: ").append(tempoLazy).append(" ms\n");

        // --- STRATEGIA 2: Accesso tramite JOIN FETCH (1 Query Ottimizzata) ---
        entityManager.clear(); // Pulizia cache di primo livello
        long startFetch = System.currentTimeMillis();

        List<Film> filmFetch = filmRepository.findFilmsByFestivalIdWithRegistaFetch(festivalId);
        for (Film f : filmFetch) {
            if (f.getRegista() != null) {
                String nomeRegista = f.getRegista().getNomeCompleto();
            }
        }
        long endFetch = System.currentTimeMillis();
        long tempoFetch = endFetch - startFetch;

        sb.append("\nStrategia 2: JOIN FETCH (Query con JOIN FETCH dei Registi)\n");
        sb.append("Film caricati: ").append(filmFetch.size()).append("\n");
        sb.append("Query SQL stimate: 1\n");
        sb.append("Tempo di esecuzione: ").append(tempoFetch).append(" ms\n");

        sb.append("===========================================================\n");
        
        System.out.println(sb.toString());
        return sb.toString();
    }
}
