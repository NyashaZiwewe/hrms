package hrms.leave.service;

import hrms.leave.dto.LeaveDecisionRequest;
import hrms.leave.dto.LeaveRequestInput;
import hrms.leave.dto.LeaveSaleRequestInput;
import hrms.leave.dto.OvertimeClaimInput;
import hrms.leave.entity.LeaveBalance;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.entity.OvertimeClaim;

import java.time.LocalDate;
import java.util.List;

public interface LeaveService {

    LeaveRequest create(LeaveRequestInput input);

    List<LeaveRequest> findAll();

    List<LeaveRequest> findByEmployee(Long employeeId);

    LeaveRequest findById(Long leaveId);

    LeaveRequest update(Long leaveId, LeaveRequestInput input);

    LeaveRequest decide(Long leaveId, LeaveDecisionRequest decisionRequest);

    LeaveSaleRequest createLeaveSale(LeaveSaleRequestInput input);

    LeaveSaleRequest decideLeaveSale(Long leaveSaleRequestId, LeaveDecisionRequest decisionRequest);

    OvertimeClaim createOvertimeClaim(OvertimeClaimInput input);

    OvertimeClaim decideOvertimeClaim(Long overtimeClaimId, LeaveDecisionRequest decisionRequest);

    void delete(Long leaveId);

    List<LeaveBalance> balances(Long employeeId);

    List<LeaveSaleRequest> leaveSales(Long employeeId);

    List<LeaveSaleRequest> leaveSales();

    List<OvertimeClaim> overtimeClaims(Long employeeId);

    List<OvertimeClaim> overtimeClaims();

    List<LeaveBalance> accrueBalances(LocalDate asOfDate);

    List<LeaveRequest> history(Long employeeId);
}
