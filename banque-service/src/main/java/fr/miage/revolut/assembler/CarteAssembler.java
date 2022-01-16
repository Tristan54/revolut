package fr.miage.revolut.assembler;

import fr.miage.revolut.boundary.CarteRepresentation;
import fr.miage.revolut.boundary.CompteRepresentation;
import fr.miage.revolut.dto.output.CarteOutput;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.mapper.CarteMapper;
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
public class CarteAssembler implements RepresentationModelAssembler<Carte, EntityModel<CarteOutput>> {

    private final CarteMapper mapper;


    public EntityModel<CarteOutput> toModelWithAccount(Carte carte, String compteId) {
        return EntityModel.of(mapper.toDto(carte),
                linkTo(methodOn(CarteRepresentation.class).getOneCarte(compteId, carte.getUuid())).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class).getOneCompte(compteId)).withSelfRel());
    }

    public CollectionModel<EntityModel<CarteOutput>> toCollectionModel(Iterable<? extends Carte> cartes, String compteId) {
        List<EntityModel<CarteOutput>> cartesModel = StreamSupport
                .stream(cartes.spliterator(), false)
                .map(o -> toModelWithAccount(o, compteId))
                .collect(Collectors.toList());
        return CollectionModel.of(cartesModel,
                linkTo(methodOn(CarteRepresentation.class).getAllCartes(compteId)).withSelfRel(),
                linkTo(methodOn(CompteRepresentation.class).getOneCompte(compteId)).withSelfRel());
    }

    @Override
    public EntityModel<CarteOutput> toModel(Carte carte) {
        return null;
    }
}
