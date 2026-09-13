package it.uniroma3.siw.festivalprof.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festivalprof.model.Film;
import it.uniroma3.siw.festivalprof.model.Recensione;
import it.uniroma3.siw.festivalprof.model.User;
import it.uniroma3.siw.festivalprof.service.FilmService;
import it.uniroma3.siw.festivalprof.service.RecensioneService;
import it.uniroma3.siw.festivalprof.service.UserService;

@Controller
public class RecensioneController {

    private final RecensioneService recensioneService;
    private final FilmService filmService;
    private final UserService userService;

    public RecensioneController(RecensioneService recensioneService, FilmService filmService, UserService userService) {
        this.recensioneService = recensioneService;
        this.filmService = filmService;
        this.userService = userService;
    }

    @PostMapping("/recensioni/new/{filmId}")
    public String addRecensione(
            @PathVariable("filmId") Long filmId,
            @RequestParam("titolo") String titolo,
            @RequestParam("testo") String testo,
            @RequestParam("voto") Integer voto,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Film film = filmService.findById(filmId).orElse(null);
        User utente = userService.findByUsername(principal.getName());

        if (film == null || utente == null) {
            return "redirect:/movies";
        }

        try {
            Recensione r = new Recensione();
            r.setTitolo(titolo);
            r.setTesto(testo);
            r.setVoto(voto);
            recensioneService.salvaRecensione(r, utente, film);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/movies/" + filmId;
    }

    @GetMapping("/recensioni/{id}/edit")
    public String showFormEditRecensione(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        Recensione r = recensioneService.findById(id).orElse(null);
        User me = userService.findByUsername(principal.getName());

        if (r == null || me == null || !r.getUtente().getId().equals(me.getId())) {
            return "redirect:/movies";
        }

        model.addAttribute("recensione", r);
        return "movies/editReview";
    }

    @PostMapping("/recensioni/{id}/edit")
    public String editRecensione(
            @PathVariable("id") Long id,
            @RequestParam("titolo") String titolo,
            @RequestParam("testo") String testo,
            @RequestParam("voto") Integer voto,
            Principal principal,
            Model model) {

        if (principal == null) return "redirect:/login";
        User me = userService.findByUsername(principal.getName());

        try {
            Recensione updated = recensioneService.modificaRecensione(id, titolo, testo, voto, me);
            return "redirect:/movies/" + updated.getFilm().getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/movies";
        }
    }

    @PostMapping("/recensioni/{id}/delete")
    public String deleteRecensione(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        User me = userService.findByUsername(principal.getName());

        Recensione r = recensioneService.findById(id).orElse(null);
        if (r == null) return "redirect:/movies";

        Long filmId = r.getFilm().getId();
        try {
            recensioneService.eliminaRecensione(id, me);
        } catch (Exception e) {
            System.err.println("Errore cancellazione recensione: " + e.getMessage());
        }

        return "redirect:/movies/" + filmId;
    }
}
