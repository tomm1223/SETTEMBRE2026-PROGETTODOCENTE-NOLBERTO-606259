package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalprof.runner.DataAccessAnalysisRunner;
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
    private final DataAccessAnalysisRunner dataAccessAnalysisRunner;

    public AdminController(FestivalService festivalService, FilmService filmService,
                           RegistaService registaService, SalaService salaService,
                           ProiezioneService proiezioneService, DataAccessAnalysisRunner dataAccessAnalysisRunner) {
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.registaService = registaService;
        this.salaService = salaService;
        this.proiezioneService = proiezioneService;
        this.dataAccessAnalysisRunner = dataAccessAnalysisRunner;
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

    @GetMapping("/admin/benchmark")
    public String runBenchmark(@RequestParam(value = "festivalId", defaultValue = "1") Long festivalId, Model model) {
        String result = dataAccessAnalysisRunner.eseguiConfrontoFetch(festivalId);
        model.addAttribute("benchmarkOutput", result);
        model.addAttribute("festivalsCount", festivalService.findAll().size());
        model.addAttribute("filmsCount", filmService.findAll().size());
        model.addAttribute("registiCount", registaService.findAll().size());
        model.addAttribute("saleCount", salaService.findAll().size());
        model.addAttribute("proiezioniCount", proiezioneService.findAllWithDetails().size());
        model.addAttribute("festivals", festivalService.findAll());
        return "admin/index";
    }
}
