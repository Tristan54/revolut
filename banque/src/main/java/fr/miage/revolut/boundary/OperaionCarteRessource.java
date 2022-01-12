package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.OperationCarte;
import fr.miage.revolut.entity.PivotOperationCarte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OperaionCarteRessource extends JpaRepository<PivotOperationCarte, OperationCarte>{

    List<PivotOperationCarte> findByOperationCarte_Carte_Uuid(String uuid);

    List<PivotOperationCarte> findByOperationCarte_Carte_UuidAndOperationCarte_Operation_DateGreaterThanEqual(String uuid, LocalDateTime date);




}
