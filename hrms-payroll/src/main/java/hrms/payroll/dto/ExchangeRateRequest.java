package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ExchangeRateRequest {

    @NotBlank
    private String currencyCode;

    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date effectiveDate;

    @NotNull
    @DecimalMin("0.000001")
    private BigDecimal rateToBase;

    private String notes;
}
