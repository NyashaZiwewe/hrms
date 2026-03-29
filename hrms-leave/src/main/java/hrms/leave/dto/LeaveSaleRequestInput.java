package hrms.leave.dto;

import hrms.leave.model.LeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LeaveSaleRequestInput {

    @NotNull
    private Long employeeId;

    @NotNull
    private LeaveType leaveType;

    @NotNull
    @Min(1)
    private Integer daysToSell;

    private String reason;
}
