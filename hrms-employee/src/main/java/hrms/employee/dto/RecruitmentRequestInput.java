package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class RecruitmentRequestInput {

    @NotBlank
    private String positionTitle;

    @NotBlank
    private String department;

    @NotNull
    private Integer requestedHeadcount;

    @NotNull
    private LocalDate requestDate;

    @NotNull
    private RecruitmentRequestStatus status;

    private String justification;
}
