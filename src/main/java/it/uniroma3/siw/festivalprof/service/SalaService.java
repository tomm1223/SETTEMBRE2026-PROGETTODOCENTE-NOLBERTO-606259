package it.uniroma3.siw.festivalprof.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Sala;
import it.uniroma3.siw.festivalprof.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional(readOnly = true)
    public List<Sala> findAll() {
        return this.salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sala> findById(Long id) {
        return this.salaRepository.findById(id);
    }

    @Transactional
    public Sala save(Sala sala) {
        return this.salaRepository.save(sala);
    }

    @Transactional
    public void deleteById(Long id) {
        this.salaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNome(String nome) {
        return this.salaRepository.existsByNome(nome);
    }
}
