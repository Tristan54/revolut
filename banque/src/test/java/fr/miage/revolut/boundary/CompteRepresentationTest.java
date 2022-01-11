package fr.miage.revolut.boundary;

import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.boundary.CompteRessource;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.mappers.CompteMapper;
import fr.miage.revolut.service.Generator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;


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
		ressource.deleteAll();
        ressource.save(new Compte("890fb578-6374-46dd-b4c3-5c23f62896f9","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0)));
		RestAssured.port = 8080;
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
	public void getOne() throws Exception {
		Compte compte = new Compte("890fb578-6374-46dd-b4c3-5c23f62896f9","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
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
	public void getForbidden() throws Exception {
		given().header(getHeaderAuthorization()).when().get("/comptes/12").then().statusCode(HttpStatus.SC_FORBIDDEN);
	}

    @Test
    public void getUnauthorized() throws Exception {
        given().when().get("/comptes/12").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

	@Test
	public void postApi() throws Exception{
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