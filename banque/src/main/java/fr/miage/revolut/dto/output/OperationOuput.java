package fr.miage.revolut.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.miage.revolut.entity.Compte;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationOuput {

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime date;
    private String libelle;
    private BigDecimal montant;
    private BigDecimal taux;
    private String nomCrediteur;
    private String ibanCrediteur;
    private String categorie;
    private String pays;
}
