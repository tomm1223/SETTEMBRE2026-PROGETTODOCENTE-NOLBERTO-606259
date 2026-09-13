package it.uniroma3.siw.festivalprof.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Proiezione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "La data della proiezione è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @NotNull(message = "L'ora della proiezione è obbligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime ora;

    @Enumerated(EnumType.STRING)
    private StatoProiezione stato = StatoProiezione.SCHEDULED;

    @NotNull(message = "Selezionare un festival")
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @NotNull(message = "Selezionare un film")
    @ManyToOne(fetch = FetchType.LAZY)
    private Film film;

    @NotNull(message = "Selezionare una sala")
    @ManyToOne(fetch = FetchType.LAZY)
    private Sala sala;

    public Proiezione() {
    }

    public Proiezione(LocalDate data, LocalTime ora, StatoProiezione stato, Festival festival, Film film, Sala sala) {
        this.data = data;
        this.ora = ora;
        this.stato = stato;
        this.festival = festival;
        this.film = film;
        this.sala = sala;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public StatoProiezione getStato() {
        return stato;
    }

    public void setStato(StatoProiezione stato) {
        this.stato = stato;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalTime getOraFine() {
        if (film != null && film.getDurata() != null && ora != null) {
            return ora.plusMinutes(film.getDurata());
        }
        return ora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proiezione that = (Proiezione) o;
        return Objects.equals(id, that.id) || 
               (Objects.equals(data, that.data) && Objects.equals(ora, that.ora) && Objects.equals(sala, that.sala));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, ora, sala);
    }
}
