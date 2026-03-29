package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class TrainingRecordRequest {

    @NotNull
    private Long employeeId;

    @NotBlank
    private String courseName;

    @NotBlank
    private String provider;

    @NotNull
    private LocalDate completionDate;

    private boolean certified;

    private String developmentPlan;
}
