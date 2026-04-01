package hrms.leave.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import hrms.common.util.DateUtils;
import hrms.employee.entity.Employee;
import hrms.employee.service.EmployeeService;
import hrms.leave.dto.LeaveDecisionRequest;
import hrms.leave.dto.LeaveRequestInput;
import hrms.leave.dto.LeaveSaleRequestInput;
import hrms.leave.dto.LeaveTypeRequest;
import hrms.leave.dto.OvertimeClaimInput;
import hrms.leave.entity.LeaveBalance;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.entity.OvertimeClaim;
import hrms.leave.model.LeaveStatus;
import hrms.leave.entity.LeaveType;
import hrms.leave.repository.LeaveBalanceRepository;
import hrms.leave.repository.LeaveRequestRepository;
import hrms.leave.repository.LeaveSaleRequestRepository;
import hrms.leave.repository.LeaveTypeRepository;
import hrms.leave.repository.OvertimeClaimRepository;
import hrms.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveSaleRequestRepository leaveSaleRequestRepository;
    private final OvertimeClaimRepository overtimeClaimRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final AuditTrailService auditTrailService;

    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository,
                        EmployeeService employeeService,
                        LeaveBalanceRepository leaveBalanceRepository,
                        LeaveSaleRequestRepository leaveSaleRequestRepository,
                        OvertimeClaimRepository overtimeClaimRepository,
                        LeaveTypeRepository leaveTypeRepository,
                        AuditTrailService auditTrailService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeService = employeeService;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveSaleRequestRepository = leaveSaleRequestRepository;
        this.overtimeClaimRepository = overtimeClaimRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.auditTrailService = auditTrailService;
    }

    public LeaveRequest create(LeaveRequestInput input) {
        LocalDate startDate = toLocalDate(input.getStartDate());
        LocalDate endDate = toLocalDate(input.getEndDate());
        if (startDate.isBefore(DateUtils.today()) || endDate.isBefore(DateUtils.today())) {
            throw new IllegalArgumentException("Leave dates cannot be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Leave end date cannot be before start date");
        }

        Employee employee = employeeService.findById(input.getEmployeeId());
        LeaveType leaveType = findLeaveTypeByCode(input.getLeaveTypeCode());
        validateManagerAccess(employee, input.getManagerEmployeeId(), input.isManagerAssigned());
        LeaveBalance balance = ensureBalance(employee, leaveType);
        int requestedDays = workingDaysBetween(startDate, endDate);
        if (requestedDays <= 0) {
            throw new IllegalArgumentException("Leave period must include at least one working day");
        }
        if (requiresBalanceCheck(leaveType) && balance.getAvailableDays() < requestedDays) {
            throw new OperationNotAllowedException("Insufficient leave balance for " + leaveType.getName());
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStartDate(input.getStartDate());
        request.setEndDate(input.getEndDate());
        request.setDaysRequested(requestedDays);
        request.setStatus(LeaveStatus.PENDING);
        request.setReason(input.getReason());
        request.setAssignedByManagerId(input.getManagerEmployeeId());
        LeaveRequest saved = leaveRequestRepository.save(request);
        auditTrailService.log("LEAVE", "LeaveRequest", String.valueOf(saved.getId()), "CREATE",
                "Created leave request for employee " + employee.getEmployeeNumber() + " with status " + saved.getStatus());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> findByEmployee(Long employeeId) {
        employeeService.findById(employeeId);
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public LeaveRequest findById(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
    }

    public LeaveRequest update(Long leaveId, LeaveRequestInput input) {
        LeaveRequest existing = findById(leaveId);
        if (existing.getStatus() != LeaveStatus.PENDING) {
            throw new OperationNotAllowedException("Only pending leave requests can be updated");
        }
        LocalDate startDate = toLocalDate(input.getStartDate());
        LocalDate endDate = toLocalDate(input.getEndDate());
        if (startDate.isBefore(DateUtils.today()) || endDate.isBefore(DateUtils.today())) {
            throw new IllegalArgumentException("Leave dates cannot be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Leave end date cannot be before start date");
        }

        Employee employee = employeeService.findById(input.getEmployeeId());
        LeaveType leaveType = findLeaveTypeByCode(input.getLeaveTypeCode());
        validateManagerAccess(employee, input.getManagerEmployeeId(), input.isManagerAssigned());
        LeaveBalance balance = ensureBalance(employee, leaveType);
        int requestedDays = workingDaysBetween(startDate, endDate);
        if (requestedDays <= 0) {
            throw new IllegalArgumentException("Leave period must include at least one working day");
        }
        if (requiresBalanceCheck(leaveType) && balance.getAvailableDays() < requestedDays) {
            throw new OperationNotAllowedException("Insufficient leave balance for " + leaveType.getName());
        }

        existing.setEmployee(employee);
        existing.setLeaveType(leaveType);
        existing.setStartDate(input.getStartDate());
        existing.setEndDate(input.getEndDate());
        existing.setDaysRequested(requestedDays);
        existing.setReason(input.getReason());
        existing.setAssignedByManagerId(input.getManagerEmployeeId());
        LeaveRequest saved = leaveRequestRepository.save(existing);
        auditTrailService.log("LEAVE", "LeaveRequest", String.valueOf(saved.getId()), "UPDATE",
                "Updated leave request for employee " + employee.getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<LeaveType> leaveTypes() {
        return leaveTypeRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public LeaveType leaveType(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found: " + leaveTypeId));
    }

    public LeaveType createLeaveType(LeaveTypeRequest input) {
        String code = normalizeCode(input.getCode());
        String name = normalizeName(input.getName());
        leaveTypeRepository.findByCodeIgnoreCase(code).ifPresent(existing -> {
            throw new DuplicateResourceException("Leave type code already exists: " + code);
        });
        leaveTypeRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            throw new DuplicateResourceException("Leave type name already exists: " + name);
        });
        LeaveType leaveType = new LeaveType();
        applyLeaveType(leaveType, input, code, name);
        return leaveTypeRepository.save(leaveType);
    }

    public LeaveType updateLeaveType(Long leaveTypeId, LeaveTypeRequest input) {
        LeaveType leaveType = leaveType(leaveTypeId);
        String code = normalizeCode(input.getCode());
        String name = normalizeName(input.getName());
        leaveTypeRepository.findByCodeIgnoreCase(code)
                .filter(existing -> !existing.getId().equals(leaveTypeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Leave type code already exists: " + code);
                });
        leaveTypeRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(leaveTypeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Leave type name already exists: " + name);
                });
        applyLeaveType(leaveType, input, code, name);
        return leaveTypeRepository.save(leaveType);
    }

    public void deleteLeaveType(Long leaveTypeId) {
        leaveTypeRepository.delete(leaveType(leaveTypeId));
    }

    public LeaveRequest decide(Long leaveId, LeaveDecisionRequest decisionRequest) {
        LeaveRequest leaveRequest = findById(leaveId);
        if (decisionRequest.getStatus() == LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Decision status must be APPROVED or REJECTED");
        }
        validateManagerAccess(leaveRequest.getEmployee(), decisionRequest.getManagerEmployeeId(), true);

        if (leaveRequest.getStatus() != LeaveStatus.APPROVED && decisionRequest.getStatus() == LeaveStatus.APPROVED) {
            LeaveBalance balance = ensureBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType());
            if (requiresBalanceCheck(leaveRequest.getLeaveType()) && balance.getAvailableDays() < leaveRequest.getDaysRequested()) {
                throw new OperationNotAllowedException("Insufficient leave balance for approval");
            }
            if (requiresBalanceCheck(leaveRequest.getLeaveType())) {
                balance.setUsedDays(balance.getUsedDays() + leaveRequest.getDaysRequested());
                balance.setAvailableDays(availableDays(balance));
                leaveBalanceRepository.save(balance);
            }
        }
        leaveRequest.setStatus(decisionRequest.getStatus());
        leaveRequest.setDecidedByManagerId(decisionRequest.getManagerEmployeeId());
        leaveRequest.setDecisionDate(new Date());
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        auditTrailService.log("LEAVE", "LeaveRequest", String.valueOf(saved.getId()), "DECIDE",
                "Manager decision " + saved.getStatus() + " for employee " + saved.getEmployee().getEmployeeNumber());
        return saved;
    }

    public void delete(Long leaveId) {
        LeaveRequest leaveRequest = findById(leaveId);
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED && requiresBalanceCheck(leaveRequest.getLeaveType())) {
            LeaveBalance balance = ensureBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType());
            balance.setUsedDays(Math.max(0, balance.getUsedDays() - leaveRequest.getDaysRequested()));
            balance.setAvailableDays(availableDays(balance));
            leaveBalanceRepository.save(balance);
        }
        leaveRequestRepository.delete(leaveRequest);
        auditTrailService.log("LEAVE", "LeaveRequest", String.valueOf(leaveId), "DELETE",
                "Deleted leave request for employee " + leaveRequest.getEmployee().getEmployeeNumber());
    }

    public List<LeaveBalance> balances(Long employeeId) {
        Employee employee = employeeService.findById(employeeId);
        List<LeaveBalance> balances = new ArrayList<LeaveBalance>();
        for (LeaveType leaveType : leaveTypeRepository.findByActiveTrueOrderByNameAsc()) {
            balances.add(ensureBalance(employee, leaveType));
        }
        return balances;
    }

    public LeaveSaleRequest createLeaveSale(LeaveSaleRequestInput input) {
        Employee employee = employeeService.findById(input.getEmployeeId());
        LeaveType leaveType = findLeaveTypeByCode(input.getLeaveTypeCode());
        if (!isLeaveSaleType(leaveType)) {
            throw new OperationNotAllowedException("Leave selling is only allowed for annual or vacation leave");
        }
        LeaveBalance balance = ensureBalance(employee, leaveType);
        if (balance.getAvailableDays() < input.getDaysToSell()) {
            throw new OperationNotAllowedException("Insufficient leave balance to sell requested days");
        }
        LeaveSaleRequest request = new LeaveSaleRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setDaysToSell(input.getDaysToSell());
        request.setRequestDate(new Date());
        request.setReason(input.getReason());
        LeaveSaleRequest saved = leaveSaleRequestRepository.save(request);
        auditTrailService.log("LEAVE", "LeaveSaleRequest", String.valueOf(saved.getId()), "CREATE",
                "Created leave sale request for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public LeaveSaleRequest decideLeaveSale(Long leaveSaleRequestId, LeaveDecisionRequest decisionRequest) {
        LeaveSaleRequest request = leaveSaleRequestRepository.findById(leaveSaleRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave sale request not found: " + leaveSaleRequestId));
        if (decisionRequest.getStatus() == LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Decision status must be APPROVED or REJECTED");
        }
        validateManagerAccess(request.getEmployee(), decisionRequest.getManagerEmployeeId(), true);
        if (request.getStatus() != LeaveStatus.APPROVED && decisionRequest.getStatus() == LeaveStatus.APPROVED) {
            LeaveBalance balance = ensureBalance(request.getEmployee(), request.getLeaveType());
            if (balance.getAvailableDays() < request.getDaysToSell()) {
                throw new OperationNotAllowedException("Insufficient leave balance to approve sale");
            }
            balance.setSoldDays(balance.getSoldDays() + request.getDaysToSell());
            balance.setAvailableDays(availableDays(balance));
            leaveBalanceRepository.save(balance);
        }
        if (request.getStatus() == LeaveStatus.APPROVED && decisionRequest.getStatus() == LeaveStatus.REJECTED) {
            LeaveBalance balance = ensureBalance(request.getEmployee(), request.getLeaveType());
            balance.setSoldDays(Math.max(0, balance.getSoldDays() - request.getDaysToSell()));
            balance.setAvailableDays(availableDays(balance));
            leaveBalanceRepository.save(balance);
        }
        request.setStatus(decisionRequest.getStatus());
        request.setDecidedByManagerId(decisionRequest.getManagerEmployeeId());
        request.setDecisionDate(new Date());
        LeaveSaleRequest saved = leaveSaleRequestRepository.save(request);
        auditTrailService.log("LEAVE", "LeaveSaleRequest", String.valueOf(saved.getId()), "DECIDE",
                "Manager decision " + saved.getStatus() + " for leave sale request " + saved.getId());
        return saved;
    }

    public OvertimeClaim createOvertimeClaim(OvertimeClaimInput input) {
        Employee employee = employeeService.findById(input.getEmployeeId());
        if (input.getHoursClaimed().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Overtime hours must be greater than zero");
        }
        OvertimeClaim claim = new OvertimeClaim();
        claim.setEmployee(employee);
        claim.setWorkDate(input.getWorkDate());
        claim.setHoursClaimed(input.getHoursClaimed());
        claim.setReason(input.getReason());
        OvertimeClaim saved = overtimeClaimRepository.save(claim);
        auditTrailService.log("LEAVE", "OvertimeClaim", String.valueOf(saved.getId()), "CREATE",
                "Created overtime claim for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public OvertimeClaim decideOvertimeClaim(Long overtimeClaimId, LeaveDecisionRequest decisionRequest) {
        OvertimeClaim claim = overtimeClaimRepository.findById(overtimeClaimId)
                .orElseThrow(() -> new ResourceNotFoundException("Overtime claim not found: " + overtimeClaimId));
        if (decisionRequest.getStatus() == LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Decision status must be APPROVED or REJECTED");
        }
        validateManagerAccess(claim.getEmployee(), decisionRequest.getManagerEmployeeId(), true);
        claim.setStatus(decisionRequest.getStatus());
        claim.setDecidedByManagerId(decisionRequest.getManagerEmployeeId());
        claim.setDecisionDate(new Date());
        OvertimeClaim saved = overtimeClaimRepository.save(claim);
        auditTrailService.log("LEAVE", "OvertimeClaim", String.valueOf(saved.getId()), "DECIDE",
                "Manager decision " + saved.getStatus() + " for overtime claim " + saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<LeaveSaleRequest> leaveSales(Long employeeId) {
        employeeService.findById(employeeId);
        return leaveSaleRequestRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<LeaveSaleRequest> leaveSales() {
        return leaveSaleRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OvertimeClaim> overtimeClaims(Long employeeId) {
        employeeService.findById(employeeId);
        return overtimeClaimRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<OvertimeClaim> overtimeClaims() {
        return overtimeClaimRepository.findAll();
    }

    public List<LeaveBalance> accrueBalances(LocalDate asOfDate) {
        List<LeaveBalance> balances = new ArrayList<LeaveBalance>();
        for (Employee employee : employeeService.findAll()) {
            for (LeaveType leaveType : leaveTypeRepository.findByActiveTrueOrderByNameAsc()) {
                LeaveBalance balance = ensureBalance(employee, leaveType);
                if (requiresBalanceCheck(leaveType)) {
                    int monthsWorked = Math.max(0, (int) java.time.temporal.ChronoUnit.MONTHS.between(
                            toLocalDate(employee.getHireDate()).withDayOfMonth(1),
                            asOfDate.withDayOfMonth(1)) + 1);
                    int accrued = monthlyEntitlement(leaveType) * monthsWorked;
                    balance.setAccruedDays(accrued);
                    balance.setAvailableDays(availableDays(balance));
                }
                leaveBalanceRepository.save(balance);
                auditTrailService.log("LEAVE", "LeaveBalance", String.valueOf(balance.getId()), "ACCRUAL",
                        "Accrued " + balance.getAccruedDays() + " days for employee " + employee.getEmployeeNumber()
                                + " leave type " + leaveType);
                balances.add(balance);
            }
        }
        return balances;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> history(Long employeeId) {
        employeeService.findById(employeeId);
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    private LeaveBalance ensureBalance(Employee employee, LeaveType leaveType) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveType_Code(employee.getId(), leaveType.getCode())
                .orElseGet(() -> {
                    LeaveBalance balance = new LeaveBalance();
                    balance.setEmployee(employee);
                    balance.setLeaveType(leaveType);
                    balance.setAccruedDays(monthlyEntitlement(leaveType));
                    balance.setUsedDays(0);
                    balance.setSoldDays(0);
                    balance.setAvailableDays(availableDays(balance));
                    return leaveBalanceRepository.save(balance);
                });
    }

    private void validateManagerAccess(Employee employee, Long managerEmployeeId, boolean managerAction) {
        if (!managerAction && managerEmployeeId == null) {
            return;
        }
        if (managerEmployeeId == null) {
            throw new IllegalArgumentException("Manager employee id is required for manager action");
        }
        if (employee.getManagerEmployeeId() == null || !employee.getManagerEmployeeId().equals(managerEmployeeId)) {
            throw new OperationNotAllowedException("Manager is not authorized for employee " + employee.getId());
        }
    }

    private boolean requiresBalanceCheck(LeaveType leaveType) {
        return leaveType.isBalanceTracked();
    }

    private int monthlyEntitlement(LeaveType leaveType) {
        return leaveType.getMonthlyEntitlement() == null ? 0 : leaveType.getMonthlyEntitlement();
    }

    private int availableDays(LeaveBalance balance) {
        return Math.max(balance.getAccruedDays() - balance.getUsedDays() - balance.getSoldDays(), 0);
    }

    private boolean isLeaveSaleType(LeaveType leaveType) {
        return leaveType.isLeaveSaleAllowed();
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LeaveType findLeaveTypeByCode(String leaveTypeCode) {
        return leaveTypeRepository.findByCodeIgnoreCase(normalizeCode(leaveTypeCode))
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found: " + leaveTypeCode));
    }

    private String normalizeCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type code is required");
        }
        return value.trim().toUpperCase();
    }

    private String normalizeName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type name is required");
        }
        return value.trim();
    }

    private void applyLeaveType(LeaveType leaveType,
                                LeaveTypeRequest input,
                                String code,
                                String name) {
        leaveType.setCode(code);
        leaveType.setName(name);
        leaveType.setMonthlyEntitlement(input.getMonthlyEntitlement() == null ? 0 : input.getMonthlyEntitlement());
        leaveType.setBalanceTracked(input.isBalanceTracked());
        leaveType.setLeaveSaleAllowed(input.isLeaveSaleAllowed());
        leaveType.setActive(input.isActive());
    }

    private int workingDaysBetween(LocalDate startDate, LocalDate endDate) {
        int days = 0;
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            DayOfWeek dayOfWeek = cursor.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                days++;
            }
            cursor = cursor.plusDays(1);
        }
        return days;
    }
}
