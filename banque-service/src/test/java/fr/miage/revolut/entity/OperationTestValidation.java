package fr.miage.revolut.entity;

import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.dto.validator.CarteValidator;
import fr.miage.revolut.dto.validator.OperationValidator;
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

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
public class OperationTestValidation {

    static private Validator validator;
    static private OperationInput operationInput;
    private OperationValidator operationValidator;

    @BeforeEach
    public void setupContext() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        operationValidator = new OperationValidator(validator);
        operationInput = new OperationInput("opération", BigDecimal.valueOf(10.0), "créditeur", "iban", "categorie", "Pays");
    }

    @DisplayName("Test sur le montant invalide")
    @ParameterizedTest
    @ValueSource(doubles =
            {
                    -50.0,
            })
    void coperationInputMontantInvalideTest(double value) {
        operationInput.setMontant(BigDecimal.valueOf(value));
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> operationValidator.validate(operationInput));
        assertThat(exception.getMessage(), containsString("Montant invalide"));
    }

    @DisplayName("Test sur le montant")
    @ParameterizedTest
    @ValueSource(doubles =
            {
                    10,
                    0.1,
            })
    void coperationInputMontantValideTest(double value) {
        operationInput.setMontant(BigDecimal.valueOf(value));
        assertDoesNotThrow(() -> operationValidator.validate(operationInput));
    }

}
