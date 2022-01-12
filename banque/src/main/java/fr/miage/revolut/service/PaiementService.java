package fr.miage.revolut.service;


import fr.miage.revolut.boundary.OperaionCarteRessource;
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
    private final OperaionCarteRessource operationCarteRessource;

    public String payer(PaiementInput paiementInput, String compteId){
        Compte compte = compteService.findById(compteId).get();

        Carte carte = carteService.findByNumero(paiementInput.getNumeroCarte());

        if(carte == null){
            return "Numéro de carte invalide";
        }

        String veriferCarte = carteService.verifierCarte(carte, paiementInput.getCryptogrammeCarte(), paiementInput.getCodeCarte(), paiementInput.getPays(), paiementInput.getMontant(), paiementInput.isSansContact());
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

        Optional<Operation> operation = operationService.createOperation(operationInput, compteId);

        if(operation.isPresent()){
            // opération pour carte
            OperationCarte operationCarte = new OperationCarte(operation.get(), carte);
            operationCarteRessource.save(new PivotOperationCarte(operationCarte));

            // suppression de la carte virtuelle apres operation
            if(carte.isVirtuelle()){
                carteService.delete(carte);
            }

            return "fait";
        }else {
            return "Echec de l'opération, le compte n'est pas assez approvisionné";
        }
    }

}
