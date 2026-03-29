package hrms.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HrmsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesModuleCatalog() throws Exception {
        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].module").value("Employee Data Management"));
    }

    @Test
    void exposesDashboardAndAuditTrailMetrics() throws Exception {
        String employeePayload = "{\n" +
                "  \"employeeNumber\": \"EMP-900\",\n" +
                "  \"firstName\": \"Tafadzwa\",\n" +
                "  \"lastName\": \"Maphosa\",\n" +
                "  \"email\": \"tafadzwa.maphosa@example.com\",\n" +
                "  \"jobTitle\": \"Accountant\",\n" +
                "  \"department\": \"Finance\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2025-06-01\",\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 2100.00,\n" +
                "  \"hourlyRate\": 12.00,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeePayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/modules/dashboards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dashboards[0].audience").value("HR"))
                .andExpect(jsonPath("$.dashboards[0].metrics[0].metric").value("Headcount"));

        mockMvc.perform(get("/api/audit-trails")
                        .param("moduleName", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].moduleName").value("EMPLOYEE"))
                .andExpect(jsonPath("$[0].action").value("CREATE"));
    }

    @Test
    void supportsEmployeeSearchLifecycleAndAnalytics() throws Exception {
        String employeePayload = "{\n" +
                "  \"employeeNumber\": \"EMP-100\",\n" +
                "  \"firstName\": \"Rumbidzai\",\n" +
                "  \"middleName\": \"Faith\",\n" +
                "  \"lastName\": \"Chari\",\n" +
                "  \"email\": \"rumbidzai.chari@example.com\",\n" +
                "  \"phoneNumber\": \"+263771000100\",\n" +
                "  \"address\": \"12 Samora Machel Ave, Harare\",\n" +
                "  \"nationalId\": \"63-123456-A-10\",\n" +
                "  \"emergencyContactName\": \"Tendai Chari\",\n" +
                "  \"emergencyContactPhone\": \"+263771000200\",\n" +
                "  \"jobTitle\": \"HR Business Partner\",\n" +
                "  \"department\": \"Human Resources\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2025-01-06\",\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 2400.00,\n" +
                "  \"hourlyRate\": 13.50,\n" +
                "  \"employmentHistory\": \"Promoted from HR Officer\",\n" +
                "  \"benefitsSummary\": \"Medical aid, transport allowance\",\n" +
                "  \"performanceSummary\": \"Consistently above expectations\",\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nationalId").value("63-123456-A-10"));

        mockMvc.perform(get("/api/employees")
                        .param("department", "Human Resources")
                        .param("jobTitle", "Business")
                        .param("query", "Rumbidzai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeNumber").value("EMP-100"));

        String statusPayload = "{\n" +
                "  \"employeeId\": 1,\n" +
                "  \"newStatus\": \"ON_LEAVE\",\n" +
                "  \"eventType\": \"LEAVE_OF_ABSENCE\",\n" +
                "  \"effectiveDate\": \"2026-03-01\",\n" +
                "  \"notes\": \"Approved study leave\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/status-changes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.newStatus").value("ON_LEAVE"));

        String onboardingPayload = "{\n" +
                "  \"employeeId\": 1,\n" +
                "  \"stage\": \"TRAINING\",\n" +
                "  \"dueDate\": \"2026-03-15\",\n" +
                "  \"completed\": false,\n" +
                "  \"notes\": \"Pending policy induction\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(onboardingPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stage").value("TRAINING"));

        String trainingPayload = "{\n" +
                "  \"employeeId\": 1,\n" +
                "  \"courseName\": \"Labour Act Compliance\",\n" +
                "  \"provider\": \"IPMZ\",\n" +
                "  \"completionDate\": \"2026-03-20\",\n" +
                "  \"certified\": true,\n" +
                "  \"developmentPlan\": \"Advanced employee relations certification\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("IPMZ"));

        String recruitmentPayload = "{\n" +
                "  \"positionTitle\": \"Training Officer\",\n" +
                "  \"department\": \"Human Resources\",\n" +
                "  \"requestedHeadcount\": 2,\n" +
                "  \"requestDate\": \"2026-03-10\",\n" +
                "  \"status\": \"OPEN\",\n" +
                "  \"justification\": \"Expand L&D function\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recruitmentPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"));

        String casePayload = "{\n" +
                "  \"employeeId\": 1,\n" +
                "  \"caseType\": \"GRIEVANCE\",\n" +
                "  \"status\": \"OPEN\",\n" +
                "  \"openedDate\": \"2026-03-12\",\n" +
                "  \"description\": \"Workstation allocation grievance\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(casePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseType").value("GRIEVANCE"));

        String slaPayload = "{\n" +
                "  \"agreementName\": \"Payroll Processing SLA\",\n" +
                "  \"counterparty\": \"ABC Outsourcing\",\n" +
                "  \"signedDate\": \"2026-01-05\",\n" +
                "  \"signed\": true,\n" +
                "  \"documentPath\": \"/contracts/payroll-sla-2026.pdf\",\n" +
                "  \"notes\": \"Signed service levels stored in repository\"\n" +
                "}";

        mockMvc.perform(post("/api/employee-management/service-level-agreements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(slaPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.signed").value(true));

        mockMvc.perform(get("/api/employee-management/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHeadcount").value(1))
                .andExpect(jsonPath("$.openRecruitmentRequests").value(1))
                .andExpect(jsonPath("$.departmentHeadcount['Human Resources']").value(1));
    }

    @Test
    void mirrorsScorecardDrivenPerformanceWorkflow() throws Exception {
        String managerPayload = "{\n" +
                "  \"employeeNumber\": \"EMP-PM-001\",\n" +
                "  \"firstName\": \"Melissa\",\n" +
                "  \"lastName\": \"Mataruse\",\n" +
                "  \"email\": \"melissa.mataruse@example.com\",\n" +
                "  \"jobTitle\": \"Operations Director\",\n" +
                "  \"department\": \"Operations\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2024-01-10\",\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 4500.00,\n" +
                "  \"hourlyRate\": 25.00,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        String employeePayload = "{\n" +
                "  \"employeeNumber\": \"EMP-PM-002\",\n" +
                "  \"firstName\": \"Tatenda\",\n" +
                "  \"lastName\": \"Moyo\",\n" +
                "  \"email\": \"tatenda.moyo@example.com\",\n" +
                "  \"jobTitle\": \"Operations Analyst\",\n" +
                "  \"department\": \"Operations\",\n" +
                "  \"employmentTypeId\": 2,\n" +
                "  \"hireDate\": \"2025-01-10\",\n" +
                "  \"managerEmployeeId\": 1,\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 1900.00,\n" +
                "  \"hourlyRate\": 11.00,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(managerPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeePayload))
                .andExpect(status().isCreated());

        String periodPayload = "{\n" +
                "  \"name\": \"2026 Mid Year\",\n" +
                "  \"startDate\": \"2026-01-01\",\n" +
                "  \"endDate\": \"2026-06-30\",\n" +
                "  \"active\": true\n" +
                "}";

        mockMvc.perform(post("/api/performance/reporting-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(periodPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("2026 Mid Year"));

        String perspectivePayload = "{\n" +
                "  \"name\": \"Customer\",\n" +
                "  \"description\": \"Customer service and stakeholder outcomes\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/perspectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(perspectivePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Customer"));

        String contractPayload = "{\n" +
                "  \"employeeId\": 2,\n" +
                "  \"reportingPeriodId\": 1,\n" +
                "  \"title\": \"2026 Mid-Year Scorecard\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contractPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        String goalPayload = "{\n" +
                "  \"contractId\": 1,\n" +
                "  \"perspectiveId\": 1,\n" +
                "  \"name\": \"Improve SLA turnaround\",\n" +
                "  \"strategicObjective\": \"Increase responsiveness\",\n" +
                "  \"allocatedWeight\": 40.0,\n" +
                "  \"measure\": \"Average turnaround time\",\n" +
                "  \"targetValue\": \"24 hours\",\n" +
                "  \"skillGap\": \"Advanced stakeholder management\",\n" +
                "  \"trainingNeed\": \"Customer service excellence\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(goalPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Improve SLA turnaround"));

        String selfReviewPayload = "{\n" +
                "  \"goalScores\": [{ \"goalId\": 1, \"score\": 4.2 }],\n" +
                "  \"comment\": \"Delivered strong turnaround improvements\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/contracts/1/self-review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(selfReviewPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SELF_REVIEWED"))
                .andExpect(jsonPath("$.employeeScore").value(4.20));

        String managerReviewPayload = "{\n" +
                "  \"goalScores\": [{ \"goalId\": 1, \"score\": 4.8 }],\n" +
                "  \"comment\": \"Exceeded expected response times\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/contracts/1/manager-review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(managerReviewPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MANAGER_REVIEWED"))
                .andExpect(jsonPath("$.weightedScore").value(90.00));

        String actionPlanPayload = "{\n" +
                "  \"contractId\": 1,\n" +
                "  \"managerId\": 1,\n" +
                "  \"name\": \"Customer escalation coaching\",\n" +
                "  \"description\": \"Structured coaching on escalations\",\n" +
                "  \"measureOfSuccess\": \"Reduced unresolved incidents\",\n" +
                "  \"startDate\": \"2026-04-01\",\n" +
                "  \"endDate\": \"2026-06-30\",\n" +
                "  \"progress\": 20,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/action-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actionPlanPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        String pipPayload = "{\n" +
                "  \"employeeId\": 2,\n" +
                "  \"reportingPeriodId\": 1,\n" +
                "  \"targetArea\": \"Documentation quality\",\n" +
                "  \"concern\": \"Inconsistent case notes\",\n" +
                "  \"expectedStandard\": \"100% complete case logs\",\n" +
                "  \"agreedAction\": \"Weekly QA review\",\n" +
                "  \"requiredSupport\": \"Supervisor coaching\",\n" +
                "  \"reviewNotes\": \"Initial intervention\",\n" +
                "  \"progress\": 15,\n" +
                "  \"status\": \"OPEN\",\n" +
                "  \"endDate\": \"2026-07-31\"\n" +
                "}";

        mockMvc.perform(post("/api/performance/improvement-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pipPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"));

        mockMvc.perform(get("/api/performance/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contracts").value(1))
                .andExpect(jsonPath("$.actionPlans").value(1))
                .andExpect(jsonPath("$.improvementPlans").value(1))
                .andExpect(jsonPath("$.goalsWithTrainingNeeds").value(1));
    }

    @Test
    void createsEmployeeAndApprovesLeaveRequest() throws Exception {
        String managerPayload = "{\n" +
                "  \"employeeNumber\": \"EMP-MGR-001\",\n" +
                "  \"firstName\": \"Tariro\",\n" +
                "  \"lastName\": \"Ncube\",\n" +
                "  \"email\": \"tariro.ncube@example.com\",\n" +
                "  \"jobTitle\": \"HR Manager\",\n" +
                "  \"department\": \"Human Resources\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2023-01-15\",\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 2500.00,\n" +
                "  \"hourlyRate\": 14.50,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(managerPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        String employeePayload = "{\n" +
                "  \"employeeNumber\": \"EMP-002\",\n" +
                "  \"firstName\": \"Nyasha\",\n" +
                "  \"lastName\": \"Moyo\",\n" +
                "  \"email\": \"nyasha.moyo@example.com\",\n" +
                "  \"jobTitle\": \"HR Officer\",\n" +
                "  \"department\": \"Human Resources\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2024-01-15\",\n" +
                "  \"managerEmployeeId\": 1,\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 1800.00,\n" +
                "  \"hourlyRate\": 10.25,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.employeeNumber").value("EMP-002"));

        mockMvc.perform(post("/api/leave-requests/accruals/run")
                        .param("asOfDate", "2024-06-30"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/leave-requests/balances")
                        .param("employeeId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availableDays").isNumber());

        String leavePayload = "{\n" +
                "  \"employeeId\": 2,\n" +
                "  \"managerEmployeeId\": 1,\n" +
                "  \"managerAssigned\": true,\n" +
                "  \"leaveType\": \"ANNUAL\",\n" +
                "  \"startDate\": \"2099-07-01\",\n" +
                "  \"endDate\": \"2099-07-03\",\n" +
                "  \"reason\": \"Family vacation\"\n" +
                "}";

        mockMvc.perform(post("/api/leave-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leavePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.daysRequested").value(3))
                .andExpect(jsonPath("$.status").value("PENDING"));

        String decisionPayload = "{ \"status\": \"APPROVED\", \"managerEmployeeId\": 1 }";

        mockMvc.perform(patch("/api/leave-requests/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(decisionPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/leave-requests/history")
                        .param("employeeId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].decidedByManagerId").value(1));
    }

    @Test
    void processesPayrollAndGeneratesReports() throws Exception {
        String managerPayload = "{\n" +
                "  \"employeeNumber\": \"EMP-MGR-010\",\n" +
                "  \"firstName\": \"Lindiwe\",\n" +
                "  \"lastName\": \"Dube\",\n" +
                "  \"email\": \"lindiwe.dube@example.com\",\n" +
                "  \"jobTitle\": \"Finance Manager\",\n" +
                "  \"department\": \"Finance\",\n" +
                "  \"employmentTypeId\": 1,\n" +
                "  \"hireDate\": \"2023-03-01\",\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 3200.00,\n" +
                "  \"hourlyRate\": 18.50,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        String employeePayload = "{\n" +
                "  \"employeeNumber\": \"EMP-011\",\n" +
                "  \"firstName\": \"Farai\",\n" +
                "  \"lastName\": \"Sibanda\",\n" +
                "  \"email\": \"farai.sibanda@example.com\",\n" +
                "  \"jobTitle\": \"Payroll Officer\",\n" +
                "  \"department\": \"Finance\",\n" +
                "  \"employmentTypeId\": 2,\n" +
                "  \"hireDate\": \"2024-02-01\",\n" +
                "  \"managerEmployeeId\": 1,\n" +
                "  \"preferredCurrency\": \"USD\",\n" +
                "  \"monthlySalary\": 2200.00,\n" +
                "  \"hourlyRate\": 12.50,\n" +
                "  \"status\": \"ACTIVE\"\n" +
                "}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(managerPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeePayload))
                .andExpect(status().isCreated());

        String baseCurrencyPayload = "{\n" +
                "  \"code\": \"USD\",\n" +
                "  \"name\": \"United States Dollar\",\n" +
                "  \"symbol\": \"$\",\n" +
                "  \"baseCurrency\": true,\n" +
                "  \"active\": true\n" +
                "}";

        mockMvc.perform(post("/api/payroll/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(baseCurrencyPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("USD"));

        String sourceCurrencyPayload = "{\n" +
                "  \"code\": \"ZAR\",\n" +
                "  \"name\": \"South African Rand\",\n" +
                "  \"symbol\": \"R\",\n" +
                "  \"baseCurrency\": false,\n" +
                "  \"active\": true\n" +
                "}";

        mockMvc.perform(post("/api/payroll/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sourceCurrencyPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ZAR"));

        String exchangeRatePayload = "{\n" +
                "  \"currencyCode\": \"ZAR\",\n" +
                "  \"effectiveDate\": \"2026-03-01\",\n" +
                "  \"rateToBase\": 0.054000,\n" +
                "  \"notes\": \"March rate\"\n" +
                "}";

        mockMvc.perform(post("/api/payroll/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeRatePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency.code").value("ZAR"));

        String compensationPayload = "{\n" +
                "  \"employeeId\": 2,\n" +
                "  \"currencyCode\": \"ZAR\",\n" +
                "  \"baseMonthlySalary\": 2200.00,\n" +
                "  \"hourlyRate\": 12.50,\n" +
                "  \"standardMonthlyHours\": 176.0,\n" +
                "  \"incomeTaxRate\": 0.15,\n" +
                "  \"socialSecurityRate\": 0.03,\n" +
                "  \"retirementContributionRate\": 0.05,\n" +
                "  \"fixedBenefitsDeduction\": 45.00,\n" +
                "  \"fixedAllowance\": 120.00\n" +
                "}";

        mockMvc.perform(post("/api/payroll/compensation-packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compensationPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currencyCode").value("ZAR"));

        String payrollPayload = "{\n" +
                "  \"payrollCode\": \"PAY-2026-03\",\n" +
                "  \"currencyCode\": \"USD\",\n" +
                "  \"payDate\": \"2026-03-31\",\n" +
                "  \"periodStart\": \"2026-03-01\",\n" +
                "  \"periodEnd\": \"2026-03-31\",\n" +
                "  \"employees\": [\n" +
                "    {\n" +
                "      \"employeeId\": 2,\n" +
                "      \"hoursWorked\": 176.0,\n" +
                "      \"overtimeHours\": 10.0,\n" +
                "      \"bonus\": 150.00,\n" +
                "      \"otherDeductions\": 25.00\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(post("/api/payroll/runs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payrollPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("POSTED"))
                .andExpect(jsonPath("$.baseCurrencyCode").value("USD"))
                .andExpect(jsonPath("$.journalPosted").value(true))
                .andExpect(jsonPath("$.entries[0].employeeNumber").value("EMP-011"))
                .andExpect(jsonPath("$.entries[0].sourceCurrencyCode").value("ZAR"))
                .andExpect(jsonPath("$.entries[0].currencyCode").value("USD"));

        mockMvc.perform(get("/api/payroll/runs/1/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCount").value(1))
                .andExpect(jsonPath("$.totalGrossPay").isNumber());

        mockMvc.perform(get("/api/payroll/runs/1/tax-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payeDue").isNumber());

        mockMvc.perform(get("/api/payroll/entries/1/payslip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeNumber").value("EMP-011"))
                .andExpect(jsonPath("$.netPay").isNumber());
    }
}
