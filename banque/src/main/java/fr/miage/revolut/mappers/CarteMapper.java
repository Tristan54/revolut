package fr.miage.revolut.mappers;

import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.output.CarteOutput;
import fr.miage.revolut.entity.Carte;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarteMapper {

    CarteOutput toDto(Carte carte);

    Carte toEntity(CarteInput dto);
}
