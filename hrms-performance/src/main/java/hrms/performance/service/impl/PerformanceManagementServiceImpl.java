package hrms.performance.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import hrms.common.util.DateUtils;
import hrms.common.util.StringUtils;
import hrms.employee.entity.Employee;
import hrms.employee.service.EmployeeService;
import hrms.performance.dto.ActionPlanRequest;
import hrms.performance.dto.GoalScoreRequest;
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
import hrms.performance.model.ApprovalStatus;
import hrms.performance.model.LockStatus;
import hrms.performance.model.PerformanceStatus;
import hrms.performance.repository.ActionPlanRepository;
import hrms.performance.repository.PerformanceContractRepository;
import hrms.performance.repository.PerformanceGoalRepository;
import hrms.performance.repository.PerformanceImprovementPlanRepository;
import hrms.performance.repository.PerspectiveRepository;
import hrms.performance.repository.ReportingPeriodRepository;
import hrms.performance.repository.StrategicObjectiveRepository;
import hrms.performance.service.PerformanceManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.LocalDate;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PerformanceManagementServiceImpl implements PerformanceManagementService {

    private final EmployeeService employeeService;
    private final ReportingPeriodRepository reportingPeriodRepository;
    private final PerspectiveRepository perspectiveRepository;
    private final StrategicObjectiveRepository strategicObjectiveRepository;
    private final PerformanceContractRepository performanceContractRepository;
    private final PerformanceGoalRepository performanceGoalRepository;
    private final ActionPlanRepository actionPlanRepository;
    private final PerformanceImprovementPlanRepository performanceImprovementPlanRepository;
    private final AuditTrailService auditTrailService;

    public PerformanceManagementServiceImpl(EmployeeService employeeService,
                                        ReportingPeriodRepository reportingPeriodRepository,
                                        PerspectiveRepository perspectiveRepository,
                                        StrategicObjectiveRepository strategicObjectiveRepository,
                                        PerformanceContractRepository performanceContractRepository,
                                        PerformanceGoalRepository performanceGoalRepository,
                                        ActionPlanRepository actionPlanRepository,
                                        PerformanceImprovementPlanRepository performanceImprovementPlanRepository,
                                        AuditTrailService auditTrailService) {
        this.employeeService = employeeService;
        this.reportingPeriodRepository = reportingPeriodRepository;
        this.perspectiveRepository = perspectiveRepository;
        this.strategicObjectiveRepository = strategicObjectiveRepository;
        this.performanceContractRepository = performanceContractRepository;
        this.performanceGoalRepository = performanceGoalRepository;
        this.actionPlanRepository = actionPlanRepository;
        this.performanceImprovementPlanRepository = performanceImprovementPlanRepository;
        this.auditTrailService = auditTrailService;
    }

    public ReportingPeriod createReportingPeriod(ReportingPeriodRequest request) {
        validateReportingPeriod(request);
        if (request.isActive()) {
            deactivateOtherReportingPeriods(null);
        }
        ReportingPeriod period = new ReportingPeriod();
        period.setName(request.getName());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setActive(request.isActive());
        ReportingPeriod saved = reportingPeriodRepository.save(period);
        auditTrailService.log("PERFORMANCE", "ReportingPeriod", String.valueOf(saved.getId()), "CREATE",
                "Created reporting period " + saved.getName());
        return saved;
    }

    public ReportingPeriod updateReportingPeriod(Long reportingPeriodId, ReportingPeriodRequest request) {
        validateReportingPeriodForUpdate(reportingPeriodId, request);
        if (request.isActive()) {
            deactivateOtherReportingPeriods(reportingPeriodId);
        }
        ReportingPeriod period = reportingPeriod(reportingPeriodId);
        period.setName(request.getName());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setActive(request.isActive());
        ReportingPeriod saved = reportingPeriodRepository.save(period);
        auditTrailService.log("PERFORMANCE", "ReportingPeriod", String.valueOf(saved.getId()), "UPDATE",
                "Updated reporting period " + saved.getName());
        return saved;
    }

    public void deleteReportingPeriod(Long reportingPeriodId) {
        ReportingPeriod period = reportingPeriod(reportingPeriodId);
        if (performanceContractRepository.existsByReportingPeriodId(reportingPeriodId)) {
            throw new OperationNotAllowedException("Cannot delete reporting period because it is linked to performance contracts");
        }
        if (strategicObjectiveRepository.existsByReportingPeriodId(reportingPeriodId)) {
            throw new OperationNotAllowedException("Cannot delete reporting period because it is linked to strategic goals");
        }
        if (performanceImprovementPlanRepository.existsByReportingPeriodId(reportingPeriodId)) {
            throw new OperationNotAllowedException("Cannot delete reporting period because it is linked to improvement plans");
        }
        reportingPeriodRepository.delete(period);
        auditTrailService.log("PERFORMANCE", "ReportingPeriod", String.valueOf(period.getId()), "DELETE",
                "Deleted reporting period " + period.getName());
    }

    @Transactional(readOnly = true)
    public ReportingPeriod reportingPeriod(Long reportingPeriodId) {
        return reportingPeriodRepository.findById(reportingPeriodId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + reportingPeriodId));
    }

    public Perspective createPerspective(PerspectiveRequest request) {
        Perspective perspective = new Perspective();
        perspective.setName(request.getName());
        perspective.setDescription(request.getDescription());
        Perspective saved = perspectiveRepository.save(perspective);
        auditTrailService.log("PERFORMANCE", "Perspective", String.valueOf(saved.getId()), "CREATE",
                "Created perspective " + saved.getName());
        return saved;
    }

    public Perspective updatePerspective(Long perspectiveId, PerspectiveRequest request) {
        Perspective perspective = perspective(perspectiveId);
        perspective.setName(request.getName());
        perspective.setDescription(request.getDescription());
        Perspective saved = perspectiveRepository.save(perspective);
        auditTrailService.log("PERFORMANCE", "Perspective", String.valueOf(saved.getId()), "UPDATE",
                "Updated perspective " + saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public Perspective perspective(Long perspectiveId) {
        return perspectiveRepository.findById(perspectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Perspective not found: " + perspectiveId));
    }

    public StrategicObjective createStrategicObjective(StrategicObjectiveRequest request) {
        ReportingPeriod reportingPeriod = reportingPeriodRepository.findById(request.getReportingPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + request.getReportingPeriodId()));
        Perspective perspective = perspectiveRepository.findById(request.getPerspectiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Perspective not found: " + request.getPerspectiveId()));
        StrategicObjective strategicObjective = new StrategicObjective();
        strategicObjective.setReportingPeriod(reportingPeriod);
        strategicObjective.setPerspective(perspective);
        strategicObjective.setName(request.getName());
        strategicObjective.setDescription(request.getDescription());
        strategicObjective.setActive(request.isActive());
        StrategicObjective saved = strategicObjectiveRepository.save(strategicObjective);
        auditTrailService.log("PERFORMANCE", "StrategicObjective", String.valueOf(saved.getId()), "CREATE",
                "Created strategic objective " + saved.getName());
        return saved;
    }

    public StrategicObjective updateStrategicObjective(Long strategicObjectiveId, StrategicObjectiveRequest request) {
        StrategicObjective strategicObjective = strategicObjective(strategicObjectiveId);
        ReportingPeriod reportingPeriod = reportingPeriodRepository.findById(request.getReportingPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + request.getReportingPeriodId()));
        Perspective perspective = perspectiveRepository.findById(request.getPerspectiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Perspective not found: " + request.getPerspectiveId()));
        strategicObjective.setReportingPeriod(reportingPeriod);
        strategicObjective.setPerspective(perspective);
        strategicObjective.setName(request.getName());
        strategicObjective.setDescription(request.getDescription());
        strategicObjective.setActive(request.isActive());
        StrategicObjective saved = strategicObjectiveRepository.save(strategicObjective);
        auditTrailService.log("PERFORMANCE", "StrategicObjective", String.valueOf(saved.getId()), "UPDATE",
                "Updated strategic objective " + saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public StrategicObjective strategicObjective(Long strategicObjectiveId) {
        return strategicObjectiveRepository.findById(strategicObjectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Strategic objective not found: " + strategicObjectiveId));
    }

    public PerformanceContract createContract(PerformanceContractRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        ReportingPeriod reportingPeriod = reportingPeriodRepository.findById(request.getReportingPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + request.getReportingPeriodId()));
        PerformanceContract contract = new PerformanceContract();
        contract.setEmployee(employee);
        contract.setReportingPeriod(reportingPeriod);
        contract.setTitle(request.getTitle());
        contract.setStatus(PerformanceStatus.IN_PROGRESS);
        contract.setApprovalStatus(ApprovalStatus.PENDING_MANAGER);
        PerformanceContract saved = performanceContractRepository.save(contract);
        auditTrailService.log("PERFORMANCE", "PerformanceContract", String.valueOf(saved.getId()), "CREATE",
                "Created performance contract for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public PerformanceContract updateContract(Long contractId, PerformanceContractRequest request) {
        PerformanceContract contract = contract(contractId);
        Employee employee = employeeService.findById(request.getEmployeeId());
        ReportingPeriod reportingPeriod = reportingPeriodRepository.findById(request.getReportingPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + request.getReportingPeriodId()));
        contract.setEmployee(employee);
        contract.setReportingPeriod(reportingPeriod);
        contract.setTitle(request.getTitle());
        contract.setUpdatedAt(DateUtils.now());
        PerformanceContract saved = performanceContractRepository.save(contract);
        auditTrailService.log("PERFORMANCE", "PerformanceContract", String.valueOf(saved.getId()), "UPDATE",
                "Updated performance contract " + saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public PerformanceContract contract(Long contractId) {
        return performanceContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Performance contract not found: " + contractId));
    }

    public PerformanceGoal createGoal(PerformanceGoalRequest request) {
        PerformanceContract contract = performanceContractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Performance contract not found: " + request.getContractId()));
        StrategicObjective strategicObjective = strategicObjectiveRepository.findById(request.getStrategicObjectiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Strategic objective not found: " + request.getStrategicObjectiveId()));
        if (!strategicObjective.getReportingPeriod().getId().equals(contract.getReportingPeriod().getId())) {
            throw new OperationNotAllowedException("Strategic objective does not belong to contract reporting period");
        }
        PerformanceGoal goal = new PerformanceGoal();
        goal.setContract(contract);
        goal.setPerspective(strategicObjective.getPerspective());
        goal.setStrategicObjective(strategicObjective);
        goal.setAssignedEmployee(contract.getEmployee());
        goal.setName(request.getName());
        goal.setAllocatedWeight(request.getAllocatedWeight());
        goal.setMeasure(request.getMeasure());
        goal.setTargetValue(request.getTargetValue());
        goal.setSkillGap(request.getSkillGap());
        goal.setTrainingNeed(request.getTrainingNeed());
        PerformanceGoal saved = performanceGoalRepository.save(goal);
        recalculateScores(contract);
        auditTrailService.log("PERFORMANCE", "PerformanceGoal", String.valueOf(saved.getId()), "CREATE",
                "Created goal " + saved.getName() + " for contract " + contract.getId());
        return saved;
    }

    public PerformanceContract submitSelfReview(Long contractId, PerformanceReviewRequest request) {
        PerformanceContract contract = getContract(contractId);
        for (GoalScoreRequest goalScore : request.getGoalScores()) {
            PerformanceGoal goal = getGoal(goalScore.getGoalId());
            assertContract(goal, contractId);
            goal.setSelfScore(goalScore.getScore());
            performanceGoalRepository.save(goal);
        }
        contract.setEmployeeComment(request.getComment());
        contract.setEmployeeScore(average(contractId, ReviewStage.SELF));
        contract.setStatus(PerformanceStatus.SELF_REVIEWED);
        contract.setUpdatedAt(DateUtils.now());
        PerformanceContract saved = performanceContractRepository.save(contract);
        recalculateScores(saved);
        auditTrailService.log("PERFORMANCE", "PerformanceContract", String.valueOf(saved.getId()), "SELF_REVIEW",
                "Submitted self review for contract " + saved.getId());
        return saved;
    }

    public PerformanceContract submitManagerReview(Long contractId, PerformanceReviewRequest request) {
        PerformanceContract contract = getContract(contractId);
        for (GoalScoreRequest goalScore : request.getGoalScores()) {
            PerformanceGoal goal = getGoal(goalScore.getGoalId());
            assertContract(goal, contractId);
            goal.setManagerScore(goalScore.getScore());
            goal.setAgreedScore(scale(goal.getSelfScore().add(goal.getManagerScore()).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)));
            goal.setModeratedScore(goal.getAgreedScore());
            performanceGoalRepository.save(goal);
        }
        contract.setManagerComment(request.getComment());
        contract.setManagerScore(average(contractId, ReviewStage.MANAGER));
        contract.setAgreedScore(average(contractId, ReviewStage.AGREED));
        contract.setModeratedScore(contract.getAgreedScore());
        contract.setStatus(PerformanceStatus.MANAGER_REVIEWED);
        contract.setApprovalStatus(ApprovalStatus.APPROVED);
        contract.setUpdatedAt(DateUtils.now());
        PerformanceContract saved = performanceContractRepository.save(contract);
        recalculateScores(saved);
        auditTrailService.log("PERFORMANCE", "PerformanceContract", String.valueOf(saved.getId()), "MANAGER_REVIEW",
                "Submitted manager review for contract " + saved.getId());
        return saved;
    }

    public ActionPlan createActionPlan(ActionPlanRequest request) {
        ActionPlan actionPlan = new ActionPlan();
        actionPlan.setContract(getContract(request.getContractId()));
        actionPlan.setManager(employeeService.findById(request.getManagerId()));
        actionPlan.setName(request.getName());
        actionPlan.setDescription(request.getDescription());
        actionPlan.setMeasureOfSuccess(request.getMeasureOfSuccess());
        actionPlan.setStartDate(request.getStartDate());
        actionPlan.setEndDate(request.getEndDate());
        actionPlan.setProgress(request.getProgress());
        actionPlan.setStatus(request.getStatus());
        ActionPlan saved = actionPlanRepository.save(actionPlan);
        auditTrailService.log("PERFORMANCE", "ActionPlan", String.valueOf(saved.getId()), "CREATE",
                "Created action plan " + saved.getName());
        return saved;
    }

    public PerformanceImprovementPlan createImprovementPlan(PerformanceImprovementPlanRequest request) {
        PerformanceImprovementPlan plan = new PerformanceImprovementPlan();
        plan.setEmployee(employeeService.findById(request.getEmployeeId()));
        plan.setReportingPeriod(reportingPeriodRepository.findById(request.getReportingPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting period not found: " + request.getReportingPeriodId())));
        plan.setTargetArea(request.getTargetArea());
        plan.setConcern(request.getConcern());
        plan.setExpectedStandard(request.getExpectedStandard());
        plan.setAgreedAction(request.getAgreedAction());
        plan.setRequiredSupport(request.getRequiredSupport());
        plan.setReviewNotes(request.getReviewNotes());
        plan.setProgress(request.getProgress());
        plan.setStatus(request.getStatus());
        plan.setEndDate(request.getEndDate());
        PerformanceImprovementPlan saved = performanceImprovementPlanRepository.save(plan);
        auditTrailService.log("PERFORMANCE", "PerformanceImprovementPlan", String.valueOf(saved.getId()), "CREATE",
                "Created PIP for employee " + saved.getEmployee().getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<PerformanceContract> contracts() {
        return performanceContractRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PerformanceGoal> goals(Long contractId) {
        return performanceGoalRepository.findByContractId(contractId);
    }

    @Transactional(readOnly = true)
    public List<ActionPlan> actionPlans(Long contractId) {
        return actionPlanRepository.findByContractId(contractId);
    }

    @Transactional(readOnly = true)
    public List<PerformanceImprovementPlan> improvementPlans(Long employeeId) {
        return performanceImprovementPlanRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<ReportingPeriod> reportingPeriods() {
        return reportingPeriodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Perspective> perspectives() {
        return perspectiveRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StrategicObjective> strategicObjectives() {
        return strategicObjectiveRepository.findAllByOrderByReportingPeriodStartDateDescPerspectiveNameAscNameAsc();
    }

    @Transactional(readOnly = true)
    public List<StrategicObjective> strategicObjectives(Long reportingPeriodId) {
        return strategicObjectiveRepository.findByReportingPeriodIdOrderByPerspectiveNameAscNameAsc(reportingPeriodId);
    }

    @Transactional(readOnly = true)
    public PerformanceAnalyticsResponse analytics() {
        List<PerformanceContract> contracts = performanceContractRepository.findAll();
        List<PerformanceGoal> goals = performanceGoalRepository.findAll();
        List<ActionPlan> actionPlans = actionPlanRepository.findAll();
        List<PerformanceImprovementPlan> pips = performanceImprovementPlanRepository.findAll();

        BigDecimal totalWeighted = BigDecimal.ZERO;
        for (PerformanceContract contract : contracts) {
            totalWeighted = totalWeighted.add(contract.getWeightedScore());
        }
        BigDecimal averageWeighted = contracts.isEmpty()
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : scale(totalWeighted.divide(new BigDecimal(contracts.size()), 2, RoundingMode.HALF_UP));

        long goalsWithTrainingNeeds = goals.stream()
                .filter(goal -> StringUtils.hasText(goal.getTrainingNeed()))
                .count();

        return PerformanceAnalyticsResponse.builder()
                .contracts(contracts.size())
                .selfReviewedContracts(contracts.stream().filter(contract -> contract.getStatus() == PerformanceStatus.SELF_REVIEWED
                        || contract.getStatus() == PerformanceStatus.MANAGER_REVIEWED
                        || contract.getStatus() == PerformanceStatus.AGREED
                        || contract.getStatus() == PerformanceStatus.MODERATED
                        || contract.getStatus() == PerformanceStatus.CLOSED).count())
                .managerReviewedContracts(contracts.stream().filter(contract -> contract.getStatus() == PerformanceStatus.MANAGER_REVIEWED
                        || contract.getStatus() == PerformanceStatus.AGREED
                        || contract.getStatus() == PerformanceStatus.MODERATED
                        || contract.getStatus() == PerformanceStatus.CLOSED).count())
                .actionPlans(actionPlans.size())
                .improvementPlans(pips.size())
                .goalsWithTrainingNeeds(goalsWithTrainingNeeds)
                .averageWeightedScore(averageWeighted)
                .build();
    }

    private void recalculateScores(PerformanceContract contract) {
        contract.setEmployeeScore(average(contract.getId(), ReviewStage.SELF));
        contract.setManagerScore(average(contract.getId(), ReviewStage.MANAGER));
        contract.setAgreedScore(average(contract.getId(), ReviewStage.AGREED));
        contract.setModeratedScore(average(contract.getId(), ReviewStage.MODERATED));
        contract.setWeightedScore(scale(contract.getModeratedScore().multiply(new BigDecimal("20"))));
        contract.setUpdatedAt(DateUtils.now());
        performanceContractRepository.save(contract);
    }

    private BigDecimal average(Long contractId, ReviewStage stage) {
        List<PerformanceGoal> goals = performanceGoalRepository.findByContractId(contractId);
        if (goals.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PerformanceGoal goal : goals) {
            switch (stage) {
                case SELF:
                    total = total.add(goal.getSelfScore());
                    break;
                case MANAGER:
                    total = total.add(goal.getManagerScore());
                    break;
                case AGREED:
                    total = total.add(goal.getAgreedScore());
                    break;
                case MODERATED:
                    total = total.add(goal.getModeratedScore());
                    break;
                default:
                    break;
            }
        }
        return scale(total.divide(new BigDecimal(goals.size()), 2, RoundingMode.HALF_UP));
    }

    private PerformanceContract getContract(Long contractId) {
        return performanceContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Performance contract not found: " + contractId));
    }

    private PerformanceGoal getGoal(Long goalId) {
        return performanceGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Performance goal not found: " + goalId));
    }

    private void assertContract(PerformanceGoal goal, Long contractId) {
        if (!goal.getContract().getId().equals(contractId)) {
            throw new OperationNotAllowedException("Goal does not belong to contract " + contractId);
        }
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateReportingPeriod(ReportingPeriodRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new OperationNotAllowedException("Reporting period name is required");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new OperationNotAllowedException("Reporting period start date and end date are required");
        }
        String name = request.getName().trim();
        if (reportingPeriodRepository.existsByNameIgnoreCase(name)) {
            throw new OperationNotAllowedException("A reporting period with that name already exists");
        }
        if (toLocalDate(request.getEndDate()).isBefore(toLocalDate(request.getStartDate()))) {
            throw new OperationNotAllowedException("Reporting period end date cannot be before start date");
        }
        request.setName(name);
    }

    private void validateReportingPeriodForUpdate(Long reportingPeriodId, ReportingPeriodRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new OperationNotAllowedException("Reporting period name is required");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new OperationNotAllowedException("Reporting period start date and end date are required");
        }
        String name = request.getName().trim();
        if (reportingPeriodRepository.existsByNameIgnoreCaseAndIdNot(name, reportingPeriodId)) {
            throw new OperationNotAllowedException("A reporting period with that name already exists");
        }
        if (toLocalDate(request.getEndDate()).isBefore(toLocalDate(request.getStartDate()))) {
            throw new OperationNotAllowedException("Reporting period end date cannot be before start date");
        }
        request.setName(name);
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void deactivateOtherReportingPeriods(Long currentId) {
        List<ReportingPeriod> periods = reportingPeriodRepository.findAll();
        for (ReportingPeriod period : periods) {
            if (currentId == null || !period.getId().equals(currentId)) {
                period.setActive(false);
            }
        }
    }

    private enum ReviewStage {
        SELF,
        MANAGER,
        AGREED,
        MODERATED
    }
}
