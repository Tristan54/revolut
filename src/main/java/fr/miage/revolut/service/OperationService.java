package fr.miage.revolut.service;

import fr.miage.revolut.boundary.OperationRessource;
import fr.miage.revolut.entity.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRessource ressource;

    public Optional<Operation> findById(String id){
        return ressource.findById(id);
    }

    public Operation save(Operation compte){
        return ressource.save(compte);
    }

}
