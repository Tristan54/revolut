package fr.miage.revolut.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteInput {

    @NotBlank(message = "Nom invalide")
    private String nom;

    @NotBlank(message = "Prénom invalide")
    private String prenom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    @NotBlank(message = "Pays invalide")
    private String pays;

    @Size(min = 9, max=9, message = "Numéro de passeport invalide")
    @NotBlank(message = "Il faut renseigner un numéro de passeport")
    private String numPasseport;

    @NotBlank(message = "Mot de passe invalide")
    private String motDePasse;

    @Pattern(regexp = "^\\+(?:[0-9]?){6,14}[0-9]$", message = "Numéro de téléphone invalide")
    @NotBlank(message = "Il faut renseigner un numéro de téléphone")
    private String numTel;
}
