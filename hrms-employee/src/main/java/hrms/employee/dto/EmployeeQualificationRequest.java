package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeQualificationRequest {

    private Long educationLevelId;
    private Long institutionId;
    private String qualificationName;
    private Integer completionYear;
    private String periodStudied;
}
