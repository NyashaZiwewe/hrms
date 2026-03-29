package hrms.leave.dto;

import hrms.leave.model.LeaveType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestInput {

    @NotNull
    private Long employeeId;

    private Long managerEmployeeId;

    @NotNull
    private LeaveType leaveType;

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

    private String reason;

    private boolean managerAssigned;
}
