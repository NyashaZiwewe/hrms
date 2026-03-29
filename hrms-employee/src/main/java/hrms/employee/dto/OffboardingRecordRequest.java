package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class OffboardingRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate lastWorkingDate;

    private boolean exitInterviewCompleted;

    private boolean assetsReturned;

    private boolean financeClearanceCompleted;

    private boolean recordsClearanceCompleted;

    private boolean accountDeactivationCompleted;

    private String notes;
}
