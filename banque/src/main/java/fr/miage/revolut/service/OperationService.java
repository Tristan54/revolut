package fr.miage.revolut.service;

import fr.miage.revolut.boundary.OperationRessource;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRessource ressource;
    private final CompteService compteService;

    public Optional<Operation> findById(String id){
        return ressource.findById(id);
    }

    public Operation save(Operation compte){
        return ressource.save(compte);
    }

    public Optional<Operation> createOperation(OperationInput operation, String compteId){
        Compte compte = compteService.findById(compteId).get();

        // verification de la localisation + taux de change


        // verification du montant
        if(compte.getMontant() < operation.getMontant()){
            return Optional.empty();
        }

        // creation de l'opération
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                operation.getLibelle(),
                operation.getMontant(),
                0,
                operation.getNomCrediteur(),
                operation.getIbanCrediteur(),
                compte.getNom(),
                compte.getIban(),
                operation.getCategorie(),
                operation.getPays()
        );

        return Optional.ofNullable(save(operation2Save));
    }

}
