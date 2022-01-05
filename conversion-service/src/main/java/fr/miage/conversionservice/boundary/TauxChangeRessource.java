package fr.miage.conversionservice.boundary;

import fr.miage.conversionservice.entity.TauxChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TauxChangeRessource extends JpaRepository<TauxChange, String> {

    TauxChange findByPays(String pays);
}
