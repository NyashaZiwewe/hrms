package hrms.employee.dto;

import hrms.employee.model.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeRequest {

    private String employeeNumber;

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    private String nationalId;

    private Long jobTitleId;

    private Long departmentId;

    private Long employmentTypeId;

    private String jobTitle;

    private String department;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date hireDate;

    private String contractDocumentPath;

    private String contractFileName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date terminationDate;

    private Long managerEmployeeId;

    @NotBlank
    private String preferredCurrency = "USD";

    private String employmentHistory;

    private String benefitsSummary;

    private String performanceSummary;

    private EmploymentStatus status = EmploymentStatus.ACTIVE;
}
