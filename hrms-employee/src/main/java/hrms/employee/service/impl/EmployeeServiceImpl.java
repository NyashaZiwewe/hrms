package hrms.employee.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import hrms.common.util.StringUtils;
import hrms.employee.dto.EmployeeDependentRequest;
import hrms.employee.dto.EmployeeDisabilityRequest;
import hrms.employee.dto.EmployeeContractRequest;
import hrms.employee.dto.EmployeeQualificationRequest;
import hrms.employee.dto.EmployeeRelatedContactRequest;
import hrms.employee.entity.Department;
import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.EmployeeDependent;
import hrms.employee.entity.EmployeeDisability;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeContract;
import hrms.employee.entity.EmployeeQualification;
import hrms.employee.entity.EmployeeRelatedContact;
import hrms.employee.entity.EmploymentType;
import hrms.employee.entity.JobTitle;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.repository.EmployeeRepository;
import hrms.employee.service.EmployeeReferenceService;
import hrms.employee.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditTrailService auditTrailService;
    private final EmployeeReferenceService employeeReferenceService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               AuditTrailService auditTrailService,
                               EmployeeReferenceService employeeReferenceService) {
        this.employeeRepository = employeeRepository;
        this.auditTrailService = auditTrailService;
        this.employeeReferenceService = employeeReferenceService;
    }

    public Employee create(EmployeeRequest request) {
        employeeRepository.findByEmployeeNumber(request.getEmployeeNumber()).ifPresent(existing -> {
            throw new DuplicateResourceException("Employee number already exists: " + request.getEmployeeNumber());
        });
        Employee employee = new Employee();
        apply(employee, request);
        Employee saved = employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(saved.getId()), "CREATE",
                "Created employee profile for " + saved.getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> search(String query,
                                 String department,
                                 String jobTitle,
                                 EmploymentStatus status,
                                 LocalDate hiredFrom,
                                 LocalDate hiredTo) {
        List<Employee> employees = new ArrayList<Employee>(employeeRepository.findAll());
        if (StringUtils.hasText(query)) {
            String normalized = query.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> !matchesQuery(employee, normalized));
        }
        if (StringUtils.hasText(department)) {
            String normalized = department.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> employee.getDepartment() == null
                    || employee.getDepartment().getName() == null
                    || !employee.getDepartment().getName().toLowerCase(Locale.ENGLISH).contains(normalized));
        }
        if (StringUtils.hasText(jobTitle)) {
            String normalized = jobTitle.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> employee.getJobTitle() == null
                    || employee.getJobTitle().getName() == null
                    || !employee.getJobTitle().getName().toLowerCase(Locale.ENGLISH).contains(normalized));
        }
        if (status != null) {
            employees.removeIf(employee -> employee.getStatus() != status);
        }
        if (hiredFrom != null) {
            employees.removeIf(employee -> employee.getHireDate() == null || employee.getHireDate().isBefore(hiredFrom));
        }
        if (hiredTo != null) {
            employees.removeIf(employee -> employee.getHireDate() == null || employee.getHireDate().isAfter(hiredTo));
        }
        return employees;
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = findById(id);
        employeeRepository.findByEmployeeNumber(request.getEmployeeNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Employee number already exists: " + request.getEmployeeNumber());
                });
        apply(employee, request);
        Employee saved = employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(saved.getId()), "UPDATE",
                "Updated employee profile for " + saved.getEmployeeNumber());
        return saved;
    }

    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(id), "DELETE",
                "Deleted employee profile for " + employee.getEmployeeNumber());
    }

    public EmployeeDependent addDependent(Long employeeId, EmployeeDependentRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getFullName())) {
            throw new IllegalArgumentException("Dependent name is required");
        }
        EmployeeDependent dependent = new EmployeeDependent();
        dependent.setEmployee(employee);
        dependent.setFullName(request.getFullName().trim());
        dependent.setRelationship(request.getRelationship());
        dependent.setDateOfBirth(request.getDateOfBirth());
        dependent.setNotes(request.getNotes());
        employee.getDependents().add(dependent);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeDependent", String.valueOf(employeeId), "CREATE",
                "Added dependent for employee " + employee.getEmployeeNumber());
        return dependent;
    }

    public EmployeeDisability addDisability(Long employeeId, EmployeeDisabilityRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getDisabilityName())) {
            throw new IllegalArgumentException("Disability name is required");
        }
        EmployeeDisability disability = new EmployeeDisability();
        disability.setEmployee(employee);
        disability.setDisabilityName(request.getDisabilityName().trim());
        disability.setNotes(request.getNotes());
        employee.getDisabilities().add(disability);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeDisability", String.valueOf(employeeId), "CREATE",
                "Added disability record for employee " + employee.getEmployeeNumber());
        return disability;
    }

    public EmployeeRelatedContact addRelatedContact(Long employeeId, EmployeeRelatedContactRequest request) {
        Employee employee = findById(employeeId);
        if (request.getContactType() == null) {
            throw new IllegalArgumentException("Contact type is required");
        }
        if (!StringUtils.hasText(request.getFullName())) {
            throw new IllegalArgumentException("Contact name is required");
        }
        EmployeeRelatedContact contact = new EmployeeRelatedContact();
        contact.setEmployee(employee);
        contact.setContactType(request.getContactType());
        contact.setFullName(request.getFullName().trim());
        contact.setRelationshipDescription(request.getRelationshipDescription());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmailAddress(request.getEmailAddress());
        contact.setAddress(request.getAddress());
        employee.getRelatedContacts().add(contact);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeRelatedContact", String.valueOf(employeeId), "CREATE",
                "Added related contact for employee " + employee.getEmployeeNumber());
        return contact;
    }

    public EmployeeContract addContract(Long employeeId, EmployeeContractRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getContractName())) {
            throw new IllegalArgumentException("Contract name is required");
        }
        if (request.getEndDate() != null && request.getStartDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }
        if (request.isActive()) {
            for (EmployeeContract contract : employee.getContracts()) {
                contract.setActive(false);
            }
        }
        EmployeeContract contract = new EmployeeContract();
        contract.setEmployee(employee);
        contract.setContractName(request.getContractName().trim());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDocumentPath(request.getDocumentPath());
        contract.setFileName(request.getFileName());
        contract.setActive(request.isActive() || employee.getContracts().isEmpty());
        contract.setNotes(request.getNotes());
        employee.getContracts().add(contract);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeContract", String.valueOf(employeeId), "CREATE",
                "Added contract for employee " + employee.getEmployeeNumber());
        return contract;
    }

    private void apply(Employee employee, EmployeeRequest request) {
        Department department = resolveDepartment(request);
        JobTitle jobTitle = resolveJobTitle(request);
        EmploymentType employmentType = resolveEmploymentType(request);
        validateManager(employee, request);
        employee.setEmployeeNumber(request.getEmployeeNumber());
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setAddress(request.getAddress());
        employee.setNationalId(request.getNationalId());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setJobTitle(jobTitle);
        employee.setDepartment(department);
        employee.setEmploymentType(employmentType);
        employee.setHireDate(request.getHireDate());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setManagerEmployeeId(request.getManagerEmployeeId());
        employee.setPreferredCurrency(request.getPreferredCurrency());
        employee.setMonthlySalary(jobTitle.getGrade().getMonthlySalary());
        employee.setHourlyRate(jobTitle.getGrade().getHourlyRate());
        employee.setContractDocumentPath(request.getContractDocumentPath());
        employee.setContractFileName(request.getContractFileName());
        employee.setEmploymentHistory(request.getEmploymentHistory());
        employee.setBenefitsSummary(request.getBenefitsSummary());
        employee.setPerformanceSummary(request.getPerformanceSummary());
        employee.setStatus(request.getStatus() == null ? EmploymentStatus.ACTIVE : request.getStatus());
        if (request.getQualifications() != null) {
            employee.getQualifications().clear();
            for (EmployeeQualificationRequest qualificationRequest : request.getQualifications()) {
                if (qualificationRequest == null || !hasQualificationContent(qualificationRequest)) {
                    continue;
                }
                if (qualificationRequest.getEducationLevelId() == null) {
                    throw new IllegalArgumentException("Education level is required for each qualification");
                }
                if (qualificationRequest.getInstitutionId() == null) {
                    throw new IllegalArgumentException("Institution is required for each qualification");
                }
                if (!StringUtils.hasText(qualificationRequest.getQualificationName())) {
                    throw new IllegalArgumentException("Qualification name is required for each qualification");
                }
                EmployeeQualification qualification = new EmployeeQualification();
                qualification.setEmployee(employee);
                qualification.setEducationLevel(employeeReferenceService.findEducationLevel(qualificationRequest.getEducationLevelId()));
                qualification.setInstitution(employeeReferenceService.findInstitution(qualificationRequest.getInstitutionId()));
                qualification.setQualificationName(qualificationRequest.getQualificationName().trim());
                qualification.setCompletionYear(qualificationRequest.getCompletionYear());
                qualification.setPeriodStudied(qualificationRequest.getPeriodStudied());
                employee.getQualifications().add(qualification);
            }
        }
        if (request.getDependents() != null) {
            employee.getDependents().clear();
            for (EmployeeDependentRequest dependentRequest : request.getDependents()) {
                if (dependentRequest == null || !hasDependentContent(dependentRequest)) {
                    continue;
                }
                if (!StringUtils.hasText(dependentRequest.getFullName())) {
                    throw new IllegalArgumentException("Dependent name is required for each dependent");
                }
                EmployeeDependent dependent = new EmployeeDependent();
                dependent.setEmployee(employee);
                dependent.setFullName(dependentRequest.getFullName().trim());
                dependent.setRelationship(dependentRequest.getRelationship());
                dependent.setDateOfBirth(dependentRequest.getDateOfBirth());
                dependent.setNotes(dependentRequest.getNotes());
                employee.getDependents().add(dependent);
            }
        }
        if (request.getDisabilities() != null) {
            employee.getDisabilities().clear();
            for (EmployeeDisabilityRequest disabilityRequest : request.getDisabilities()) {
                if (disabilityRequest == null || !hasDisabilityContent(disabilityRequest)) {
                    continue;
                }
                if (!StringUtils.hasText(disabilityRequest.getDisabilityName())) {
                    throw new IllegalArgumentException("Disability name is required for each disability record");
                }
                EmployeeDisability disability = new EmployeeDisability();
                disability.setEmployee(employee);
                disability.setDisabilityName(disabilityRequest.getDisabilityName().trim());
                disability.setNotes(disabilityRequest.getNotes());
                employee.getDisabilities().add(disability);
            }
        }
        if (request.getRelatedContacts() != null) {
            employee.getRelatedContacts().clear();
            for (EmployeeRelatedContactRequest contactRequest : request.getRelatedContacts()) {
                if (contactRequest == null || !hasRelatedContactContent(contactRequest)) {
                    continue;
                }
                if (contactRequest.getContactType() == null) {
                    throw new IllegalArgumentException("Contact type is required for each related contact");
                }
                if (!StringUtils.hasText(contactRequest.getFullName())) {
                    throw new IllegalArgumentException("Contact name is required for each related contact");
                }
                EmployeeRelatedContact contact = new EmployeeRelatedContact();
                contact.setEmployee(employee);
                contact.setContactType(contactRequest.getContactType());
                contact.setFullName(contactRequest.getFullName().trim());
                contact.setRelationshipDescription(contactRequest.getRelationshipDescription());
                contact.setPhoneNumber(contactRequest.getPhoneNumber());
                contact.setEmailAddress(contactRequest.getEmailAddress());
                contact.setAddress(contactRequest.getAddress());
                employee.getRelatedContacts().add(contact);
            }
        }
    }

    private void validateManager(Employee employee, EmployeeRequest request) {
        if (employee.getId() != null
                && request.getManagerEmployeeId() != null
                && employee.getId().equals(request.getManagerEmployeeId())) {
            throw new OperationNotAllowedException("Employee cannot report to themselves");
        }
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

    private boolean hasQualificationContent(EmployeeQualificationRequest request) {
        return request.getEducationLevelId() != null
                || request.getInstitutionId() != null
                || StringUtils.hasText(request.getQualificationName())
                || request.getCompletionYear() != null
                || StringUtils.hasText(request.getPeriodStudied());
    }

    private boolean hasDependentContent(EmployeeDependentRequest request) {
        return StringUtils.hasText(request.getFullName())
                || StringUtils.hasText(request.getRelationship())
                || request.getDateOfBirth() != null
                || StringUtils.hasText(request.getNotes());
    }

    private boolean hasDisabilityContent(EmployeeDisabilityRequest request) {
        return StringUtils.hasText(request.getDisabilityName())
                || StringUtils.hasText(request.getNotes());
    }

    private boolean hasRelatedContactContent(EmployeeRelatedContactRequest request) {
        return request.getContactType() != null
                || StringUtils.hasText(request.getFullName())
                || StringUtils.hasText(request.getRelationshipDescription())
                || StringUtils.hasText(request.getPhoneNumber())
                || StringUtils.hasText(request.getEmailAddress())
                || StringUtils.hasText(request.getAddress());
    }

    private boolean matchesQuery(Employee employee, String normalized) {
        return contains(employee.getEmployeeNumber(), normalized)
                || contains(employee.getFirstName(), normalized)
                || contains(employee.getMiddleName(), normalized)
                || contains(employee.getLastName(), normalized)
                || contains(employee.getEmail(), normalized)
                || contains(employee.getNationalId(), normalized);
    }

    private boolean contains(String value, String normalized) {
        return value != null && value.toLowerCase(Locale.ENGLISH).contains(normalized);
    }
}
