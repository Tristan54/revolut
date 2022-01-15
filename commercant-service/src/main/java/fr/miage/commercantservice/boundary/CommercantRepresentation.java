package fr.miage.commercantservice.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import fr.miage.commercantservice.entity.Paiement;
import fr.miage.commercantservice.entity.PaiementInput;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.InputStream;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
public class CommercantRepresentation {

    private final BanqueService banqueService;

    @PostMapping(value = "/payer")
    public ResponseEntity<?> converstion(@RequestBody @Valid PaiementInput paiementInput){

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Paiement paiement = new Paiement(
                paiementInput.getNumeroCarte(),
                paiementInput.getCryptogrammeCarte(),
                paiementInput.getCodeCarte(),
                "France",
                "Service commerçant",
                "FR45678908",
                date.format(formatter),
                paiementInput.getMontant(),
                paiementInput.isSansContact(),
                "Opération par carte"
        );


        try {
            ResponseEntity<String> response = banqueService.payerParCarte(paiement);
            if(response.getStatusCodeValue() == 200){
                return ResponseEntity.ok().body(response.getBody());
            }else {

                return ResponseEntity.badRequest().body(response.getBody());
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Le service est injoignable");
        }
    }
}
