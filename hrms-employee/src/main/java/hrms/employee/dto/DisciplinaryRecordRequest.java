package hrms.employee.dto;

import hrms.employee.model.DisciplinaryRecordType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class DisciplinaryRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private DisciplinaryRecordType recordType;

    @NotNull
    private LocalDate recordDate;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String subject;

    private String details;

    private String outcome;
}
