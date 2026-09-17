package it.uniroma3.siw.festivalprof.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.repository.FestivalRepository;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;
import it.uniroma3.siw.festivalprof.repository.RegistaRepository;

@Service
public class FetchBenchmarkService {

    private final FestivalRepository festivalRepository;
    private final FilmRepository filmRepository;
    private final RegistaRepository registaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public FetchBenchmarkService(FestivalRepository festivalRepository,
                                 FilmRepository filmRepository,
                                 RegistaRepository registaRepository) {
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
        this.registaRepository = registaRepository;
    }

    public static class BenchmarkResult {
        private int count;
        private double lazyTimeMs;
        private int lazyQueries;
        private double lazyMemoryMb;
        private double joinFetchTimeMs;
        private int joinFetchQueries;
        private double joinFetchMemoryMb;
        private double entityGraphTimeMs;
        private int entityGraphQueries;
        private double entityGraphMemoryMb;

        public BenchmarkResult(int count, double lazyTimeMs, int lazyQueries, double lazyMemoryMb,
                               double joinFetchTimeMs, int joinFetchQueries, double joinFetchMemoryMb,
                               double entityGraphTimeMs, int entityGraphQueries, double entityGraphMemoryMb) {
            this.count = count;
            this.lazyTimeMs = lazyTimeMs;
            this.lazyQueries = lazyQueries;
            this.lazyMemoryMb = lazyMemoryMb;
            this.joinFetchTimeMs = joinFetchTimeMs;
            this.joinFetchQueries = joinFetchQueries;
            this.joinFetchMemoryMb = joinFetchMemoryMb;
            this.entityGraphTimeMs = entityGraphTimeMs;
            this.entityGraphQueries = entityGraphQueries;
            this.entityGraphMemoryMb = entityGraphMemoryMb;
        }

        public int getCount() { return count; }
        public double getLazyTimeMs() { return lazyTimeMs; }
        public int getLazyQueries() { return lazyQueries; }
        public double getLazyMemoryMb() { return lazyMemoryMb; }
        public double getJoinFetchTimeMs() { return joinFetchTimeMs; }
        public int getJoinFetchQueries() { return joinFetchQueries; }
        public double getJoinFetchMemoryMb() { return joinFetchMemoryMb; }
        public double getEntityGraphTimeMs() { return entityGraphTimeMs; }
        public int getEntityGraphQueries() { return entityGraphQueries; }
        public double getEntityGraphMemoryMb() { return entityGraphMemoryMb; }
    }

    private void prepareCleanMemoryState() {
        entityManager.flush();
        entityManager.clear();
        System.gc();
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {
        }
    }

    @Transactional
    public BenchmarkResult runBenchmark(int count) {
        if (count <= 0) count = 10;
        if (count > 10000) count = 10000;

        Festival testFestival = null;
        List<Long> filmIds = new ArrayList<>();
        List<Long> registaIds = new ArrayList<>();

        try {
            // 1. Creazione Festival Dummy per il Test
            testFestival = new Festival();
            testFestival.setNome("Festival Benchmark " + System.currentTimeMillis());
            testFestival.setAnno(2099);
            testFestival.setCitta("Benchmark City");
            testFestival.setDataInizio(LocalDate.of(2099, 1, 1));
            testFestival.setDataFine(LocalDate.of(2099, 1, 15));
            testFestival.setDescrizione("Festival creato temporaneamente per il benchmark delle strategie di fetch.");
            testFestival = festivalRepository.save(testFestival);

            // 2. Creazione di N Registi e N Film Dummy
            for (int i = 0; i < count; i++) {
                Regista r = new Regista();
                r.setNome("RegistaBench_" + i);
                r.setCognome("Test");
                r.setDataNascita(LocalDate.of(1980, 1, 1));
                r.setNazionalita("Benchmark");
                r = registaRepository.save(r);
                registaIds.add(r.getId());

                Film f = new Film();
                f.setTitolo("Film Benchmark " + i);
                f.setAnno(2099);
                f.setDurata(120);
                f.setGenere("Benchmark");
                f.setPaeseProduzione("Test");
                f.setRegista(r);
                f = filmRepository.save(f);
                filmIds.add(f.getId());

                testFestival.getFilms().add(f);
                f.getFestivals().add(testFestival);
            }

            festivalRepository.save(testFestival);
            Long festivalId = testFestival.getId();

            // =========================================================================
            // MISURAZIONE 1: Accesso LAZY Standard (1 query lista film + N query registi = N+1)
            // =========================================================================
            prepareCleanMemoryState();
            long beforeMemLazy = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startLazy = System.nanoTime();
            int lazyQueryCounter = 0;

            List<Film> lazyFilms = filmRepository.findFilmsByFestivalIdLazy(festivalId);
            lazyQueryCounter++; // 1 query per recuperare la lista dei film

            for (Film f : lazyFilms) {
                if (f != null && f.getRegista() != null) {
                    lazyQueryCounter++; // 1 query LAZY per ciascun regista (N+1)
                    f.getRegista().getNomeCompleto();
                }
            }
            long endLazy = System.nanoTime();
            long afterMemLazy = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double lazyTimeMs = (endLazy - startLazy) / 1_000_000.0;
            double lazyMemoryMb = Math.max(0.01, (afterMemLazy - beforeMemLazy) / (1024.0 * 1024.0));

            // =========================================================================
            // MISURAZIONE 2: JPQL JOIN FETCH (1 SOLA Query SQL per l'intera lista)
            // =========================================================================
            prepareCleanMemoryState();
            long beforeMemJoin = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startJoin = System.nanoTime();
            int joinQueryCounter = 0;

            List<Film> joinFilms = filmRepository.findFilmsByFestivalIdWithRegistaFetch(festivalId);
            joinQueryCounter++; // 1 SOLA query SQL JOIN FETCH

            for (Film f : joinFilms) {
                if (f != null && f.getRegista() != null) {
                    f.getRegista().getNomeCompleto();
                }
            }
            long endJoin = System.nanoTime();
            long afterMemJoin = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double joinFetchTimeMs = (endJoin - startJoin) / 1_000_000.0;
            double joinFetchMemoryMb = Math.max(0.01, (afterMemJoin - beforeMemJoin) / (1024.0 * 1024.0));

            // =========================================================================
            // MISURAZIONE 3: @EntityGraph (1 SOLA Query SQL per l'intera lista)
            // =========================================================================
            prepareCleanMemoryState();
            long beforeMemGraph = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startGraph = System.nanoTime();
            int graphQueryCounter = 0;

            List<Film> graphFilms = filmRepository.findFilmsByFestivalIdWithEntityGraph(festivalId);
            graphQueryCounter++; // 1 SOLA query SQL con EntityGraph

            for (Film f : graphFilms) {
                if (f != null && f.getRegista() != null) {
                    f.getRegista().getNomeCompleto();
                }
            }
            long endGraph = System.nanoTime();
            long afterMemGraph = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double entityGraphTimeMs = (endGraph - startGraph) / 1_000_000.0;
            double entityGraphMemoryMb = Math.max(0.01, (afterMemGraph - beforeMemGraph) / (1024.0 * 1024.0));

            return new BenchmarkResult(count, lazyTimeMs, lazyQueryCounter, lazyMemoryMb,
                                       joinFetchTimeMs, joinQueryCounter, joinFetchMemoryMb,
                                       entityGraphTimeMs, graphQueryCounter, entityGraphMemoryMb);

        } finally {
            // Ripulitura automatica dei dati Dummy per mantenere pulito il DB
            try {
                if (testFestival != null && testFestival.getId() != null) {
                    testFestival.getFilms().clear();
                    festivalRepository.save(testFestival);
                    festivalRepository.deleteById(testFestival.getId());
                }
                if (!filmIds.isEmpty()) {
                    filmRepository.deleteAllById(filmIds);
                }
                if (!registaIds.isEmpty()) {
                    registaRepository.deleteAllById(registaIds);
                }
                entityManager.flush();
                entityManager.clear();
            } catch (Exception e) {
                // Ignora errori di pulizia per preservare il risultato del benchmark
            }
        }
    }

    @Transactional
    public List<BenchmarkResult> runCurveBenchmark(List<Integer> steps) {
        List<BenchmarkResult> results = new ArrayList<>();
        if (steps == null || steps.isEmpty()) {
            steps = List.of(20, 40, 60, 80, 100, 150, 200, 300, 400, 500, 750, 1000, 1500, 2000);
        }

        for (int count : steps) {
            results.add(runBenchmark(count));
        }
        return results;
    }
}
