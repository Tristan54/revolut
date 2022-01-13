package fr.miage.revolut.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarteUpdate {

    @Range(min = 0, max=Integer.MAX_VALUE, message = "Plafond invalide")
    @NotNull(message = "Il faut renseigner le plafond de la carte")
    private Integer plafond;

    @NotNull(message = "Il faut renseigner si vous voulez renouveler le cryptogramme de la carte")
    private boolean cryptogramme;

    @NotNull(message = "Il faut renseigner si vous voulez renouveler le code de la carte")
    private boolean code;

    @NotNull(message = "Il faut renseigner si vous voulez activer la localisation de la carte")
    private boolean localisation;

    @NotNull(message = "Il faut renseigner si vous voulez bloquer la carte")
    private boolean bloque;

    @NotNull(message = "Il faut renseigner si vous voulez supprimer la carte")
    private boolean supprime;

    @NotNull(message = "Il faut renseigner si vous voulez chanegr le fonctionnement sans contact de la carte")
    private boolean sansContact;

}
