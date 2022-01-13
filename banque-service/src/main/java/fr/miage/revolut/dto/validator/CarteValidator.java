package fr.miage.revolut.dto.validator;

import fr.miage.revolut.dto.input.CarteInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarteValidator {

    private final Validator validator;

    public void validate(CarteInput carteInput) {
        Set<ConstraintViolation<CarteInput>> violations = validator.validate(carteInput);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
