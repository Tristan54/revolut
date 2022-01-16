package fr.miage.revolut.mapper;

import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.output.OperationOutput;
import fr.miage.revolut.entity.Operation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationOutput toDto(Operation compte);

    Operation toEntity(OperationInput dto);
}
