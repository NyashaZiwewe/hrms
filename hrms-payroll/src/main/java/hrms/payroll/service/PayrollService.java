package hrms.payroll.service;

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
import hrms.payroll.entity.PayrollRun;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollService {

    CompensationPackage saveCompensationPackage(CompensationPackageRequest request);

    List<CompensationPackage> listCompensationPackages();

    CompensationPackage getCompensationPackage(Long compensationPackageId);

    void deleteCompensationPackage(Long compensationPackageId);

    PayrollRunResponse processPayroll(PayrollProcessRequest request);

    PayslipResponse payslip(Long payrollEntryId);

    PayrollRunResponse getPayrollRun(Long payrollRunId);

    PayrollReportResponse report(Long payrollRunId);

    TaxFilingReportResponse taxReport(Long payrollRunId);

    List<PayrollRun> listRuns();

    Currency saveCurrency(CurrencyRequest request);

    List<Currency> listCurrencies();

    Optional<Currency> findCurrency(String currencyCode);

    Currency getCurrency(String currencyCode);

    Currency getBaseCurrency();

    ExchangeRate saveExchangeRate(ExchangeRateRequest request);

    List<ExchangeRate> listExchangeRates();

    List<ExchangeRate> listExchangeRates(String currencyCode);

    Optional<ExchangeRate> findEffectiveExchangeRate(String currencyCode, LocalDate effectiveDate);
}
