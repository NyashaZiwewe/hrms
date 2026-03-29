package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class GoalScoreRequest {

    @NotNull
    private Long goalId;

    @NotNull
    private BigDecimal score;
}
