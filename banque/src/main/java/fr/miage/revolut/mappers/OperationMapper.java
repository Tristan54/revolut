package fr.miage.revolut.mappers;

import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.output.OperationOuput;
import fr.miage.revolut.entity.Operation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationOuput toDto(Operation compte);

    Operation toEntity(OperationInput dto);
}
