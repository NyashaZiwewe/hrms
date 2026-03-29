package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PerformanceReviewRequest {

    @NotNull
    private List<GoalScoreRequest> goalScores;

    private String comment;
}
