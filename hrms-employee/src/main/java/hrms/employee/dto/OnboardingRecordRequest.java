package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class OnboardingRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private OnboardingStage stage;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    private boolean completed;

    private String notes;
}
