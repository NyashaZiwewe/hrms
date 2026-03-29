package hrms.employee.config;

import hrms.employee.dto.DepartmentRequest;
import hrms.employee.dto.EducationLevelRequest;
import hrms.employee.dto.EmploymentTypeRequest;
import hrms.employee.dto.GradeRequest;
import hrms.employee.dto.InstitutionRequest;
import hrms.employee.dto.JobTitleRequest;
import hrms.employee.entity.Grade;
import hrms.employee.service.EmployeeReferenceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EmployeeReferenceDataInitializer implements ApplicationRunner {

    private final EmployeeReferenceService employeeReferenceService;

    public EmployeeReferenceDataInitializer(EmployeeReferenceService employeeReferenceService) {
        this.employeeReferenceService = employeeReferenceService;
    }

    public void run(ApplicationArguments args) {
        if (employeeReferenceService.departments().isEmpty()) {
            createDepartment("Human Resources");
            createDepartment("Finance");
            createDepartment("Operations");
            createDepartment("Information Technology");
        }
        if (employeeReferenceService.grades().isEmpty()) {
            createGrade("Grade A", new BigDecimal("4500.00"), new BigDecimal("25.96"));
            createGrade("Grade B", new BigDecimal("3000.00"), new BigDecimal("17.31"));
            createGrade("Grade C", new BigDecimal("1800.00"), new BigDecimal("10.38"));
        }
        if (employeeReferenceService.jobTitles().isEmpty()) {
            Map<String, String> gradeByTitle = new LinkedHashMap<String, String>();
            gradeByTitle.put("HR Manager", "Grade A");
            gradeByTitle.put("Finance Manager", "Grade A");
            gradeByTitle.put("Operations Director", "Grade A");
            gradeByTitle.put("Accountant", "Grade B");
            gradeByTitle.put("Finance Officer", "Grade B");
            gradeByTitle.put("Payroll Officer", "Grade B");
            gradeByTitle.put("HR Business Partner", "Grade B");
            gradeByTitle.put("Systems Administrator", "Grade B");
            gradeByTitle.put("Operations Analyst", "Grade C");
            gradeByTitle.put("HR Officer", "Grade C");
            gradeByTitle.put("HR Assistant", "Grade C");
            for (Map.Entry<String, String> entry : gradeByTitle.entrySet()) {
                JobTitleRequest request = new JobTitleRequest();
                request.setName(entry.getKey());
                request.setGradeId(findGradeId(entry.getValue()));
                employeeReferenceService.createJobTitle(request);
            }
        }
        if (employeeReferenceService.educationLevels().isEmpty()) {
            createEducationLevel("Certificate");
            createEducationLevel("Diploma");
            createEducationLevel("Bachelor's Degree");
            createEducationLevel("Master's Degree");
            createEducationLevel("Doctorate");
        }
        if (employeeReferenceService.institutions().isEmpty()) {
            createInstitution("University of Zimbabwe");
            createInstitution("Midlands State University");
            createInstitution("Harare Polytechnic");
            createInstitution("Chinhoyi University of Technology");
        }
        if (employeeReferenceService.employmentTypes().isEmpty()) {
            createEmploymentType("Permanent");
            createEmploymentType("Contract");
            createEmploymentType("Temporary");
            createEmploymentType("Internship");
            createEmploymentType("Consultant");
        }
    }

    private void createDepartment(String name) {
        DepartmentRequest request = new DepartmentRequest();
        request.setName(name);
        employeeReferenceService.createDepartment(request);
    }

    private void createGrade(String name, BigDecimal monthlySalary, BigDecimal hourlyRate) {
        GradeRequest request = new GradeRequest();
        request.setName(name);
        request.setMonthlySalary(monthlySalary);
        request.setHourlyRate(hourlyRate);
        employeeReferenceService.createGrade(request);
    }

    private Long findGradeId(String name) {
        for (Grade grade : employeeReferenceService.grades()) {
            if (grade.getName().equals(name)) {
                return grade.getId();
            }
        }
        throw new IllegalStateException("Grade not found during bootstrap: " + name);
    }

    private void createEducationLevel(String name) {
        EducationLevelRequest request = new EducationLevelRequest();
        request.setName(name);
        employeeReferenceService.createEducationLevel(request);
    }

    private void createInstitution(String name) {
        InstitutionRequest request = new InstitutionRequest();
        request.setName(name);
        employeeReferenceService.createInstitution(request);
    }

    private void createEmploymentType(String name) {
        EmploymentTypeRequest request = new EmploymentTypeRequest();
        request.setName(name);
        employeeReferenceService.createEmploymentType(request);
    }
}
