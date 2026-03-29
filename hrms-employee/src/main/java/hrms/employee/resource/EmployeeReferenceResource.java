package hrms.employee.resource;

import hrms.employee.dto.DepartmentRequest;
import hrms.employee.dto.EducationLevelRequest;
import hrms.employee.dto.EmploymentTypeRequest;
import hrms.employee.dto.GradeRequest;
import hrms.employee.dto.InstitutionRequest;
import hrms.employee.dto.JobTitleRequest;
import hrms.employee.entity.Department;
import hrms.employee.entity.EducationLevel;
import hrms.employee.entity.EmploymentType;
import hrms.employee.entity.Grade;
import hrms.employee.entity.Institution;
import hrms.employee.entity.JobTitle;
import hrms.employee.service.EmployeeReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employee-reference")
public class EmployeeReferenceResource {

    private final EmployeeReferenceService employeeReferenceService;

    public EmployeeReferenceResource(EmployeeReferenceService employeeReferenceService) {
        this.employeeReferenceService = employeeReferenceService;
    }

    @PostMapping("/departments")
    @ResponseStatus(HttpStatus.CREATED)
    public Department createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return employeeReferenceService.createDepartment(request);
    }

    @GetMapping("/departments")
    public List<Department> departments() {
        return employeeReferenceService.departments();
    }

    @PostMapping("/grades")
    @ResponseStatus(HttpStatus.CREATED)
    public Grade createGrade(@Valid @RequestBody GradeRequest request) {
        return employeeReferenceService.createGrade(request);
    }

    @GetMapping("/grades")
    public List<Grade> grades() {
        return employeeReferenceService.grades();
    }

    @PostMapping("/job-titles")
    @ResponseStatus(HttpStatus.CREATED)
    public JobTitle createJobTitle(@Valid @RequestBody JobTitleRequest request) {
        return employeeReferenceService.createJobTitle(request);
    }

    @GetMapping("/job-titles")
    public List<JobTitle> jobTitles() {
        return employeeReferenceService.jobTitles();
    }

    @PostMapping("/education-levels")
    @ResponseStatus(HttpStatus.CREATED)
    public EducationLevel createEducationLevel(@Valid @RequestBody EducationLevelRequest request) {
        return employeeReferenceService.createEducationLevel(request);
    }

    @GetMapping("/education-levels")
    public List<EducationLevel> educationLevels() {
        return employeeReferenceService.educationLevels();
    }

    @PostMapping("/employment-types")
    @ResponseStatus(HttpStatus.CREATED)
    public EmploymentType createEmploymentType(@Valid @RequestBody EmploymentTypeRequest request) {
        return employeeReferenceService.createEmploymentType(request);
    }

    @GetMapping("/employment-types")
    public List<EmploymentType> employmentTypes() {
        return employeeReferenceService.employmentTypes();
    }

    @PostMapping("/institutions")
    @ResponseStatus(HttpStatus.CREATED)
    public Institution createInstitution(@Valid @RequestBody InstitutionRequest request) {
        return employeeReferenceService.createInstitution(request);
    }

    @GetMapping("/institutions")
    public List<Institution> institutions() {
        return employeeReferenceService.institutions();
    }
}
