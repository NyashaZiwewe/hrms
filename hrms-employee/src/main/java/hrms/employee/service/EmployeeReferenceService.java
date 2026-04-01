package hrms.employee.service;

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

import java.util.List;

public interface EmployeeReferenceService {

    Department createDepartment(DepartmentRequest request);

    Department updateDepartment(Long id, DepartmentRequest request);

    List<Department> departments();

    Department findDepartment(Long id);

    Department findDepartmentByName(String name);

    Grade createGrade(GradeRequest request);

    List<Grade> grades();

    Grade findGrade(Long id);

    JobTitle createJobTitle(JobTitleRequest request);

    JobTitle updateJobTitle(Long id, JobTitleRequest request);

    List<JobTitle> jobTitles();

    JobTitle findJobTitle(Long id);

    JobTitle findJobTitleByName(String name);

    EducationLevel createEducationLevel(EducationLevelRequest request);

    EducationLevel updateEducationLevel(Long id, EducationLevelRequest request);

    List<EducationLevel> educationLevels();

    EducationLevel findEducationLevel(Long id);

    EmploymentType createEmploymentType(EmploymentTypeRequest request);

    EmploymentType updateEmploymentType(Long id, EmploymentTypeRequest request);

    List<EmploymentType> employmentTypes();

    EmploymentType findEmploymentType(Long id);

    Institution createInstitution(InstitutionRequest request);

    List<Institution> institutions();

    Institution findInstitution(Long id);
}
