package fr.miage.revolut.dto.validator;

import fr.miage.revolut.dto.input.OperationInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class OperationValidator {

    private final Validator validator;

    public OperationValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(OperationInput operationInput) {
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(operationInput);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
