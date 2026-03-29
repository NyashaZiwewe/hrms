package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class CompensationPackageRequest {

    @NotNull
    private Long employeeId;

    @NotBlank
    private String currencyCode;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseMonthlySalary = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("1.0")
    private BigDecimal standardMonthlyHours = new BigDecimal("173.33");

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal incomeTaxRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal socialSecurityRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal retirementContributionRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fixedBenefitsDeduction = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fixedAllowance = BigDecimal.ZERO;
}
