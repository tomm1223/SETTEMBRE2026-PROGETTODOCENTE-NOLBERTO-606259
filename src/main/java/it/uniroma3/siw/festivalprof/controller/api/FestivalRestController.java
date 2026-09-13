package it.uniroma3.siw.festivalprof.controller.api;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.service.FestivalService;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;

@RestController
@RequestMapping("/api/festivals")
public class FestivalRestController {

    private final FestivalService festivalService;
    private final FilmService filmService;
    private final ProiezioneService proiezioneService;

    public FestivalRestController(FestivalService festivalService, FilmService filmService, ProiezioneService proiezioneService) {
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.proiezioneService = proiezioneService;
    }

    @GetMapping
    public ResponseEntity<List<FestivalDTO>> getAllFestivals() {
        List<Festival> festivals = festivalService.findAll();
        List<FestivalDTO> response = festivals.stream()
                .map(f -> new FestivalDTO(f.getId(), f.getNome(), f.getAnno(), f.getCitta(), f.getDataInizio(), f.getDataFine(), f.getDescrizione()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FestivalDTO> getFestivalById(@PathVariable("id") Long id) {
        Festival f = festivalService.findById(id).orElse(null);
        if (f == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new FestivalDTO(f.getId(), f.getNome(), f.getAnno(), f.getCitta(), f.getDataInizio(), f.getDataFine(), f.getDescrizione()));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieSummaryDTO>> getFestivalMovies(@PathVariable("id") Long id) {
        Festival f = festivalService.findByIdWithFilmsAndRegisti(id).orElse(null);
        if (f == null) return ResponseEntity.notFound().build();

        List<MovieSummaryDTO> response = f.getFilms().stream()
                .map(m -> new MovieSummaryDTO(m.getId(), m.getTitolo(), m.getAnno(), m.getDurata(), m.getGenere(),
                        m.getRegista() != null ? m.getRegista().getNomeCompleto() : "N/D"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/screenings")
    public ResponseEntity<List<ScreeningDTO>> getFestivalScreenings(@PathVariable("id") Long id) {
        List<Proiezione> proiezioni = proiezioneService.findByFestivalId(id);
        List<ScreeningDTO> response = proiezioni.stream()
                .map(p -> new ScreeningDTO(p.getId(), p.getData().toString(), p.getOra().toString(), p.getStato().getLabel(),
                        p.getFilm().getTitolo(), p.getSala().getNome(), p.getSala().getIndirizzo()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public static class FestivalDTO {
        public Long id;
        public String nome;
        public Integer anno;
        public String citta;
        public LocalDate dataInizio;
        public LocalDate dataFine;
        public String descrizione;

        public FestivalDTO(Long id, String nome, Integer anno, String citta, LocalDate dataInizio, LocalDate dataFine, String descrizione) {
            this.id = id;
            this.nome = nome;
            this.anno = anno;
            this.citta = citta;
            this.dataInizio = dataInizio;
            this.dataFine = dataFine;
            this.descrizione = descrizione;
        }
    }

    public static class MovieSummaryDTO {
        public Long id;
        public String titolo;
        public Integer anno;
        public Integer durata;
        public String genere;
        public String regista;

        public MovieSummaryDTO(Long id, String titolo, Integer anno, Integer durata, String genere, String regista) {
            this.id = id;
            this.titolo = titolo;
            this.anno = anno;
            this.durata = durata;
            this.genere = genere;
            this.regista = regista;
        }
    }

    public static class ScreeningDTO {
        public Long id;
        public String data;
        public String ora;
        public String stato;
        public String filmTitolo;
        public String salaNome;
        public String salaIndirizzo;

        public ScreeningDTO(Long id, String data, String ora, String stato, String filmTitolo, String salaNome, String salaIndirizzo) {
            this.id = id;
            this.data = data;
            this.ora = ora;
            this.stato = stato;
            this.filmTitolo = filmTitolo;
            this.salaNome = salaNome;
            this.salaIndirizzo = salaIndirizzo;
        }
    }
}
