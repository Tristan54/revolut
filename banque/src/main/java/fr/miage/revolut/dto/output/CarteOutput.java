package fr.miage.revolut.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CarteOutput {

    private String numero;
    private int code;
    private int cryptogramme;
    private Integer plafond;
    private boolean bloque;
    private boolean localisation;
    private boolean sansContact;
    private boolean virtuelle;
}
