package hrms.payroll;

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
        "hrms.leave.config",
        "hrms.leave.service",
        "hrms.payroll.config",
        "hrms.payroll.service",
        "hrms.payroll.resource"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                hrms.audit.AuditApplication.class,
                hrms.employee.EmployeeApplication.class,
                hrms.leave.LeaveApplication.class
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
public class PayrollApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollApplication.class, args);
    }
}
