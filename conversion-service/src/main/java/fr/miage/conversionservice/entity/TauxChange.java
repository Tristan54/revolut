package fr.miage.conversionservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TauxChange {

    @Id
    private String id;
    private String pays;
    private String code;
    private BigDecimal tauxConversion;
}
