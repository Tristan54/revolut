package fr.miage.revolut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
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
    private String compteUuid;
    private LocalDateTime date;
    private String libelle;
    private BigDecimal montant;
    private BigDecimal taux;
    private String nomCrediteur;
    private String ibanCrediteur;
    private String categorie;
    private String pays;

}
