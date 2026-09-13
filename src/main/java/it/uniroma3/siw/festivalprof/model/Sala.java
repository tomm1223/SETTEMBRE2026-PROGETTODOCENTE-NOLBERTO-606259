package it.uniroma3.siw.festivalprof.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome della sala è obbligatorio")
    private String nome;

    @NotBlank(message = "L'indirizzo è obbligatorio")
    private String indirizzo;

    @NotNull(message = "La capienza è obbligatoria")
    @Min(value = 1, message = "La capienza deve essere di almeno 1 posto")
    private Integer capienza;

    @JsonIgnore
    @OneToMany(mappedBy = "sala")
    private List<Proiezione> proiezioni = new ArrayList<>();

    public Sala() {
    }

    public Sala(String nome, String indirizzo, Integer capienza) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.capienza = capienza;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public Integer getCapienza() {
        return capienza;
    }

    public void setCapienza(Integer capienza) {
        this.capienza = capienza;
    }

    public List<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(List<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return Objects.equals(id, sala.id) || (Objects.equals(nome, sala.nome) && Objects.equals(indirizzo, sala.indirizzo));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, indirizzo);
    }
}
