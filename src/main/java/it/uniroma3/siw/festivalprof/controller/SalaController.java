package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festivalprof.model.Sala;
import it.uniroma3.siw.festivalprof.service.SalaService;
import jakarta.validation.Valid;

@Controller
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping("/admin/halls")
    public String getHalls(Model model) {
        model.addAttribute("sale", salaService.findAll());
        return "halls/listHalls";
    }

    @GetMapping("/admin/halls/new")
    public String showFormNewHall(Model model) {
        model.addAttribute("sala", new Sala());
        return "halls/formHall";
    }

    @PostMapping("/admin/halls/new")
    public String newHall(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
        if (salaService.existsByNome(sala.getNome())) {
            bindingResult.rejectValue("nome", "duplicate", "Esiste già una sala con questo nome.");
        }

        if (bindingResult.hasErrors()) {
            return "halls/formHall";
        }

        salaService.save(sala);
        return "redirect:/admin/halls";
    }

    @GetMapping("/admin/halls/{id}/edit")
    public String showFormEditHall(@PathVariable("id") Long id, Model model) {
        Sala sala = salaService.findById(id).orElse(null);
        if (sala == null) return "redirect:/admin/halls";

        model.addAttribute("sala", sala);
        return "halls/formHall";
    }

    @PostMapping("/admin/halls/{id}/edit")
    public String editHall(@PathVariable("id") Long id, @Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "halls/formHall";
        }

        sala.setId(id);
        salaService.save(sala);
        return "redirect:/admin/halls";
    }

    @PostMapping("/admin/halls/{id}/delete")
    public String deleteHall(@PathVariable("id") Long id) {
        salaService.deleteById(id);
        return "redirect:/admin/halls";
    }
}
