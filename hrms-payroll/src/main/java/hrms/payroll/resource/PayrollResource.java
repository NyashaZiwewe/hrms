package hrms.payroll.resource;

import hrms.payroll.dto.CompensationPackageRequest;
import hrms.payroll.dto.CurrencyRequest;
import hrms.payroll.dto.ExchangeRateRequest;
import hrms.payroll.dto.PayrollProcessRequest;
import hrms.payroll.dto.PayrollReportResponse;
import hrms.payroll.dto.PayrollRunResponse;
import hrms.payroll.dto.PayslipResponse;
import hrms.payroll.dto.TaxFilingReportResponse;
import hrms.payroll.entity.CompensationPackage;
import hrms.payroll.entity.Currency;
import hrms.payroll.entity.ExchangeRate;
import org.springframework.web.bind.annotation.DeleteMapping;
import hrms.payroll.service.PayrollService;
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
@RequestMapping("/api/payroll")
public class PayrollResource {

    private final PayrollService payrollService;

    public PayrollResource(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/compensation-packages")
    @ResponseStatus(HttpStatus.CREATED)
    public CompensationPackage saveCompensationPackage(@Valid @RequestBody CompensationPackageRequest request) {
        return payrollService.saveCompensationPackage(request);
    }

    @GetMapping("/compensation-packages")
    public List<CompensationPackage> compensationPackages() {
        return payrollService.listCompensationPackages();
    }

    @GetMapping("/compensation-packages/{compensationPackageId}")
    public CompensationPackage compensationPackage(@PathVariable Long compensationPackageId) {
        return payrollService.getCompensationPackage(compensationPackageId);
    }

    @DeleteMapping("/compensation-packages/{compensationPackageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompensationPackage(@PathVariable Long compensationPackageId) {
        payrollService.deleteCompensationPackage(compensationPackageId);
    }

    @PostMapping("/runs")
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollRunResponse processPayroll(@Valid @RequestBody PayrollProcessRequest request) {
        return payrollService.processPayroll(request);
    }

    @GetMapping("/runs/{payrollRunId}")
    public PayrollRunResponse getPayrollRun(@PathVariable Long payrollRunId) {
        return payrollService.getPayrollRun(payrollRunId);
    }

    @GetMapping("/runs/{payrollRunId}/report")
    public PayrollReportResponse getPayrollReport(@PathVariable Long payrollRunId) {
        return payrollService.report(payrollRunId);
    }

    @GetMapping("/runs/{payrollRunId}/tax-report")
    public TaxFilingReportResponse getTaxReport(@PathVariable Long payrollRunId) {
        return payrollService.taxReport(payrollRunId);
    }

    @GetMapping("/entries/{payrollEntryId}/payslip")
    public PayslipResponse getPayslip(@PathVariable Long payrollEntryId) {
        return payrollService.payslip(payrollEntryId);
    }

    @PostMapping("/currencies")
    @ResponseStatus(HttpStatus.CREATED)
    public Currency saveCurrency(@Valid @RequestBody CurrencyRequest request) {
        return payrollService.saveCurrency(request);
    }

    @GetMapping("/currencies")
    public List<Currency> currencies() {
        return payrollService.listCurrencies();
    }

    @PostMapping("/exchange-rates")
    @ResponseStatus(HttpStatus.CREATED)
    public ExchangeRate saveExchangeRate(@Valid @RequestBody ExchangeRateRequest request) {
        return payrollService.saveExchangeRate(request);
    }

    @GetMapping("/exchange-rates")
    public List<ExchangeRate> exchangeRates() {
        return payrollService.listExchangeRates();
    }
}
