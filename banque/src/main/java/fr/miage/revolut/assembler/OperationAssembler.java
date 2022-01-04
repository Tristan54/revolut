package fr.miage.revolut.assembler;

import fr.miage.revolut.boundary.OperationRepresentation;
import fr.miage.revolut.dto.output.OperationOuput;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.mappers.OperationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<OperationOuput>> {

    private final OperationMapper mapper;


    public EntityModel<OperationOuput> toModelWithAccount(Operation operation, String compteId) {
        return EntityModel.of(mapper.toDto(operation), linkTo(methodOn(OperationRepresentation.class).getOneOperation(compteId, operation.getUuid())).withSelfRel());
    }

    @Override
    public EntityModel<OperationOuput> toModel(Operation Operation) {
        return null;
    }
}