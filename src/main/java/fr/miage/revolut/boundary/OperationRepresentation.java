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
    private final CompteService compteService;
    private final OperationAssembler assembler;
    private final OperationValidator validator;

    public OperationRepresentation(OperationService service, CompteService compteService, OperationAssembler assembler, OperationValidator validator) {
        this.service = service;
        this.compteService = compteService;
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
        Compte compte = compteService.findById(compteId).get();
        // faire des opérations dans le service
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                operation.getLibelle(),
                operation.getMontant(),
                0,
                operation.getNomCrediteur(),
                operation.getIbanCrediteur(),
                compte.getNom(),
                compte.getIban(),
                operation.getCategorie(),
                operation.getPays()
        );
        Operation saved = service.save(operation2Save);
        URI location = linkTo(OperationRepresentation.class, compteId).slash(saved.getUuid()).toUri();
        return ResponseEntity.created(location).build();
    }

}
