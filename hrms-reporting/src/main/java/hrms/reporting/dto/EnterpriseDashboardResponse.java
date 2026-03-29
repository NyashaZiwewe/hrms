package hrms.reporting.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EnterpriseDashboardResponse {

    private List<RoleDashboardResponse> dashboards;
}
