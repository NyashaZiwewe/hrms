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
public class RecruitmentRequestInput {

    @NotBlank
    private String positionTitle;

    @NotBlank
    private String department;

    @NotNull
    private Integer requestedHeadcount;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date requestDate;

    @NotNull
    private RecruitmentRequestStatus status;

    private String justification;
}
