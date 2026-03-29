package hrms.reporting.service.impl;

import hrms.audit.repository.AuditTrailRepository;
import hrms.employee.entity.Employee;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.repository.EmployeeRepository;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.model.LeaveStatus;
import hrms.leave.repository.LeaveRequestRepository;
import hrms.payroll.entity.PayrollRun;
import hrms.payroll.repository.PayrollRunRepository;
import hrms.reporting.dto.EnterpriseDashboardResponse;
import hrms.reporting.dto.MetricCardResponse;
import hrms.reporting.dto.RoleDashboardResponse;
import hrms.reporting.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AuditTrailRepository auditTrailRepository;

    public DashboardServiceImpl(EmployeeRepository employeeRepository,
                                PayrollRunRepository payrollRunRepository,
                                LeaveRequestRepository leaveRequestRepository,
                                AuditTrailRepository auditTrailRepository) {
        this.employeeRepository = employeeRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.auditTrailRepository = auditTrailRepository;
    }

    public EnterpriseDashboardResponse dashboard() {
        List<Employee> employees = employeeRepository.findAll();
        List<PayrollRun> payrollRuns = payrollRunRepository.findAll();
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();

        long activeHeadcount = employees.stream().filter(employee -> employee.getStatus() == EmploymentStatus.ACTIVE).count();
        long pendingLeave = leaveRequests.stream().filter(request -> request.getStatus() == LeaveStatus.PENDING).count();
        long approvedLeave = leaveRequests.stream().filter(request -> request.getStatus() == LeaveStatus.APPROVED).count();
        BigDecimal totalPayrollExpense = payrollRuns.stream()
                .map(PayrollRun::getGrossPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetPayroll = payrollRuns.stream()
                .map(PayrollRun::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long postedPayrollRuns = payrollRuns.stream().filter(PayrollRun::isJournalPosted).count();
        long auditEvents = auditTrailRepository.count();

        List<RoleDashboardResponse> dashboards = new ArrayList<RoleDashboardResponse>();
        dashboards.add(RoleDashboardResponse.builder()
                .audience("HR")
                .metrics(Arrays.asList(
                        new MetricCardResponse("Headcount", String.valueOf(employees.size()), "Total employees in the centralized database"),
                        new MetricCardResponse("Active Employees", String.valueOf(activeHeadcount), "Employees currently in active service"),
                        new MetricCardResponse("Pending Leave Requests", String.valueOf(pendingLeave), "Leave requests awaiting manager action")
                ))
                .build());
        dashboards.add(RoleDashboardResponse.builder()
                .audience("Payroll")
                .metrics(Arrays.asList(
                        new MetricCardResponse("Payroll Expense", totalPayrollExpense.toPlainString(), "Gross payroll cost across processed runs"),
                        new MetricCardResponse("Net Salaries", totalNetPayroll.toPlainString(), "Total net salaries due across processed runs"),
                        new MetricCardResponse("Posted Payroll Runs", String.valueOf(postedPayrollRuns), "Payroll runs successfully posted to the GL")
                ))
                .build());
        dashboards.add(RoleDashboardResponse.builder()
                .audience("Finance")
                .metrics(Arrays.asList(
                        new MetricCardResponse("Salary Journal Postings", String.valueOf(postedPayrollRuns), "Payroll runs with automatic general ledger postings"),
                        new MetricCardResponse("Audit Events", String.valueOf(auditEvents), "Recorded changes across HR and payroll data"),
                        new MetricCardResponse("Approved Leave Impact", String.valueOf(approvedLeave), "Approved leave requests relevant to payroll planning")
                ))
                .build());
        dashboards.add(RoleDashboardResponse.builder()
                .audience("Management")
                .metrics(Arrays.asList(
                        new MetricCardResponse("Total Workforce", String.valueOf(employees.size()), "Overall workforce count"),
                        new MetricCardResponse("Headcount in Active Service", String.valueOf(activeHeadcount), "Employees actively contributing to operations"),
                        new MetricCardResponse("Change Activity", String.valueOf(auditEvents), "Tracked operational changes for governance oversight")
                ))
                .build());

        return EnterpriseDashboardResponse.builder()
                .dashboards(dashboards)
                .build();
    }
}
