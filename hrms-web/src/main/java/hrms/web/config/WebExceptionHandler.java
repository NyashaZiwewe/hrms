package hrms.web.config;

import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "hrms.web.controller")
public class WebExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException exception) {
        return errorView(HttpStatus.NOT_FOUND, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, DuplicateResourceException.class, OperationNotAllowedException.class})
    public ModelAndView handleBadRequest(RuntimeException exception) {
        return errorView(HttpStatus.BAD_REQUEST, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception exception) {
        return errorView(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong while processing your request. Please try again.");
    }

    private ModelAndView errorView(HttpStatus status, String message) {
        ModelAndView modelAndView = new ModelAndView("errors/generalError");
        modelAndView.setStatus(status);
        modelAndView.addObject("pageDomain", "System");
        modelAndView.addObject("pageName", "Error");
        modelAndView.addObject("pageTitle", status.getReasonPhrase());
        modelAndView.addObject("statusCode", status.value());
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }

    private String friendlyMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Something went wrong while processing your request.";
        }
        return message;
    }
}
