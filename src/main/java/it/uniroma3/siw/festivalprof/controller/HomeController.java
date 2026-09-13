package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.festivalprof.service.FestivalService;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;

@Controller
public class HomeController {

    private final FestivalService festivalService;
    private final FilmService filmService;
    private final ProiezioneService proiezioneService;

    public HomeController(FestivalService festivalService, FilmService filmService, ProiezioneService proiezioneService) {
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.proiezioneService = proiezioneService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("festivals", festivalService.findAll());
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("proiezioni", proiezioneService.findAllWithDetails());
        return "index";
    }
}
