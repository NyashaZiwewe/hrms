package hrms.performance;

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
        "hrms.performance.service",
        "hrms.performance.resource"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                hrms.audit.AuditApplication.class,
                hrms.employee.EmployeeApplication.class
        })
})
@EntityScan(basePackages = {
        "hrms.audit.entity",
        "hrms.employee.entity",
        "hrms.performance.entity"
})
@EnableJpaRepositories(basePackages = {
        "hrms.audit.repository",
        "hrms.employee.repository",
        "hrms.performance.repository"
})
public class PerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApplication.class, args);
    }
}
