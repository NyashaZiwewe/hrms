package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EmploymentConfirmationRequestInput {

    @NotNull
    private Long employeeId;

    private String purpose;

    @Email
    private String deliveryEmail;
}
