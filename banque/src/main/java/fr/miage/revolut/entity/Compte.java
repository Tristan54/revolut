package fr.miage.revolut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Compte  {

    @Id
    private String uuid;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String pays;
    private String numPasseport;
    private String numTel;
    @Column(unique=true)
    private String iban;
    private double montant;
}
