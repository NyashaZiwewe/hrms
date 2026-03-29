package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDependentRequest {

    private String fullName;
    private String relationship;
    private LocalDate dateOfBirth;
    private String notes;
}
