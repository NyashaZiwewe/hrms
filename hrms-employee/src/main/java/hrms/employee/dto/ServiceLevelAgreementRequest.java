package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class ServiceLevelAgreementRequest {

    @NotBlank
    private String agreementName;

    @NotBlank
    private String counterparty;

    @NotNull
    private LocalDate signedDate;

    private boolean signed;

    @NotBlank
    private String documentPath;

    private String notes;
}
