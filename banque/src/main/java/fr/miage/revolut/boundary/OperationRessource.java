package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Operation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OperationRessource extends JpaRepository<Operation, String>, JpaSpecificationExecutor<Operation> {

}
