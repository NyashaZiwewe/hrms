package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EmploymentConfirmationApprovalRequest {

    @NotNull
    private Long requestId;

    @NotNull
    private Long signerEmployeeId;

    private String signedDocumentPath;

    private String signedDocumentFileName;
}
