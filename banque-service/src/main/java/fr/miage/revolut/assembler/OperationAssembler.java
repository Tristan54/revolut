package fr.miage.revolut.assembler;

import fr.miage.revolut.boundary.CompteRepresentation;
import fr.miage.revolut.boundary.OperationRepresentation;
import fr.miage.revolut.dto.output.OperationOutput;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.mapper.OperationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<OperationOutput>> {

    private final OperationMapper mapper;


    public EntityModel<OperationOutput> toModelWithAccount(Operation operation, String compteId) {
        return EntityModel.of(mapper.toDto(operation), linkTo(methodOn(OperationRepresentation.class).getOneOperation(compteId, operation.getUuid())).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class).getOneCompte(compteId)).withSelfRel());
    }

    public CollectionModel<EntityModel<OperationOutput>> toCollectionModel(Iterable<? extends Operation> operations, String compteId) {
        List<EntityModel<OperationOutput>> operationsModel = StreamSupport
                .stream(operations.spliterator(), false)
                .map(o -> toModelWithAccount(o, compteId))
                .collect(Collectors.toList());
        return CollectionModel.of(operationsModel,
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperations(compteId, null)).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class).getOneCompte(compteId)).withSelfRel());
    }

    @Override
    public EntityModel<OperationOutput> toModel(Operation Operation) {
        return null;
    }
}
