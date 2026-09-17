package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.festivalprof.service.FestivalService;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.ProiezioneService;
import it.uniroma3.siw.festivalprof.service.RegistaService;
import it.uniroma3.siw.festivalprof.service.SalaService;

@Controller
public class AdminController {

    private final FestivalService festivalService;
    private final FilmService filmService;
    private final RegistaService registaService;
    private final SalaService salaService;
    private final ProiezioneService proiezioneService;

    public AdminController(FestivalService festivalService, FilmService filmService,
                           RegistaService registaService, SalaService salaService,
                           ProiezioneService proiezioneService) {
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.registaService = registaService;
        this.salaService = salaService;
        this.proiezioneService = proiezioneService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("festivalsCount", festivalService.findAll().size());
        model.addAttribute("filmsCount", filmService.findAll().size());
        model.addAttribute("registiCount", registaService.findAll().size());
        model.addAttribute("saleCount", salaService.findAll().size());
        model.addAttribute("proiezioniCount", proiezioneService.findAllWithDetails().size());
        model.addAttribute("festivals", festivalService.findAll());
        return "admin/index";
    }
}
