package fr.miage.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.boundary.CompteRepresentation;
import fr.miage.revolut.boundary.CompteRessource;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.service.CompteService;
import fr.miage.revolut.service.Generator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CompteTests extends AccessTokenProvider{

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource ressource;

	@Autowired
	CompteService service;

	@Autowired
	CompteRepresentation representation;

	@BeforeEach
	public void setupContext() {
		ressource.deleteAll();
		RestAssured.port = 8080;
	}

	@Test
	public void getOne() {
		Compte compte = new Compte("a2d239e0-a0fa-4514-9831-7114cc74cd93","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
		ressource.save(compte);
		Response response = given()
				.when().get("/comptes/"+compte.getUuid())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("Nom"));
	}
	@Test
	public void getNotFound() {
		when().get("/comptes/12").then().statusCode(HttpStatus.SC_NOT_FOUND);
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
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		String location = response.getHeader("Location");

		when().get(location).then().statusCode(HttpStatus.SC_OK);
	}

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}