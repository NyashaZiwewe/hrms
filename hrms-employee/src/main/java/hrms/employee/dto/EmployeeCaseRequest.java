package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeCaseRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private CaseType caseType;

    @NotNull
    private CaseStatus status;

    @NotNull
    private LocalDate openedDate;

    private String description;

    private String resolution;
}
