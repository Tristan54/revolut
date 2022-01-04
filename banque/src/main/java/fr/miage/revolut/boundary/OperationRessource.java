package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRessource extends JpaRepository<Operation, String> {
}
