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

    @NotNull
    @Size(min = 16, max = 16, message = "Numéro de la carte invalide")
    private String numeroCarte;

    @Range(min = 3, max = 3, message = "Cryptogramme de la carte invalide")
    private int cryptogrammeCarte;

    @Range(min = 4, max = 4, message = "Code de carte invalide")
    private int codeCarte;

    @NotBlank(message = "Nom du pays invalide")
    private String pays;

    @NotBlank(message = "Nom du commerçant invalide")
    private String nomCommercant;

    @NotBlank(message = "Iban du commerçant invalide")
    private String ibanCommercant;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @NotNull(message = "Date invalide")
    private String date;

    @NotBlank(message = "Libellé invalide")
    private String libelle;

    @Size(min = 0, message = "Montant invalide")
    private BigDecimal montant;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;
}
