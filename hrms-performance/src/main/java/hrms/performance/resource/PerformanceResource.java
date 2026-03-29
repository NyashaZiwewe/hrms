package hrms.performance.resource;

import hrms.performance.dto.ActionPlanRequest;
import hrms.performance.dto.PerformanceAnalyticsResponse;
import hrms.performance.dto.PerformanceContractRequest;
import hrms.performance.dto.PerformanceGoalRequest;
import hrms.performance.dto.PerformanceImprovementPlanRequest;
import hrms.performance.dto.PerformanceReviewRequest;
import hrms.performance.dto.PerspectiveRequest;
import hrms.performance.dto.ReportingPeriodRequest;
import hrms.performance.entity.ActionPlan;
import hrms.performance.entity.PerformanceContract;
import hrms.performance.entity.PerformanceGoal;
import hrms.performance.entity.PerformanceImprovementPlan;
import hrms.performance.entity.Perspective;
import hrms.performance.entity.ReportingPeriod;
import hrms.performance.service.PerformanceManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceResource {

    private final PerformanceManagementService performanceManagementService;

    public PerformanceResource(PerformanceManagementService performanceManagementService) {
        this.performanceManagementService = performanceManagementService;
    }

    @PostMapping("/reporting-periods")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportingPeriod createReportingPeriod(@Valid @RequestBody ReportingPeriodRequest request) {
        return performanceManagementService.createReportingPeriod(request);
    }

    @GetMapping("/reporting-periods")
    public List<ReportingPeriod> reportingPeriods() {
        return performanceManagementService.reportingPeriods();
    }

    @PostMapping("/perspectives")
    @ResponseStatus(HttpStatus.CREATED)
    public Perspective createPerspective(@Valid @RequestBody PerspectiveRequest request) {
        return performanceManagementService.createPerspective(request);
    }

    @GetMapping("/perspectives")
    public List<Perspective> perspectives() {
        return performanceManagementService.perspectives();
    }

    @PostMapping("/contracts")
    @ResponseStatus(HttpStatus.CREATED)
    public PerformanceContract createContract(@Valid @RequestBody PerformanceContractRequest request) {
        return performanceManagementService.createContract(request);
    }

    @GetMapping("/contracts")
    public List<PerformanceContract> contracts() {
        return performanceManagementService.contracts();
    }

    @PostMapping("/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public PerformanceGoal createGoal(@Valid @RequestBody PerformanceGoalRequest request) {
        return performanceManagementService.createGoal(request);
    }

    @GetMapping("/contracts/{contractId}/goals")
    public List<PerformanceGoal> goals(@PathVariable Long contractId) {
        return performanceManagementService.goals(contractId);
    }

    @PostMapping("/contracts/{contractId}/self-review")
    public PerformanceContract submitSelfReview(@PathVariable Long contractId,
                                                @Valid @RequestBody PerformanceReviewRequest request) {
        return performanceManagementService.submitSelfReview(contractId, request);
    }

    @PostMapping("/contracts/{contractId}/manager-review")
    public PerformanceContract submitManagerReview(@PathVariable Long contractId,
                                                   @Valid @RequestBody PerformanceReviewRequest request) {
        return performanceManagementService.submitManagerReview(contractId, request);
    }

    @PostMapping("/action-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public ActionPlan createActionPlan(@Valid @RequestBody ActionPlanRequest request) {
        return performanceManagementService.createActionPlan(request);
    }

    @GetMapping("/contracts/{contractId}/action-plans")
    public List<ActionPlan> actionPlans(@PathVariable Long contractId) {
        return performanceManagementService.actionPlans(contractId);
    }

    @PostMapping("/improvement-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public PerformanceImprovementPlan createImprovementPlan(@Valid @RequestBody PerformanceImprovementPlanRequest request) {
        return performanceManagementService.createImprovementPlan(request);
    }

    @GetMapping("/employees/{employeeId}/improvement-plans")
    public List<PerformanceImprovementPlan> improvementPlans(@PathVariable Long employeeId) {
        return performanceManagementService.improvementPlans(employeeId);
    }

    @GetMapping("/analytics")
    public PerformanceAnalyticsResponse analytics() {
        return performanceManagementService.analytics();
    }
}
