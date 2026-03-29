package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PerformanceContractRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long reportingPeriodId;

    @NotBlank
    private String title;
}
