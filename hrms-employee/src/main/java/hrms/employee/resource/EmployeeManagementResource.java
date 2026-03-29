package hrms.employee.resource;

import hrms.employee.dto.EmployeeCaseRequest;
import hrms.employee.dto.DisciplinaryRecordRequest;
import hrms.employee.dto.EmploymentConfirmationApprovalRequest;
import hrms.employee.dto.EmploymentConfirmationRequestInput;
import hrms.employee.dto.EmployeeStatusChangeRequest;
import hrms.employee.dto.OffboardingRecordRequest;
import hrms.employee.dto.OnboardingRecordRequest;
import hrms.employee.dto.RecruitmentRequestInput;
import hrms.employee.dto.ServiceLevelAgreementRequest;
import hrms.employee.dto.SuccessionPlanRequest;
import hrms.employee.dto.TrainingRecordRequest;
import hrms.employee.dto.WorkforceAnalyticsResponse;
import hrms.employee.entity.DisciplinaryRecord;
import hrms.employee.entity.EmploymentConfirmationRequest;
import hrms.employee.entity.EmployeeCase;
import hrms.employee.entity.EmployeeStatusChange;
import hrms.employee.entity.OffboardingRecord;
import hrms.employee.entity.OnboardingRecord;
import hrms.employee.entity.RecruitmentRequest;
import hrms.employee.entity.ServiceLevelAgreement;
import hrms.employee.entity.SuccessionPlan;
import hrms.employee.entity.TrainingRecord;
import hrms.employee.service.EmployeeOperationsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employee-management")
public class EmployeeManagementResource {

    private final EmployeeOperationsService employeeOperationsService;

    public EmployeeManagementResource(EmployeeOperationsService employeeOperationsService) {
        this.employeeOperationsService = employeeOperationsService;
    }

    @PostMapping("/status-changes")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeStatusChange createStatusChange(@Valid @RequestBody EmployeeStatusChangeRequest request) {
        return employeeOperationsService.recordStatusChange(request);
    }

    @PostMapping("/disciplinary-records")
    @ResponseStatus(HttpStatus.CREATED)
    public DisciplinaryRecord createDisciplinaryRecord(@Valid @RequestBody DisciplinaryRecordRequest request) {
        return employeeOperationsService.createDisciplinaryRecord(request);
    }

    @PostMapping("/employment-confirmations")
    @ResponseStatus(HttpStatus.CREATED)
    public EmploymentConfirmationRequest requestEmploymentConfirmation(@Valid @RequestBody EmploymentConfirmationRequestInput request) {
        return employeeOperationsService.requestEmploymentConfirmation(request);
    }

    @PostMapping("/employment-confirmations/sign")
    @ResponseStatus(HttpStatus.CREATED)
    public EmploymentConfirmationRequest signEmploymentConfirmation(@Valid @RequestBody EmploymentConfirmationApprovalRequest request) {
        return employeeOperationsService.signEmploymentConfirmation(request);
    }

    @GetMapping("/status-changes/{employeeId}")
    public List<EmployeeStatusChange> history(@PathVariable Long employeeId) {
        return employeeOperationsService.history(employeeId);
    }

    @GetMapping("/disciplinary-records/{employeeId}")
    public List<DisciplinaryRecord> disciplinaryRecords(@PathVariable Long employeeId) {
        return employeeOperationsService.disciplinaryRecords(employeeId);
    }

    @GetMapping("/employment-confirmations/{employeeId}")
    public List<EmploymentConfirmationRequest> employmentConfirmations(@PathVariable Long employeeId) {
        return employeeOperationsService.employmentConfirmations(employeeId);
    }

    @PostMapping("/onboarding")
    @ResponseStatus(HttpStatus.CREATED)
    public OnboardingRecord createOnboarding(@Valid @RequestBody OnboardingRecordRequest request) {
        return employeeOperationsService.createOnboarding(request);
    }

    @GetMapping("/onboarding/{employeeId}")
    public List<OnboardingRecord> onboarding(@PathVariable Long employeeId) {
        return employeeOperationsService.onboarding(employeeId);
    }

    @PostMapping("/training")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingRecord createTraining(@Valid @RequestBody TrainingRecordRequest request) {
        return employeeOperationsService.createTraining(request);
    }

    @GetMapping("/training/{employeeId}")
    public List<TrainingRecord> training(@PathVariable Long employeeId) {
        return employeeOperationsService.training(employeeId);
    }

    @PostMapping("/recruitment")
    @ResponseStatus(HttpStatus.CREATED)
    public RecruitmentRequest createRecruitmentRequest(@Valid @RequestBody RecruitmentRequestInput request) {
        return employeeOperationsService.createRecruitmentRequest(request);
    }

    @GetMapping("/recruitment")
    public List<RecruitmentRequest> recruitmentRequests() {
        return employeeOperationsService.recruitmentRequests();
    }

    @PostMapping("/cases")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeCase createCase(@Valid @RequestBody EmployeeCaseRequest request) {
        return employeeOperationsService.createCase(request);
    }

    @GetMapping("/cases/{employeeId}")
    public List<EmployeeCase> cases(@PathVariable Long employeeId) {
        return employeeOperationsService.cases(employeeId);
    }

    @PostMapping("/succession")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessionPlan createSuccessionPlan(@Valid @RequestBody SuccessionPlanRequest request) {
        return employeeOperationsService.createSuccessionPlan(request);
    }

    @GetMapping("/succession")
    public List<SuccessionPlan> successionPlans() {
        return employeeOperationsService.successionPlans();
    }

    @PostMapping("/offboarding")
    @ResponseStatus(HttpStatus.CREATED)
    public OffboardingRecord createOffboarding(@Valid @RequestBody OffboardingRecordRequest request) {
        return employeeOperationsService.createOffboarding(request);
    }

    @GetMapping("/offboarding/{employeeId}")
    public List<OffboardingRecord> offboarding(@PathVariable Long employeeId) {
        return employeeOperationsService.offboarding(employeeId);
    }

    @PostMapping("/service-level-agreements")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceLevelAgreement createAgreement(@Valid @RequestBody ServiceLevelAgreementRequest request) {
        return employeeOperationsService.createServiceLevelAgreement(request);
    }

    @GetMapping("/service-level-agreements")
    public List<ServiceLevelAgreement> agreements() {
        return employeeOperationsService.agreements();
    }

    @GetMapping("/analytics")
    public WorkforceAnalyticsResponse analytics() {
        return employeeOperationsService.analytics();
    }
}
