package hrms.performance.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ActionPlanRequest {

    @NotNull
    private Long contractId;

    @NotNull
    private Long managerId;

    @NotBlank
    private String name;

    private String description;

    private String measureOfSuccess;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    private Integer progress = 0;

    @NotBlank
    private String status;
}
