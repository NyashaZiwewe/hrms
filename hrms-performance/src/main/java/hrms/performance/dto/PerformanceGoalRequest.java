package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class PerformanceGoalRequest {

    @NotNull
    private Long contractId;

    @NotNull
    private Long strategicObjectiveId;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal allocatedWeight;

    private String measure;

    private String targetValue;

    private String skillGap;

    private String trainingNeed;
}
