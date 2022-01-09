package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.CarteAssembler;
import fr.miage.revolut.assembler.OperationAssembler;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.validator.CarteValidator;
import fr.miage.revolut.dto.validator.OperationValidator;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.filter.OperationSpecificationsBuilder;
import fr.miage.revolut.service.CarteService;
import fr.miage.revolut.service.OperationService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes/{compteId}/cartes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class CarteRepresentation {


    private final CarteService service;
    private final CarteAssembler assembler;
    private final CarteValidator validator;

    public CarteRepresentation(CarteService service, CarteAssembler assembler, CarteValidator validator) {
        this.service = service;
        this.assembler = assembler;
        this.validator = validator;
    }

    @GetMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<?> getAllCartes(@PathVariable("compteId") String compteId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value="/{carteId}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<?> getOneCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        return Optional.ofNullable(service.findById(carteId)).filter(Optional::isPresent)
                .map(c -> ResponseEntity.ok(assembler.toModelWithAccount(c.get(), compteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<?> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("compteId") String compteId){

        Optional<Carte> saved = service.createCarte(carte, compteId);

        if(saved.isPresent()){
            URI location = linkTo(CarteRepresentation.class, compteId).slash(saved.get().getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping(value="/{carteId}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<?> modifierUneCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId, @RequestBody @Valid CarteUpdate carte) {
        if(service.findById(carteId).isPresent()){
            Carte saved = service.update(carteId, carte);

            URI location = linkTo(CarteRepresentation.class, compteId).slash(saved.getUuid()).toUri();
            return ResponseEntity.created(location).build();
        } else{
            return ResponseEntity.notFound().build();
        }
    }

}
