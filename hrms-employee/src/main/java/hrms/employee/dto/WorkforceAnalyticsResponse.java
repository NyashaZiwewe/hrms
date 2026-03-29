package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class WorkforceAnalyticsResponse {

    private long totalHeadcount;
    private long activeHeadcount;
    private long inactiveHeadcount;
    private long terminatedHeadcount;
    private long onboardingInProgress;
    private long offboardingInProgress;
    private long openRecruitmentRequests;
    private Map<String, Long> departmentHeadcount;
}
