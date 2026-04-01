package hrms.web.config;

import hrms.common.util.DateUtils;
import hrms.web.util.PortletUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

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

    @ModelAttribute("errorMsgs")
    public List<String> errorMsgs(HttpSession session) {
        return PortletUtils.consumeErrorMsgs(session);
    }

    @ModelAttribute("infoMsgs")
    public List<String> infoMsgs(HttpSession session) {
        return PortletUtils.consumeInfoMsgs(session);
    }
}
