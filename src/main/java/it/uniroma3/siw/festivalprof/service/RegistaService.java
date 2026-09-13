package it.uniroma3.siw.festivalprof.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalprof.model.Regista;
import it.uniroma3.siw.festivalprof.repository.RegistaRepository;

@Service
public class RegistaService {

    private final RegistaRepository registaRepository;

    public RegistaService(RegistaRepository registaRepository) {
        this.registaRepository = registaRepository;
    }

    @Transactional(readOnly = true)
    public List<Regista> findAll() {
        return this.registaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Regista> findById(Long id) {
        return this.registaRepository.findById(id);
    }

    @Transactional
    public Regista save(Regista regista) {
        return this.registaRepository.save(regista);
    }

    @Transactional
    public void deleteById(Long id) {
        this.registaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNomeCognomeDataNascita(Regista r) {
        return this.registaRepository.existsByNomeAndCognomeAndDataNascita(r.getNome(), r.getCognome(), r.getDataNascita());
    }
}
