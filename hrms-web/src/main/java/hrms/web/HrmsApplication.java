package hrms.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "hrms.web",
        "hrms.employee.config",
        "hrms.audit.service",
        "hrms.audit.resource",
        "hrms.employee.service",
        "hrms.employee.resource",
        "hrms.leave.service",
        "hrms.leave.resource",
        "hrms.payroll.service",
        "hrms.payroll.resource",
        "hrms.performance.service",
        "hrms.performance.resource",
        "hrms.reporting.service",
        "hrms.reporting.resource"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                hrms.audit.AuditApplication.class,
                hrms.employee.EmployeeApplication.class,
                hrms.leave.LeaveApplication.class,
                hrms.payroll.PayrollApplication.class,
                hrms.performance.PerformanceApplication.class,
                hrms.reporting.ReportingApplication.class
        })
})
@EntityScan(basePackages = {
        "hrms.audit.entity",
        "hrms.employee.entity",
        "hrms.leave.entity",
        "hrms.payroll.entity",
        "hrms.performance.entity"
})
@EnableJpaRepositories(basePackages = {
        "hrms.audit.repository",
        "hrms.employee.repository",
        "hrms.leave.repository",
        "hrms.payroll.repository",
        "hrms.performance.repository"
})
public class HrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmsApplication.class, args);
    }
}
