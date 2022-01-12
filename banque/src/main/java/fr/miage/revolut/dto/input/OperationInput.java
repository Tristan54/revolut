package fr.miage.revolut.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotBlank(message = "Libéllé invalide")
    private String libelle;

    @DecimalMin(value = "0.01", message = "Montant invalide")
    @NotNull(message = "Il faut renseigner le montant de l'opération")
    private BigDecimal montant;

    @NotBlank(message = "Nom du créditeur invalide")
    private String nomCrediteur;

    @NotBlank(message = "Iban du créditeur invalide invalide")
    private String ibanCrediteur;

    private String categorie;

    @NotBlank(message = "Pays invalide")
    private String pays;
}
