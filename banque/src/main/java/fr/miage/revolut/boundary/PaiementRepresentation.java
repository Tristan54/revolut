package fr.miage.revolut.boundary;

import fr.miage.revolut.dto.input.PaiementInput;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.service.PaiementService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/paiements", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class PaiementRepresentation {


    private final PaiementService service;

    public PaiementRepresentation(PaiementService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> payer(@RequestBody @Valid PaiementInput paiement){

        String res = service.payer(paiement);

        if(res.equals("fait")){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().body(res);
        }

    }

}
