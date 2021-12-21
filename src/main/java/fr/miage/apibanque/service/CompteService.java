package fr.miage.apibanque.service;

import fr.miage.apibanque.boundary.CompteRessource;
import fr.miage.apibanque.dto.input.CompteInput;
import fr.miage.apibanque.dto.output.CompteOutput;
import fr.miage.apibanque.entity.Compte;
import fr.miage.apibanque.mappers.CompteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRessource ressource;
    private final CompteMapper mapper;

    public Iterable<? extends CompteOutput> findAll() {
        return mapper.toDto(ressource.findAll());
    }

    public Optional<CompteOutput> findById(String id){
        return mapper.toDto(ressource.findById(id));
    }
}
