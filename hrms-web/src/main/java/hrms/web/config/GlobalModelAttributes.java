package hrms.web.config;

import hrms.common.util.DateUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("systemName")
    public String systemName() {
        return "HRMS";
    }

    @ModelAttribute("companyName")
    public String companyName() {
        return "HRMS";
    }

    @ModelAttribute("companyLogo")
    public String companyLogo() {
        return "logo.png";
    }

    @ModelAttribute("localDate")
    public LocalDate localDate() {
        return DateUtils.today();
    }
}
