package fr.miage.revolut.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarteUpdate {

    @Range(min = 0, max=Integer.MAX_VALUE, message = "Plafond invalide")
    private Integer plafond;

    @NotNull
    private boolean cryptogramme;

    @NotNull
    private boolean code;

    @NotNull
    private boolean localisation;

    @NotNull
    private boolean bloque;

    @NotNull
    private boolean sansContact;

}
