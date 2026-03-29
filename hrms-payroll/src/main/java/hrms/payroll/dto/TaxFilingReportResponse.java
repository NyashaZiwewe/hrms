package hrms.payroll.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TaxFilingReportResponse {

    private String payrollCode;
    private String currencyCode;
    private BigDecimal payeDue;
    private BigDecimal socialSecurityDue;
    private BigDecimal retirementDue;
}
