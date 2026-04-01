package hrms.leave.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LeaveTypeRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @Min(0)
    private Integer monthlyEntitlement = 0;

    private boolean balanceTracked;

    private boolean leaveSaleAllowed;

    private boolean active = true;
}
