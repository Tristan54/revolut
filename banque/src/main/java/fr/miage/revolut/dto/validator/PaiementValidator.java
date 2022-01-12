package fr.miage.revolut.dto.validator;

import fr.miage.revolut.dto.input.PaiementInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class PaiementValidator {

    private final Validator validator;

    public PaiementValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(PaiementInput paiementInput) {
        Set<ConstraintViolation<PaiementInput>> violations = validator.validate(paiementInput);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
