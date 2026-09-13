package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class RegistaController {

    private final RegistaService registaService;
    private final FilmService filmService;

    public RegistaController(RegistaService registaService, FilmService filmService) {
        this.registaService = registaService;
        this.filmService = filmService;
    }

    @GetMapping("/directors")
    public String getDirectors(Model model) {
        model.addAttribute("registi", registaService.findAll());
        return "directors/listDirectors";
    }

    @GetMapping("/directors/{id}")
    public String getDirector(@PathVariable("id") Long id, Model model) {
        Regista regista = registaService.findById(id).orElse(null);
        if (regista == null) return "redirect:/directors";

        model.addAttribute("regista", regista);
        model.addAttribute("films", filmService.findByRegista(regista));
        return "directors/showDirector";
    }

    @GetMapping("/admin/directors/new")
    public String showFormNewDirector(Model model) {
        model.addAttribute("regista", new Regista());
        return "directors/formDirector";
    }

    @PostMapping("/admin/directors/new")
    public String newDirector(@Valid @ModelAttribute("regista") Regista regista, BindingResult bindingResult) {
        if (registaService.existsByNomeCognomeDataNascita(regista)) {
            bindingResult.rejectValue("nome", "duplicate", "Esiste già un regista con questo nome, cognome e data di nascita.");
        }

        if (bindingResult.hasErrors()) {
            return "directors/formDirector";
        }

        registaService.save(regista);
        return "redirect:/directors/" + regista.getId();
    }

    @GetMapping("/admin/directors/{id}/edit")
    public String showFormEditDirector(@PathVariable("id") Long id, Model model) {
        Regista regista = registaService.findById(id).orElse(null);
        if (regista == null) return "redirect:/directors";

        model.addAttribute("regista", regista);
        return "directors/formDirector";
    }

    @PostMapping("/admin/directors/{id}/edit")
    public String editDirector(@PathVariable("id") Long id, @Valid @ModelAttribute("regista") Regista regista, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "directors/formDirector";
        }

        regista.setId(id);
        registaService.save(regista);
        return "redirect:/directors/" + id;
    }

    @PostMapping("/admin/directors/{id}/delete")
    public String deleteDirector(@PathVariable("id") Long id) {
        registaService.deleteById(id);
        return "redirect:/directors";
    }
}
