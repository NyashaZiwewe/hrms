package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StrategicObjectiveRequest {

    @NotNull
    private Long reportingPeriodId;

    @NotNull
    private Long perspectiveId;

    @NotBlank
    private String name;

    private String description;

    private boolean active = true;
}
