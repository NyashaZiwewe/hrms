package hrms.web.controller;

import hrms.common.util.DateUtils;
import hrms.employee.service.EmployeeService;
import hrms.performance.dto.ActionPlanRequest;
import hrms.performance.dto.GoalScoreRequest;
import hrms.performance.dto.PerformanceContractRequest;
import hrms.performance.dto.PerformanceGoalRequest;
import hrms.performance.dto.PerformanceImprovementPlanRequest;
import hrms.performance.dto.PerformanceReviewRequest;
import hrms.performance.dto.PerspectiveRequest;
import hrms.performance.dto.ReportingPeriodRequest;
import hrms.performance.dto.StrategicObjectiveRequest;
import hrms.performance.entity.PerformanceContract;
import hrms.performance.entity.PerformanceGoal;
import hrms.performance.service.PerformanceManagementService;
import hrms.web.constants.Pages;
import hrms.web.util.PortletUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
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
        ModelAndView modelAndView = new ModelAndView(Pages.PERFORMANCE_DASHBOARD);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Performance Overview");
        modelAndView.addObject("contracts", performanceManagementService.contracts());
        modelAndView.addObject("perspectives", performanceManagementService.perspectives());
        modelAndView.addObject("strategicObjectives", performanceManagementService.strategicObjectives());
        modelAndView.addObject("analytics", performanceManagementService.analytics());
        return modelAndView;
    }

    @GetMapping("/contracts")
    public ModelAndView contracts() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PERFORMANCE_CONTRACTS);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Performance Contracts");
        modelAndView.addObject("contracts", performanceManagementService.contracts());
        return modelAndView;
    }

    @GetMapping("/reporting-periods")
    public ModelAndView reportingPeriods() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_REPORTING_PERIODS);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Reporting Periods");
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        return modelAndView;
    }

    @GetMapping("/reporting-periods/add")
    public ModelAndView addReportingPeriod() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_REPORTING_PERIOD);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Reporting Period");
        modelAndView.addObject("reportingPeriodRequest", new ReportingPeriodRequest());
        return modelAndView;
    }

    @PostMapping("/reporting-periods/save")
    public ModelAndView saveReportingPeriod(@Valid @ModelAttribute ReportingPeriodRequest reportingPeriodRequest,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_REPORTING_PERIOD);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Reporting Period");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        try {
            performanceManagementService.createReportingPeriod(reportingPeriodRequest);
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_REPORTING_PERIOD);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Reporting Period");
            modelAndView.addObject("errorMsgs", Collections.singletonList(exception.getMessage()));
            return modelAndView;
        }
        return redirectWithInfo("/performance/reporting-periods", "Reporting period saved successfully.");
    }

    @GetMapping("/reporting-periods/{reportingPeriodId}")
    public ModelAndView viewReportingPeriod(@PathVariable Long reportingPeriodId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_REPORTING_PERIOD);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Reporting Period");
        modelAndView.addObject("reportingPeriod", performanceManagementService.reportingPeriod(reportingPeriodId));
        return modelAndView;
    }

    @GetMapping("/reporting-periods/{reportingPeriodId}/edit")
    public ModelAndView editReportingPeriod(@PathVariable Long reportingPeriodId) {
        hrms.performance.entity.ReportingPeriod reportingPeriod = performanceManagementService.reportingPeriod(reportingPeriodId);
        ReportingPeriodRequest request = new ReportingPeriodRequest();
        request.setName(reportingPeriod.getName());
        request.setStartDate(reportingPeriod.getStartDate());
        request.setEndDate(reportingPeriod.getEndDate());
        request.setActive(reportingPeriod.isActive());
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_REPORTING_PERIOD);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Edit Reporting Period");
        modelAndView.addObject("reportingPeriodId", reportingPeriodId);
        modelAndView.addObject("reportingPeriodRequest", request);
        return modelAndView;
    }

    @PostMapping("/reporting-periods/{reportingPeriodId}/update")
    public ModelAndView updateReportingPeriod(@PathVariable Long reportingPeriodId,
                                              @Valid @ModelAttribute ReportingPeriodRequest reportingPeriodRequest,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_REPORTING_PERIOD);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Edit Reporting Period");
            modelAndView.addObject("reportingPeriodId", reportingPeriodId);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        try {
            performanceManagementService.updateReportingPeriod(reportingPeriodId, reportingPeriodRequest);
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_REPORTING_PERIOD);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Edit Reporting Period");
            modelAndView.addObject("reportingPeriodId", reportingPeriodId);
            modelAndView.addObject("errorMsgs", Collections.singletonList(exception.getMessage()));
            return modelAndView;
        }
        return redirectWithInfo("/performance/reporting-periods/" + reportingPeriodId,
                "Reporting period updated successfully.");
    }

    @PostMapping("/reporting-periods/{reportingPeriodId}/delete")
    public ModelAndView deleteReportingPeriod(@PathVariable Long reportingPeriodId) {
        try {
            performanceManagementService.deleteReportingPeriod(reportingPeriodId);
        } catch (RuntimeException exception) {
            PortletUtils.addErrorMsg(exception.getMessage());
            return new ModelAndView("redirect:/performance/reporting-periods/" + reportingPeriodId);
        }
        return redirectWithInfo("/performance/reporting-periods", "Reporting period deleted successfully.");
    }

    @GetMapping("/perspectives")
    public ModelAndView perspectives() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PERSPECTIVES);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Perspectives");
        modelAndView.addObject("perspectives", performanceManagementService.perspectives());
        return modelAndView;
    }

    @GetMapping("/perspectives/add")
    public ModelAndView addPerspective() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_PERSPECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Perspective");
        modelAndView.addObject("perspectiveRequest", new PerspectiveRequest());
        return modelAndView;
    }

    @PostMapping("/perspectives/save")
    public ModelAndView savePerspective(@Valid @ModelAttribute PerspectiveRequest perspectiveRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_PERSPECTIVE);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Perspective");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.createPerspective(perspectiveRequest);
        return redirectWithInfo("/performance/perspectives", "Perspective saved successfully.");
    }

    @GetMapping("/perspectives/{perspectiveId}")
    public ModelAndView viewPerspective(@PathVariable Long perspectiveId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PERSPECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Perspective");
        modelAndView.addObject("perspective", performanceManagementService.perspective(perspectiveId));
        return modelAndView;
    }

    @GetMapping("/perspectives/{perspectiveId}/edit")
    public ModelAndView editPerspective(@PathVariable Long perspectiveId) {
        PerspectiveRequest request = new PerspectiveRequest();
        request.setName(performanceManagementService.perspective(perspectiveId).getName());
        request.setDescription(performanceManagementService.perspective(perspectiveId).getDescription());
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_PERSPECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Edit Perspective");
        modelAndView.addObject("perspectiveId", perspectiveId);
        modelAndView.addObject("perspectiveRequest", request);
        return modelAndView;
    }

    @PostMapping("/perspectives/{perspectiveId}/update")
    public ModelAndView updatePerspective(@PathVariable Long perspectiveId,
                                          @Valid @ModelAttribute PerspectiveRequest perspectiveRequest,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_PERSPECTIVE);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Edit Perspective");
            modelAndView.addObject("perspectiveId", perspectiveId);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.updatePerspective(perspectiveId, perspectiveRequest);
        return redirectWithInfo("/performance/perspectives/" + perspectiveId, "Perspective updated successfully.");
    }

    @GetMapping("/strategic-goals")
    public ModelAndView strategicObjectives() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_STRATEGIC_OBJECTIVES);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Strategic Goals");
        modelAndView.addObject("strategicObjectives", performanceManagementService.strategicObjectives());
        return modelAndView;
    }

    @GetMapping("/strategic-goals/add")
    public ModelAndView addStrategicObjective() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_STRATEGIC_OBJECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Strategic Goal");
        modelAndView.addObject("strategicObjectiveRequest", new StrategicObjectiveRequest());
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        modelAndView.addObject("perspectives", performanceManagementService.perspectives());
        return modelAndView;
    }

    @PostMapping("/strategic-goals/save")
    public ModelAndView saveStrategicObjective(@Valid @ModelAttribute StrategicObjectiveRequest strategicObjectiveRequest,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_STRATEGIC_OBJECTIVE);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Strategic Goal");
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            modelAndView.addObject("perspectives", performanceManagementService.perspectives());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.createStrategicObjective(strategicObjectiveRequest);
        return redirectWithInfo("/performance/strategic-goals", "Strategic goal saved successfully.");
    }

    @GetMapping("/strategic-goals/{strategicObjectiveId}")
    public ModelAndView viewStrategicObjective(@PathVariable Long strategicObjectiveId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_STRATEGIC_OBJECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Strategic Goal");
        modelAndView.addObject("strategicObjective", performanceManagementService.strategicObjective(strategicObjectiveId));
        return modelAndView;
    }

    @GetMapping("/strategic-goals/{strategicObjectiveId}/edit")
    public ModelAndView editStrategicObjective(@PathVariable Long strategicObjectiveId) {
        hrms.performance.entity.StrategicObjective strategicObjective = performanceManagementService.strategicObjective(strategicObjectiveId);
        StrategicObjectiveRequest request = new StrategicObjectiveRequest();
        request.setReportingPeriodId(strategicObjective.getReportingPeriod().getId());
        request.setPerspectiveId(strategicObjective.getPerspective().getId());
        request.setName(strategicObjective.getName());
        request.setDescription(strategicObjective.getDescription());
        request.setActive(strategicObjective.isActive());
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_STRATEGIC_OBJECTIVE);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Edit Strategic Goal");
        modelAndView.addObject("strategicObjectiveId", strategicObjectiveId);
        modelAndView.addObject("strategicObjectiveRequest", request);
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        modelAndView.addObject("perspectives", performanceManagementService.perspectives());
        return modelAndView;
    }

    @PostMapping("/strategic-goals/{strategicObjectiveId}/update")
    public ModelAndView updateStrategicObjective(@PathVariable Long strategicObjectiveId,
                                                 @Valid @ModelAttribute StrategicObjectiveRequest strategicObjectiveRequest,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_STRATEGIC_OBJECTIVE);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Edit Strategic Goal");
            modelAndView.addObject("strategicObjectiveId", strategicObjectiveId);
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            modelAndView.addObject("perspectives", performanceManagementService.perspectives());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.updateStrategicObjective(strategicObjectiveId, strategicObjectiveRequest);
        return redirectWithInfo("/performance/strategic-goals/" + strategicObjectiveId,
                "Strategic goal updated successfully.");
    }

    @GetMapping("/contracts/add")
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

    @PostMapping("/contracts/save")
    public ModelAndView saveContract(@Valid @ModelAttribute PerformanceContractRequest performanceContractRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_CONTRACT);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Performance Contract");
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        PerformanceContract contract = performanceManagementService.createContract(performanceContractRequest);
        return redirectWithInfo("/performance/contracts/" + contract.getId(), "Performance contract saved successfully.");
    }

    @GetMapping("/contracts/{contractId}")
    public ModelAndView viewContract(@PathVariable Long contractId) {
        PerformanceContract contract = performanceManagementService.contract(contractId);
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PERFORMANCE_CONTRACT);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "View Performance Contract");
        modelAndView.addObject("contract", contract);
        modelAndView.addObject("goals", groupedGoals(contractId));
        return modelAndView;
    }

    @GetMapping("/contracts/{contractId}/edit")
    public ModelAndView editContract(@PathVariable Long contractId) {
        PerformanceContract contract = performanceManagementService.contract(contractId);
        PerformanceContractRequest request = new PerformanceContractRequest();
        request.setEmployeeId(contract.getEmployee().getId());
        request.setReportingPeriodId(contract.getReportingPeriod().getId());
        request.setTitle(contract.getTitle());
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_PERFORMANCE_CONTRACT);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Edit Performance Contract");
        modelAndView.addObject("contractId", contractId);
        modelAndView.addObject("performanceContractRequest", request);
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
        return modelAndView;
    }

    @PostMapping("/contracts/{contractId}/update")
    public ModelAndView updateContract(@PathVariable Long contractId,
                                       @Valid @ModelAttribute PerformanceContractRequest performanceContractRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_PERFORMANCE_CONTRACT);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Edit Performance Contract");
            modelAndView.addObject("contractId", contractId);
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("reportingPeriods", performanceManagementService.reportingPeriods());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.updateContract(contractId, performanceContractRequest);
        return redirectWithInfo("/performance/contracts/" + contractId, "Performance contract updated successfully.");
    }

    @GetMapping("/contracts/{contractId}/goals/add")
    public ModelAndView addGoal(@PathVariable Long contractId) {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_GOAL);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Goal");
        PerformanceGoalRequest request = new PerformanceGoalRequest();
        request.setContractId(contractId);
        modelAndView.addObject("performanceGoalRequest", request);
        PerformanceContract contract = performanceManagementService.contract(contractId);
        modelAndView.addObject("contract", contract);
        modelAndView.addObject("goals", groupedGoals(contractId));
        modelAndView.addObject("strategicObjectives", performanceManagementService.strategicObjectives(contract.getReportingPeriod().getId()));
        return modelAndView;
    }

    @PostMapping("/contracts/goals/save")
    public ModelAndView saveGoal(@Valid @ModelAttribute PerformanceGoalRequest performanceGoalRequest,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_GOAL);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Goal");
            PerformanceContract contract = performanceManagementService.contract(performanceGoalRequest.getContractId());
            modelAndView.addObject("contract", contract);
            modelAndView.addObject("goals", groupedGoals(performanceGoalRequest.getContractId()));
            modelAndView.addObject("strategicObjectives", performanceManagementService.strategicObjectives(contract.getReportingPeriod().getId()));
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.createGoal(performanceGoalRequest);
        return redirectWithInfo("/performance/contracts/" + performanceGoalRequest.getContractId(),
                "Goal saved successfully.");
    }

    @GetMapping("/self-review/{contractId}")
    public ModelAndView selfReview(@PathVariable Long contractId) {
        return buildReviewPage(Pages.SELF_REVIEW, "Submit Self Review", contractId);
    }

    @PostMapping("/self-review/{contractId}")
    public ModelAndView saveSelfReview(@PathVariable Long contractId,
                                       @ModelAttribute PerformanceReviewRequest performanceReviewRequest) {
        performanceManagementService.submitSelfReview(contractId, performanceReviewRequest);
        return redirectWithInfo("/performance/contracts/" + contractId, "Self review submitted successfully.");
    }

    @GetMapping("/manager-review/{contractId}")
    public ModelAndView managerReview(@PathVariable Long contractId) {
        return buildReviewPage(Pages.MANAGER_REVIEW, "Submit Manager Review", contractId);
    }

    @PostMapping("/manager-review/{contractId}")
    public ModelAndView saveManagerReview(@PathVariable Long contractId,
                                          @ModelAttribute PerformanceReviewRequest performanceReviewRequest) {
        performanceManagementService.submitManagerReview(contractId, performanceReviewRequest);
        return redirectWithInfo("/performance/contracts/" + contractId, "Manager review submitted successfully.");
    }

    @GetMapping("/contracts/{contractId}/action-plans/add")
    public ModelAndView addActionPlan(@PathVariable Long contractId) {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_ACTION_PLAN);
        modelAndView.addObject("pageDomain", "Performance Management");
        modelAndView.addObject("pageName", "Performance");
        modelAndView.addObject("pageTitle", "Add Action Plan");
        ActionPlanRequest request = new ActionPlanRequest();
        LocalDate today = DateUtils.today();
        request.setContractId(contractId);
        request.setStartDate(toDate(today));
        request.setEndDate(toDate(today.plusMonths(1)));
        modelAndView.addObject("actionPlanRequest", request);
        modelAndView.addObject("contract", performanceManagementService.contract(contractId));
        modelAndView.addObject("employees", employeeService.findAll());
        return modelAndView;
    }

    @PostMapping("/contracts/action-plans/save")
    public ModelAndView saveActionPlan(@Valid @ModelAttribute ActionPlanRequest actionPlanRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_ACTION_PLAN);
            modelAndView.addObject("pageDomain", "Performance Management");
            modelAndView.addObject("pageName", "Performance");
            modelAndView.addObject("pageTitle", "Add Action Plan");
            modelAndView.addObject("contract", performanceManagementService.contract(actionPlanRequest.getContractId()));
            modelAndView.addObject("employees", employeeService.findAll());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.createActionPlan(actionPlanRequest);
        return redirectWithInfo("/performance/contracts/" + actionPlanRequest.getContractId(),
                "Action plan saved successfully.");
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
        request.setEndDate(toDate(today.plusMonths(1)));
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
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        performanceManagementService.createImprovementPlan(performanceImprovementPlanRequest);
        return redirectWithInfo("/performance/contracts", "Improvement plan saved successfully.");
    }

    private ModelAndView buildReviewPage(String viewName, String pageTitle, Long contractId) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        PerformanceContract contract = performanceManagementService.contract(contractId);
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

    private ModelAndView redirectWithInfo(String path, String message) {
        PortletUtils.addInfoMsg(message);
        return new ModelAndView("redirect:" + path);
    }

    private List<PerformanceGoal> groupedGoals(Long contractId) {
        List<PerformanceGoal> goals = new ArrayList<PerformanceGoal>(performanceManagementService.goals(contractId));
        goals.sort(Comparator
                .comparing((PerformanceGoal goal) -> goal.getStrategicObjective() != null
                        && goal.getStrategicObjective().getPerspective() != null
                        && goal.getStrategicObjective().getPerspective().getName() != null
                        ? goal.getStrategicObjective().getPerspective().getName() : "")
                .thenComparing(goal -> goal.getStrategicObjective() != null && goal.getStrategicObjective().getName() != null
                        ? goal.getStrategicObjective().getName() : "")
                .thenComparing(goal -> goal.getName() != null ? goal.getName() : ""));
        return goals;
    }

    private Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @GetMapping("/add-contract")
    public RedirectView legacyAddContract() {
        return new RedirectView("/performance/contracts/add");
    }

    @GetMapping("/view-contract/{contractId}")
    public RedirectView legacyViewContract(@PathVariable Long contractId) {
        return new RedirectView("/performance/contracts/" + contractId);
    }

    @GetMapping("/add-goal/{contractId}")
    public RedirectView legacyAddGoal(@PathVariable Long contractId) {
        return new RedirectView("/performance/contracts/" + contractId + "/goals/add");
    }

    @GetMapping("/add-action-plan/{contractId}")
    public RedirectView legacyAddActionPlan(@PathVariable Long contractId) {
        return new RedirectView("/performance/contracts/" + contractId + "/action-plans/add");
    }

    @GetMapping("/add-perspective")
    public RedirectView legacyAddPerspective() {
        return new RedirectView("/performance/perspectives/add");
    }

    @GetMapping("/add-strategic-objective")
    public RedirectView legacyAddStrategicObjective() {
        return new RedirectView("/performance/strategic-goals/add");
    }
}
