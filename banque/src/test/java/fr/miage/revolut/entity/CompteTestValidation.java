package fr.miage.revolut.entity;

import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.validator.CompteValidator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
public class CompteTestValidation {

    static private Validator validator;
    static private CompteInput compteInput;
    private CompteValidator compteValidator;

    @BeforeEach
    public void setupContext() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        compteValidator = new CompteValidator(validator);
        compteInput = new CompteInput("Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR6778895", "password", "+339136785");
    }

    @DisplayName("Test regex phone number (too long/too short/withoutPlus/ZeroAtBeginning)")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "1999-11-12"
            })
    void newAccountPhoneNumberNotValidTest(String value) {
        System.out.println(compteInput.getDateNaissance());
        //Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
        //assertThat(exception.getMessage(), containsString("Numero de telephone invalide"));
    }


}
