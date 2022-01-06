package fr.miage.revolut.service;

import fr.miage.revolut.boundary.OperationRessource;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Executable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRessource ressource;
    private final CompteService compteService;
    private final ConversionService conversionService;

    public Optional<Operation> findById(String id){
        return ressource.findById(id);
    }

    public Operation save(Operation compte){
        return ressource.save(compte);
    }

    public Optional<Operation> createOperation(OperationInput operation, String compteId){
        Compte compte = compteService.findById(compteId).get();

        // verification de la localisation + taux de change
        BigDecimal taux;
        try {
           taux = conversionService.conversion(operation.getPays(), compte.getPays());
        }catch (Exception e){
            return Optional.empty();
        }
        System.out.println(taux);

        // verification du montant
        if(compte.getMontant().compareTo(operation.getMontant()) < 0){
            return Optional.empty();
        }

        compte.debit(operation.getMontant().multiply(taux));
        compteService.update(compte);

        // creation de l'opération
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                operation.getLibelle(),
                operation.getMontant().multiply(taux),
                taux,
                operation.getNomCrediteur(),
                operation.getIbanCrediteur(),
                compte.getNom(),
                compte.getIban(),
                operation.getCategorie(),
                operation.getPays()
        );

        return Optional.ofNullable(save(operation2Save));
    }

    public boolean deposer(BigDecimal montant, String compteId) {
        Compte compte = compteService.findById(compteId).get();

        if(montant.compareTo(BigDecimal.valueOf(0)) > 0 ){
            compte.credit(montant);
            compteService.update(compte);
            return true;
        }

        return false;
    }
}
