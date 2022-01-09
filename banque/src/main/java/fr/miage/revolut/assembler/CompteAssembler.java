package fr.miage.revolut.assembler;

import fr.miage.revolut.boundary.CarteRepresentation;
import fr.miage.revolut.boundary.CompteRepresentation;
import fr.miage.revolut.boundary.OperationRepresentation;
import fr.miage.revolut.dto.output.CompteOutput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.mappers.CompteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.server.RepresentationModelAssembler;


import java.util.HashMap;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class CompteAssembler implements RepresentationModelAssembler<Compte, EntityModel<CompteOutput>> {

    private final CompteMapper mapper;

    @Override
    public EntityModel<CompteOutput> toModel(Compte compte) {
        return EntityModel.of(mapper.toDto(compte),
                linkTo(methodOn(CompteRepresentation.class).getOneCompte(compte.getUuid())).withSelfRel(),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllOperations(compte.getUuid(), null)).withRel("collection"),
                linkTo(methodOn(CarteRepresentation.class)
                        .getAllCartes(compte.getUuid())).withRel("collection"));

    }

}
