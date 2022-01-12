package fr.miage.revolut.entity;

import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.validator.CompteValidator;
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
import java.time.LocalDate;

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

    @DisplayName("Test sur le numéro de téléphone invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "+3391367859849867",
                    "33913678598",
                    "+33365",
                    "+33913+678598",
                    "+33 91367854",
                    ""
            })
    void compteInputNumTelInvalideTest(String value) {
        compteInput.setNumTel(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Numéro de téléphone invalide"));
    }

    @DisplayName("Test sur le numéro de téléphone")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "+33913678598494",
                    "+339136",
            })
    void compteInputNumTelValideTest(String value) {
        compteInput.setNumTel(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le numéro de passeport invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "FR10",
                    "111111111",
                    ""
            })
    void compteInputNumpasseportInvalideTest(String value) {
        compteInput.setNumPasseport(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Numéro de passeport invalide"));
    }

    @DisplayName("Test sur le numéro de passeport")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "FR1015484",
                    "FRaRE5124",
            })
    void compteInputNumpasseportValideTest(String value) {
        compteInput.setNumPasseport(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le nom")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "Frank",
                    "Quentin",
            })
    void compteInputNomValideTest(String value) {
        compteInput.setNom(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le prénom")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "Frank",
                    "Quentin",
            })
    void compteInputPrenomValideTest(String value) {
        compteInput.setPrenom(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le pays")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "France",
                    "Allemagne",
            })
    void compteInputPaysValideTest(String value) {
        compteInput.setPays(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le mot de passe")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "ytguhiljk557",
                    "123456",
            })
    void compteInputMotDePasseValideTest(String value) {
        compteInput.setPrenom(value);
        assertDoesNotThrow(() -> compteValidator.validate(compteInput));
    }

    @DisplayName("Test sur le nom invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "",
            })
    void compteInputNomInvalideTest(String value) {
        compteInput.setNom(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Nom invalide"));
    }

    @DisplayName("Test sur le prénom invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "",
            })
    void compteInputPrenomInvalideTest(String value) {
        compteInput.setPrenom(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Prénom invalide"));
    }

    @DisplayName("Test sur le pays invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "",
            })
    void compteInputPaysInvalideTest(String value) {
        compteInput.setPays(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Pays invalide"));
    }

    @DisplayName("Test sur le mot de passe invalide")
    @ParameterizedTest
    @ValueSource(strings =
            {
                    "",
                    "654"
            })
    void compteInputMotDePasseInvalideTest(String value) {
        compteInput.setMotDePasse(value);
        Throwable exception = assertThrows(ConstraintViolationException.class, () -> compteValidator.validate(compteInput));
        assertThat(exception.getMessage(), containsString("Mot de passe invalide"));
    }

}
