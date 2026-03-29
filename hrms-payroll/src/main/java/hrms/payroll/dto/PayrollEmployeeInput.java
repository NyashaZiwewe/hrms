package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class PayrollEmployeeInput {

    @NotNull
    private Long employeeId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal hoursWorked = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal bonus = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal otherDeductions = BigDecimal.ZERO;
}
