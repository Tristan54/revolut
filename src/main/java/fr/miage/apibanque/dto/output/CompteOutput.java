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

    @Id
    private String uuid;

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

    @Pattern(regexp = "[0-9]+")
    @Size(min = 10)
    private String numTel;
}
