package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.CarteAssembler;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.dto.output.CarteOutput;
import fr.miage.revolut.dto.validator.CarteValidator;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.service.CarteService;
import org.springframework.hateoas.EntityModel;
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
@RequestMapping(value="/api/v1/comptes/{compteId}/cartes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Carte.class)
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
        return Optional.ofNullable(service.findByUuid(compteId))
                .map(c -> ResponseEntity.ok(assembler.toCollectionModel(c, compteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value="/{carteId}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<EntityModel<CarteOutput>> getOneCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId) {
        return Optional.ofNullable(service.findById(carteId)).filter(Optional::isPresent)
                .map(c -> ResponseEntity.ok(assembler.toModelWithAccount(c.get(), compteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<URI> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("compteId") String compteId){

        Optional<Carte> saved = service.createCarte(carte, compteId);

        if(saved.isPresent()){
            URI location = linkTo(CarteRepresentation.class, compteId).slash(saved.get().getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping(value="/{carteId}")
    @Transactional
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<URI> modifierUneCarte(@PathVariable("compteId") String compteId, @PathVariable("carteId") String carteId, @RequestBody @Valid CarteUpdate carte) {
        if(service.findById(carteId).isPresent()){
            Carte saved = service.update(carteId, carte);

            URI location = linkTo(CarteRepresentation.class, compteId).slash(saved.getUuid()).toUri();
            return ResponseEntity.created(location).build();
        } else{
            return ResponseEntity.notFound().build();
        }
    }

}
