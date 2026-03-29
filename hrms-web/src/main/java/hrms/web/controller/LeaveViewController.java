package hrms.web.controller;

import hrms.employee.service.EmployeeService;
import hrms.leave.dto.LeaveDecisionRequest;
import hrms.leave.dto.LeaveRequestInput;
import hrms.leave.dto.LeaveSaleRequestInput;
import hrms.leave.dto.OvertimeClaimInput;
import hrms.leave.entity.LeaveBalance;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.entity.OvertimeClaim;
import hrms.leave.model.LeaveType;
import hrms.leave.service.LeaveService;
import hrms.web.constants.Pages;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/leave")
public class LeaveViewController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    public LeaveViewController(LeaveService leaveService, EmployeeService employeeService) {
        this.leaveService = leaveService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public ModelAndView leave(@RequestParam(required = false) Long employeeId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_LEAVE_REQUESTS);
        populateRequestForm(modelAndView, "View Leave Requests");
        modelAndView.addObject("leaveRequests", leaveService.findAll());
        return modelAndView;
    }

    @GetMapping("/balances")
    public ModelAndView balances(@RequestParam(required = false) Long employeeId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_LEAVE_BALANCES);
        populateRequestForm(modelAndView, "View Leave Balances");
        modelAndView.addObject("selectedEmployeeId", employeeId);
        modelAndView.addObject("balances", employeeId == null
                ? java.util.Collections.<LeaveBalance>emptyList()
                : leaveService.balances(employeeId));
        return modelAndView;
    }

    @GetMapping("/sell-leave")
    public ModelAndView sellLeave() {
        ModelAndView modelAndView = new ModelAndView(Pages.SELL_LEAVE_DAYS);
        populateRequestForm(modelAndView, "Sell Leave Days");
        modelAndView.addObject("leaveSaleRequestInput", new LeaveSaleRequestInput());
        modelAndView.addObject("leaveSaleRequests", leaveService.leaveSales());
        return modelAndView;
    }

    @GetMapping("/overtime-claims")
    public ModelAndView overtimeClaims() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_OVERTIME_CLAIMS);
        populateRequestForm(modelAndView, "Overtime Claims");
        modelAndView.addObject("overtimeClaimInput", new OvertimeClaimInput());
        modelAndView.addObject("overtimeClaims", leaveService.overtimeClaims());
        return modelAndView;
    }

    @GetMapping("/add-request")
    public ModelAndView addRequest() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_LEAVE_REQUEST);
        modelAndView.addObject("pageDomain", "Leave Management");
        modelAndView.addObject("pageName", "Leave");
        modelAndView.addObject("pageTitle", "Add Leave Request");
        modelAndView.addObject("leaveRequestInput", new LeaveRequestInput());
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("leaveTypes", LeaveType.values());
        return modelAndView;
    }

    @PostMapping("/save-request")
    public ModelAndView saveRequest(@Valid @ModelAttribute("leaveRequestInput") LeaveRequestInput leaveRequestInput,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_LEAVE_REQUEST);
            populateRequestForm(modelAndView, "Add Leave Request");
            return modelAndView;
        }
        LeaveRequest leaveRequest = leaveService.create(leaveRequestInput);
        return new ModelAndView("redirect:/leave/view-request/" + leaveRequest.getId());
    }

    @GetMapping("/view-request/{leaveId}")
    public ModelAndView viewRequest(@PathVariable Long leaveId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_LEAVE_REQUEST);
        modelAndView.addObject("pageDomain", "Leave Management");
        modelAndView.addObject("pageName", "Leave");
        modelAndView.addObject("pageTitle", "View Leave Request");
        modelAndView.addObject("leaveRequest", leaveService.findById(leaveId));
        modelAndView.addObject("decisionRequest", new LeaveDecisionRequest());
        return modelAndView;
    }

    @GetMapping("/edit-request/{leaveId}")
    public ModelAndView editRequest(@PathVariable Long leaveId) {
        LeaveRequest leaveRequest = leaveService.findById(leaveId);
        LeaveRequestInput input = new LeaveRequestInput();
        input.setEmployeeId(leaveRequest.getEmployee().getId());
        input.setManagerEmployeeId(leaveRequest.getAssignedByManagerId());
        input.setLeaveType(leaveRequest.getLeaveType());
        input.setStartDate(leaveRequest.getStartDate());
        input.setEndDate(leaveRequest.getEndDate());
        input.setReason(leaveRequest.getReason());
        input.setManagerAssigned(leaveRequest.getAssignedByManagerId() != null);

        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_LEAVE_REQUEST);
        populateRequestForm(modelAndView, "Edit Leave Request");
        modelAndView.addObject("leaveId", leaveId);
        modelAndView.addObject("leaveRequestInput", input);
        return modelAndView;
    }

    @PostMapping("/update-request/{leaveId}")
    public ModelAndView updateRequest(@PathVariable Long leaveId,
                                      @Valid @ModelAttribute("leaveRequestInput") LeaveRequestInput leaveRequestInput,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_LEAVE_REQUEST);
            populateRequestForm(modelAndView, "Edit Leave Request");
            modelAndView.addObject("leaveId", leaveId);
            return modelAndView;
        }
        leaveService.update(leaveId, leaveRequestInput);
        return new ModelAndView("redirect:/leave/view-request/" + leaveId);
    }

    @PostMapping("/decide-request/{leaveId}")
    public ModelAndView decideRequest(@PathVariable Long leaveId,
                                      @ModelAttribute LeaveDecisionRequest decisionRequest) {
        leaveService.decide(leaveId, decisionRequest);
        return new ModelAndView("redirect:/leave/view-request/" + leaveId);
    }

    @PostMapping("/delete-request/{leaveId}")
    public ModelAndView deleteRequest(@PathVariable Long leaveId) {
        leaveService.delete(leaveId);
        return new ModelAndView("redirect:/leave");
    }

    @PostMapping("/save-leave-sale")
    public ModelAndView saveLeaveSale(@Valid @ModelAttribute("leaveSaleRequestInput") LeaveSaleRequestInput input,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.SELL_LEAVE_DAYS);
            populateRequestForm(modelAndView, "Sell Leave Days");
            modelAndView.addObject("leaveSaleRequests", leaveService.leaveSales());
            return modelAndView;
        }
        leaveService.createLeaveSale(input);
        return new ModelAndView("redirect:/leave/sell-leave");
    }

    @PostMapping("/decide-leave-sale/{leaveSaleRequestId}")
    public ModelAndView decideLeaveSale(@PathVariable Long leaveSaleRequestId,
                                        @ModelAttribute LeaveDecisionRequest decisionRequest) {
        leaveService.decideLeaveSale(leaveSaleRequestId, decisionRequest);
        return new ModelAndView("redirect:/leave/sell-leave");
    }

    @PostMapping("/save-overtime-claim")
    public ModelAndView saveOvertimeClaim(@Valid @ModelAttribute("overtimeClaimInput") OvertimeClaimInput input,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.VIEW_OVERTIME_CLAIMS);
            populateRequestForm(modelAndView, "Overtime Claims");
            modelAndView.addObject("overtimeClaims", leaveService.overtimeClaims());
            return modelAndView;
        }
        leaveService.createOvertimeClaim(input);
        return new ModelAndView("redirect:/leave/overtime-claims");
    }

    @PostMapping("/decide-overtime-claim/{overtimeClaimId}")
    public ModelAndView decideOvertimeClaim(@PathVariable Long overtimeClaimId,
                                            @ModelAttribute LeaveDecisionRequest decisionRequest) {
        leaveService.decideOvertimeClaim(overtimeClaimId, decisionRequest);
        return new ModelAndView("redirect:/leave/overtime-claims");
    }

    private void populateRequestForm(ModelAndView modelAndView, String pageTitle) {
        modelAndView.addObject("pageDomain", "Leave Management");
        modelAndView.addObject("pageName", "Leave");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("leaveTypes", LeaveType.values());
    }

}
