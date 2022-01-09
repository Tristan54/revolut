package fr.miage.revolut.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementInput {

    @NotBlank(message = "Il faut renseigner le numéro de la carte")
    @Size(min = 16, max = 16, message = "Numéro de la carte invalide")
    private String numeroCarte;

    @Range(min = 3, max = 3, message = "Cryptogramme de la carcryptogrammete invalide")
    @NotBlank(message = "Il faut renseigner le code de la carte")
    private int cryptogrammeCarte;

    @Range(min = 4, max = 4, message = "Code de carte invalide")
    @NotBlank(message = "Il faut renseigner le code de la carte")
    private int codeCarte;

    @NotBlank(message = "Nom du pays invalide")
    private String pays;

    @NotBlank(message = "Nom du commerçant invalide")
    private String nomCommercant;

    @NotBlank(message = "Iban du commerçant invalide")
    private String ibanCommercant;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull(message = "Il faut renseigner une date et une heure")
    private String date;

    @Size(min = 0, message = "Montant invalide")
    @NotBlank(message = "Il faut renseigner un montant")
    private BigDecimal montant;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;

    private String categorie;
}
