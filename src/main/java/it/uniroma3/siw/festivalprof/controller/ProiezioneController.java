package it.uniroma3.siw.festivalprof.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalprof.model.Proiezione;
import it.uniroma3.siw.festivalprof.model.StatoProiezione;
import it.uniroma3.siw.festivalprof.service.FestivalService;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;
import it.uniroma3.siw.festivalprof.service.SalaService;

@Controller
public class ProiezioneController {

    private final ProiezioneService proiezioneService;
    private final FestivalService festivalService;
    private final FilmService filmService;
    private final SalaService salaService;

    public ProiezioneController(ProiezioneService proiezioneService, FestivalService festivalService,
                                FilmService filmService, SalaService salaService) {
        this.proiezioneService = proiezioneService;
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.salaService = salaService;
    }

    @GetMapping("/screenings")
    public String getScreenings(Model model) {
        model.addAttribute("proiezioni", proiezioneService.findAllWithDetails());
        model.addAttribute("festivals", festivalService.findAll());
        return "screenings/listScreenings";
    }

    @GetMapping("/admin/screenings/new")
    public String showFormNewScreening(@RequestParam(value = "festivalId", required = false) Long festivalId, Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("selectedFestivalId", festivalId);
        return "screenings/formScreening";
    }

    @PostMapping("/admin/screenings/new")
    public String newScreening(
            @RequestParam("festivalId") Long festivalId,
            @RequestParam("filmId") Long filmId,
            @RequestParam("salaId") Long salaId,
            @RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,
            @RequestParam("ora") @DateTimeFormat(pattern = "HH:mm") LocalTime ora,
            Model model) {

        try {
            // Esecuzione dell'operazione atomica transazionale
            Proiezione p = proiezioneService.programmaProiezione(festivalId, filmId, salaId, data, ora);
            return "redirect:/festivals/" + festivalId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("festivals", festivalService.findAll());
            model.addAttribute("films", filmService.findAll());
            model.addAttribute("sale", salaService.findAll());
            model.addAttribute("selectedFestivalId", festivalId);
            return "screenings/formScreening";
        }
    }

    @GetMapping("/admin/screenings/{id}/edit")
    public String showFormEditScreening(@PathVariable("id") Long id, Model model) {
        Proiezione p = proiezioneService.findById(id).orElse(null);
        if (p == null) return "redirect:/screenings";

        model.addAttribute("proiezione", p);
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("stati", StatoProiezione.values());
        return "screenings/formScreening";
    }

    @PostMapping("/admin/screenings/{id}/edit")
    public String editScreening(
            @PathVariable("id") Long id,
            @RequestParam("stato") StatoProiezione stato,
            @RequestParam("salaId") Long salaId,
            @RequestParam("data") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate data,
            @RequestParam("ora") @DateTimeFormat(pattern = "HH:mm") LocalTime ora,
            Model model) {

        try {
            proiezioneService.modificaProiezione(id, stato, data, ora, salaId);
            return "redirect:/screenings";
        } catch (Exception e) {
            Proiezione p = proiezioneService.findById(id).orElse(null);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("proiezione", p);
            model.addAttribute("sale", salaService.findAll());
            model.addAttribute("stati", StatoProiezione.values());
            return "screenings/formScreening";
        }
    }

    @PostMapping("/admin/screenings/{id}/delete")
    public String deleteScreening(@PathVariable("id") Long id) {
        proiezioneService.deleteById(id);
        return "redirect:/screenings";
    }
}
