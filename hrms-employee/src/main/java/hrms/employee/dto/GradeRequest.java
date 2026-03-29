package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class GradeRequest {

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal monthlySalary;

    @NotNull
    private BigDecimal hourlyRate;
}
