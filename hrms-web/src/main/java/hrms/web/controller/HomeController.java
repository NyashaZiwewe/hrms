package hrms.web.controller;

import hrms.reporting.service.DashboardService;
import hrms.web.constants.Pages;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView(Pages.INDEX);
        modelAndView.addObject("pageDomain", "Dashboard");
        modelAndView.addObject("pageName", "Home");
        modelAndView.addObject("pageTitle", "HRMS Dashboard");
        modelAndView.addObject("dashboard", dashboardService.dashboard());
        return modelAndView;
    }
}
