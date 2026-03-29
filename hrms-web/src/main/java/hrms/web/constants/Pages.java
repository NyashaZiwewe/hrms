package hrms.web.constants;

public final class Pages {

    private Pages() {
    }

    public static final String INDEX = "index";

    public static final String VIEW_EMPLOYEES = "employees/viewEmployees";
    public static final String VIEW_EMPLOYEE = "employees/viewEmployee";
    public static final String ADD_EMPLOYEE = "employees/addEmployee";
    public static final String EDIT_EMPLOYEE = "employees/editEmployee";
    public static final String MANAGE_EMPLOYEE_DEPENDENTS = "employees/manageDependents";
    public static final String MANAGE_EMPLOYEE_DISABILITIES = "employees/manageDisabilities";
    public static final String MANAGE_EMPLOYEE_CONTACTS = "employees/manageContacts";
    public static final String MANAGE_EMPLOYEE_CONTRACTS = "employees/manageContracts";

    public static final String VIEW_LEAVE_REQUESTS = "leave/viewLeaveRequests";
    public static final String VIEW_LEAVE_REQUEST = "leave/viewLeaveRequest";
    public static final String ADD_LEAVE_REQUEST = "leave/addLeaveRequest";
    public static final String EDIT_LEAVE_REQUEST = "leave/editLeaveRequest";
    public static final String VIEW_LEAVE_BALANCES = "leave/viewLeaveBalances";
    public static final String SELL_LEAVE_DAYS = "leave/sellLeaveDays";
    public static final String VIEW_OVERTIME_CLAIMS = "leave/viewOvertimeClaims";

    public static final String VIEW_PAYROLL_RUNS = "payroll/viewPayrollRuns";
    public static final String VIEW_PAYROLL_RUN = "payroll/viewPayrollRun";
    public static final String PROCESS_PAYROLL_RUN = "payroll/processPayrollRun";
    public static final String VIEW_COMPENSATION_PACKAGES = "payroll/viewCompensationPackages";
    public static final String VIEW_COMPENSATION_PACKAGE = "payroll/viewCompensationPackage";
    public static final String ADD_COMPENSATION_PACKAGE = "payroll/addCompensationPackage";
    public static final String EDIT_COMPENSATION_PACKAGE = "payroll/editCompensationPackage";
    public static final String VIEW_PAYROLL_CURRENCIES = "payroll/viewCurrencies";
    public static final String VIEW_PAYROLL_EXCHANGE_RATES = "payroll/viewExchangeRates";

    public static final String VIEW_CONTRACTS = "performance/viewContracts";
    public static final String VIEW_CONTRACT = "performance/viewContract";
    public static final String ADD_CONTRACT = "performance/addContract";
    public static final String ADD_GOAL = "performance/addGoal";
    public static final String SELF_REVIEW = "performance/selfReview";
    public static final String MANAGER_REVIEW = "performance/managerReview";
    public static final String ADD_ACTION_PLAN = "performance/addActionPlan";
    public static final String ADD_IMPROVEMENT_PLAN = "performance/addImprovementPlan";

    public static final String VIEW_REPORTS = "reports/viewReports";
}
