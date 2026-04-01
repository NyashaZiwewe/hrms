package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class OffboardingRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date lastWorkingDate;

    private boolean exitInterviewCompleted;

    private boolean assetsReturned;

    private boolean financeClearanceCompleted;

    private boolean recordsClearanceCompleted;

    private boolean accountDeactivationCompleted;

    private String notes;
}
