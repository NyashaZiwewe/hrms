package hrms.reporting.resource;

import hrms.reporting.dto.EnterpriseDashboardResponse;
import hrms.reporting.dto.ModuleSummaryResponse;
import hrms.reporting.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ReportingResource {

    private final DashboardService dashboardService;

    public ReportingResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public List<ModuleSummaryResponse> listModules() {
        return Arrays.asList(
                new ModuleSummaryResponse("Employee Data Management", "Centralized employee records and lifecycle management", "Implemented"),
                new ModuleSummaryResponse("Payroll Management", "Automated payroll processing with statutory compliance and general ledger integration", "Implemented"),
                new ModuleSummaryResponse("Leave Management", "Leave request workflows, manager actions, balance tracking, and accruals", "Implemented"),
                new ModuleSummaryResponse("Performance Management", "Scorecards, goals, self/manager reviews, action plans, and PIPs", "Implemented"),
                new ModuleSummaryResponse("Employee Self-Service Portal", "Access to payslips, leave balances, and personal profile data", "Scaffolded")
        );
    }

    @GetMapping("/dashboards")
    public EnterpriseDashboardResponse dashboards() {
        return dashboardService.dashboard();
    }
}
