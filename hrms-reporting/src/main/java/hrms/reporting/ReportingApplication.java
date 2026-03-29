package hrms.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "hrms.reporting.service",
        "hrms.reporting.resource"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                hrms.audit.AuditApplication.class,
                hrms.employee.EmployeeApplication.class,
                hrms.leave.LeaveApplication.class,
                hrms.payroll.PayrollApplication.class
        })
})
@EntityScan(basePackages = {
        "hrms.audit.entity",
        "hrms.employee.entity",
        "hrms.leave.entity",
        "hrms.payroll.entity"
})
@EnableJpaRepositories(basePackages = {
        "hrms.audit.repository",
        "hrms.employee.repository",
        "hrms.leave.repository",
        "hrms.payroll.repository"
})
public class ReportingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingApplication.class, args);
    }
}
