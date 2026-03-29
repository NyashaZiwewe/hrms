package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmploymentTypeRequest {

    @NotBlank
    private String name;
}
