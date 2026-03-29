package hrms.payroll.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PayrollReportResponse {

    private String payrollCode;
    private String currencyCode;
    private String baseCurrencyCode;
    private int employeeCount;
    private BigDecimal totalGrossPay;
    private BigDecimal totalTax;
    private BigDecimal totalSocialSecurity;
    private BigDecimal totalRetirement;
    private BigDecimal totalBenefits;
    private BigDecimal totalOtherDeductions;
    private BigDecimal totalNetPay;
    private BigDecimal totalOvertimeHours;
}
