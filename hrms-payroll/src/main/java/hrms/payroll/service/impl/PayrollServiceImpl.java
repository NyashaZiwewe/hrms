package hrms.payroll.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import hrms.employee.entity.Employee;
import hrms.employee.service.EmployeeService;
import hrms.leave.entity.LeaveRequest;
import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.entity.OvertimeClaim;
import hrms.leave.model.LeaveStatus;
import hrms.leave.model.LeaveType;
import hrms.leave.repository.LeaveRequestRepository;
import hrms.leave.repository.LeaveSaleRequestRepository;
import hrms.leave.repository.OvertimeClaimRepository;
import hrms.payroll.dto.CompensationPackageRequest;
import hrms.payroll.dto.CurrencyRequest;
import hrms.payroll.dto.ExchangeRateRequest;
import hrms.payroll.dto.PayrollEmployeeInput;
import hrms.payroll.dto.PayrollEntryResponse;
import hrms.payroll.dto.PayrollJournalEntryResponse;
import hrms.payroll.dto.PayrollProcessRequest;
import hrms.payroll.dto.PayrollReportResponse;
import hrms.payroll.dto.PayrollRunResponse;
import hrms.payroll.dto.PayslipResponse;
import hrms.payroll.dto.TaxFilingReportResponse;
import hrms.payroll.entity.CompensationPackage;
import hrms.payroll.entity.Currency;
import hrms.payroll.entity.ExchangeRate;
import hrms.payroll.entity.PayrollEntry;
import hrms.payroll.entity.PayrollJournalEntry;
import hrms.payroll.entity.PayrollRun;
import hrms.payroll.model.GeneralLedgerAccount;
import hrms.payroll.model.JournalEntryType;
import hrms.payroll.model.PayrollStatus;
import hrms.payroll.repository.CompensationPackageRepository;
import hrms.payroll.repository.CurrencyRepository;
import hrms.payroll.repository.ExchangeRateRepository;
import hrms.payroll.repository.PayrollEntryRepository;
import hrms.payroll.repository.PayrollJournalEntryRepository;
import hrms.payroll.repository.PayrollRunRepository;
import hrms.payroll.service.PayrollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.50");

    private final EmployeeService employeeService;
    private final CompensationPackageRepository compensationPackageRepository;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final PayrollEntryRepository payrollEntryRepository;
    private final PayrollJournalEntryRepository payrollJournalEntryRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveSaleRequestRepository leaveSaleRequestRepository;
    private final OvertimeClaimRepository overtimeClaimRepository;
    private final AuditTrailService auditTrailService;

    public PayrollServiceImpl(EmployeeService employeeService,
                              CompensationPackageRepository compensationPackageRepository,
                              CurrencyRepository currencyRepository,
                              ExchangeRateRepository exchangeRateRepository,
                              PayrollRunRepository payrollRunRepository,
                              PayrollEntryRepository payrollEntryRepository,
                              PayrollJournalEntryRepository payrollJournalEntryRepository,
                              LeaveRequestRepository leaveRequestRepository,
                              LeaveSaleRequestRepository leaveSaleRequestRepository,
                              OvertimeClaimRepository overtimeClaimRepository,
                              AuditTrailService auditTrailService) {
        this.employeeService = employeeService;
        this.compensationPackageRepository = compensationPackageRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.payrollEntryRepository = payrollEntryRepository;
        this.payrollJournalEntryRepository = payrollJournalEntryRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveSaleRequestRepository = leaveSaleRequestRepository;
        this.overtimeClaimRepository = overtimeClaimRepository;
        this.auditTrailService = auditTrailService;
    }

    public CompensationPackage saveCompensationPackage(CompensationPackageRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        Currency currency = getCurrency(request.getCurrencyCode());
        CompensationPackage compensationPackage = compensationPackageRepository.findByEmployeeId(request.getEmployeeId())
                .orElseGet(CompensationPackage::new);
        compensationPackage.setEmployee(employee);
        compensationPackage.setCurrencyCode(currency.getCode());
        compensationPackage.setBaseMonthlySalary(scale(request.getBaseMonthlySalary()));
        compensationPackage.setHourlyRate(scale(request.getHourlyRate()));
        compensationPackage.setStandardMonthlyHours(request.getStandardMonthlyHours());
        compensationPackage.setIncomeTaxRate(request.getIncomeTaxRate());
        compensationPackage.setSocialSecurityRate(request.getSocialSecurityRate());
        compensationPackage.setRetirementContributionRate(request.getRetirementContributionRate());
        compensationPackage.setFixedBenefitsDeduction(scale(request.getFixedBenefitsDeduction()));
        compensationPackage.setFixedAllowance(scale(request.getFixedAllowance()));
        CompensationPackage saved = compensationPackageRepository.save(compensationPackage);
        auditTrailService.log("PAYROLL", "CompensationPackage", String.valueOf(saved.getId()), "SAVE",
                "Saved compensation package for employee " + employee.getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<CompensationPackage> listCompensationPackages() {
        return compensationPackageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CompensationPackage getCompensationPackage(Long compensationPackageId) {
        return compensationPackageRepository.findById(compensationPackageId)
                .orElseThrow(() -> new ResourceNotFoundException("Compensation package not found: " + compensationPackageId));
    }

    public void deleteCompensationPackage(Long compensationPackageId) {
        CompensationPackage compensationPackage = getCompensationPackage(compensationPackageId);
        compensationPackageRepository.delete(compensationPackage);
        auditTrailService.log("PAYROLL", "CompensationPackage", String.valueOf(compensationPackageId), "DELETE",
                "Deleted compensation package for employee " + compensationPackage.getEmployee().getEmployeeNumber());
    }

    public PayrollRunResponse processPayroll(PayrollProcessRequest request) {
        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new IllegalArgumentException("Payroll period end date cannot be before start date");
        }

        Currency runCurrency = getCurrency(request.getCurrencyCode());
        Currency baseCurrency = getBaseCurrency();

        PayrollRun payrollRun = new PayrollRun();
        payrollRun.setPayrollCode(request.getPayrollCode());
        payrollRun.setCurrencyCode(runCurrency.getCode());
        payrollRun.setBaseCurrencyCode(baseCurrency.getCode());
        payrollRun.setPayDate(request.getPayDate());
        payrollRun.setPeriodStart(request.getPeriodStart());
        payrollRun.setPeriodEnd(request.getPeriodEnd());
        payrollRun.setStatus(PayrollStatus.PROCESSED);
        payrollRun = payrollRunRepository.save(payrollRun);

        List<PayrollEntry> entries = new ArrayList<PayrollEntry>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalSocialSecurity = BigDecimal.ZERO;
        BigDecimal totalRetirement = BigDecimal.ZERO;
        BigDecimal totalBenefits = BigDecimal.ZERO;
        BigDecimal totalOtherDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        for (PayrollEmployeeInput employeeInput : request.getEmployees()) {
            Employee employee = employeeService.findById(employeeInput.getEmployeeId());
            CompensationPackage compensationPackage = compensationPackageRepository.findByEmployeeId(employee.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Compensation package not found for employee " + employee.getId()));

            PayrollEntry entry = buildEntry(payrollRun, employee, compensationPackage, employeeInput, request);
            entries.add(payrollEntryRepository.save(entry));

            totalGross = totalGross.add(entry.getGrossPay());
            totalTax = totalTax.add(entry.getTaxAmount());
            totalSocialSecurity = totalSocialSecurity.add(entry.getSocialSecurityAmount());
            totalRetirement = totalRetirement.add(entry.getRetirementAmount());
            totalBenefits = totalBenefits.add(entry.getBenefitsDeduction());
            totalOtherDeductions = totalOtherDeductions.add(entry.getOtherDeductions()).add(entry.getUnpaidLeaveDeduction());
            totalNet = totalNet.add(entry.getNetPay());
        }

        payrollRun.setGrossPay(scale(totalGross));
        payrollRun.setTotalDeductions(scale(totalGross.subtract(totalNet)));
        payrollRun.setNetPay(scale(totalNet));
        payrollRun.setJournalPosted(true);
        payrollRun.setStatus(PayrollStatus.POSTED);
        payrollRun = payrollRunRepository.save(payrollRun);

        List<PayrollJournalEntry> journalEntries = postJournal(payrollRun, runCurrency.getCode(), totalGross, totalTax,
                totalSocialSecurity, totalRetirement, totalBenefits, totalOtherDeductions, totalNet);

        auditTrailService.log("PAYROLL", "PayrollRun", String.valueOf(payrollRun.getId()), "PROCESS",
                "Processed payroll run " + payrollRun.getPayrollCode() + " for " + entries.size() + " employees");
        return mapRun(payrollRun, entries, journalEntries);
    }

    @Transactional(readOnly = true)
    public PayslipResponse payslip(Long payrollEntryId) {
        PayrollEntry entry = payrollEntryRepository.findById(payrollEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll entry not found: " + payrollEntryId));
        return PayslipResponse.builder()
                .payrollCode(entry.getPayrollRun().getPayrollCode())
                .employeeNumber(entry.getEmployee().getEmployeeNumber())
                .employeeName(entry.getEmployee().getFirstName() + " " + entry.getEmployee().getLastName())
                .currencyCode(entry.getCurrencyCode())
                .sourceCurrencyCode(entry.getSourceCurrencyCode())
                .baseCurrencyCode(entry.getBaseCurrencyCode())
                .exchangeRateUsed(entry.getExchangeRateUsed())
                .grossPay(entry.getGrossPay())
                .taxAmount(entry.getTaxAmount())
                .socialSecurityAmount(entry.getSocialSecurityAmount())
                .retirementAmount(entry.getRetirementAmount())
                .benefitsDeduction(entry.getBenefitsDeduction())
                .unpaidLeaveDeduction(entry.getUnpaidLeaveDeduction())
                .otherDeductions(entry.getOtherDeductions())
                .netPay(entry.getNetPay())
                .build();
    }

    @Transactional(readOnly = true)
    public PayrollRunResponse getPayrollRun(Long payrollRunId) {
        PayrollRun payrollRun = payrollRunRepository.findById(payrollRunId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found: " + payrollRunId));
        return mapRun(payrollRun, payrollEntryRepository.findByPayrollRunId(payrollRunId),
                payrollJournalEntryRepository.findByPayrollRunId(payrollRunId));
    }

    @Transactional(readOnly = true)
    public PayrollReportResponse report(Long payrollRunId) {
        PayrollRun payrollRun = payrollRunRepository.findById(payrollRunId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found: " + payrollRunId));
        List<PayrollEntry> entries = payrollEntryRepository.findByPayrollRunId(payrollRunId);

        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalSocial = BigDecimal.ZERO;
        BigDecimal totalRetirement = BigDecimal.ZERO;
        BigDecimal totalBenefits = BigDecimal.ZERO;
        BigDecimal totalOther = BigDecimal.ZERO;
        BigDecimal totalOvertime = BigDecimal.ZERO;
        for (PayrollEntry entry : entries) {
            totalTax = totalTax.add(entry.getTaxAmount());
            totalSocial = totalSocial.add(entry.getSocialSecurityAmount());
            totalRetirement = totalRetirement.add(entry.getRetirementAmount());
            totalBenefits = totalBenefits.add(entry.getBenefitsDeduction());
            totalOther = totalOther.add(entry.getOtherDeductions()).add(entry.getUnpaidLeaveDeduction());
            totalOvertime = totalOvertime.add(entry.getOvertimeHours());
        }

        return PayrollReportResponse.builder()
                .payrollCode(payrollRun.getPayrollCode())
                .currencyCode(payrollRun.getCurrencyCode())
                .baseCurrencyCode(payrollRun.getBaseCurrencyCode())
                .employeeCount(entries.size())
                .totalGrossPay(payrollRun.getGrossPay())
                .totalTax(scale(totalTax))
                .totalSocialSecurity(scale(totalSocial))
                .totalRetirement(scale(totalRetirement))
                .totalBenefits(scale(totalBenefits))
                .totalOtherDeductions(scale(totalOther))
                .totalNetPay(payrollRun.getNetPay())
                .totalOvertimeHours(totalOvertime)
                .build();
    }

    @Transactional(readOnly = true)
    public TaxFilingReportResponse taxReport(Long payrollRunId) {
        PayrollRun payrollRun = payrollRunRepository.findById(payrollRunId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found: " + payrollRunId));
        List<PayrollEntry> entries = payrollEntryRepository.findByPayrollRunId(payrollRunId);

        BigDecimal paye = BigDecimal.ZERO;
        BigDecimal social = BigDecimal.ZERO;
        BigDecimal retirement = BigDecimal.ZERO;
        for (PayrollEntry entry : entries) {
            paye = paye.add(entry.getTaxAmount());
            social = social.add(entry.getSocialSecurityAmount());
            retirement = retirement.add(entry.getRetirementAmount());
        }

        return TaxFilingReportResponse.builder()
                .payrollCode(payrollRun.getPayrollCode())
                .currencyCode(payrollRun.getCurrencyCode())
                .payeDue(scale(paye))
                .socialSecurityDue(scale(social))
                .retirementDue(scale(retirement))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PayrollRun> listRuns() {
        return payrollRunRepository.findAll();
    }

    public Currency saveCurrency(CurrencyRequest request) {
        String code = normalizeCurrencyCode(request.getCode());
        Currency currency = currencyRepository.findByCodeIgnoreCase(code).orElseGet(Currency::new);
        if (request.isBaseCurrency()) {
            enforceSingleBaseCurrency(currency.getId());
        } else if (currency.getId() == null && !currencyRepository.existsByBaseCurrencyTrue()) {
            throw new OperationNotAllowedException("Create the base currency first");
        }
        currency.setCode(code);
        currency.setName(request.getName().trim());
        currency.setSymbol(request.getSymbol() == null ? null : request.getSymbol().trim());
        currency.setBaseCurrency(request.isBaseCurrency());
        currency.setActive(request.isActive());
        Currency saved = currencyRepository.save(currency);
        auditTrailService.log("PAYROLL", "Currency", String.valueOf(saved.getId()), "SAVE",
                "Saved currency " + saved.getCode());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Currency> listCurrencies() {
        return currencyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Currency> findCurrency(String currencyCode) {
        return currencyRepository.findByCodeIgnoreCase(normalizeCurrencyCode(currencyCode));
    }

    @Transactional(readOnly = true)
    public Currency getCurrency(String currencyCode) {
        return findCurrency(currencyCode)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + currencyCode));
    }

    @Transactional(readOnly = true)
    public Currency getBaseCurrency() {
        return currencyRepository.findByBaseCurrencyTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Base currency not configured"));
    }

    public ExchangeRate saveExchangeRate(ExchangeRateRequest request) {
        Currency currency = getCurrency(request.getCurrencyCode());
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(currency);
        exchangeRate.setEffectiveDate(request.getEffectiveDate());
        exchangeRate.setRateToBase(currency.isBaseCurrency()
                ? BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP)
                : rateScale(request.getRateToBase()));
        exchangeRate.setNotes(request.getNotes());
        ExchangeRate saved = exchangeRateRepository.save(exchangeRate);
        auditTrailService.log("PAYROLL", "ExchangeRate", String.valueOf(saved.getId()), "SAVE",
                "Saved exchange rate for " + currency.getCode() + " effective " + saved.getEffectiveDate());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> listExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> listExchangeRates(String currencyCode) {
        return exchangeRateRepository.findByCurrencyCodeIgnoreCaseOrderByEffectiveDateDescIdDesc(normalizeCurrencyCode(currencyCode));
    }

    @Transactional(readOnly = true)
    public Optional<ExchangeRate> findEffectiveExchangeRate(String currencyCode, LocalDate effectiveDate) {
        String code = normalizeCurrencyCode(currencyCode);
        Optional<Currency> currency = currencyRepository.findByCodeIgnoreCase(code);
        if (!currency.isPresent()) {
            return Optional.empty();
        }
        if (currency.get().isBaseCurrency()) {
            ExchangeRate baseRate = new ExchangeRate();
            baseRate.setCurrency(currency.get());
            baseRate.setEffectiveDate(effectiveDate);
            baseRate.setRateToBase(BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP));
            baseRate.setNotes("Base currency");
            return Optional.of(baseRate);
        }
        return exchangeRateRepository.findTopByCurrencyCodeIgnoreCaseAndEffectiveDateLessThanEqualOrderByEffectiveDateDescIdDesc(
                code, effectiveDate
        );
    }

    private PayrollEntry buildEntry(PayrollRun payrollRun,
                                    Employee employee,
                                    CompensationPackage compensationPackage,
                                    PayrollEmployeeInput employeeInput,
                                    PayrollProcessRequest request) {
        BigDecimal conversionRate = conversionRate(compensationPackage.getCurrencyCode(), payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal convertedBaseSalary = convertAmount(compensationPackage.getBaseMonthlySalary(),
                compensationPackage.getCurrencyCode(), payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal hourlyRate = convertAmount(compensationPackage.getHourlyRate(),
                compensationPackage.getCurrencyCode(), payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal regularPay = convertedBaseSalary.compareTo(BigDecimal.ZERO) > 0
                ? convertedBaseSalary.divide(compensationPackage.getStandardMonthlyHours(), 2, RoundingMode.HALF_UP)
                .multiply(employeeInput.getHoursWorked())
                : hourlyRate.multiply(employeeInput.getHoursWorked());
        ApprovedOvertime approvedOvertime = approvedOvertime(employee, request);
        ApprovedLeaveSale approvedLeaveSale = approvedLeaveSale(employee, compensationPackage, request,
                payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal totalOvertimeHours = employeeInput.getOvertimeHours().add(approvedOvertime.hours);
        BigDecimal overtimePay = hourlyRate.multiply(totalOvertimeHours).multiply(OVERTIME_MULTIPLIER);
        BigDecimal unpaidLeaveDeduction = unpaidLeaveDeduction(employee, compensationPackage, request,
                payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal bonus = convertAmount(employeeInput.getBonus(), compensationPackage.getCurrencyCode(),
                payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal fixedAllowance = convertAmount(compensationPackage.getFixedAllowance(), compensationPackage.getCurrencyCode(),
                payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal benefitsDeduction = convertAmount(compensationPackage.getFixedBenefitsDeduction(),
                compensationPackage.getCurrencyCode(), payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal otherDeductions = convertAmount(employeeInput.getOtherDeductions(), compensationPackage.getCurrencyCode(),
                payrollRun.getCurrencyCode(), request.getPayDate());
        BigDecimal grossPay = scale(regularPay
                .add(overtimePay)
                .add(approvedLeaveSale.amount)
                .add(bonus)
                .add(fixedAllowance));
        BigDecimal taxAmount = percentage(grossPay, compensationPackage.getIncomeTaxRate());
        BigDecimal socialSecurityAmount = percentage(grossPay, compensationPackage.getSocialSecurityRate());
        BigDecimal retirementAmount = percentage(grossPay, compensationPackage.getRetirementContributionRate());
        BigDecimal totalDeductions = scale(taxAmount
                .add(socialSecurityAmount)
                .add(retirementAmount)
                .add(benefitsDeduction)
                .add(otherDeductions)
                .add(unpaidLeaveDeduction));

        PayrollEntry entry = new PayrollEntry();
        entry.setPayrollRun(payrollRun);
        entry.setEmployee(employee);
        entry.setCurrencyCode(payrollRun.getCurrencyCode());
        entry.setSourceCurrencyCode(compensationPackage.getCurrencyCode());
        entry.setBaseCurrencyCode(payrollRun.getBaseCurrencyCode());
        entry.setExchangeRateUsed(conversionRate);
        entry.setHoursWorked(employeeInput.getHoursWorked());
        entry.setOvertimeHours(totalOvertimeHours);
        entry.setApprovedOvertimeHours(approvedOvertime.hours);
        entry.setApprovedOvertimePay(scale(hourlyRate.multiply(approvedOvertime.hours).multiply(OVERTIME_MULTIPLIER)));
        entry.setLeaveDaysSold(approvedLeaveSale.daysSold);
        entry.setLeaveSaleAmount(approvedLeaveSale.amount);
        entry.setBonus(scale(bonus));
        entry.setGrossPay(grossPay);
        entry.setTaxAmount(taxAmount);
        entry.setSocialSecurityAmount(socialSecurityAmount);
        entry.setRetirementAmount(retirementAmount);
        entry.setBenefitsDeduction(scale(benefitsDeduction));
        entry.setUnpaidLeaveDeduction(unpaidLeaveDeduction);
        entry.setOtherDeductions(scale(otherDeductions));
        entry.setNetPay(scale(grossPay.subtract(totalDeductions)));
        markProcessed(approvedOvertime.claims, approvedLeaveSale.requests);
        return entry;
    }

    private BigDecimal unpaidLeaveDeduction(Employee employee,
                                            CompensationPackage compensationPackage,
                                            PayrollProcessRequest request,
                                            String targetCurrencyCode,
                                            LocalDate conversionDate) {
        BigDecimal unpaidDays = BigDecimal.ZERO;
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeIdAndStatus(employee.getId(), LeaveStatus.APPROVED);
        for (LeaveRequest leaveRequest : leaveRequests) {
            boolean overlaps = !leaveRequest.getEndDate().isBefore(request.getPeriodStart())
                    && !leaveRequest.getStartDate().isAfter(request.getPeriodEnd());
            if (leaveRequest.getLeaveType() == LeaveType.UNPAID && overlaps) {
                unpaidDays = unpaidDays.add(new BigDecimal(leaveRequest.getDaysRequested()));
            }
        }
        if (unpaidDays.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal dailyRate = compensationPackage.getBaseMonthlySalary()
                .divide(new BigDecimal("22"), 2, RoundingMode.HALF_UP);
        return convertAmount(scale(dailyRate.multiply(unpaidDays)),
                compensationPackage.getCurrencyCode(),
                targetCurrencyCode,
                conversionDate);
    }

    private ApprovedOvertime approvedOvertime(Employee employee, PayrollProcessRequest request) {
        BigDecimal approvedHours = BigDecimal.ZERO;
        List<OvertimeClaim> approvedClaims = new ArrayList<OvertimeClaim>();
        for (OvertimeClaim claim : overtimeClaimRepository.findByStatusAndPayrollProcessed(LeaveStatus.APPROVED, false)) {
            if (!claim.getEmployee().getId().equals(employee.getId())) {
                continue;
            }
            boolean overlaps = !claim.getWorkDate().isBefore(request.getPeriodStart())
                    && !claim.getWorkDate().isAfter(request.getPeriodEnd());
            if (overlaps) {
                approvedHours = approvedHours.add(claim.getHoursClaimed());
                approvedClaims.add(claim);
            }
        }
        return new ApprovedOvertime(scale(approvedHours), approvedClaims);
    }

    private ApprovedLeaveSale approvedLeaveSale(Employee employee,
                                                CompensationPackage compensationPackage,
                                                PayrollProcessRequest request,
                                                String targetCurrencyCode,
                                                LocalDate conversionDate) {
        int daysSold = 0;
        List<LeaveSaleRequest> approvedRequests = new ArrayList<LeaveSaleRequest>();
        for (LeaveSaleRequest leaveSaleRequest : leaveSaleRequestRepository.findByStatusAndPayrollProcessed(LeaveStatus.APPROVED, false)) {
            if (!leaveSaleRequest.getEmployee().getId().equals(employee.getId())) {
                continue;
            }
            if (leaveSaleRequest.getDecisionDate() == null) {
                continue;
            }
            boolean overlaps = !leaveSaleRequest.getDecisionDate().isBefore(request.getPeriodStart())
                    && !leaveSaleRequest.getDecisionDate().isAfter(request.getPeriodEnd());
            if (overlaps) {
                daysSold += leaveSaleRequest.getDaysToSell();
                approvedRequests.add(leaveSaleRequest);
            }
        }
        BigDecimal dailyRate = compensationPackage.getBaseMonthlySalary()
                .divide(new BigDecimal("22"), 2, RoundingMode.HALF_UP);
        return new ApprovedLeaveSale(daysSold,
                convertAmount(scale(dailyRate.multiply(new BigDecimal(daysSold))),
                        compensationPackage.getCurrencyCode(),
                        targetCurrencyCode,
                        conversionDate),
                approvedRequests);
    }

    private void markProcessed(List<OvertimeClaim> overtimeClaims, List<LeaveSaleRequest> leaveSaleRequests) {
        for (OvertimeClaim overtimeClaim : overtimeClaims) {
            overtimeClaim.setPayrollProcessed(true);
            overtimeClaimRepository.save(overtimeClaim);
        }
        for (LeaveSaleRequest leaveSaleRequest : leaveSaleRequests) {
            leaveSaleRequest.setPayrollProcessed(true);
            leaveSaleRequestRepository.save(leaveSaleRequest);
        }
    }

    private List<PayrollJournalEntry> postJournal(PayrollRun payrollRun,
                                                  String currencyCode,
                                                  BigDecimal totalGross,
                                                  BigDecimal totalTax,
                                                  BigDecimal totalSocialSecurity,
                                                  BigDecimal totalRetirement,
                                                  BigDecimal totalBenefits,
                                                  BigDecimal totalOtherDeductions,
                                                  BigDecimal totalNet) {
        List<PayrollJournalEntry> journalEntries = new ArrayList<PayrollJournalEntry>();
        journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.SALARY_EXPENSE, JournalEntryType.DEBIT, totalGross, currencyCode, "Payroll gross salary"));
        journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.PAYE_CONTROL, JournalEntryType.CREDIT, totalTax, currencyCode, "PAYE withholding"));
        journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.SOCIAL_SECURITY_CONTROL, JournalEntryType.CREDIT, totalSocialSecurity, currencyCode, "Social security contributions"));
        journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.RETIREMENT_CONTROL, JournalEntryType.CREDIT, totalRetirement, currencyCode, "Retirement contributions"));
        if (totalBenefits.compareTo(BigDecimal.ZERO) > 0) {
            journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.BENEFITS_CONTROL, JournalEntryType.CREDIT, totalBenefits, currencyCode, "Benefits deductions"));
        }
        if (totalOtherDeductions.compareTo(BigDecimal.ZERO) > 0) {
            journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.OTHER_DEDUCTIONS_CONTROL, JournalEntryType.CREDIT, totalOtherDeductions, currencyCode, "Other payroll deductions"));
        }
        journalEntries.add(saveJournal(payrollRun, GeneralLedgerAccount.CASH_AND_BANK, JournalEntryType.CREDIT, totalNet, currencyCode, "Net salaries payable"));
        return journalEntries;
    }

    private PayrollJournalEntry saveJournal(PayrollRun payrollRun,
                                            GeneralLedgerAccount account,
                                            JournalEntryType entryType,
                                            BigDecimal amount,
                                            String currencyCode,
                                            String narration) {
        PayrollJournalEntry journalEntry = new PayrollJournalEntry();
        journalEntry.setPayrollRun(payrollRun);
        journalEntry.setAccount(account);
        journalEntry.setEntryType(entryType);
        journalEntry.setAmount(scale(amount));
        journalEntry.setCurrencyCode(currencyCode);
        journalEntry.setNarration(narration);
        PayrollJournalEntry saved = payrollJournalEntryRepository.save(journalEntry);
        auditTrailService.log("PAYROLL", "PayrollJournalEntry", String.valueOf(saved.getId()), "POST",
                "Posted journal " + saved.getAccount() + " amount " + saved.getAmount());
        return saved;
    }

    private PayrollRunResponse mapRun(PayrollRun payrollRun,
                                      List<PayrollEntry> entries,
                                      List<PayrollJournalEntry> journalEntries) {
        List<PayrollEntryResponse> entryResponses = new ArrayList<PayrollEntryResponse>();
        for (PayrollEntry entry : entries) {
            entryResponses.add(PayrollEntryResponse.builder()
                    .payrollEntryId(entry.getId())
                    .employeeId(entry.getEmployee().getId())
                    .employeeNumber(entry.getEmployee().getEmployeeNumber())
                    .employeeName(entry.getEmployee().getFirstName() + " " + entry.getEmployee().getLastName())
                    .currencyCode(entry.getCurrencyCode())
                    .sourceCurrencyCode(entry.getSourceCurrencyCode())
                    .baseCurrencyCode(entry.getBaseCurrencyCode())
                    .exchangeRateUsed(entry.getExchangeRateUsed())
                    .hoursWorked(entry.getHoursWorked())
                    .overtimeHours(entry.getOvertimeHours())
                    .approvedOvertimeHours(entry.getApprovedOvertimeHours())
                    .approvedOvertimePay(entry.getApprovedOvertimePay())
                    .leaveDaysSold(entry.getLeaveDaysSold())
                    .leaveSaleAmount(entry.getLeaveSaleAmount())
                    .bonus(entry.getBonus())
                    .grossPay(entry.getGrossPay())
                    .taxAmount(entry.getTaxAmount())
                    .socialSecurityAmount(entry.getSocialSecurityAmount())
                    .retirementAmount(entry.getRetirementAmount())
                    .benefitsDeduction(entry.getBenefitsDeduction())
                    .unpaidLeaveDeduction(entry.getUnpaidLeaveDeduction())
                    .otherDeductions(entry.getOtherDeductions())
                    .netPay(entry.getNetPay())
                    .build());
        }

        List<PayrollJournalEntryResponse> journalResponses = new ArrayList<PayrollJournalEntryResponse>();
        for (PayrollJournalEntry journalEntry : journalEntries) {
            journalResponses.add(PayrollJournalEntryResponse.builder()
                    .account(journalEntry.getAccount())
                    .entryType(journalEntry.getEntryType())
                    .amount(journalEntry.getAmount())
                    .currencyCode(journalEntry.getCurrencyCode())
                    .narration(journalEntry.getNarration())
                    .build());
        }

        return PayrollRunResponse.builder()
                .payrollRunId(payrollRun.getId())
                .payrollCode(payrollRun.getPayrollCode())
                .currencyCode(payrollRun.getCurrencyCode())
                .baseCurrencyCode(payrollRun.getBaseCurrencyCode())
                .payDate(payrollRun.getPayDate())
                .periodStart(payrollRun.getPeriodStart())
                .periodEnd(payrollRun.getPeriodEnd())
                .grossPay(payrollRun.getGrossPay())
                .totalDeductions(payrollRun.getTotalDeductions())
                .netPay(payrollRun.getNetPay())
                .journalPosted(payrollRun.isJournalPosted())
                .status(payrollRun.getStatus())
                .entries(entryResponses)
                .journalEntries(journalResponses)
                .build();
    }

    private BigDecimal convertAmount(BigDecimal amount,
                                     String sourceCurrencyCode,
                                     String targetCurrencyCode,
                                     LocalDate effectiveDate) {
        return scale(amount.multiply(conversionRate(sourceCurrencyCode, targetCurrencyCode, effectiveDate)));
    }

    private BigDecimal conversionRate(String sourceCurrencyCode,
                                      String targetCurrencyCode,
                                      LocalDate effectiveDate) {
        String sourceCode = normalizeCurrencyCode(sourceCurrencyCode);
        String targetCode = normalizeCurrencyCode(targetCurrencyCode);
        if (sourceCode.equals(targetCode)) {
            return BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP);
        }
        BigDecimal sourceToBase = rateToBase(sourceCode, effectiveDate);
        BigDecimal targetToBase = rateToBase(targetCode, effectiveDate);
        return sourceToBase.divide(targetToBase, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal rateToBase(String currencyCode, LocalDate effectiveDate) {
        ExchangeRate exchangeRate = findEffectiveExchangeRate(currencyCode, effectiveDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exchange rate not found for " + currencyCode + " on or before " + effectiveDate));
        return rateScale(exchangeRate.getRateToBase());
    }

    private void enforceSingleBaseCurrency(Long currencyId) {
        if (currencyId == null && currencyRepository.existsByBaseCurrencyTrue()) {
            throw new DuplicateResourceException("Only one base currency is allowed");
        }
        if (currencyId != null && currencyRepository.existsByBaseCurrencyTrueAndIdNot(currencyId)) {
            throw new DuplicateResourceException("Only one base currency is allowed");
        }
    }

    private String normalizeCurrencyCode(String currencyCode) {
        return currencyCode == null ? null : currencyCode.trim().toUpperCase();
    }

    private BigDecimal percentage(BigDecimal base, BigDecimal rate) {
        return scale(base.multiply(rate));
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal rateScale(BigDecimal value) {
        return value.setScale(6, RoundingMode.HALF_UP);
    }

    private static class ApprovedOvertime {
        private final BigDecimal hours;
        private final List<OvertimeClaim> claims;

        private ApprovedOvertime(BigDecimal hours, List<OvertimeClaim> claims) {
            this.hours = hours;
            this.claims = claims;
        }
    }

    private static class ApprovedLeaveSale {
        private final int daysSold;
        private final BigDecimal amount;
        private final List<LeaveSaleRequest> requests;

        private ApprovedLeaveSale(int daysSold, BigDecimal amount, List<LeaveSaleRequest> requests) {
            this.daysSold = daysSold;
            this.amount = amount;
            this.requests = requests;
        }
    }
}
