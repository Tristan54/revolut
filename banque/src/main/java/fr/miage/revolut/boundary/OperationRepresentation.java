package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.OperationAssembler;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.validator.OperationValidator;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.service.CompteService;
import fr.miage.revolut.service.OperationService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes/{compteId}/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {


    private final OperationService service;
    private final OperationAssembler assembler;
    private final OperationValidator validator;

    public OperationRepresentation(OperationService service, OperationAssembler assembler, OperationValidator validator) {
        this.service = service;
        this.assembler = assembler;
        this.validator = validator;
    }

    @GetMapping(value="/{operationId}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<?> getOneOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        return Optional.ofNullable(service.findById(operationId)).filter(Optional::isPresent)
                .map(o -> ResponseEntity.ok(assembler.toModelWithAccount(o.get(), compteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("compteId") String compteId){

        Optional<Operation> saved = service.createOperation(operation, compteId);

        if(saved.isPresent()){
            URI location = linkTo(OperationRepresentation.class, compteId).slash(saved.get().getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

    @PatchMapping("/deposer/{montant}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<?> deposer(@PathVariable("compteId") String compteId, @Valid @PathVariable("montant") BigDecimal montant){

        boolean fait = service.deposer(montant, compteId);

        return fait ? ResponseEntity.noContent().build() :  ResponseEntity.badRequest().build();

    }

}
