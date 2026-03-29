package hrms.performance.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PerformanceAnalyticsResponse {

    private long contracts;
    private long selfReviewedContracts;
    private long managerReviewedContracts;
    private long actionPlans;
    private long improvementPlans;
    private long goalsWithTrainingNeeds;
    private BigDecimal averageWeightedScore;
}
