package it.uniroma3.siw.festivalprof.controller.api;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Recensione;
import it.uniroma3.siw.festivalprof.model.User;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.RecensioneService;
import it.uniroma3.siw.festivalprof.service.UserService;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private final FilmService filmService;
    private final RecensioneService recensioneService;
    private final UserService userService;

    public MovieRestController(FilmService filmService, RecensioneService recensioneService, UserService userService) {
        this.filmService = filmService;
        this.recensioneService = recensioneService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable("id") Long id) {
        Film f = filmService.findByIdWithDetails(id).orElse(null);
        if (f == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new MovieDTO(
                f.getId(), f.getTitolo(), f.getAnno(), f.getDurata(), f.getGenere(), f.getPaeseProduzione(),
                f.getRegista() != null ? f.getRegista().getNomeCompleto() : "N/D",
                f.getMediaVoti()
        ));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getMovieReviews(@PathVariable("id") Long filmId) {
        Film film = filmService.findById(filmId).orElse(null);
        if (film == null) return ResponseEntity.notFound().build();

        List<Recensione> reviews = recensioneService.getRecensioniByFilm(film);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<ReviewDTO> response = reviews.stream()
                .map(r -> new ReviewDTO(
                        r.getId(),
                        r.getTitolo(),
                        r.getTesto(),
                        r.getVoto(),
                        r.getUtente() != null ? r.getUtente().getUsername() : "Anonimo",
                        r.getData() != null ? r.getData().format(formatter) : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable("id") Long filmId, @RequestBody ReviewRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Devi effettuare il login per inserire una recensione.");
        }

        Film film = filmService.findById(filmId).orElse(null);
        User user = userService.findByUsername(principal.getName());

        if (film == null || user == null) {
            return ResponseEntity.badRequest().body("Dati del film o dell'utente non validi.");
        }

        try {
            Recensione newRec = new Recensione();
            newRec.setTitolo(request.titolo);
            newRec.setTesto(request.testo);
            newRec.setVoto(request.voto);

            Recensione saved = recensioneService.salvaRecensione(newRec, user, film);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            ReviewDTO response = new ReviewDTO(
                    saved.getId(), saved.getTitolo(), saved.getTesto(), saved.getVoto(),
                    user.getUsername(), saved.getData().format(formatter)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    public static class MovieDTO {
        public Long id;
        public String titolo;
        public Integer anno;
        public Integer durata;
        public String genere;
        public String paeseProduzione;
        public String regista;
        public Double mediaVoti;

        public MovieDTO(Long id, String titolo, Integer anno, Integer durata, String genere, String paeseProduzione, String regista, Double mediaVoti) {
            this.id = id;
            this.titolo = titolo;
            this.anno = anno;
            this.durata = durata;
            this.genere = genere;
            this.paeseProduzione = paeseProduzione;
            this.regista = regista;
            this.mediaVoti = mediaVoti;
        }
    }

    public static class ReviewDTO {
        public Long id;
        public String titolo;
        public String testo;
        public Integer voto;
        public String autore;
        public String data;

        public ReviewDTO(Long id, String titolo, String testo, Integer voto, String autore, String data) {
            this.id = id;
            this.titolo = titolo;
            this.testo = testo;
            this.voto = voto;
            this.autore = autore;
            this.data = data;
        }
    }

    public static class ReviewRequest {
        public String titolo;
        public String testo;
        public Integer voto;
    }
}
