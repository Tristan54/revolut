package fr.miage.conversionservice.boundary;

import fr.miage.conversionservice.entity.TauxChange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ConvertionRepresentation {

    private final TauxChangeRessource ressource;

    public ConvertionRepresentation(TauxChangeRessource ressource) {
        this.ressource = ressource;
    }

    @GetMapping(value = "/conversion/source/{source}/cible/{cible}")
    public ResponseEntity<?> converstion(@PathVariable String source, @PathVariable String cible){
        TauxChange tauxChangeSource = ressource.findByCode(source);
        TauxChange tauxChangeCible = ressource.findByCode(cible);



        BigDecimal tauxChange = tauxChangeCible.getTauxConversion().divide(tauxChangeSource.getTauxConversion(), 4);

        return ResponseEntity.ok(tauxChange);
    }
}
