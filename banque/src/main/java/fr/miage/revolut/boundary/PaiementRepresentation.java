package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.CarteAssembler;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.dto.validator.CarteValidator;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.service.CarteService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/paiement", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class PaiementRepresentation {


    private final CarteService service;
    private final CarteAssembler assembler;
    private final CarteValidator validator;

    public PaiementRepresentation(CarteService service, CarteAssembler assembler, CarteValidator validator) {
        this.service = service;
        this.assembler = assembler;
        this.validator = validator;
    }

    @PostMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<?> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("compteId") String compteId){

        Optional<Carte> saved = service.createCarte(carte, compteId);

        if(saved.isPresent()){
            URI location = linkTo(PaiementRepresentation.class, compteId).slash(saved.get().getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

}
