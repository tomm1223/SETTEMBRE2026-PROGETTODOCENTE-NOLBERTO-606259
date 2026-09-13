package it.uniroma3.siw.festivalprof.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;

@RestController
@RequestMapping("/api/screenings")
public class ProiezioneRestController {

    private final ProiezioneService proiezioneService;

    public ProiezioneRestController(ProiezioneService proiezioneService) {
        this.proiezioneService = proiezioneService;
    }

    @GetMapping
    public ResponseEntity<List<ScreeningFullDTO>> getScreenings(
            @RequestParam(value = "festivalId", required = false) Long festivalId,
            @RequestParam(value = "filmId", required = false) Long filmId) {

        List<Proiezione> proiezioni;
        if (festivalId != null) {
            proiezioni = proiezioneService.findByFestivalId(festivalId);
        } else if (filmId != null) {
            proiezioni = proiezioneService.findByFilmId(filmId);
        } else {
            proiezioni = proiezioneService.findAllWithDetails();
        }

        List<ScreeningFullDTO> response = proiezioni.stream()
                .map(p -> new ScreeningFullDTO(
                        p.getId(),
                        p.getFestival().getNome(),
                        p.getFilm().getTitolo(),
                        p.getSala().getNome(),
                        p.getSala().getIndirizzo(),
                        p.getData().toString(),
                        p.getOra().toString(),
                        p.getStato().getLabel()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    public static class ScreeningFullDTO {
        public Long id;
        public String festivalNome;
        public String filmTitolo;
        public String salaNome;
        public String salaIndirizzo;
        public String data;
        public String ora;
        public String stato;

        public ScreeningFullDTO(Long id, String festivalNome, String filmTitolo, String salaNome, String salaIndirizzo, String data, String ora, String stato) {
            this.id = id;
            this.festivalNome = festivalNome;
            this.filmTitolo = filmTitolo;
            this.salaNome = salaNome;
            this.salaIndirizzo = salaIndirizzo;
            this.data = data;
            this.ora = ora;
            this.stato = stato;
        }
    }
}
