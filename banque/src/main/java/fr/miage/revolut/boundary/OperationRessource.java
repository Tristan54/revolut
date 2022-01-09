package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperationRessource extends JpaRepository<Operation, String>, JpaSpecificationExecutor<Operation> {

}
