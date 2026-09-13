package it.uniroma3.siw.festivalprof.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il titolo è obbligatorio")
    private String titolo;

    @NotNull(message = "L'anno è obbligatorio")
    @Min(value = 1880, message = "Anno di produzione non valido")
    private Integer anno;

    @NotNull(message = "La durata (in minuti) è obbligatoria")
    @Min(value = 1, message = "La durata deve essere di almeno 1 minuto")
    private Integer durata;

    @NotBlank(message = "Il genere è obbligatorio")
    private String genere;

    @NotBlank(message = "Il paese di produzione è obbligatorio")
    private String paeseProduzione;

    @ManyToOne(fetch = FetchType.LAZY)
    private Regista regista;

    @JsonIgnore
    @ManyToMany(mappedBy = "films")
    private Set<Festival> festivals = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private List<Proiezione> proiezioni = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    private List<Recensione> recensioni = new ArrayList<>();

    public Film() {
    }

    public Film(String titolo, Integer anno, Integer durata, String genere, String paeseProduzione) {
        this.titolo = titolo;
        this.anno = anno;
        this.durata = durata;
        this.genere = genere;
        this.paeseProduzione = paeseProduzione;
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

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getDurata() {
        return durata;
    }

    public void setDurata(Integer durata) {
        this.durata = durata;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getPaeseProduzione() {
        return paeseProduzione;
    }

    public void setPaeseProduzione(String paeseProduzione) {
        this.paeseProduzione = paeseProduzione;
    }

    public Regista getRegista() {
        return regista;
    }

    public void setRegista(Regista regista) {
        this.regista = regista;
    }

    public Set<Festival> getFestivals() {
        return festivals;
    }

    public void setFestivals(Set<Festival> festivals) {
        this.festivals = festivals;
    }

    public List<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(List<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public Double getMediaVoti() {
        if (recensioni == null || recensioni.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Recensione r : recensioni) {
            sum += r.getVoto();
        }
        return Math.round((sum / recensioni.size()) * 10.0) / 10.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) || (Objects.equals(titolo, film.titolo) && Objects.equals(anno, film.anno));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titolo, anno);
    }
}
