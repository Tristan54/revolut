package fr.miage.revolut;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class RevolutApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevolutApplication.class, args);
	}

    @Bean
    public OpenAPI revolutAPI() {
        return new OpenAPI().info(new Info()
                .title("revolut API")
                .version("1.0")
                .description("Documentation de l'API revolut 1.0"));
    }

}
