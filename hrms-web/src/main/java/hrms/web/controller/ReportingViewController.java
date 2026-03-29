package hrms.web.controller;

import hrms.audit.service.AuditTrailService;
import hrms.reporting.service.DashboardService;
import hrms.web.constants.Pages;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/reports")
public class ReportingViewController {

    private final DashboardService dashboardService;
    private final AuditTrailService auditTrailService;

    public ReportingViewController(DashboardService dashboardService, AuditTrailService auditTrailService) {
        this.dashboardService = dashboardService;
        this.auditTrailService = auditTrailService;
    }

    @GetMapping
    public ModelAndView reports() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_REPORTS);
        modelAndView.addObject("pageDomain", "Reporting");
        modelAndView.addObject("pageName", "Reports");
        modelAndView.addObject("pageTitle", "Enterprise Reporting");
        modelAndView.addObject("dashboard", dashboardService.dashboard());
        modelAndView.addObject("auditTrails", auditTrailService.latest(null));
        return modelAndView;
    }
}
