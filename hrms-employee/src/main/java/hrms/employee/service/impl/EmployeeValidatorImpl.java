package hrms.employee.service.impl;

import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import hrms.common.util.StringUtils;
import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.Employee;
import hrms.employee.repository.EmployeeRepository;
import hrms.employee.service.EmployeeValidator;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidatorImpl implements EmployeeValidator {

    private final EmployeeRepository employeeRepository;

    public EmployeeValidatorImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void validateForCreate(EmployeeRequest request) {
        validateEmployeeNumber(null, request.getEmployeeNumber());
        employeeRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(existing -> {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        });
        validateNationalId(null, request.getNationalId());
    }

    public void validateForUpdate(Long employeeId, EmployeeRequest request) {
        validateEmployeeNumber(employeeId, request.getEmployeeNumber());
        employeeRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(existing -> !existing.getId().equals(employeeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Email already exists: " + request.getEmail());
                });
        validateNationalId(employeeId, request.getNationalId());
    }

    public void validateManagerAssignment(Employee employee, Long managerEmployeeId) {
        if (managerEmployeeId == null) {
            return;
        }
        if (employee.getId() != null && employee.getId().equals(managerEmployeeId)) {
            throw new OperationNotAllowedException("Employee cannot report to themselves");
        }
        if (!employeeRepository.existsById(managerEmployeeId)) {
            throw new ResourceNotFoundException("Supervisor not found: " + managerEmployeeId);
        }
    }

    public void validateEmployeeNumber(Long employeeId, String employeeNumber) {
        if (!StringUtils.hasText(employeeNumber)) {
            return;
        }
        employeeRepository.findByEmployeeNumber(employeeNumber)
                .filter(existing -> employeeId == null || !existing.getId().equals(employeeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Employee number already exists: " + employeeNumber);
                });
    }

    private void validateNationalId(Long employeeId, String nationalId) {
        if (!StringUtils.hasText(nationalId)) {
            return;
        }
        employeeRepository.findByNationalId(nationalId)
                .filter(existing -> employeeId == null || !existing.getId().equals(employeeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("National ID already exists: " + nationalId);
                });
    }
}
