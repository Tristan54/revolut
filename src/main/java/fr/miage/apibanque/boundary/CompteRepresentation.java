package fr.miage.apibanque.boundary;

import fr.miage.apibanque.entity.Compte;
import fr.miage.apibanque.dto.input.CompteInput;
import fr.miage.apibanque.dto.validator.CompteValidator;
import fr.miage.apibanque.assembler.CompteAssembler;
import fr.miage.apibanque.service.CompteService;
import fr.miage.apibanque.service.IbanGenerator;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    // mettre un système de service

    private final CompteService service;
    private final CompteAssembler assembler;
    private final CompteValidator validator;

    public CompteRepresentation(CompteService service, CompteAssembler assembler, CompteValidator validator) {
        this.service = service;
        this.assembler = assembler;
        this.validator = validator;
    }

    // GET one
    @GetMapping(value="/{compteId}")
    public ResponseEntity<?> getOneCompte(@PathVariable("compteId") String id) {
        return Optional.ofNullable(service.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> addCompte(@RequestBody @Valid CompteInput compte)  {
        Compte compte2Save = new Compte(
                UUID.randomUUID().toString(),
                compte.getNom(),
                compte.getPrenom(),
                compte.getDateNaissance(),
                compte.getPays(),
                compte.getNumPasseport(),
                compte.getMotDePasse(),
                compte.getNumTel(),
                IbanGenerator.generate("FR")
        );
        Compte saved = service.save(compte2Save);
        URI location = linkTo(CompteRepresentation.class).slash(saved.getUuid()).toUri();
        return ResponseEntity.created(location).build();
    }
}
