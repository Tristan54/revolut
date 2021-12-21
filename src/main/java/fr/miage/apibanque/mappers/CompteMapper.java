package fr.miage.apibanque.mappers;

import fr.miage.apibanque.dto.input.CompteInput;
import fr.miage.apibanque.dto.output.CompteOutput;
import fr.miage.apibanque.entity.Compte;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompteMapper {

    CompteOutput toDto(Compte compte);

    Compte toEntity(CompteInput dto);
}
