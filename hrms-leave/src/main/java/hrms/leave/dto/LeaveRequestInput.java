package hrms.leave.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class LeaveRequestInput {

    @NotNull
    private Long employeeId;

    private Long managerEmployeeId;

    @NotNull
    @JsonAlias("leaveType")
    private String leaveTypeCode;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    private String reason;

    private boolean managerAssigned;
}
