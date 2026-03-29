package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeStatusChangeRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private EmploymentStatus newStatus;

    @NotBlank
    private String eventType;

    @NotNull
    private LocalDate effectiveDate;

    private String notes;
}
