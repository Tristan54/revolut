package fr.miage.revolut.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteSignIn {

    @NotNull
    private String motDePasse;

    @Pattern(regexp = "^\\+(?:[0-9]?){6,14}[0-9]$")
    private String numTel;
}
