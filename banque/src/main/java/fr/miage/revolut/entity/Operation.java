package fr.miage.revolut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    private String uuid;
    private LocalDateTime date;
    private String libelle;
    private BigDecimal montant;
    private BigDecimal taux;
    private String nomCrediteur;
    private String ibanCrediteur;
    private String nomDebiteur;
    private String ibanDebiteur;
    private String categorie;
    private String pays;
}
