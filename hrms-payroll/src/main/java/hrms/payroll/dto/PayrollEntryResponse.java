package hrms.payroll.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PayrollEntryResponse {

    private Long payrollEntryId;
    private Long employeeId;
    private String employeeNumber;
    private String employeeName;
    private String currencyCode;
    private String sourceCurrencyCode;
    private String baseCurrencyCode;
    private BigDecimal exchangeRateUsed;
    private BigDecimal hoursWorked;
    private BigDecimal overtimeHours;
    private BigDecimal approvedOvertimeHours;
    private BigDecimal approvedOvertimePay;
    private Integer leaveDaysSold;
    private BigDecimal leaveSaleAmount;
    private BigDecimal bonus;
    private BigDecimal grossPay;
    private BigDecimal taxAmount;
    private BigDecimal socialSecurityAmount;
    private BigDecimal retirementAmount;
    private BigDecimal benefitsDeduction;
    private BigDecimal unpaidLeaveDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal netPay;
}
