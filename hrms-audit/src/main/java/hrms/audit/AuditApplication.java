package hrms.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "hrms.audit.service",
        "hrms.audit.resource"
})
@EntityScan(basePackages = {
        "hrms.audit.entity"
})
@EnableJpaRepositories(basePackages = {
        "hrms.audit.repository"
})
public class AuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }
}
