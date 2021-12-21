package fr.miage.apibanque.assembler;

import fr.miage.apibanque.boundary.CompteRepresentation;
import fr.miage.apibanque.dto.output.CompteOutput;
import fr.miage.apibanque.entity.Compte;
import fr.miage.apibanque.mappers.CompteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.server.RepresentationModelAssembler;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class CompteAssembler implements RepresentationModelAssembler<Compte, EntityModel<CompteOutput>> {

    private final CompteMapper mapper;

    @Override
    public EntityModel<CompteOutput> toModel(Compte compte) {
        return EntityModel.of(mapper.toDto(compte), linkTo(methodOn(CompteRepresentation.class).getOneCompte(compte.getUuid())).withSelfRel());
    }

}
