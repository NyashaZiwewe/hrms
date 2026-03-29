package hrms.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetricCardResponse {

    private String metric;
    private String value;
    private String description;
}
