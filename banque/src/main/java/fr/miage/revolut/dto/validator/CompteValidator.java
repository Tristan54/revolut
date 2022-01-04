package fr.miage.revolut.dto.validator;

import fr.miage.revolut.dto.input.CompteInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class CompteValidator {

    private Validator validator;

    CompteValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(CompteInput compteInput) {
        Set<ConstraintViolation<CompteInput>> violations = validator.validate(compteInput);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
