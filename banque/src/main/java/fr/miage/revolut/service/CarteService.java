package fr.miage.revolut.service;

import fr.miage.revolut.boundary.CarteRessource;
import fr.miage.revolut.boundary.OperationRessource;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarteService {

    private final CarteRessource ressource;
    private final CompteService compteService;

    public Optional<Carte> findById(String id){
        return ressource.findById(id);
    }

    public Iterable<Carte> findAll(){
        return ressource.findAll();
    }

    public Carte save(Carte carte){
        return ressource.save(carte);
    }

    public Optional<Carte> createCarte(CarteInput carteInput, String compteId){
        Compte compte = compteService.findById(compteId).get();

        // creation de l'opération
        Carte carte2Save = new Carte(
                UUID.randomUUID().toString(),
                Generator.generateNumeroCarte(),
                Generator.generateCodeCarte(),
                Generator.generateCryptogrammeCarte(),
                carteInput.getPlafond(),
                false,
                carteInput.isLocalisation(),
                carteInput.isSansContact(),
                carteInput.isSansContact(),
                compte
        );

        return Optional.ofNullable(save(carte2Save));
    }

    public Carte update(String carteId, CarteUpdate carteUpdate) {

        Carte carte = ressource.getById(carteId);

        if (carteUpdate.isCode()) {
            carte.setCode(Generator.generateCodeCarte());
        }

        if(carteUpdate.isCryptogramme()){
            carte.setCryptogramme(Generator.generateCryptogrammeCarte());
        }

        carte.setLocalisation(carteUpdate.isLocalisation());
        carte.setPlafond(carteUpdate.getPlafond());
        carte.setSansContact(carteUpdate.isSansContact());
        carte.setBloque(carteUpdate.isBloque());

        return ressource.save(carte);
    }
}
