package hrms.employee.entity;

import hrms.employee.model.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String middleName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phoneNumber;

    @Column(length = 1000)
    private String address;

    @Column(unique = true)
    private String nationalId;

    @Column
    private String emergencyContactName;

    @Column
    private String emergencyContactPhone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_title_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private JobTitle jobTitle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employment_type_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EmploymentType employmentType;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column
    private String contractDocumentPath;

    @Column
    private String contractFileName;

    @Column
    private LocalDate terminationDate;

    @Column
    private Long managerEmployeeId;

    @Column(nullable = false)
    private boolean accountActive = true;

    @Column(nullable = false)
    private boolean recordsCleared = false;

    @Column(nullable = false, length = 3)
    private String preferredCurrency = "USD";

    @Column(nullable = false, precision = 14, scale = 2)
    private java.math.BigDecimal monthlySalary = java.math.BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private java.math.BigDecimal hourlyRate = java.math.BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    @Column(length = 2000)
    private String employmentHistory;

    @Column(length = 2000)
    private String benefitsSummary;

    @Column(length = 2000)
    private String performanceSummary;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeQualification> qualifications = new ArrayList<EmployeeQualification>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeDependent> dependents = new ArrayList<EmployeeDependent>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeDisability> disabilities = new ArrayList<EmployeeDisability>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeRelatedContact> relatedContacts = new ArrayList<EmployeeRelatedContact>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeContract> contracts = new ArrayList<EmployeeContract>();
}
