package fr.miage.revolut.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
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
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Date de naissance invalide")
    private LocalDate dateNaissance;

    @NotBlank(message = "Pays invalide")
    private String pays;

    @Size(min = 9, max=9, message = "Numéro de passeport invalide")
    private String numPasseport;

    @NotBlank(message = "Mot de passe invalide")
    private String motDePasse;

    @Pattern(regexp = "^\\+(?:[0-9]?){6,14}[0-9]$", message = "Numéro de téléphone invalide")
    private String numTel;
}
