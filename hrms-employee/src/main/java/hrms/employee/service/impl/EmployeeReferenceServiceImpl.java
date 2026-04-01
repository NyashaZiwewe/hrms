package hrms.employee.service.impl;

import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.ResourceNotFoundException;
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
import hrms.employee.repository.DepartmentRepository;
import hrms.employee.repository.EducationLevelRepository;
import hrms.employee.repository.EmploymentTypeRepository;
import hrms.employee.repository.GradeRepository;
import hrms.employee.repository.InstitutionRepository;
import hrms.employee.repository.JobTitleRepository;
import hrms.employee.service.EmployeeReferenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeReferenceServiceImpl implements EmployeeReferenceService {

    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;
    private final JobTitleRepository jobTitleRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final InstitutionRepository institutionRepository;

    public EmployeeReferenceServiceImpl(DepartmentRepository departmentRepository,
                                        GradeRepository gradeRepository,
                                        JobTitleRepository jobTitleRepository,
                                        EducationLevelRepository educationLevelRepository,
                                        EmploymentTypeRepository employmentTypeRepository,
                                        InstitutionRepository institutionRepository) {
        this.departmentRepository = departmentRepository;
        this.gradeRepository = gradeRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.educationLevelRepository = educationLevelRepository;
        this.employmentTypeRepository = employmentTypeRepository;
        this.institutionRepository = institutionRepository;
    }

    public Department createDepartment(DepartmentRequest request) {
        departmentRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Department already exists: " + request.getName());
        });
        Department department = new Department();
        department.setName(request.getName().trim());
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);
        departmentRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Department already exists: " + request.getName());
                });
        department.setName(request.getName().trim());
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<Department> departments() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    @Transactional(readOnly = true)
    public Department findDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + name));
    }

    public Grade createGrade(GradeRequest request) {
        gradeRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Grade already exists: " + request.getName());
        });
        Grade grade = new Grade();
        grade.setName(request.getName().trim());
        grade.setMonthlySalary(request.getMonthlySalary());
        grade.setHourlyRate(request.getHourlyRate());
        return gradeRepository.save(grade);
    }

    @Transactional(readOnly = true)
    public List<Grade> grades() {
        return gradeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Grade findGrade(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + id));
    }

    public JobTitle createJobTitle(JobTitleRequest request) {
        jobTitleRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Job title already exists: " + request.getName());
        });
        JobTitle jobTitle = new JobTitle();
        jobTitle.setName(request.getName().trim());
        jobTitle.setGrade(findGrade(request.getGradeId()));
        return jobTitleRepository.save(jobTitle);
    }

    public JobTitle updateJobTitle(Long id, JobTitleRequest request) {
        JobTitle jobTitle = findJobTitle(id);
        jobTitleRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Job title already exists: " + request.getName());
                });
        jobTitle.setName(request.getName().trim());
        jobTitle.setGrade(findGrade(request.getGradeId()));
        return jobTitleRepository.save(jobTitle);
    }

    @Transactional(readOnly = true)
    public List<JobTitle> jobTitles() {
        return jobTitleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public JobTitle findJobTitle(Long id) {
        return jobTitleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job title not found: " + id));
    }

    @Transactional(readOnly = true)
    public JobTitle findJobTitleByName(String name) {
        return jobTitleRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Job title not found: " + name));
    }

    public EducationLevel createEducationLevel(EducationLevelRequest request) {
        educationLevelRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Education level already exists: " + request.getName());
        });
        EducationLevel educationLevel = new EducationLevel();
        educationLevel.setName(request.getName().trim());
        return educationLevelRepository.save(educationLevel);
    }

    public EducationLevel updateEducationLevel(Long id, EducationLevelRequest request) {
        EducationLevel educationLevel = findEducationLevel(id);
        educationLevelRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Education level already exists: " + request.getName());
                });
        educationLevel.setName(request.getName().trim());
        return educationLevelRepository.save(educationLevel);
    }

    @Transactional(readOnly = true)
    public List<EducationLevel> educationLevels() {
        return educationLevelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EducationLevel findEducationLevel(Long id) {
        return educationLevelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education level not found: " + id));
    }

    public EmploymentType createEmploymentType(EmploymentTypeRequest request) {
        employmentTypeRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Employment type already exists: " + request.getName());
        });
        EmploymentType employmentType = new EmploymentType();
        employmentType.setName(request.getName().trim());
        return employmentTypeRepository.save(employmentType);
    }

    public EmploymentType updateEmploymentType(Long id, EmploymentTypeRequest request) {
        EmploymentType employmentType = findEmploymentType(id);
        employmentTypeRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Employment type already exists: " + request.getName());
                });
        employmentType.setName(request.getName().trim());
        return employmentTypeRepository.save(employmentType);
    }

    @Transactional(readOnly = true)
    public List<EmploymentType> employmentTypes() {
        return employmentTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EmploymentType findEmploymentType(Long id) {
        return employmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment type not found: " + id));
    }

    public Institution createInstitution(InstitutionRequest request) {
        institutionRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Institution already exists: " + request.getName());
        });
        Institution institution = new Institution();
        institution.setName(request.getName().trim());
        return institutionRepository.save(institution);
    }

    @Transactional(readOnly = true)
    public List<Institution> institutions() {
        return institutionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Institution findInstitution(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found: " + id));
    }
}
