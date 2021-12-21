package fr.miage.apibanque.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteOutput {

    private String nom;
    private String prenom;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateNaissance;
    private String pays;
    private String numPasseport;
    private String numTel;
    private String iban;
}
