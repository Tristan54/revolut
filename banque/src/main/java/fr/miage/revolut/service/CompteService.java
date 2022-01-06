package fr.miage.revolut.service;

import fr.miage.revolut.boundary.CompteRessource;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.entity.Compte;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompteService {

    @Autowired
    private Environment env;

    private final CompteRessource ressource;

    public Optional<Compte> findById(String id){
        return ressource.findById(id);
    }

    public Compte save(CompteInput compte, String userId){
        Compte compte2Save = new Compte(
                userId,
                compte.getNom(),
                compte.getPrenom(),
                compte.getDateNaissance(),
                compte.getPays(),
                compte.getNumPasseport(),
                compte.getNumTel(),
                IbanGenerator.generate(compte.getPays()),
                BigDecimal.valueOf(0)
        );

        return ressource.save(compte2Save);
    }

    public String createCompte(CompteInput compte){

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(env.getProperty("keycloak.auth-server-url"))
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(env.getProperty("app.keycloak.username")).password(env.getProperty("app.keycloak.password"))
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(compte.getNumTel());
        user.setFirstName(compte.getPrenom());
        user.setLastName(compte.getNom());

        RealmResource realmResource = keycloak.realm(env.getProperty("keycloak.realm"));
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);

        String userId = "";
        if (response.getStatus() == 201) {

            userId = CreatedResponseUtil.getCreatedId(response);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(compte.getMotDePasse());

            UserResource userResource = usersRessource.get(userId);

            userResource.resetPassword(passwordCred);

            RoleRepresentation realmRoleUser = realmResource.roles().get(env.getProperty("app.keycloak.role")).toRepresentation();

            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
        }

        return userId;
    }

    public Compte findByNumTel(String numTel) {
        return ressource.findByNumTel(numTel);
    }

    public AccessTokenResponse connexion(CompteSignIn compte){

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", env.getProperty("app.keycloak.clientSecret"));
        clientCredentials.put("grant_type", "password");

        Configuration configuration =
                new Configuration(env.getProperty("keycloak.auth-server-url"), env.getProperty("keycloak.realm"), env.getProperty("keycloak.resource"), clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        return authzClient.obtainAccessToken(compte.getNumTel(), compte.getMotDePasse());
    }

    public Compte update(Compte compte) {
        return ressource.save(compte);
    }
}
