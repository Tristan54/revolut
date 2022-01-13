package fr.miage.revolut.entity;

import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.validator.CarteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
public class CarteTestValidation {

    static private Validator validator;
    static private CarteInput carteInput;
    private CarteValidator carteValidator;

    @BeforeEach
    public void setupContext() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        carteValidator = new CarteValidator(validator);
        carteInput = new CarteInput(1000, false, false, false);
    }

    @DisplayName("Test sur le plafond invalide")
    @ParameterizedTest
    @ValueSource(ints =
            {
                    -50,
                    Integer.MAX_VALUE,
                    5,
            })
    void carteInputPlafondInvalideTest(Integer value) {
        carteInput.setPlafond(value+1);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> carteValidator.validate(carteInput));
        assertThat(exception.getMessage(), containsString("Plafond invalide"));
    }

    @DisplayName("Test sur le plafond")
    @ParameterizedTest
    @ValueSource(ints =
            {
                    10,
                    1000000,
            })
    void carteInputPlafondValideTest(Integer value) {
        carteInput.setPlafond(value);
        assertDoesNotThrow(() -> carteValidator.validate(carteInput));
    }

}
