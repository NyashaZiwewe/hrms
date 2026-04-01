package hrms.employee.dto;

import hrms.employee.model.DisciplinaryRecordType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class DisciplinaryRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private DisciplinaryRecordType recordType;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date recordDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date effectiveFrom;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date effectiveTo;

    private String subject;

    private String details;

    private String outcome;
}
