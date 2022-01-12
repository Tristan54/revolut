package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.OperationCarte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperaionCarteRessource extends JpaRepository<OperationCarte, String>{

    List<OperationCarte> findAllByCarteUuid(String carte_uuid);
}
