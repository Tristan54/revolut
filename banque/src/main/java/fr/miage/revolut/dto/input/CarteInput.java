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
public class CarteInput {

    @Range(min = 10, max=Integer.MAX_VALUE, message = "Plafond invalide")
    private Integer plafond;

    @NotNull(message = "Veuillez renseigner si la carte doit être soumise à la localisation")
    private boolean localisation;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;

    @NotNull(message = "Veuillez renseigner si la carte est virtuelle")
    private boolean virtuelle;
}
