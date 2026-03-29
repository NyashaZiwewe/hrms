package hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CurrencyRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String symbol;

    private boolean baseCurrency;

    private boolean active = true;
}
