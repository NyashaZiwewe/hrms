package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PayrollProcessRequest {

    @NotBlank
    private String payrollCode;

    @NotBlank
    private String currencyCode;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date payDate;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date periodStart;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date periodEnd;

    @Valid
    @NotEmpty
    private List<PayrollEmployeeInput> employees;
}
