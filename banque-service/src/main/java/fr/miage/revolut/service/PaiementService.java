package fr.miage.revolut.service;


import fr.miage.revolut.ressource.OperationCarteRessource;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.input.PaiementInput;
import fr.miage.revolut.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final CompteService compteService;
    private final OperationService operationService;
    private final CarteService carteService;
    private final OperationCarteRessource operationCarteRessource;

    public String payer(PaiementInput paiementInput){

        Carte carte = carteService.findByNumero(paiementInput.getNumeroCarte());

        Compte compte;
        try {
            compte = compteService.findById(carte.getCompte().getUuid()).get();
        }catch (Exception e){
            return "Utilisateur introuvable";
        }


        if(carte == null ){
            return "Numéro de carte invalide";
        }

        String veriferCarte = carteService.verifierCarte(carte, paiementInput.getCryptogrammeCarte(), paiementInput.getCodeCarte(), paiementInput.getPays(), paiementInput.getMontant(), paiementInput.isSansContact(), paiementInput.getDate());
        if(veriferCarte != "fait"){
            return veriferCarte;
        }

        OperationInput operationInput = new OperationInput(
                "Réglement par carte chez " + paiementInput.getNomCommercant(),
                paiementInput.getMontant(),
                paiementInput.getNomCommercant(),
                paiementInput.getIbanCommercant(),
                paiementInput.getCategorie(),
                paiementInput.getPays()
        );

        Optional<Operation> operation = operationService.createOperation(operationInput, compte.getUuid());

        if(operation.isPresent()){
            // opération pour carte
            operationCarteRessource.save(new PivotOperationCarte(new OperationCarte(operation.get(), carte)));

            // suppression de la carte virtuelle apres operation
            if(carte.isVirtuelle()){
                carte.setSupprime(true);
                carteService.save(carte);
            }

            return "fait";
        }else {
            return "Echec de l'opération, le compte n'est pas assez approvisionné";
        }
    }

}
