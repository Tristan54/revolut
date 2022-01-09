package fr.miage.revolut.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarteInput {

    @Range(min = 0, max=Integer.MAX_VALUE, message = "Plafond invalide")
    private Integer plafond;

    @NotNull(message = "Veuillez renseigner si la carte doit être soumise à la localisation")
    private boolean localisation;

    @NotNull(message = "Veuillez renseigner si la carte est sans contact")
    private boolean sansContact;

    @NotNull(message = "Veuillez renseigner si la carte est virtuelle")
    private boolean virtuelle;
}
