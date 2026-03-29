package hrms.leave.resource;

import hrms.leave.dto.LeaveDecisionRequest;
import hrms.leave.dto.LeaveRequestInput;
import hrms.leave.dto.LeaveSaleRequestInput;
import hrms.leave.dto.OvertimeClaimInput;
import hrms.leave.entity.LeaveBalance;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.entity.OvertimeClaim;
import hrms.leave.service.LeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveResource {

    private final LeaveService leaveService;

    public LeaveResource(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest create(@Valid @RequestBody LeaveRequestInput input) {
        return leaveService.create(input);
    }

    @GetMapping
    public List<LeaveRequest> findAll(@RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return leaveService.findByEmployee(employeeId);
        }
        return leaveService.findAll();
    }

    @GetMapping("/{leaveId}")
    public LeaveRequest findById(@PathVariable Long leaveId) {
        return leaveService.findById(leaveId);
    }

    @PutMapping("/{leaveId}")
    public LeaveRequest update(@PathVariable Long leaveId, @Valid @RequestBody LeaveRequestInput input) {
        return leaveService.update(leaveId, input);
    }

    @PatchMapping("/{leaveId}/status")
    public LeaveRequest decide(@PathVariable Long leaveId, @Valid @RequestBody LeaveDecisionRequest request) {
        return leaveService.decide(leaveId, request);
    }

    @PostMapping("/leave-sales")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveSaleRequest createLeaveSale(@Valid @RequestBody LeaveSaleRequestInput input) {
        return leaveService.createLeaveSale(input);
    }

    @PatchMapping("/leave-sales/{leaveSaleRequestId}/status")
    public LeaveSaleRequest decideLeaveSale(@PathVariable Long leaveSaleRequestId,
                                            @Valid @RequestBody LeaveDecisionRequest request) {
        return leaveService.decideLeaveSale(leaveSaleRequestId, request);
    }

    @GetMapping("/leave-sales")
    public List<LeaveSaleRequest> leaveSales(@RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return leaveService.leaveSales(employeeId);
        }
        return leaveService.leaveSales();
    }

    @PostMapping("/overtime-claims")
    @ResponseStatus(HttpStatus.CREATED)
    public OvertimeClaim createOvertimeClaim(@Valid @RequestBody OvertimeClaimInput input) {
        return leaveService.createOvertimeClaim(input);
    }

    @PatchMapping("/overtime-claims/{overtimeClaimId}/status")
    public OvertimeClaim decideOvertimeClaim(@PathVariable Long overtimeClaimId,
                                             @Valid @RequestBody LeaveDecisionRequest request) {
        return leaveService.decideOvertimeClaim(overtimeClaimId, request);
    }

    @GetMapping("/overtime-claims")
    public List<OvertimeClaim> overtimeClaims(@RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return leaveService.overtimeClaims(employeeId);
        }
        return leaveService.overtimeClaims();
    }

    @GetMapping("/history")
    public List<LeaveRequest> history(@RequestParam Long employeeId) {
        return leaveService.history(employeeId);
    }

    @PostMapping("/accruals/run")
    public List<LeaveBalance> accrue(@RequestParam String asOfDate) {
        return leaveService.accrueBalances(LocalDate.parse(asOfDate));
    }

    @GetMapping("/balances")
    public List<LeaveBalance> balances(@RequestParam Long employeeId) {
        return leaveService.balances(employeeId);
    }

    @DeleteMapping("/{leaveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long leaveId) {
        leaveService.delete(leaveId);
    }
}
