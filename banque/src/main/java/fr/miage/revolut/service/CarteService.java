package fr.miage.revolut.service;

import fr.miage.revolut.boundary.CarteRessource;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.Compte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Carte findByNumero(String numero){
        return ressource.findByNumero(numero);
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

    public void delete(Carte carte){
        ressource.delete(carte);
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

    public String verifierCarte(Carte carte, int cryptogramme, int code, String pays, BigDecimal montant, boolean sansContact){
        if(carte.isBloque()){
            return "La carte est bloquée";
        }else if(carte.getCryptogramme() != cryptogramme){
            return "Cryptogramme invalide";
        }else if(carte.getCode() != code){
            return "Code invalide";
        }else if(carte.getPlafond().compareTo(montant.intValue()) < 0){
            return "Le montant de l'opération dépasse le plafond de la carte";
        }else if(!carte.getCompte().getPays().equals(pays) && carte.isLocalisation()){
            return "L'opération à lieu dans un pays différent de celui de compte et la carte n'autorise pas les opérations à l'étranger";
        }else if(!carte.isSansContact() && sansContact){
            return "Le sans contact n'est pas autorisé sur cette carte";
        }else {
            return "fait";
        }
    }
}
