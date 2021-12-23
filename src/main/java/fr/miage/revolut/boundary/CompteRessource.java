package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRessource extends JpaRepository<Compte, String> {
}
