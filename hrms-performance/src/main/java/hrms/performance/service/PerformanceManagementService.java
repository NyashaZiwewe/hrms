package hrms.performance.service;

import hrms.performance.dto.ActionPlanRequest;
import hrms.performance.dto.PerformanceAnalyticsResponse;
import hrms.performance.dto.PerformanceContractRequest;
import hrms.performance.dto.PerformanceGoalRequest;
import hrms.performance.dto.PerformanceImprovementPlanRequest;
import hrms.performance.dto.PerformanceReviewRequest;
import hrms.performance.dto.PerspectiveRequest;
import hrms.performance.dto.ReportingPeriodRequest;
import hrms.performance.dto.StrategicObjectiveRequest;
import hrms.performance.entity.ActionPlan;
import hrms.performance.entity.PerformanceContract;
import hrms.performance.entity.PerformanceGoal;
import hrms.performance.entity.PerformanceImprovementPlan;
import hrms.performance.entity.Perspective;
import hrms.performance.entity.ReportingPeriod;
import hrms.performance.entity.StrategicObjective;

import java.util.List;

public interface PerformanceManagementService {

    ReportingPeriod createReportingPeriod(ReportingPeriodRequest request);

    ReportingPeriod updateReportingPeriod(Long reportingPeriodId, ReportingPeriodRequest request);

    ReportingPeriod reportingPeriod(Long reportingPeriodId);

    void deleteReportingPeriod(Long reportingPeriodId);

    Perspective createPerspective(PerspectiveRequest request);

    Perspective updatePerspective(Long perspectiveId, PerspectiveRequest request);

    Perspective perspective(Long perspectiveId);

    StrategicObjective createStrategicObjective(StrategicObjectiveRequest request);

    StrategicObjective updateStrategicObjective(Long strategicObjectiveId, StrategicObjectiveRequest request);

    StrategicObjective strategicObjective(Long strategicObjectiveId);

    PerformanceContract createContract(PerformanceContractRequest request);

    PerformanceContract updateContract(Long contractId, PerformanceContractRequest request);

    PerformanceContract contract(Long contractId);

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

    List<StrategicObjective> strategicObjectives();

    List<StrategicObjective> strategicObjectives(Long reportingPeriodId);

    PerformanceAnalyticsResponse analytics();
}
