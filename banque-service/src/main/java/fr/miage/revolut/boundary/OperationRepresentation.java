package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.OperationAssembler;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.output.OperationOutput;
import fr.miage.revolut.dto.validator.OperationValidator;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.filter.OperationSpecificationsBuilder;
import fr.miage.revolut.service.OperationService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @GetMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<CollectionModel<EntityModel<OperationOutput>>> getAllOperations(@PathVariable("compteId") String compteId, @Nullable @RequestParam(value = "filtres") String filtres){
        OperationSpecificationsBuilder builder = new OperationSpecificationsBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(filtres + ",");

        builder.with("compteUuid", ":", compteId);
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<Operation> spec = builder.build();
        return ResponseEntity.ok(assembler.toCollectionModel(service.findAll(spec), compteId));
    }

    @GetMapping(value="/{operationId}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    public ResponseEntity<EntityModel<OperationOutput>> getOneOperation(@PathVariable("compteId") String compteId, @PathVariable("operationId") String operationId) {
        return Optional.ofNullable(service.findById(operationId)).filter(Optional::isPresent)
                .map(o -> ResponseEntity.ok(assembler.toModelWithAccount(o.get(), compteId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<String> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("compteId") String compteId){

        Optional<Operation> saved = service.createOperation(operation, compteId);

        if(saved.isPresent()){
            URI location = linkTo(OperationRepresentation.class, compteId).slash(saved.get().getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }else {
            return ResponseEntity.badRequest().body("Echec de l'opération, le compte n'est pas assez approvisionné");
        }

    }

    @PatchMapping("/deposer/{montant}")
    @PreAuthorize(value = "authentication.name.equals(#compteId)")
    @Transactional
    public ResponseEntity<String> deposer(@PathVariable("compteId") String compteId, @Valid @PathVariable("montant") BigDecimal montant){

        boolean fait = service.deposer(montant, compteId);

        return fait ? ResponseEntity.ok().build() :  ResponseEntity.badRequest().build();

    }

}
