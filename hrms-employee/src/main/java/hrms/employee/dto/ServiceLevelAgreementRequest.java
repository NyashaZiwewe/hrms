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
public class ServiceLevelAgreementRequest {

    @NotBlank
    private String agreementName;

    @NotBlank
    private String counterparty;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date signedDate;

    private boolean signed;

    @NotBlank
    private String documentPath;

    private String notes;
}
