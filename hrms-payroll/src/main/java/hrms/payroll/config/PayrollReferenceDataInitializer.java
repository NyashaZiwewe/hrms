package hrms.payroll.config;

import hrms.payroll.dto.CurrencyRequest;
import hrms.payroll.dto.ExchangeRateRequest;
import hrms.payroll.service.PayrollService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class PayrollReferenceDataInitializer {

    @Bean
    CommandLineRunner payrollReferenceInitializer(PayrollService payrollService) {
        return args -> {
            seedCurrency(payrollService, "USD", "United States Dollar", "$", true);
            seedCurrency(payrollService, "ZWG", "Zimbabwe Gold", "ZWG", false);
            seedCurrency(payrollService, "ZAR", "South African Rand", "R", false);

            LocalDate today = LocalDate.now();
            seedRate(payrollService, "USD", today, new BigDecimal("1.000000"), "Base currency");
            seedRate(payrollService, "ZWG", today, new BigDecimal("0.026000"), "Default seeded rate");
            seedRate(payrollService, "ZAR", today, new BigDecimal("0.054000"), "Default seeded rate");
        };
    }

    private void seedCurrency(PayrollService payrollService,
                              String code,
                              String name,
                              String symbol,
                              boolean baseCurrency) {
        if (payrollService.findCurrency(code).isPresent()) {
            return;
        }
        CurrencyRequest request = new CurrencyRequest();
        request.setCode(code);
        request.setName(name);
        request.setSymbol(symbol);
        request.setBaseCurrency(baseCurrency);
        request.setActive(true);
        payrollService.saveCurrency(request);
    }

    private void seedRate(PayrollService payrollService,
                          String currencyCode,
                          LocalDate effectiveDate,
                          BigDecimal rateToBase,
                          String notes) {
        if (payrollService.findEffectiveExchangeRate(currencyCode, effectiveDate).isPresent()) {
            return;
        }
        ExchangeRateRequest request = new ExchangeRateRequest();
        request.setCurrencyCode(currencyCode);
        request.setEffectiveDate(java.util.Date.from(effectiveDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setRateToBase(rateToBase);
        request.setNotes(notes);
        payrollService.saveExchangeRate(request);
    }
}
