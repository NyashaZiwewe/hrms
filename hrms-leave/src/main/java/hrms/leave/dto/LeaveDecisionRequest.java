package hrms.leave.dto;

import hrms.leave.model.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LeaveDecisionRequest {

    @NotNull
    private LeaveStatus status;

    private Long managerEmployeeId;
}
