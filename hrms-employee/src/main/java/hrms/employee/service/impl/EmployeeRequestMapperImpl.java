package hrms.employee.service.impl;

import hrms.common.util.StringUtils;
import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.Department;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmploymentType;
import hrms.employee.entity.JobTitle;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.service.EmployeeReferenceService;
import hrms.employee.service.EmployeeRequestMapper;
import hrms.employee.service.EmployeeValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeRequestMapperImpl implements EmployeeRequestMapper {

    private final EmployeeReferenceService employeeReferenceService;
    private final EmployeeValidator employeeValidator;

    public EmployeeRequestMapperImpl(EmployeeReferenceService employeeReferenceService,
                                     EmployeeValidator employeeValidator) {
        this.employeeReferenceService = employeeReferenceService;
        this.employeeValidator = employeeValidator;
    }

    public void apply(Employee employee, EmployeeRequest request) {
        employeeValidator.validateManagerAssignment(employee, request.getManagerEmployeeId());

        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setNationalId(request.getNationalId());
        employee.setJobTitle(resolveJobTitle(request));
        employee.setDepartment(resolveDepartment(request));
        employee.setEmploymentType(resolveEmploymentType(request));
        employee.setHireDate(request.getHireDate());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setManagerEmployeeId(request.getManagerEmployeeId());
        employee.setPreferredCurrency(request.getPreferredCurrency());
        employee.setContractDocumentPath(request.getContractDocumentPath());
        employee.setContractFileName(request.getContractFileName());
        employee.setEmploymentHistory(request.getEmploymentHistory());
        employee.setBenefitsSummary(request.getBenefitsSummary());
        employee.setPerformanceSummary(request.getPerformanceSummary());
        employee.setStatus(request.getStatus() == null ? EmploymentStatus.ACTIVE : request.getStatus());
    }

    private Department resolveDepartment(EmployeeRequest request) {
        if (request.getDepartmentId() != null) {
            return employeeReferenceService.findDepartment(request.getDepartmentId());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            return employeeReferenceService.findDepartmentByName(request.getDepartment());
        }
        throw new IllegalArgumentException("Department is required");
    }

    private JobTitle resolveJobTitle(EmployeeRequest request) {
        if (request.getJobTitleId() != null) {
            return employeeReferenceService.findJobTitle(request.getJobTitleId());
        }
        if (StringUtils.hasText(request.getJobTitle())) {
            return employeeReferenceService.findJobTitleByName(request.getJobTitle());
        }
        throw new IllegalArgumentException("Job title is required");
    }

    private EmploymentType resolveEmploymentType(EmployeeRequest request) {
        if (request.getEmploymentTypeId() == null) {
            throw new IllegalArgumentException("Employment type is required");
        }
        return employeeReferenceService.findEmploymentType(request.getEmploymentTypeId());
    }

}
