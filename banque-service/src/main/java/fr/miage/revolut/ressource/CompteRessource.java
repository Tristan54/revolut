package fr.miage.revolut.ressource;

import fr.miage.revolut.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompteRessource extends JpaRepository<Compte, String> {

    Compte findByNumTel(String numTel);

    Optional<Compte> findByIban(String iban);

}
