package fr.miage.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import fr.miage.revolut.boundary.CompteRessource;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.service.CompteService;
import fr.miage.revolut.service.IbanGenerator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompteTests {

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource ressource;
	CompteService service;

	private DateTimeFormatter formatter;

	@BeforeEach
	public void setupContext() {
		ressource.deleteAll();
		RestAssured.port = port;
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}

	@Test
	public void getOne() {
		Compte compte = new Compte(UUID.randomUUID().toString(),"Nom", "Prénom", LocalDate.parse("12/11/1999", formatter), "France", "FR98156470", "0951366785", IbanGenerator.generate("FR"), BigDecimal.valueOf(0));
		ressource.save(compte);
		Response response = when().get("/comptes/"+compte.getUuid())
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
		CompteInput compteInput = new CompteInput("Nom", "Prénom", LocalDate.parse("12/11/1999", formatter), "France", "FR9815647", "password", "0951366785");
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
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}