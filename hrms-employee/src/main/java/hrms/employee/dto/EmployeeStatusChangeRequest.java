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
public class EmployeeStatusChangeRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private EmploymentStatus newStatus;

    @NotBlank
    private String eventType;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date effectiveDate;

    private String notes;
}
