package uz.app.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;



@OpenAPIDefinition(
        info = @Info(
                title = "Book shop project",
                version = "${api.version}",
                contact = @Contact(
                        name = "Adashov Muxriddin", email = "developmuxriddin@gmail.com", url = "https://github.com/Muxriddin071103/book-shop.git"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://springdoc.org"),
                termsOfService = "http://swagger.io/terms/",
                description = "Book shop project"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Book Shop"
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")

)
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

@Configuration
public class OpenApiConfig {
}
