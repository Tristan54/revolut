package fr.miage.revolut.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private boolean supprime;
    private boolean localisation;
    private boolean sansContact;
    private boolean virtuelle;
}
