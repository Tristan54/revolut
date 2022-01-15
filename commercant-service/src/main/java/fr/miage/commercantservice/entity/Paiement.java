package fr.miage.commercantservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    private String numeroCarte;
    private String cryptogrammeCarte;
    private String codeCarte;
    private String pays;
    private String nomCommercant;
    private String ibanCommercant;
    private String date;
    private BigDecimal montant;
    private boolean sansContact;
    private String categorie;
}
