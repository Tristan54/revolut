package fr.miage.revolut.service;

import fr.miage.revolut.ressource.OperationRessource;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.entity.PivotOperationCarte;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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

    public List<Operation> findAll(Specification<Operation> spec){
        return ressource.findAll(spec);
    }

    public Operation save(Operation compte){
        return ressource.save(compte);
    }

    public Optional<Operation> createOperation(OperationInput operation, String compteId){
        Compte compte = compteService.findById(compteId).get();

        // verification de la localisation + taux de change
        BigDecimal taux;
        if(operation.getPays().equals(compte.getPays())){
            taux = BigDecimal.valueOf(1.0);
        }else{
            try {
                taux = conversionService.conversion(operation.getPays(), compte.getPays());
            }catch (Exception e){
                return Optional.empty();
            }
        }

        // verification du montant
        if(compte.getMontant().compareTo(operation.getMontant()) < 0){
            return Optional.empty();
        }

        compte.debit(operation.getMontant().multiply(taux));
        compteService.update(compte);

        Optional<Compte> compte2Credit = compteService.findByIban(operation.getIbanCrediteur());
        if(compte2Credit.isPresent()){
            compte2Credit.get().credit(operation.getMontant());
            compteService.update(compte2Credit.get());
        }

        // creation de l'op??ration
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                compteId,
                LocalDateTime.now(),
                operation.getLibelle(),
                operation.getMontant().multiply(taux),
                taux,
                operation.getNomCrediteur(),
                operation.getIbanCrediteur(),
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

    public BigDecimal calculerMontant(List<PivotOperationCarte> operations){
        BigDecimal res = BigDecimal.valueOf(0);
        for(PivotOperationCarte pivot : operations){
            Operation operation = pivot.getOperationCarte().getOperation();
            Operation o = ressource.findById(operation.getUuid()).get();
            res = res.add(o.getMontant());
        }

        return res;
    }

    public List<Operation> findByIbanCrediteur(String iban) {
        return ressource.findByIbanCrediteur(iban);
    }
}
