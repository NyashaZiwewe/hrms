package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class JobTitleRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long gradeId;
}
