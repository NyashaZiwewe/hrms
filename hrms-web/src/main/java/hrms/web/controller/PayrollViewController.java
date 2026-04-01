package hrms.web.controller;

import hrms.employee.entity.Employee;
import hrms.employee.service.EmployeeService;
import hrms.common.util.DateUtils;
import hrms.payroll.dto.CompensationPackageRequest;
import hrms.payroll.dto.CurrencyRequest;
import hrms.payroll.dto.ExchangeRateRequest;
import hrms.payroll.dto.PayrollEmployeeInput;
import hrms.payroll.dto.PayrollProcessRequest;
import hrms.payroll.entity.CompensationPackage;
import hrms.payroll.service.PayrollService;
import hrms.web.constants.Pages;
import hrms.web.util.PortletUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/payroll")
public class PayrollViewController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    public PayrollViewController(PayrollService payrollService, EmployeeService employeeService) {
        this.payrollService = payrollService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public ModelAndView payroll() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_RUNS);
        modelAndView.addObject("pageDomain", "Payroll Management");
        modelAndView.addObject("pageName", "Payroll");
        modelAndView.addObject("pageTitle", "View Payroll Runs");
        modelAndView.addObject("payrollRuns", payrollService.listRuns());
        return modelAndView;
    }

    @GetMapping("/view-run/{payrollRunId}")
    public ModelAndView viewRun(@PathVariable Long payrollRunId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_RUN);
        modelAndView.addObject("pageDomain", "Payroll Management");
        modelAndView.addObject("pageName", "Payroll");
        modelAndView.addObject("pageTitle", "View Payroll Run");
        modelAndView.addObject("payrollRun", payrollService.getPayrollRun(payrollRunId));
        modelAndView.addObject("payrollReport", payrollService.report(payrollRunId));
        modelAndView.addObject("taxReport", payrollService.taxReport(payrollRunId));
        return modelAndView;
    }

    @GetMapping("/process-run")
    public ModelAndView processRun() {
        ModelAndView modelAndView = new ModelAndView(Pages.PROCESS_PAYROLL_RUN);
        populatePage(modelAndView, "Process Payroll Run");
        modelAndView.addObject("payrollProcessRequest", buildProcessRequest());
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("currencies", payrollService.listCurrencies());
        modelAndView.addObject("baseCurrency", payrollService.getBaseCurrency());
        return modelAndView;
    }

    @PostMapping("/process-run")
    public ModelAndView processRun(@Valid @ModelAttribute PayrollProcessRequest payrollProcessRequest,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.PROCESS_PAYROLL_RUN);
            populatePage(modelAndView, "Process Payroll Run");
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("currencies", payrollService.listCurrencies());
            modelAndView.addObject("baseCurrency", payrollService.getBaseCurrency());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        return new ModelAndView("redirect:/payroll/view-run/" + payrollService.processPayroll(payrollProcessRequest).getPayrollRunId());
    }

    @GetMapping("/compensation-packages")
    public ModelAndView compensationPackages() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_COMPENSATION_PACKAGES);
        modelAndView.addObject("pageDomain", "Payroll Management");
        modelAndView.addObject("pageName", "Payroll");
        modelAndView.addObject("pageTitle", "View Compensation Packages");
        modelAndView.addObject("compensationPackages", payrollService.listCompensationPackages());
        return modelAndView;
    }

    @GetMapping("/add-compensation-package")
    public ModelAndView addCompensationPackage() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_COMPENSATION_PACKAGE);
        populatePage(modelAndView, "Add Compensation Package");
        modelAndView.addObject("compensationPackageRequest", new CompensationPackageRequest());
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("currencies", payrollService.listCurrencies());
        return modelAndView;
    }

    @PostMapping("/save-compensation-package")
    public ModelAndView saveCompensationPackage(@Valid @ModelAttribute CompensationPackageRequest compensationPackageRequest,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_COMPENSATION_PACKAGE);
            populatePage(modelAndView, "Add Compensation Package");
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("currencies", payrollService.listCurrencies());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        CompensationPackage saved = payrollService.saveCompensationPackage(compensationPackageRequest);
        return new ModelAndView("redirect:/payroll/view-compensation-package/" + saved.getId());
    }

    @GetMapping("/view-compensation-package/{compensationPackageId}")
    public ModelAndView viewCompensationPackage(@PathVariable Long compensationPackageId) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_COMPENSATION_PACKAGE);
        modelAndView.addObject("pageDomain", "Payroll Management");
        modelAndView.addObject("pageName", "Payroll");
        modelAndView.addObject("pageTitle", "View Compensation Package");
        modelAndView.addObject("compensationPackage", payrollService.getCompensationPackage(compensationPackageId));
        return modelAndView;
    }

    @GetMapping("/edit-compensation-package/{compensationPackageId}")
    public ModelAndView editCompensationPackage(@PathVariable Long compensationPackageId) {
        CompensationPackage compensationPackage = payrollService.getCompensationPackage(compensationPackageId);
        CompensationPackageRequest request = new CompensationPackageRequest();
        request.setEmployeeId(compensationPackage.getEmployee().getId());
        request.setCurrencyCode(compensationPackage.getCurrencyCode());
        request.setBaseMonthlySalary(compensationPackage.getBaseMonthlySalary());
        request.setHourlyRate(compensationPackage.getHourlyRate());
        request.setStandardMonthlyHours(compensationPackage.getStandardMonthlyHours());
        request.setIncomeTaxRate(compensationPackage.getIncomeTaxRate());
        request.setSocialSecurityRate(compensationPackage.getSocialSecurityRate());
        request.setRetirementContributionRate(compensationPackage.getRetirementContributionRate());
        request.setFixedBenefitsDeduction(compensationPackage.getFixedBenefitsDeduction());
        request.setFixedAllowance(compensationPackage.getFixedAllowance());

        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_COMPENSATION_PACKAGE);
        populatePage(modelAndView, "Edit Compensation Package");
        modelAndView.addObject("compensationPackageId", compensationPackageId);
        modelAndView.addObject("compensationPackageRequest", request);
        modelAndView.addObject("employees", employeeService.findAll());
        modelAndView.addObject("currencies", payrollService.listCurrencies());
        return modelAndView;
    }

    @PostMapping("/update-compensation-package/{compensationPackageId}")
    public ModelAndView updateCompensationPackage(@PathVariable Long compensationPackageId,
                                                  @Valid @ModelAttribute CompensationPackageRequest compensationPackageRequest,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_COMPENSATION_PACKAGE);
            populatePage(modelAndView, "Edit Compensation Package");
            modelAndView.addObject("compensationPackageId", compensationPackageId);
            modelAndView.addObject("employees", employeeService.findAll());
            modelAndView.addObject("currencies", payrollService.listCurrencies());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        payrollService.saveCompensationPackage(compensationPackageRequest);
        return new ModelAndView("redirect:/payroll/view-compensation-package/" + compensationPackageId);
    }

    @PostMapping("/delete-compensation-package/{compensationPackageId}")
    public ModelAndView deleteCompensationPackage(@PathVariable Long compensationPackageId) {
        payrollService.deleteCompensationPackage(compensationPackageId);
        return new ModelAndView("redirect:/payroll/compensation-packages");
    }

    @GetMapping("/currencies")
    public ModelAndView currencies() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_CURRENCIES);
        populatePage(modelAndView, "Payroll Currencies");
        modelAndView.addObject("currencyRequest", new CurrencyRequest());
        modelAndView.addObject("currencies", payrollService.listCurrencies());
        return modelAndView;
    }

    @PostMapping("/currencies")
    public ModelAndView saveCurrency(@Valid @ModelAttribute CurrencyRequest currencyRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_CURRENCIES);
            populatePage(modelAndView, "Payroll Currencies");
            modelAndView.addObject("currencies", payrollService.listCurrencies());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        payrollService.saveCurrency(currencyRequest);
        return new ModelAndView("redirect:/payroll/currencies");
    }

    @GetMapping("/exchange-rates")
    public ModelAndView exchangeRates() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_EXCHANGE_RATES);
        populatePage(modelAndView, "Exchange Rates");
        ExchangeRateRequest request = new ExchangeRateRequest();
        request.setEffectiveDate(toDate(DateUtils.today()));
        modelAndView.addObject("exchangeRateRequest", request);
        modelAndView.addObject("currencies", payrollService.listCurrencies());
        modelAndView.addObject("exchangeRates", payrollService.listExchangeRates());
        modelAndView.addObject("baseCurrency", payrollService.getBaseCurrency());
        return modelAndView;
    }

    @PostMapping("/exchange-rates")
    public ModelAndView saveExchangeRate(@Valid @ModelAttribute ExchangeRateRequest exchangeRateRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.VIEW_PAYROLL_EXCHANGE_RATES);
            populatePage(modelAndView, "Exchange Rates");
            modelAndView.addObject("currencies", payrollService.listCurrencies());
            modelAndView.addObject("exchangeRates", payrollService.listExchangeRates());
            modelAndView.addObject("baseCurrency", payrollService.getBaseCurrency());
            PortletUtils.addBindingErrors(modelAndView, bindingResult);
            return modelAndView;
        }
        payrollService.saveExchangeRate(exchangeRateRequest);
        return new ModelAndView("redirect:/payroll/exchange-rates");
    }

    private PayrollProcessRequest buildProcessRequest() {
        PayrollProcessRequest request = new PayrollProcessRequest();
        LocalDate today = DateUtils.today();
        request.setPayrollCode("PR-" + today);
        request.setCurrencyCode(payrollService.getBaseCurrency().getCode());
        request.setPayDate(toDate(today));
        request.setPeriodStart(toDate(today.withDayOfMonth(1)));
        request.setPeriodEnd(toDate(today));
        List<PayrollEmployeeInput> employeeInputs = new ArrayList<PayrollEmployeeInput>();
        for (Employee employee : employeeService.findAll()) {
            PayrollEmployeeInput input = new PayrollEmployeeInput();
            input.setEmployeeId(employee.getId());
            input.setHoursWorked(new BigDecimal("173.33"));
            input.setOvertimeHours(BigDecimal.ZERO);
            input.setBonus(BigDecimal.ZERO);
            input.setOtherDeductions(BigDecimal.ZERO);
            employeeInputs.add(input);
        }
        request.setEmployees(employeeInputs);
        return request;
    }

    private void populatePage(ModelAndView modelAndView, String title) {
        modelAndView.addObject("pageDomain", "Payroll Management");
        modelAndView.addObject("pageName", "Payroll");
        modelAndView.addObject("pageTitle", title);
    }

    private Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
