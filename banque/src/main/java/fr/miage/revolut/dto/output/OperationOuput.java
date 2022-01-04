package fr.miage.revolut.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationOuput {

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime date;
    private String libelle;
    private double montant;
    private double taux;
    private String nomCrediteur;
    private String ibanCrediteur;
    private String nomDebiteur;
    private String ibanDebiteur;
    private String categorie;
    private String pays;
}
