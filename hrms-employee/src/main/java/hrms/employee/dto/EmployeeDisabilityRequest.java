package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDisabilityRequest {

    private String disabilityName;
    private String notes;
}
