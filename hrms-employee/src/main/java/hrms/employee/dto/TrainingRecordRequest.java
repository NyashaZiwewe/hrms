package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date completionDate;

    private boolean certified;

    private String developmentPlan;
}
