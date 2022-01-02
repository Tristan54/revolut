package fr.miage.revolut.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotNull
    @NotBlank
    private String libelle;

    @DecimalMin("0.0")
    private double montant;

    @NotNull
    @NotBlank
    private String nomCrediteur;

    @NotNull
    @NotBlank
    private String ibanCrediteur;

    private String categorie;

    @NotNull
    @NotBlank
    private String pays;
}
