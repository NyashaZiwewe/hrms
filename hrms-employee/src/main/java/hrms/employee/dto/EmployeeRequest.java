package hrms.employee.dto;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeRequest {

    @NotBlank
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

    private String address;

    private String nationalId;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private Long jobTitleId;

    private Long departmentId;

    private Long employmentTypeId;

    private String jobTitle;

    private String department;

    @NotNull
    private LocalDate hireDate;

    private String contractDocumentPath;

    private String contractFileName;

    private LocalDate terminationDate;

    private Long managerEmployeeId;

    @NotBlank
    private String preferredCurrency = "USD";

    private BigDecimal monthlySalary = BigDecimal.ZERO;

    private BigDecimal hourlyRate = BigDecimal.ZERO;

    private String employmentHistory;

    private String benefitsSummary;

    private String performanceSummary;

    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    private List<EmployeeQualificationRequest> qualifications;

    private List<EmployeeDependentRequest> dependents;

    private List<EmployeeDisabilityRequest> disabilities;

    private List<EmployeeRelatedContactRequest> relatedContacts;
}
