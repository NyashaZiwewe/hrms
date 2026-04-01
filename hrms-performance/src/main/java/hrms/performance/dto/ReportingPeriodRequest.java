package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ReportingPeriodRequest {

    @NotBlank
    private String name;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    private boolean active;
}
