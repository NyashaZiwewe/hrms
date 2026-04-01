package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

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
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date openedDate;

    private String description;

    private String resolution;
}
