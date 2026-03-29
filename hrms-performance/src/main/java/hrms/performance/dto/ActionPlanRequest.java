package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class ActionPlanRequest {

    @NotNull
    private Long contractId;

    @NotNull
    private Long managerId;

    @NotBlank
    private String name;

    private String description;

    private String measureOfSuccess;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Integer progress = 0;

    @NotBlank
    private String status;
}
