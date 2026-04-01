package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class PerformanceImprovementPlanRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long reportingPeriodId;

    @NotBlank
    private String targetArea;

    private String concern;

    private String expectedStandard;

    private String agreedAction;

    private String requiredSupport;

    private String reviewNotes;

    private Integer progress = 0;

    @NotBlank
    private String status;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
}
