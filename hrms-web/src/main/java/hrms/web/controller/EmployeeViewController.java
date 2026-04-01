package hrms.web.controller;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.dto.EmployeeAddressRequest;
import hrms.employee.dto.EmployeeContractRequest;
import hrms.employee.dto.EmployeeQualificationRequest;
import hrms.employee.dto.EmployeeDependentRequest;
import hrms.employee.dto.EmployeeDisabilityRequest;
import hrms.employee.dto.EmployeeRelatedContactRequest;
import hrms.employee.dto.DepartmentRequest;
import hrms.employee.dto.EducationLevelRequest;
import hrms.employee.dto.EmploymentTypeRequest;
import hrms.employee.dto.JobTitleRequest;
import hrms.employee.dto.DisciplinaryRecordRequest;
import hrms.employee.dto.EmploymentConfirmationApprovalRequest;
import hrms.employee.dto.EmploymentConfirmationRequestInput;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeContract;
import hrms.employee.entity.EmploymentConfirmationRequest;
import hrms.employee.service.EmployeeReferenceService;
import hrms.employee.service.EmployeeOperationsService;
import hrms.employee.service.EmployeeService;
import hrms.web.constants.Pages;
import hrms.web.service.EmploymentConfirmationDocumentService;
import hrms.web.service.EmploymentConfirmationNotificationService;
import hrms.web.util.PortletUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private final EmployeeService employeeService;
    private final EmployeeReferenceService employeeReferenceService;
    private final EmployeeOperationsService employeeOperationsService;
    private final EmploymentConfirmationDocumentService employmentConfirmationDocumentService;
    private final EmploymentConfirmationNotificationService employmentConfirmationNotificationService;

    public EmployeeViewController(EmployeeService employeeService,
                                  EmployeeReferenceService employeeReferenceService,
                                  EmployeeOperationsService employeeOperationsService,
                                  EmploymentConfirmationDocumentService employmentConfirmationDocumentService,
                                  EmploymentConfirmationNotificationService employmentConfirmationNotificationService) {
        this.employeeService = employeeService;
        this.employeeReferenceService = employeeReferenceService;
        this.employeeOperationsService = employeeOperationsService;
        this.employmentConfirmationDocumentService = employmentConfirmationDocumentService;
        this.employmentConfirmationNotificationService = employmentConfirmationNotificationService;
    }

    @GetMapping
    public ModelAndView employees() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEES);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employees");
        modelAndView.addObject("employees", employeeService.findAll());
        return modelAndView;
    }

    @GetMapping("/add-employee")
    public ModelAndView addEmployee() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYEE);
        modelAndView.addObject("employeeRequest", new EmployeeRequest());
        modelAndView.addObject("employeeNumberPreview", "Auto-generated on save");
        modelAndView.addObject("showPreferredCurrency", false);
        populateForm(modelAndView, "Add Employee");
        return modelAndView;
    }

    @PostMapping("/save-employee")
    public ModelAndView saveEmployee(@Valid @ModelAttribute("employeeRequest") EmployeeRequest employeeRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYEE);
            modelAndView.addObject("employeeNumberPreview", "Auto-generated on save");
            modelAndView.addObject("showPreferredCurrency", false);
            populateForm(modelAndView, "Add Employee");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        Employee employee;
        try {
            employee = employeeService.create(employeeRequest);
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYEE);
            modelAndView.addObject("employeeNumberPreview", "Auto-generated on save");
            modelAndView.addObject("showPreferredCurrency", false);
            modelAndView.addObject("errorMsgs", Collections.singletonList(exception.getMessage()));
            populateForm(modelAndView, "Add Employee");
            return modelAndView;
        }
        return redirectWithInfo("/employees/view-employee/" + employee.getId(), "Employee record saved successfully.");
    }

    @GetMapping("/view-employee/{id}")
    public ModelAndView viewEmployee(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEE);
        Employee employee = employeeService.findById(id);
        List<Employee> employees = employeeService.findAll();
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employee");
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("manager", findManager(employee, employees));
        modelAndView.addObject("directReports", findDirectReports(employee, employees));
        modelAndView.addObject("coworkers", findCoworkers(employee, employees));
        modelAndView.addObject("activeContract", findActiveContract(employee));
        return modelAndView;
    }

    @GetMapping("/edit-employee/{id}")
    public ModelAndView editEmployee(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName(employee.getFirstName());
        request.setMiddleName(employee.getMiddleName());
        request.setLastName(employee.getLastName());
        request.setEmail(employee.getEmail());
        request.setPhoneNumber(employee.getPhoneNumber());
        request.setNationalId(employee.getNationalId());
        request.setJobTitleId(employee.getJobTitle() == null ? null : employee.getJobTitle().getId());
        request.setDepartmentId(employee.getDepartment() == null ? null : employee.getDepartment().getId());
        request.setEmploymentTypeId(employee.getEmploymentType() == null ? null : employee.getEmploymentType().getId());
        request.setHireDate(employee.getHireDate());
        request.setTerminationDate(employee.getTerminationDate());
        request.setManagerEmployeeId(employee.getManagerEmployeeId());
        request.setPreferredCurrency(employee.getPreferredCurrency());
        request.setEmploymentHistory(employee.getEmploymentHistory());
        request.setBenefitsSummary(employee.getBenefitsSummary());
        request.setPerformanceSummary(employee.getPerformanceSummary());
        request.setStatus(employee.getStatus());

        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYEE);
        modelAndView.addObject("employeeId", id);
        modelAndView.addObject("employeeNumberPreview", employee.getEmployeeNumber());
        modelAndView.addObject("employeeRequest", request);
        modelAndView.addObject("showPreferredCurrency", true);
        populateForm(modelAndView, "Edit Employee");
        return modelAndView;
    }

    @PostMapping("/update-employee/{id}")
    public ModelAndView updateEmployee(@PathVariable Long id,
                                       @Valid @ModelAttribute("employeeRequest") EmployeeRequest employeeRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYEE);
            modelAndView.addObject("employeeId", id);
            modelAndView.addObject("employeeNumberPreview", employeeService.findById(id).getEmployeeNumber());
            modelAndView.addObject("showPreferredCurrency", true);
            populateForm(modelAndView, "Edit Employee");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        try {
            employeeService.update(id, employeeRequest);
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYEE);
            modelAndView.addObject("employeeId", id);
            modelAndView.addObject("employeeNumberPreview", employeeService.findById(id).getEmployeeNumber());
            modelAndView.addObject("showPreferredCurrency", true);
            modelAndView.addObject("errorMsgs", Collections.singletonList(exception.getMessage()));
            populateForm(modelAndView, "Edit Employee");
            return modelAndView;
        }
        return redirectWithInfo("/employees/view-employee/" + id, "Employee record updated successfully.");
    }

    @GetMapping("/departments")
    public ModelAndView departments() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_DEPARTMENTS);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Departments");
        modelAndView.addObject("departments", employeeReferenceService.departments());
        return modelAndView;
    }

    @GetMapping("/departments/add")
    public ModelAndView addDepartment() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_DEPARTMENT);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Add Department");
        modelAndView.addObject("departmentRequest", new DepartmentRequest());
        return modelAndView;
    }

    @PostMapping("/departments/save")
    public ModelAndView saveDepartment(@Valid @ModelAttribute DepartmentRequest departmentRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_DEPARTMENT);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Add Department");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.createDepartment(departmentRequest);
        return redirectWithInfo("/employees/departments", "Department saved successfully.");
    }

    @GetMapping("/departments/{departmentId}/edit")
    public ModelAndView editDepartment(@PathVariable Long departmentId) {
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_DEPARTMENT);
        DepartmentRequest request = new DepartmentRequest();
        request.setName(employeeReferenceService.findDepartment(departmentId).getName());
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Edit Department");
        modelAndView.addObject("departmentId", departmentId);
        modelAndView.addObject("departmentRequest", request);
        return modelAndView;
    }

    @PostMapping("/departments/{departmentId}/update")
    public ModelAndView updateDepartment(@PathVariable Long departmentId,
                                         @Valid @ModelAttribute DepartmentRequest departmentRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_DEPARTMENT);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Edit Department");
            modelAndView.addObject("departmentId", departmentId);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.updateDepartment(departmentId, departmentRequest);
        return redirectWithInfo("/employees/departments", "Department updated successfully.");
    }

    @GetMapping("/employment-types")
    public ModelAndView employmentTypes() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYMENT_TYPES);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employment Types");
        modelAndView.addObject("employmentTypes", employeeReferenceService.employmentTypes());
        return modelAndView;
    }

    @GetMapping("/education-levels")
    public ModelAndView educationLevels() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EDUCATION_LEVELS);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Education Levels");
        modelAndView.addObject("educationLevels", employeeReferenceService.educationLevels());
        return modelAndView;
    }

    @GetMapping("/education-levels/add")
    public ModelAndView addEducationLevel() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_EDUCATION_LEVEL);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Add Education Level");
        modelAndView.addObject("educationLevelRequest", new EducationLevelRequest());
        return modelAndView;
    }

    @PostMapping("/education-levels/save")
    public ModelAndView saveEducationLevel(@Valid @ModelAttribute EducationLevelRequest educationLevelRequest,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_EDUCATION_LEVEL);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Add Education Level");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.createEducationLevel(educationLevelRequest);
        return redirectWithInfo("/employees/education-levels", "Education level saved successfully.");
    }

    @GetMapping("/education-levels/{educationLevelId}/edit")
    public ModelAndView editEducationLevel(@PathVariable Long educationLevelId) {
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EDUCATION_LEVEL);
        EducationLevelRequest request = new EducationLevelRequest();
        request.setName(employeeReferenceService.findEducationLevel(educationLevelId).getName());
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Edit Education Level");
        modelAndView.addObject("educationLevelId", educationLevelId);
        modelAndView.addObject("educationLevelRequest", request);
        return modelAndView;
    }

    @PostMapping("/education-levels/{educationLevelId}/update")
    public ModelAndView updateEducationLevel(@PathVariable Long educationLevelId,
                                             @Valid @ModelAttribute EducationLevelRequest educationLevelRequest,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EDUCATION_LEVEL);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Edit Education Level");
            modelAndView.addObject("educationLevelId", educationLevelId);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.updateEducationLevel(educationLevelId, educationLevelRequest);
        return redirectWithInfo("/employees/education-levels", "Education level updated successfully.");
    }

    @GetMapping("/employment-types/add")
    public ModelAndView addEmploymentType() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYMENT_TYPE);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Add Employment Type");
        modelAndView.addObject("employmentTypeRequest", new EmploymentTypeRequest());
        return modelAndView;
    }

    @PostMapping("/employment-types/save")
    public ModelAndView saveEmploymentType(@Valid @ModelAttribute EmploymentTypeRequest employmentTypeRequest,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYMENT_TYPE);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Add Employment Type");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.createEmploymentType(employmentTypeRequest);
        return redirectWithInfo("/employees/employment-types", "Employment type saved successfully.");
    }

    @GetMapping("/employment-types/{employmentTypeId}/edit")
    public ModelAndView editEmploymentType(@PathVariable Long employmentTypeId) {
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYMENT_TYPE);
        EmploymentTypeRequest request = new EmploymentTypeRequest();
        request.setName(employeeReferenceService.findEmploymentType(employmentTypeId).getName());
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Edit Employment Type");
        modelAndView.addObject("employmentTypeId", employmentTypeId);
        modelAndView.addObject("employmentTypeRequest", request);
        return modelAndView;
    }

    @PostMapping("/employment-types/{employmentTypeId}/update")
    public ModelAndView updateEmploymentType(@PathVariable Long employmentTypeId,
                                             @Valid @ModelAttribute EmploymentTypeRequest employmentTypeRequest,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYMENT_TYPE);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Edit Employment Type");
            modelAndView.addObject("employmentTypeId", employmentTypeId);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.updateEmploymentType(employmentTypeId, employmentTypeRequest);
        return redirectWithInfo("/employees/employment-types", "Employment type updated successfully.");
    }

    @GetMapping("/job-titles")
    public ModelAndView jobTitles() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_JOB_TITLES);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Job Titles");
        modelAndView.addObject("jobTitles", employeeReferenceService.jobTitles());
        modelAndView.addObject("grades", employeeReferenceService.grades());
        return modelAndView;
    }

    @GetMapping("/job-titles/add")
    public ModelAndView addJobTitle() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_JOB_TITLE);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Add Job Title");
        modelAndView.addObject("jobTitleRequest", new JobTitleRequest());
        modelAndView.addObject("grades", employeeReferenceService.grades());
        return modelAndView;
    }

    @PostMapping("/job-titles/save")
    public ModelAndView saveJobTitle(@Valid @ModelAttribute JobTitleRequest jobTitleRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_JOB_TITLE);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Add Job Title");
            modelAndView.addObject("grades", employeeReferenceService.grades());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.createJobTitle(jobTitleRequest);
        return redirectWithInfo("/employees/job-titles", "Job title saved successfully.");
    }

    @GetMapping("/job-titles/{jobTitleId}/edit")
    public ModelAndView editJobTitle(@PathVariable Long jobTitleId) {
        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_JOB_TITLE);
        JobTitleRequest request = new JobTitleRequest();
        request.setName(employeeReferenceService.findJobTitle(jobTitleId).getName());
        request.setGradeId(employeeReferenceService.findJobTitle(jobTitleId).getGrade().getId());
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "Edit Job Title");
        modelAndView.addObject("jobTitleId", jobTitleId);
        modelAndView.addObject("jobTitleRequest", request);
        modelAndView.addObject("grades", employeeReferenceService.grades());
        return modelAndView;
    }

    @PostMapping("/job-titles/{jobTitleId}/update")
    public ModelAndView updateJobTitle(@PathVariable Long jobTitleId,
                                       @Valid @ModelAttribute JobTitleRequest jobTitleRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_JOB_TITLE);
            modelAndView.addObject("pageDomain", "Employee Management");
            modelAndView.addObject("pageName", "Employees");
            modelAndView.addObject("pageTitle", "Edit Job Title");
            modelAndView.addObject("jobTitleId", jobTitleId);
            modelAndView.addObject("grades", employeeReferenceService.grades());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeReferenceService.updateJobTitle(jobTitleId, jobTitleRequest);
        return redirectWithInfo("/employees/job-titles", "Job title updated successfully.");
    }

    @GetMapping("/{id}/qualifications")
    public ModelAndView manageQualifications(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_QUALIFICATIONS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Qualifications");
        modelAndView.addObject("qualificationRequest", new EmployeeQualificationRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/qualifications")
    public ModelAndView saveQualification(@PathVariable Long id,
                                          @Valid @ModelAttribute("qualificationRequest") EmployeeQualificationRequest qualificationRequest,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_QUALIFICATIONS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Qualifications");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeService.addQualification(id, qualificationRequest);
        return redirectWithInfo("/employees/" + id + "/qualifications", "Qualification saved successfully.");
    }

    @GetMapping("/contracts/{contractId}/download")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long contractId) {
        EmployeeContract contract = findContract(contractId);
        if (contract.getDocumentPath() == null) {
            throw new IllegalArgumentException("No contract attached for contract " + contractId);
        }
        Resource resource = new FileSystemResource(contract.getDocumentPath());
        if (!resource.exists()) {
            throw new IllegalArgumentException("Contract file not found for contract " + contractId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + contract.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/addresses")
    public ModelAndView manageAddresses(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_ADDRESSES);
        populateEmployeeRecordPage(modelAndView, id, "Manage Addresses");
        modelAndView.addObject("addressRequest", new EmployeeAddressRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/addresses")
    public ModelAndView saveAddress(@PathVariable Long id,
                                    @Valid @ModelAttribute("addressRequest") EmployeeAddressRequest addressRequest,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_ADDRESSES);
            populateEmployeeRecordPage(modelAndView, id, "Manage Addresses");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        try {
            employeeService.addAddress(id, addressRequest);
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_ADDRESSES);
            populateEmployeeRecordPage(modelAndView, id, "Manage Addresses");
            modelAndView.addObject("addressRequest", addressRequest);
            modelAndView.addObject("errorMsgs", Collections.singletonList(exception.getMessage()));
            return modelAndView;
        }
        return redirectWithInfo("/employees/" + id + "/addresses", "Address saved successfully.");
    }

    @GetMapping("/{id}/dependents")
    public ModelAndView manageDependents(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DEPENDENTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Dependants");
        modelAndView.addObject("dependentRequest", new EmployeeDependentRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/dependents")
    public ModelAndView saveDependent(@PathVariable Long id,
                                      @Valid @ModelAttribute("dependentRequest") EmployeeDependentRequest dependentRequest,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DEPENDENTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Dependants");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeService.addDependent(id, dependentRequest);
        return redirectWithInfo("/employees/" + id + "/dependents", "Dependant saved successfully.");
    }

    @GetMapping("/{id}/disabilities")
    public ModelAndView manageDisabilities(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DISABILITIES);
        populateEmployeeRecordPage(modelAndView, id, "Manage Disabilities");
        modelAndView.addObject("disabilityRequest", new EmployeeDisabilityRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/disabilities")
    public ModelAndView saveDisability(@PathVariable Long id,
                                       @Valid @ModelAttribute("disabilityRequest") EmployeeDisabilityRequest disabilityRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DISABILITIES);
            populateEmployeeRecordPage(modelAndView, id, "Manage Disabilities");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeService.addDisability(id, disabilityRequest);
        return redirectWithInfo("/employees/" + id + "/disabilities", "Disability record saved successfully.");
    }

    @GetMapping("/{id}/contacts")
    public ModelAndView manageContacts(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTACTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Related Contacts");
        modelAndView.addObject("contactRequest", new EmployeeRelatedContactRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/contacts")
    public ModelAndView saveContact(@PathVariable Long id,
                                    @Valid @ModelAttribute("contactRequest") EmployeeRelatedContactRequest contactRequest,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTACTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Related Contacts");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeService.addRelatedContact(id, contactRequest);
        return redirectWithInfo("/employees/" + id + "/contacts", "Related contact saved successfully.");
    }

    @GetMapping("/{id}/contracts")
    public ModelAndView manageContracts(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTRACTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Contracts");
        modelAndView.addObject("contractRequest", new EmployeeContractRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/contracts")
    public ModelAndView saveContract(@PathVariable Long id,
                                     @Valid @ModelAttribute("contractRequest") EmployeeContractRequest contractRequest,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "contractFile", required = false) MultipartFile contractFile) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTRACTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Contracts");
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        storeContractFile(contractRequest, contractFile);
        employeeService.addContract(id, contractRequest);
        return redirectWithInfo("/employees/" + id + "/contracts", "Contract saved successfully.");
    }

    @GetMapping("/{id}/disciplinary-records")
    public ModelAndView manageDisciplinaryRecords(@PathVariable Long id) {
        return buildDisciplinaryRecordsPage(id, new DisciplinaryRecordRequest());
    }

    @PostMapping("/{id}/disciplinary-records")
    public ModelAndView saveDisciplinaryRecord(@PathVariable Long id,
                                               @Valid @ModelAttribute("disciplinaryRecordRequest") DisciplinaryRecordRequest disciplinaryRecordRequest,
                                               BindingResult bindingResult) {
        disciplinaryRecordRequest.setEmployeeId(id);
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = buildDisciplinaryRecordsPage(id, disciplinaryRecordRequest);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        employeeOperationsService.createDisciplinaryRecord(disciplinaryRecordRequest);
        return redirectWithInfo("/employees/" + id + "/disciplinary-records",
                "Disciplinary record saved successfully.");
    }

    @GetMapping("/{id}/employment-confirmations")
    public ModelAndView manageEmploymentConfirmations(@PathVariable Long id) {
        EmploymentConfirmationRequestInput confirmationRequestInput = new EmploymentConfirmationRequestInput();
        return buildEmploymentConfirmationsPage(id, confirmationRequestInput, new EmploymentConfirmationApprovalRequest());
    }

    @GetMapping("/employment-confirmations/{requestId}/download")
    public ResponseEntity<Resource> downloadEmploymentConfirmation(@PathVariable Long requestId) {
        EmploymentConfirmationRequest request = findEmploymentConfirmationRequest(requestId);
        if (request.getSignedDocumentPath() == null) {
            throw new IllegalArgumentException("No signed confirmation document found for request " + requestId);
        }
        Resource resource = new FileSystemResource(request.getSignedDocumentPath());
        if (!resource.exists()) {
            throw new IllegalArgumentException("Confirmation document file not found for request " + requestId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + request.getSignedDocumentFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/delete-employee/{id}")
    public ModelAndView deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return redirectWithInfo("/employees", "Employee record deleted successfully.");
    }

    @PostMapping("/{id}/employment-confirmations")
    public ModelAndView requestEmploymentConfirmation(@PathVariable Long id,
                                                      @Valid @ModelAttribute EmploymentConfirmationRequestInput employmentConfirmationRequestInput,
                                                      BindingResult bindingResult) {
        employmentConfirmationRequestInput.setEmployeeId(id);
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = buildEmploymentConfirmationsPage(id,
                    employmentConfirmationRequestInput,
                    new EmploymentConfirmationApprovalRequest());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        EmploymentConfirmationRequest saved = employeeOperationsService.requestEmploymentConfirmation(employmentConfirmationRequestInput);
        Employee employee = employeeService.findById(id);
        employmentConfirmationNotificationService.notifyHrManagersOfRequest(employee, saved, findHrManagers(employeeService.findAll()));
        return redirectWithInfo("/employees/" + id + "/employment-confirmations",
                "Employment confirmation request submitted successfully.");
    }

    @PostMapping("/{id}/employment-confirmations/sign")
    public ModelAndView signEmploymentConfirmation(@PathVariable Long id,
                                                   @Valid @ModelAttribute EmploymentConfirmationApprovalRequest employmentConfirmationApprovalRequest,
                                                   BindingResult bindingResult) {
        EmploymentConfirmationRequest existingRequest = findEmploymentConfirmationRequest(employmentConfirmationApprovalRequest.getRequestId());
        Long employeeId = existingRequest.getEmployee().getId();
        if (bindingResult.hasErrors()) {
            EmploymentConfirmationRequestInput confirmationRequestInput = new EmploymentConfirmationRequestInput();
            confirmationRequestInput.setEmployeeId(employeeId);
            confirmationRequestInput.setDeliveryEmail(existingRequest.getDeliveryEmail());
            confirmationRequestInput.setPurpose(existingRequest.getPurpose());
            ModelAndView modelAndView = buildEmploymentConfirmationsPage(id,
                    confirmationRequestInput,
                    employmentConfirmationApprovalRequest);
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        Employee hrManager = employeeService.findById(employmentConfirmationApprovalRequest.getSignerEmployeeId());
        EmploymentConfirmationDocumentService.GeneratedDocument generatedDocument =
                employmentConfirmationDocumentService.generate(existingRequest.getEmployee(), existingRequest, hrManager);
        employmentConfirmationApprovalRequest.setSignedDocumentPath(generatedDocument.getPath());
        employmentConfirmationApprovalRequest.setSignedDocumentFileName(generatedDocument.getFileName());
        EmploymentConfirmationRequest saved = employeeOperationsService.signEmploymentConfirmation(employmentConfirmationApprovalRequest);
        employmentConfirmationNotificationService.notifyEmployeeOfSignedLetter(saved.getEmployee(), saved);
        return redirectWithInfo("/employees/" + id + "/employment-confirmations",
                "Employment confirmation signed successfully.");
    }

    private void populateForm(ModelAndView modelAndView, String pageTitle) {
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("departments", employeeReferenceService.departments());
        modelAndView.addObject("employmentTypes", employeeReferenceService.employmentTypes());
        modelAndView.addObject("jobTitles", employeeReferenceService.jobTitles());
        modelAndView.addObject("educationLevels", employeeReferenceService.educationLevels());
        modelAndView.addObject("institutions", employeeReferenceService.institutions());
        modelAndView.addObject("relatedContactTypes", hrms.employee.model.RelatedContactType.values());
        modelAndView.addObject("employees", employeeService.findAll());
    }

    private ModelAndView redirectWithInfo(String path, String message) {
        PortletUtils.addInfoMsg(message);
        return new ModelAndView("redirect:" + path);
    }

    private ModelAndView buildEmployeeView(Long employeeId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEE);
        Employee employee = employeeService.findById(employeeId);
        List<Employee> employees = employeeService.findAll();
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employee");
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("manager", findManager(employee, employees));
        modelAndView.addObject("directReports", findDirectReports(employee, employees));
        modelAndView.addObject("coworkers", findCoworkers(employee, employees));
        modelAndView.addObject("activeContract", findActiveContract(employee));
        return modelAndView;
    }

    private ModelAndView buildDisciplinaryRecordsPage(Long employeeId,
                                                      DisciplinaryRecordRequest disciplinaryRecordRequest) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DISCIPLINARY_RECORDS);
        populateEmployeeRecordPage(modelAndView, employeeId, "Manage Disciplinary Records");
        disciplinaryRecordRequest.setEmployeeId(employeeId);
        modelAndView.addObject("disciplinaryRecordRequest", disciplinaryRecordRequest);
        modelAndView.addObject("disciplinaryRecords", employeeOperationsService.disciplinaryRecords(employeeId));
        modelAndView.addObject("disciplinaryTypes", hrms.employee.model.DisciplinaryRecordType.values());
        return modelAndView;
    }

    private ModelAndView buildEmploymentConfirmationsPage(Long employeeId,
                                                          EmploymentConfirmationRequestInput confirmationRequestInput,
                                                          EmploymentConfirmationApprovalRequest confirmationApprovalRequest) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_EMPLOYMENT_CONFIRMATIONS);
        Employee employee = employeeService.findById(employeeId);
        List<Employee> employees = employeeService.findAll();
        populateEmployeeRecordPage(modelAndView, employeeId, "Manage Confirmation Of Employment");
        if (confirmationRequestInput.getEmployeeId() == null) {
            confirmationRequestInput.setEmployeeId(employeeId);
        }
        if (confirmationRequestInput.getDeliveryEmail() == null) {
            confirmationRequestInput.setDeliveryEmail(employee.getEmail());
        }
        modelAndView.addObject("employmentConfirmationRequestInput", confirmationRequestInput);
        modelAndView.addObject("employmentConfirmationApprovalRequest", confirmationApprovalRequest);
        modelAndView.addObject("hrManagers", findHrManagers(employees));
        modelAndView.addObject("confirmationRequests", employeeOperationsService.employmentConfirmations(employeeId));
        modelAndView.addObject("confirmationSignerNames", buildEmployeeNameMap(employees));
        return modelAndView;
    }

    private void populateEmployeeRecordPage(ModelAndView modelAndView, Long employeeId, String pageTitle) {
        Employee employee = employeeService.findById(employeeId);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("addressRequest", new EmployeeAddressRequest());
        modelAndView.addObject("educationLevels", employeeReferenceService.educationLevels());
        modelAndView.addObject("institutions", employeeReferenceService.institutions());
        modelAndView.addObject("relatedContactTypes", hrms.employee.model.RelatedContactType.values());
    }

    private void storeContractFile(EmployeeContractRequest contractRequest, MultipartFile contractFile) {
        if (contractFile == null || contractFile.isEmpty()) {
            return;
        }
        try {
            Path directory = Paths.get("uploads", "employee-contracts");
            Files.createDirectories(directory);
            String storedFileName = UUID.randomUUID().toString() + "-" + contractFile.getOriginalFilename();
            Path target = directory.resolve(storedFileName);
            Files.copy(contractFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            contractRequest.setDocumentPath(target.toAbsolutePath().toString());
            contractRequest.setFileName(contractFile.getOriginalFilename());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store contract file", exception);
        }
    }

    private Employee findManager(Employee employee, List<Employee> employees) {
        if (employee.getManagerEmployeeId() == null) {
            return null;
        }
        for (Employee candidate : employees) {
            if (employee.getManagerEmployeeId().equals(candidate.getId())) {
                return candidate;
            }
        }
        return null;
    }

    private List<Employee> findDirectReports(Employee employee, List<Employee> employees) {
        List<Employee> directReports = new ArrayList<Employee>();
        for (Employee candidate : employees) {
            if (employee.getId().equals(candidate.getManagerEmployeeId())) {
                directReports.add(candidate);
            }
        }
        return directReports;
    }

    private List<Employee> findCoworkers(Employee employee, List<Employee> employees) {
        List<Employee> coworkers = new ArrayList<Employee>();
        if (employee.getDepartment() == null) {
            return coworkers;
        }
        for (Employee candidate : employees) {
            if (candidate.getId().equals(employee.getId())) {
                continue;
            }
            if (candidate.getDepartment() != null
                    && employee.getDepartment().getId().equals(candidate.getDepartment().getId())) {
                coworkers.add(candidate);
            }
        }
        return coworkers;
    }

    private List<Employee> findHrManagers(List<Employee> employees) {
        List<Employee> hrManagers = new ArrayList<Employee>();
        for (Employee employee : employees) {
            if (employee.getDepartment() != null
                    && employee.getDepartment().getName() != null
                    && "human resources".equalsIgnoreCase(employee.getDepartment().getName())
                    && employee.getJobTitle() != null
                    && employee.getJobTitle().getName() != null
                    && employee.getJobTitle().getName().toLowerCase().contains("manager")) {
                hrManagers.add(employee);
            }
        }
        return hrManagers;
    }

    private EmployeeContract findActiveContract(Employee employee) {
        for (EmployeeContract contract : employee.getContracts()) {
            if (contract.isActive()) {
                return contract;
            }
        }
        return employee.getContracts().isEmpty() ? null : employee.getContracts().get(0);
    }

    private EmployeeContract findContract(Long contractId) {
        for (Employee employee : employeeService.findAll()) {
            for (EmployeeContract contract : employee.getContracts()) {
                if (contract.getId().equals(contractId)) {
                    return contract;
                }
            }
        }
        throw new IllegalArgumentException("Employee contract not found: " + contractId);
    }

    private Map<Long, String> buildEmployeeNameMap(List<Employee> employees) {
        Map<Long, String> employeeNameMap = new LinkedHashMap<Long, String>();
        for (Employee employee : employees) {
            employeeNameMap.put(employee.getId(),
                    employee.getFirstName() + " " + employee.getLastName() + " (" + employee.getEmployeeNumber() + ")");
        }
        return employeeNameMap;
    }

    private EmploymentConfirmationRequest findEmploymentConfirmationRequest(Long requestId) {
        for (Employee employee : employeeService.findAll()) {
            for (EmploymentConfirmationRequest request : employeeOperationsService.employmentConfirmations(employee.getId())) {
                if (request.getId().equals(requestId)) {
                    return request;
                }
            }
        }
        throw new IllegalArgumentException("Employment confirmation request not found: " + requestId);
    }
}
