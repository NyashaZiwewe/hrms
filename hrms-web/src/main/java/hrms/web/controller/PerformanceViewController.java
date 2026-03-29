package hrms.web.controller;

import hrms.common.util.DateUtils;
import hrms.employee.service.EmployeeService;
import hrms.performance.dto.ActionPlanRequest;
import hrms.performance.dto.GoalScoreRequest;
import hrms.performance.dto.PerformanceContractRequest;
import hrms.performance.dto.PerformanceGoalRequest;
import hrms.performance.dto.PerformanceImprovementPlanRequest;
import hrms.performance.dto.PerformanceReviewRequest;
import hrms.performance.entity.PerformanceContract;
import hrms.performance.entity.PerformanceGoal;
import hrms.performance.service.PerformanceManagementService;
import hrms.web.constants.Pages;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/performance")
public class PerformanceViewController {

    private final PerformanceManagementService performanceManagementService;
    private final EmployeeService employeeService;

    public PerformanceViewController(PerformanceManagementService performanceManagementService, EmployeeService employeeService) {
        this.performanceManagementService = performanceManagementService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public ModelAndView performance() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_CONTRACTS);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Performance Contracts");
        modelAndView.addObject("contracts", performanceManagementService.contracts());
        modelAndView.addObject("analytics", performanceManagementService.analytics());
        return modelAndView;
    }

    @GetMapping("/add-contract")
    public ModelAndView addContract() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_CONTRACT);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Performance Contract");
        modelAndView.addObject("performanceContractRequest", new PerformanceContractRequest());
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        return modelAndView;
    }

    @PostMapping("/save-contract")
    public ModelAndView saveContract(@Valid @ModelAttribute PerformanceContractRequest performanceContractRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_CONTRACT);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Performance Contract");
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            return modelAndView;
        }
        PerformanceContract contract = performanceManagementService.createContract(performanceContractRequest);
        return new ModelAndView("redirect:/performance/view-contract/" + contract.getId());
    }

    @GetMapping("/view-contract/{contractId}")
    public ModelAndView viewContract(@PathVariable Long contractId) {
        PerformanceContract contract = getContract(contractId);
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_CONTRACT);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Performance Contract");
        modelAndView.addObject("contract", contract);
        modelAndView.addObject("goals", performanceManagementService.goals(contractId));
        modelAndView.addObject("actionPlans", performanceManagementService.actionPlans(contractId));
        modelAndView.addObject("improvementPlans", performanceManagementService.improvementPlans(contract.getEmployee().getId()));
        return modelAndView;
    }

    @GetMapping("/add-goal/{contractId}")
    public ModelAndView addGoal(@PathVariable Long contractId) {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_GOAL);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Goal");
        PerformanceGoalRequest request = new PerformanceGoalRequest();
        request.setContractId(contractId);
        modelAndView.addObject("performanceGoalRequest", request);
        modelAndView.addObject("contract", getContract(contractId));
        modelAndView.addObject("perspectives", performanceManagementService.perspectives());
        return modelAndView;
    }

    @PostMapping("/save-goal")
    public ModelAndView saveGoal(@Valid @ModelAttribute PerformanceGoalRequest performanceGoalRequest,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_GOAL);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Goal");
            modelAndView.addObject("contract", getContract(performanceGoalRequest.getContractId()));
            modelAndView.addObject("perspectives", performanceManagementService.perspectives());
            return modelAndView;
        }
        performanceManagementService.createGoal(performanceGoalRequest);
        return new ModelAndView("redirect:/performance/view-contract/" + performanceGoalRequest.getContractId());
    }

    @GetMapping("/self-review/{contractId}")
    public ModelAndView selfReview(@PathVariable Long contractId) {
        return buildReviewPage(Pages.SELF_REVIEW, "Submit Self Review", contractId);
    }

    @PostMapping("/self-review/{contractId}")
    public ModelAndView saveSelfReview(@PathVariable Long contractId,
                                       @ModelAttribute PerformanceReviewRequest performanceReviewRequest) {
        performanceManagementService.submitSelfReview(contractId, performanceReviewRequest);
        return new ModelAndView("redirect:/performance/view-contract/" + contractId);
    }

    @GetMapping("/manager-review/{contractId}")
    public ModelAndView managerReview(@PathVariable Long contractId) {
        return buildReviewPage(Pages.MANAGER_REVIEW, "Submit Manager Review", contractId);
    }

    @PostMapping("/manager-review/{contractId}")
    public ModelAndView saveManagerReview(@PathVariable Long contractId,
                                          @ModelAttribute PerformanceReviewRequest performanceReviewRequest) {
        performanceManagementService.submitManagerReview(contractId, performanceReviewRequest);
        return new ModelAndView("redirect:/performance/view-contract/" + contractId);
    }

    @GetMapping("/add-action-plan/{contractId}")
    public ModelAndView addActionPlan(@PathVariable Long contractId) {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_ACTION_PLAN);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Action Plan");
        ActionPlanRequest request = new ActionPlanRequest();
        LocalDate today = DateUtils.today();
        request.setContractId(contractId);
        request.setStartDate(today);
        request.setEndDate(today.plusMonths(1));
        modelAndView.addObject("actionPlanRequest", request);
        modelAndView.addObject("contract", getContract(contractId));
        modelAndView.addObject("employees", employeeService.findAll());
        return modelAndView;
    }

    @PostMapping("/save-action-plan")
    public ModelAndView saveActionPlan(@Valid @ModelAttribute ActionPlanRequest actionPlanRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_ACTION_PLAN);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Action Plan");
            modelAndView.addObject("contract", getContract(actionPlanRequest.getContractId()));
            modelAndView.addObject("employees", employeeService.findAll());
            return modelAndView;
        }
        performanceManagementService.createActionPlan(actionPlanRequest);
        return new ModelAndView("redirect:/performance/view-contract/" + actionPlanRequest.getContractId());
    }

    @GetMapping("/add-improvement-plan/{employeeId}")
    public ModelAndView addImprovementPlan(@PathVariable Long employeeId) {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_IMPROVEMENT_PLAN);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Improvement Plan");
        PerformanceImprovementPlanRequest request = new PerformanceImprovementPlanRequest();
        LocalDate today = DateUtils.today();
        request.setEmployeeId(employeeId);
        request.setEndDate(today.plusMonths(1));
        modelAndView.addObject("performanceImprovementPlanRequest", request);
        modelAndView.addObject("employee", employeeService.findById(employeeId));
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        return modelAndView;
    }

    @PostMapping("/save-improvement-plan")
    public ModelAndView saveImprovementPlan(@Valid @ModelAttribute PerformanceImprovementPlanRequest performanceImprovementPlanRequest,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_IMPROVEMENT_PLAN);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Improvement Plan");
            modelAndView.addObject("employee", employeeService.findById(performanceImprovementPlanRequest.getEmployeeId()));
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            return modelAndView;
        }
        performanceManagementService.createImprovementPlan(performanceImprovementPlanRequest);
        return new ModelAndView("redirect:/performance");
    }

    private ModelAndView buildReviewPage(String viewName, String pageTitle, Long contractId) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        PerformanceContract contract = getContract(contractId);
        List<PerformanceGoal> goals = performanceManagementService.goals(contractId);
        PerformanceReviewRequest request = new PerformanceReviewRequest();
        List<GoalScoreRequest> scores = new ArrayList<GoalScoreRequest>();
        for (PerformanceGoal goal : goals) {
            GoalScoreRequest goalScoreRequest = new GoalScoreRequest();
            goalScoreRequest.setGoalId(goal.getId());
            goalScoreRequest.setScore(BigDecimal.ZERO);
            scores.add(goalScoreRequest);
        }
        request.setGoalScores(scores);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("contract", contract);
        modelAndView.addObject("goals", goals);
        modelAndView.addObject("performanceReviewRequest", request);
        return modelAndView;
    }

    private PerformanceContract getContract(Long contractId) {
        return performanceManagementService.contracts().stream()
                .filter(contract -> contract.getId().equals(contractId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Performance contract not found: " + contractId));
    }
}
