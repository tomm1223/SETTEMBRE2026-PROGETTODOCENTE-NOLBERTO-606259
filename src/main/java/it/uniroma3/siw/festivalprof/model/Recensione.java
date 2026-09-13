package it.uniroma3.siw.festivalprof.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "recensioni", uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "utente_id"}))
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il titolo della recensione è obbligatorio")
    private String titolo;

    @NotBlank(message = "Il testo della recensione è obbligatorio")
    @Column(length = 2000)
    private String testo;

    @NotNull(message = "Il voto è obbligatorio")
    @Min(value = 1, message = "Il voto minimo è 1")
    @Max(value = 5, message = "Il voto massimo è 5")
    private Integer voto;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime data;

    @NotNull
    @ManyToOne
    private Film film;

    @NotNull
    @ManyToOne
    private User utente;

    public Recensione() {
    }

    public Recensione(String titolo, String testo, Integer voto, LocalDateTime data, Film film, User utente) {
        this.titolo = titolo;
        this.testo = testo;
        this.voto = voto;
        this.data = data;
        this.film = film;
        this.utente = utente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public User getUtente() {
        return utente;
    }

    public void setUtente(User utente) {
        this.utente = utente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recensione that = (Recensione) o;
        return Objects.equals(id, that.id) || (Objects.equals(film, that.film) && Objects.equals(utente, that.utente));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, film, utente);
    }
}
