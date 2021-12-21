package fr.miage.apibanque.mappers;

import fr.miage.apibanque.dto.input.CompteInput;
import fr.miage.apibanque.dto.output.CompteOutput;
import fr.miage.apibanque.entity.Compte;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CompteMapper {

    default List<CompteOutput> toDto(Iterable<Compte> comptes) {
        var result = new ArrayList<CompteOutput>();
        comptes.forEach(compte -> result.add(toDto(compte)));

        return result;
    }

    CompteOutput toDto(Compte compte);

    Compte toEntity(CompteInput dto);
}
