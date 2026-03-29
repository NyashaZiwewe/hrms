package hrms.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleSummaryResponse {

    private String module;
    private String description;
    private String status;
}
