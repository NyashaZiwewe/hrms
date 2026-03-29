package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SuccessionPlanRequest {

    @NotNull
    private Long positionOwnerEmployeeId;

    @NotNull
    private Long successorEmployeeId;

    @NotBlank
    private String readinessLevel;

    private String developmentActions;
}
