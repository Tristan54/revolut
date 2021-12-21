package fr.miage.apibanque.assembler;

import fr.miage.apibanque.boundary.CompteRepresentation;
import fr.miage.apibanque.dto.output.CompteOutput;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompteAssembler implements RepresentationModelAssembler<CompteOutput, EntityModel<CompteOutput>> {


    @Override
    public EntityModel<CompteOutput> toModel(CompteOutput compte) {
        return EntityModel.of(compte,
                linkTo(methodOn(CompteRepresentation.class)
                        .getOneCompte(compte.getUuid())).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class)
                        .getAllComptes()).withRel("collection"));
    }

    @Override
    public CollectionModel<EntityModel<CompteOutput>> toCollectionModel(Iterable<? extends CompteOutput> comptes) {
        List<EntityModel<CompteOutput>> intervenantModel = StreamSupport
                .stream(comptes.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(intervenantModel,
                linkTo(methodOn(CompteRepresentation.class)
                        .getAllComptes()).withSelfRel());
    }
}
