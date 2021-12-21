package fr.miage.apibanque.boundary;

import fr.miage.apibanque.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRessource extends JpaRepository<Compte, String> {
}
