package hrms.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "hrms.audit.service",
        "hrms.employee.service",
        "hrms.employee.resource"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                hrms.audit.AuditApplication.class
        })
})
@EntityScan(basePackages = {
        "hrms.audit.entity",
        "hrms.employee.entity"
})
@EnableJpaRepositories(basePackages = {
        "hrms.audit.repository",
        "hrms.employee.repository"
})
public class EmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
