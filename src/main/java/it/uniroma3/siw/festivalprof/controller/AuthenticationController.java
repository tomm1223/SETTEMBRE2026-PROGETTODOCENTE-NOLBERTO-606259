package it.uniroma3.siw.festivalprof.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.festivalprof.model.Credentials;
import it.uniroma3.siw.festivalprof.model.User;
import it.uniroma3.siw.festivalprof.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    private final CredentialsService credentialsService;

    public AuthenticationController(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @GetMapping(value = "/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "authentication/registerUser";
    }

    @GetMapping(value = "/login")
    public String showLoginForm(Model model) {
        return "authentication/login";
    }

    @GetMapping(value = "/success")
    public String defaultSuccessLogin(Model model) {
        return "redirect:/";
    }

    @PostMapping(value = "/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user, BindingResult userBindingResult,
            @Valid @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult,
            Model model) {

        if (credentialsService.existsByUsername(credentials.getUsername())) {
            credentialsBindingResult.rejectValue("username", "duplicate", "Username già in uso da un altro utente.");
        }

        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            user.setUsername(credentials.getUsername());
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            return "redirect:/login?registered=true";
        }

        return "authentication/registerUser";
    }
}
