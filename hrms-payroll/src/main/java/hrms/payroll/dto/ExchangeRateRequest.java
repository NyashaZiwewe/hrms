package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExchangeRateRequest {

    @NotBlank
    private String currencyCode;

    @NotNull
    private LocalDate effectiveDate;

    @NotNull
    @DecimalMin("0.000001")
    private BigDecimal rateToBase;

    private String notes;
}
