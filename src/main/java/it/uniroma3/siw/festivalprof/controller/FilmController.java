package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;
import it.uniroma3.siw.festivalprof.service.RecensioneService;
import it.uniroma3.siw.festivalprof.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class FilmController {

    private final FilmService filmService;
    private final RegistaService registaService;
    private final ProiezioneService proiezioneService;
    private final RecensioneService recensioneService;

    public FilmController(FilmService filmService, RegistaService registaService,
                          ProiezioneService proiezioneService, RecensioneService recensioneService) {
        this.filmService = filmService;
        this.registaService = registaService;
        this.proiezioneService = proiezioneService;
        this.recensioneService = recensioneService;
    }

    @GetMapping("/movies")
    public String getMovies(Model model) {
        model.addAttribute("films", filmService.findAll());
        return "movies/listMovies";
    }

    @GetMapping("/movies/{id}")
    public String getMovie(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findByIdWithDetails(id).orElse(filmService.findById(id).orElse(null));
        if (film == null) {
            return "redirect:/movies";
        }

        model.addAttribute("film", film);
        model.addAttribute("proiezioni", proiezioneService.findByFilmId(id));
        model.addAttribute("recensioni", recensioneService.getRecensioniByFilm(film));
        return "movies/showMovie";
    }

    @GetMapping("/admin/movies/new")
    public String showFormNewMovie(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("registi", registaService.findAll());
        return "movies/formMovie";
    }

    @PostMapping("/admin/movies/new")
    public String newMovie(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult,
                           @RequestParam(value = "registaId", required = false) Long registaId, Model model) {

        if (filmService.existsByTitoloAndAnno(film.getTitolo(), film.getAnno())) {
            bindingResult.rejectValue("titolo", "duplicate", "Esiste già un film con questo titolo e anno di produzione.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", registaService.findAll());
            return "movies/formMovie";
        }

        if (registaId != null) {
            Regista r = registaService.findById(registaId).orElse(null);
            film.setRegista(r);
        }

        filmService.save(film);
        return "redirect:/movies/" + film.getId();
    }

    @GetMapping("/admin/movies/{id}/edit")
    public String showFormEditMovie(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findById(id).orElse(null);
        if (film == null) return "redirect:/movies";

        model.addAttribute("film", film);
        model.addAttribute("registi", registaService.findAll());
        return "movies/formMovie";
    }

    @PostMapping("/admin/movies/{id}/edit")
    public String editMovie(@PathVariable("id") Long id, @Valid @ModelAttribute("film") Film film, BindingResult bindingResult,
                            @RequestParam(value = "registaId", required = false) Long registaId, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", registaService.findAll());
            return "movies/formMovie";
        }

        film.setId(id);
        if (registaId != null) {
            Regista r = registaService.findById(registaId).orElse(null);
            film.setRegista(r);
        }

        filmService.save(film);
        return "redirect:/movies/" + id;
    }

    @PostMapping("/admin/movies/{id}/delete")
    public String deleteMovie(@PathVariable("id") Long id) {
        filmService.deleteById(id);
        return "redirect:/movies";
    }
}
