package fr.miage.apibanque.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
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
public class CompteInput {

    @NotNull
    @NotBlank
    private String nom;

    @Size(min = 2)
    private String prenom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate dateNaissance;

    @Size(min = 2)
    private String pays;

    @Size(min = 9, max=9)
    private String numPasseport;

    @NotNull
    private String motDePasse;

    @Pattern(regexp = "[0-9]+")
    @Size(min = 10)
    private String numTel;
}
