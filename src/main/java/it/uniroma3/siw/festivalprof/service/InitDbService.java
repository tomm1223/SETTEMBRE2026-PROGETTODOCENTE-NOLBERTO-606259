package it.uniroma3.siw.festivalprof.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Credentials;
import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.model.Recensione;
import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.model.Sala;
import it.uniroma3.siw.festivalprof.model.StatoProiezione;
import it.uniroma3.siw.festivalprof.model.User;
import it.uniroma3.siw.festivalprof.repository.CredentialsRepository;
import it.uniroma3.siw.festivalprof.repository.FestivalRepository;
import it.uniroma3.siw.festivalprof.repository.FilmRepository;
import it.uniroma3.siw.festivalprof.repository.ProiezioneRepository;
import it.uniroma3.siw.festivalprof.repository.RecensioneRepository;
import it.uniroma3.siw.festivalprof.repository.RegistaRepository;
import it.uniroma3.siw.festivalprof.repository.SalaRepository;

@Component
public class InitDbService implements CommandLineRunner {

    private final CredentialsService credentialsService;
    private final CredentialsRepository credentialsRepository;
    private final RegistaRepository registaRepository;
    private final FilmRepository filmRepository;
    private final SalaRepository salaRepository;
    private final FestivalRepository festivalRepository;
    private final ProiezioneRepository proiezioneRepository;
    private final RecensioneRepository recensioneRepository;

    public InitDbService(CredentialsService credentialsService,
                         CredentialsRepository credentialsRepository,
                         RegistaRepository registaRepository,
                         FilmRepository filmRepository,
                         SalaRepository salaRepository,
                         FestivalRepository festivalRepository,
                         ProiezioneRepository proiezioneRepository,
                         RecensioneRepository recensioneRepository) {
        this.credentialsService = credentialsService;
        this.credentialsRepository = credentialsRepository;
        this.registaRepository = registaRepository;
        this.filmRepository = filmRepository;
        this.salaRepository = salaRepository;
        this.festivalRepository = festivalRepository;
        this.proiezioneRepository = proiezioneRepository;
        this.recensioneRepository = recensioneRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Se i dati sono già presenti, evita la reinserimento
        if (credentialsRepository.count() > 0) {
            return;
        }

        System.out.println(">>> Popolamento del database iniziale con dati di prova...");

        // 1. Creazione Utenti e Credenziali
        User adminUser = new User("Mario", "Rossi", "admin@festival.it", "admin");
        Credentials adminCreds = new Credentials("admin", "admin", Credentials.ADMIN_ROLE);
        adminCreds.setUser(adminUser);
        credentialsService.saveCredentials(adminCreds);

        User normalUser = new User("Giuseppe", "Verdi", "user@festival.it", "user");
        Credentials normalCreds = new Credentials("user", "user", Credentials.DEFAULT_ROLE);
        normalCreds.setUser(normalUser);
        credentialsService.saveCredentials(normalCreds);

        User user2 = new User("Laura", "Bianchi", "laura@festival.it", "laura");
        Credentials user2Creds = new Credentials("laura", "laura", Credentials.DEFAULT_ROLE);
        user2Creds.setUser(user2);
        credentialsService.saveCredentials(user2Creds);

        // 2. Creazione Registi
        Regista r1 = registaRepository.save(new Regista("Christopher", "Nolan", LocalDate.of(1970, 7, 30), "Regno Unito"));
        Regista r2 = registaRepository.save(new Regista("Paolo", "Sorrentino", LocalDate.of(1970, 5, 31), "Italia"));
        Regista r3 = registaRepository.save(new Regista("Quentin", "Tarantino", LocalDate.of(1963, 3, 27), "Stati Uniti"));
        Regista r4 = registaRepository.save(new Regista("Hayao", "Miyazaki", LocalDate.of(1941, 1, 5), "Giappone"));

        // 3. Creazione Sale Cinematografiche
        Sala s1 = salaRepository.save(new Sala("Sala Grande Lido", "Lungomare Marconi 30, Venezia", 1000));
        Sala s2 = salaRepository.save(new Sala("Palais des Festivals - Debussy", "Boulevard de la Croisette, Cannes", 800));
        Sala s3 = salaRepository.save(new Sala("Auditorium Parco della Musica - Sala Petrassi", "Viale Pietro de Coubertin 30, Roma", 500));
        Sala s4 = salaRepository.save(new Sala("Cinema Astra", "Via Corrado Segre 4, Roma", 250));

        // 4. Creazione Film
        Film f1 = new Film("Oppenheimer", 2023, 180, "Biografico / Drammatico", "Stati Uniti");
        f1.setRegista(r1);
        f1 = filmRepository.save(f1);

        Film f2 = new Film("La Grande Bellezza", 2013, 142, "Drammatico", "Italia");
        f2.setRegista(r2);
        f2 = filmRepository.save(f2);

        Film f3 = new Film("Pulp Fiction", 1994, 154, "Crime / Thriller", "Stati Uniti");
        f3.setRegista(r3);
        f3 = filmRepository.save(f3);

        Film f4 = new Film("Il Ragazzo e l'Airone", 2023, 124, "Animazione / Fantastico", "Giappone");
        f4.setRegista(r4);
        f4 = filmRepository.save(f4);

        Film f5 = new Film("È stata la mano di Dio", 2021, 130, "Drammatico / Autobiografico", "Italia");
        f5.setRegista(r2);
        f5 = filmRepository.save(f5);

        // 5. Creazione Festival
        Festival fest1 = new Festival("Mostra Internazionale d'Arte Cinematografica di Venezia", 2026, "Venezia",
                LocalDate.of(2026, 8, 28), LocalDate.of(2026, 9, 7),
                "La prestigiosa mostra del cinema che si tiene ogni anno al Lido di Venezia.");
        fest1.getFilms().add(f1);
        fest1.getFilms().add(f2);
        fest1.getFilms().add(f5);
        fest1 = festivalRepository.save(fest1);

        Festival fest2 = new Festival("Festa del Cinema di Roma", 2026, "Roma",
                LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 25),
                "Celebrazione del cinema internazionale presso l'Auditorium Parco della Musica di Roma.");
        fest2.getFilms().add(f3);
        fest2.getFilms().add(f4);
        fest2.getFilms().add(f1);
        fest2 = festivalRepository.save(fest2);

        // 6. Creazione Proiezioni
        Proiezione p1 = proiezioneRepository.save(new Proiezione(LocalDate.of(2026, 8, 29), LocalTime.of(16, 0), StatoProiezione.SCHEDULED, fest1, f1, s1));
        Proiezione p2 = proiezioneRepository.save(new Proiezione(LocalDate.of(2026, 8, 30), LocalTime.of(20, 30), StatoProiezione.SCHEDULED, fest1, f2, s1));
        Proiezione p3 = proiezioneRepository.save(new Proiezione(LocalDate.of(2026, 10, 16), LocalTime.of(18, 0), StatoProiezione.SCHEDULED, fest2, f3, s3));
        Proiezione p4 = proiezioneRepository.save(new Proiezione(LocalDate.of(2026, 10, 17), LocalTime.of(21, 0), StatoProiezione.SCHEDULED, fest2, f4, s4));

        // 7. Creazione Recensioni
        recensioneRepository.save(new Recensione("Capolavoro assoluto!", "Un'opera straordinaria, regia magistrale e interpretazioni monumentali.", 5, LocalDateTime.now().minusDays(2), f1, normalUser));
        recensioneRepository.save(new Recensione("Poetico ed elegante", "Roma e la vita descritte con la consueta sensibilità di Sorrentino.", 4, LocalDateTime.now().minusDays(1), f2, user2));
        recensioneRepository.save(new Recensione("Un cult intramontabile", "Sceneggiatura geniale e colonna sonora indimenticabile.", 5, LocalDateTime.now(), f3, normalUser));

        System.out.println(">>> Database inizializzato con successo con Utenti, Festival, Film, Registi, Sale e Proiezioni!");
    }
}
