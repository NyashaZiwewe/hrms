package hrms.performance.service;

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

import java.util.List;

public interface PerformanceManagementService {

    ReportingPeriod createReportingPeriod(ReportingPeriodRequest request);

    Perspective createPerspective(PerspectiveRequest request);

    PerformanceContract createContract(PerformanceContractRequest request);

    PerformanceGoal createGoal(PerformanceGoalRequest request);

    PerformanceContract submitSelfReview(Long contractId, PerformanceReviewRequest request);

    PerformanceContract submitManagerReview(Long contractId, PerformanceReviewRequest request);

    ActionPlan createActionPlan(ActionPlanRequest request);

    PerformanceImprovementPlan createImprovementPlan(PerformanceImprovementPlanRequest request);

    List<PerformanceContract> contracts();

    List<PerformanceGoal> goals(Long contractId);

    List<ActionPlan> actionPlans(Long contractId);

    List<PerformanceImprovementPlan> improvementPlans(Long employeeId);

    List<ReportingPeriod> reportingPeriods();

    List<Perspective> perspectives();

    PerformanceAnalyticsResponse analytics();
}
