package fr.miage.revolut.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotBlank(message = "Nom du pays invalide")
    private String pays;

    @NotBlank(message = "Nom du commerçant invalide")
    private String nomCommercant;

    @NotBlank(message = "Iban du commerçant invalide")
    private String ibanCommercant;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull(message = "Il faut renseigner une date et une heure")
    private LocalDateTime date;

    @DecimalMin(value = "0", message = "Montant invalide")
    @NotNull(message = "Il faut renseigner un montant")
    private BigDecimal montant;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;

    private String categorie;
}
