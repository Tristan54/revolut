package fr.miage.apibanque.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Compte implements Serializable {

    private static final long serialVersionId = 3456786534L;

    @Id
    private String uuid;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String pays;
    private String numPasseport;
    private String motDePasse;
    private String numTel;
    private String iban;

}
