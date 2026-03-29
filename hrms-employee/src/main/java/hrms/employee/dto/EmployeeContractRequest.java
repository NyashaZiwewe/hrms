package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeContractRequest {

    @NotBlank
    private String contractName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String documentPath;

    private String fileName;

    private boolean active;

    private String notes;
}
