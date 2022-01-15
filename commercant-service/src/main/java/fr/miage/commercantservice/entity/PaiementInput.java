package fr.miage.commercantservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementInput {

    @NotBlank(message = "Il faut renseigner le numéro de la carte")
    @Size(min = 16, max = 16, message = "Numéro de la carte invalide")
    private String numeroCarte;

    @Pattern(regexp = "[0-9]{3}", message = "Le Cryptogramme de la carte doit contenir 3 numéros")
    private String cryptogrammeCarte;

    @Pattern(regexp = "[0-9]{4}", message = "Le code de la carte doit contenir 4 numéros")
    private String codeCarte;

    @DecimalMin(value = "0", message = "Montant invalide")
    @NotNull(message = "Il faut renseigner un montant")
    private BigDecimal montant;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;
}
