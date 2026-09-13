package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalprof.model.Festival;
import it.uniroma3.siw.festivalprof.service.FestivalService;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

    private final FestivalService festivalService;
    private final FilmService filmService;
    private final ProiezioneService proiezioneService;

    public FestivalController(FestivalService festivalService, FilmService filmService, ProiezioneService proiezioneService) {
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.proiezioneService = proiezioneService;
    }

    @GetMapping("/festivals")
    public String getFestivals(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        return "festivals/listFestivals";
    }

    @GetMapping("/festivals/{id}")
    public String getFestival(@PathVariable("id") Long id, Model model) {
        Festival festival = festivalService.findByIdWithProiezioniDetails(id)
                .orElse(festivalService.findById(id).orElse(null));

        if (festival == null) {
            return "redirect:/festivals";
        }

        model.addAttribute("festival", festival);
        model.addAttribute("allFilms", filmService.findAll());
        model.addAttribute("proiezioni", proiezioneService.findByFestivalId(id));
        return "festivals/showFestival";
    }

    @GetMapping("/admin/festivals/new")
    public String showFormNewFestival(Model model) {
        model.addAttribute("festival", new Festival());
        return "festivals/formFestival";
    }

    @PostMapping("/admin/festivals/new")
    public String newFestival(@Valid @ModelAttribute("festival") Festival festival, BindingResult bindingResult, Model model) {
        if (festivalService.existsByNomeAndAnno(festival.getNome(), festival.getAnno())) {
            bindingResult.rejectValue("nome", "duplicate", "Esiste già un festival con questo nome e anno.");
        }

        if (festival.getDataInizio() != null && festival.getDataFine() != null && festival.getDataFine().isBefore(festival.getDataInizio())) {
            bindingResult.rejectValue("dataFine", "invalid", "La data di fine non può essere precedente alla data d'inizio.");
        }

        if (bindingResult.hasErrors()) {
            return "festivals/formFestival";
        }

        festivalService.save(festival);
        return "redirect:/festivals/" + festival.getId();
    }

    @GetMapping("/admin/festivals/{id}/edit")
    public String showFormEditFestival(@PathVariable("id") Long id, Model model) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null) return "redirect:/festivals";
        model.addAttribute("festival", festival);
        return "festivals/formFestival";
    }

    @PostMapping("/admin/festivals/{id}/edit")
    public String editFestival(@PathVariable("id") Long id, @Valid @ModelAttribute("festival") Festival festival, BindingResult bindingResult) {
        if (festival.getDataInizio() != null && festival.getDataFine() != null && festival.getDataFine().isBefore(festival.getDataInizio())) {
            bindingResult.rejectValue("dataFine", "invalid", "La data di fine non può essere precedente alla data d'inizio.");
        }

        if (bindingResult.hasErrors()) {
            return "festivals/formFestival";
        }

        festival.setId(id);
        festivalService.save(festival);
        return "redirect:/festivals/" + id;
    }

    @PostMapping("/admin/festivals/{id}/delete")
    public String deleteFestival(@PathVariable("id") Long id) {
        festivalService.deleteById(id);
        return "redirect:/festivals";
    }

    @PostMapping("/admin/festivals/{id}/add-film")
    public String addFilmToFestival(@PathVariable("id") Long festivalId, @RequestParam("filmId") Long filmId) {
        festivalService.addFilmToFestival(festivalId, filmId);
        return "redirect:/festivals/" + festivalId;
    }

    @PostMapping("/admin/festivals/{id}/remove-film/{filmId}")
    public String removeFilmFromFestival(@PathVariable("id") Long festivalId, @PathVariable("filmId") Long filmId) {
        festivalService.removeFilmFromFestival(festivalId, filmId);
        return "redirect:/festivals/" + festivalId;
    }
}
