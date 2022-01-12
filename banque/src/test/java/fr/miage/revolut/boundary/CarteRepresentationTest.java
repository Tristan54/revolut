package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.CarteInput;
import fr.miage.revolut.dto.input.CarteUpdate;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.entity.Carte;
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
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
@DirtiesContext
class CarteRepresentationTest {

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource compteRessource;

	@Autowired
	CarteRessource carteRessource;

    private String access_token = null;
	private Compte compte;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;
		carteRessource.deleteAll();
		compteRessource.deleteAll();

		compte = new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
        compte.credit(BigDecimal.valueOf(10000));
		compteRessource.save(compte);

		Carte carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		Carte carteVirtuelle = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1500, false, false, true, true, true, compte);

		carteRessource.save(carte);
		carteRessource.save(carteVirtuelle);
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
	public void getAllCarte() throws Exception {
		Carte carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		carteRessource.save(carte);
		Response response = given()
                .header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/cartes/"+carte.getUuid())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString(carte.getUuid()));
	}

	@Test
	public void getOneCarte() throws Exception {
		Response response = given()
				.header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/cartes")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("1000"));
		assertThat(jsonAsString,containsString("1500"));
	}

	@Test
	public void postCarte() throws Exception{
		CarteInput carteInput = new CarteInput(1000, true, true, false);
		Response response = given()
				.header(getHeaderAuthorization())
				.body(this.toJsonString(carteInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/comptes/"+compte.getUuid()+"/cartes")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		String location = response.getHeader("Location");
        given().header(getHeaderAuthorization()).when().get(location).then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void putCarte() throws Exception{
		Carte carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		carteRessource.save(carte);
		CarteUpdate carteUpdate = new CarteUpdate(100, true, true, false, true, true, false);
		Response response = given()
				.header(getHeaderAuthorization())
				.body(this.toJsonString(carteUpdate))
				.contentType(ContentType.JSON)
				.when()
				.put("/comptes/"+compte.getUuid()+"/cartes/"+carte.getUuid())
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		Carte carteUpdated = carteRessource.getById(carte.getUuid());
		assertNotEquals(carte.getPlafond(), carteUpdated.getPlafond());
		assertNotEquals(carte.getCryptogramme(), carteUpdated.getCryptogramme());
		assertNotEquals(carte.isBloque(), carteUpdated.isBloque());
		assertNotEquals(carte.isSupprime(), carteUpdated.isSupprime());
		assertNotEquals(carte.isLocalisation(), carteUpdated.isLocalisation());
		assertNotEquals(carte.isSansContact(), carteUpdated.isSansContact());
	}

	@Test
	public void putCarteNotFound() throws Exception{
		Carte carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		CarteUpdate carteUpdate = new CarteUpdate(100, true, true, false, true, true, false);
		Response response = given()
				.header(getHeaderAuthorization())
				.body(this.toJsonString(carteUpdate))
				.contentType(ContentType.JSON)
				.when()
				.put("/comptes/"+compte.getUuid()+"/cartes/"+carte.getUuid())
				.then()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.extract()
				.response();
	}

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}