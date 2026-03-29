package hrms.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hrmsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HRMS API")
                        .description("API documentation for the Human Resource Management System")
                        .version("v1")
                        .contact(new Contact().name("HRMS")));
    }
}
