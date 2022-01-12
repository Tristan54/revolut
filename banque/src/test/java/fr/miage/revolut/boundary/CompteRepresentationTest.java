package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.service.Generator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
@DirtiesContext
class CompteRepresentationTest {

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource ressource;

    private String access_token = null;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;

		ressource.deleteAll();
        ressource.save(new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0)));
	}

    private Header getHeaderAuthorization() throws Exception {
        if(access_token == null) {
            CompteSignIn compteSignIn = new CompteSignIn();
            compteSignIn.setMotDePasse("password");
            compteSignIn.setNumTel("+339136735");
            Response response = given().body(this.toJsonString(compteSignIn)).contentType(ContentType.JSON).
                    when().post("/comptes/connexion").then().extract().response();
            access_token = response.jsonPath().getString("access_token");
        }
        return new Header("Authorization","bearer "+ access_token);
    }

	@Test
	public void connexionFailedIdentifiant() throws Exception {
		CompteSignIn compteSignIn = new CompteSignIn();
		compteSignIn.setMotDePasse("password");
		compteSignIn.setNumTel("");
		Response response = given().body(this.toJsonString(compteSignIn)).contentType(ContentType.JSON).
				when().post("/comptes/connexion").then().statusCode(HttpStatus.SC_BAD_REQUEST).extract().response();
	}

	@Test
	public void connexionFailedMotDePasse() throws Exception {
		CompteSignIn compteSignIn = new CompteSignIn();
		compteSignIn.setMotDePasse("");
		compteSignIn.setNumTel("+339136735");
		Response response = given().body(this.toJsonString(compteSignIn)).contentType(ContentType.JSON).
				when().post("/comptes/connexion").then().statusCode(HttpStatus.SC_BAD_REQUEST).extract().response();
	}

	@Test
	public void getCompte() throws Exception {
		Compte compte = new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
		ressource.save(compte);
		Response response = given()
                .header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("Nom"));
	}

	@Test
	public void getCompteForbidden() throws Exception {
		given().header(getHeaderAuthorization()).when().get("/comptes/12").then().statusCode(HttpStatus.SC_FORBIDDEN);
	}

    @Test
    public void getCompteUnauthorized() throws Exception {
        given().when().get("/comptes/12").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

	@Test
	public void postCompte() throws Exception{
		CompteInput compteInput = new CompteInput("Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR6778895", "password", "+339136735");
		Response response = given()
				.body(this.toJsonString(compteInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/comptes")
				.then()
				.statusCode(HttpStatus.SC_CONFLICT)
				.extract()
				.response();

		String location = response.getHeader("Location");
        given().header(getHeaderAuthorization()).when().get(location).then().statusCode(HttpStatus.SC_OK);
	}

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}