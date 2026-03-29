package hrms.employee.service;

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

import java.util.List;

public interface EmployeeOperationsService {

    EmployeeStatusChange recordStatusChange(EmployeeStatusChangeRequest request);

    DisciplinaryRecord createDisciplinaryRecord(DisciplinaryRecordRequest request);

    EmploymentConfirmationRequest requestEmploymentConfirmation(EmploymentConfirmationRequestInput request);

    EmploymentConfirmationRequest signEmploymentConfirmation(EmploymentConfirmationApprovalRequest request);

    List<EmployeeStatusChange> history(Long employeeId);

    List<DisciplinaryRecord> disciplinaryRecords(Long employeeId);

    List<EmploymentConfirmationRequest> employmentConfirmations(Long employeeId);

    OnboardingRecord createOnboarding(OnboardingRecordRequest request);

    TrainingRecord createTraining(TrainingRecordRequest request);

    RecruitmentRequest createRecruitmentRequest(RecruitmentRequestInput request);

    EmployeeCase createCase(EmployeeCaseRequest request);

    SuccessionPlan createSuccessionPlan(SuccessionPlanRequest request);

    OffboardingRecord createOffboarding(OffboardingRecordRequest request);

    ServiceLevelAgreement createServiceLevelAgreement(ServiceLevelAgreementRequest request);

    List<OnboardingRecord> onboarding(Long employeeId);

    List<TrainingRecord> training(Long employeeId);

    List<EmployeeCase> cases(Long employeeId);

    List<OffboardingRecord> offboarding(Long employeeId);

    List<RecruitmentRequest> recruitmentRequests();

    List<SuccessionPlan> successionPlans();

    List<ServiceLevelAgreement> agreements();

    WorkforceAnalyticsResponse analytics();
}
