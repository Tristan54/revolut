package fr.miage.revolut.service;

import fr.miage.revolut.boundary.CompteRessource;
import fr.miage.revolut.entity.Compte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRessource ressource;

    public Optional<Compte> findById(String id){
        return ressource.findById(id);
    }

    public Compte save(Compte compte){
        return ressource.save(compte);
    }

}
