package fr.miage.revolut.mapper;

import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.output.CompteOutput;
import fr.miage.revolut.entity.Compte;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompteMapper {

    CompteOutput toDto(Compte compte);

    Compte toEntity(CompteInput dto);
}
