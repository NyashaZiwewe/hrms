package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class OnboardingRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private OnboardingStage stage;

    @NotNull
    private LocalDate dueDate;

    private boolean completed;

    private String notes;
}
