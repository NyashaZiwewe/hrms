package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PayrollProcessRequest {

    @NotBlank
    private String payrollCode;

    @NotBlank
    private String currencyCode;

    @NotNull
    private LocalDate payDate;

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;

    @Valid
    @NotEmpty
    private List<PayrollEmployeeInput> employees;
}
