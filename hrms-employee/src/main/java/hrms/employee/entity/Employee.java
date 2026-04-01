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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    @JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
    private List<EmployeeAddress> addresses = new ArrayList<EmployeeAddress>();

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
    @Temporal(TemporalType.DATE)
    private Date hireDate;

    @Column
    private String contractDocumentPath;

    @Column
    private String contractFileName;

    @Column
    @Temporal(TemporalType.DATE)
    private Date terminationDate;

    @Column
    private Long managerEmployeeId;

    @Column(nullable = false)
    private boolean accountActive = true;

    @Column(nullable = false)
    private boolean recordsCleared = false;

    @Column(nullable = false, length = 3)
    private String preferredCurrency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    @Column(length = 2000)
    private String employmentHistory;

    @Column(length = 2000)
    private String benefitsSummary;

    @Column(length = 2000)
    private String performanceSummary;

    public String getAddress() {
        return primaryAddress() == null ? null : primaryAddress().getFullAddress();
    }

    public String getProvince() {
        return primaryAddress() == null ? null : primaryAddress().getProvince();
    }

    public String getDistrict() {
        return primaryAddress() == null ? null : primaryAddress().getDistrict();
    }

    public String getStreetAddress() {
        return primaryAddress() == null ? null : primaryAddress().getStreetAddress();
    }

    public java.math.BigDecimal getMonthlySalary() {
        if (jobTitle == null || jobTitle.getGrade() == null || jobTitle.getGrade().getMonthlySalary() == null) {
            return java.math.BigDecimal.ZERO;
        }
        return jobTitle.getGrade().getMonthlySalary();
    }

    public java.math.BigDecimal getHourlyRate() {
        if (jobTitle == null || jobTitle.getGrade() == null || jobTitle.getGrade().getHourlyRate() == null) {
            return java.math.BigDecimal.ZERO;
        }
        return jobTitle.getGrade().getHourlyRate();
    }

    private EmployeeAddress primaryAddress() {
        return addresses == null || addresses.isEmpty() ? null : addresses.get(0);
    }
}
