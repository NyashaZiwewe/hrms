package hrms.leave.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("leaveType")
    private String leaveTypeCode;

    @NotNull
    @Min(1)
    private Integer daysToSell;

    private String reason;
}
