package hrms.web.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public final class PortletUtils {

    public static final String ERROR_MSGS = "errorMsgs";
    public static final String INFO_MSGS = "infoMsgs";

    private PortletUtils() {
    }

    public static void addErrorMsg(String msg) {
        addMessage(ERROR_MSGS, msg, currentRequest());
    }

    public static void addErrorMsg(String msg, HttpServletRequest request) {
        addMessage(ERROR_MSGS, msg, request);
    }

    public static void addInfoMsg(String msg) {
        addMessage(INFO_MSGS, msg, currentRequest());
    }

    public static void addInfoMsg(String msg, HttpServletRequest request) {
        addMessage(INFO_MSGS, msg, request);
    }

    public static List<String> consumeErrorMsgs(HttpSession session) {
        return consumeMessages(session, ERROR_MSGS);
    }

    public static List<String> consumeInfoMsgs(HttpSession session) {
        return consumeMessages(session, INFO_MSGS);
    }

    public static void addBindingErrors(ModelAndView modelAndView, BindingResult bindingResult) {
        if (modelAndView == null || bindingResult == null || !bindingResult.hasErrors()) {
            return;
        }
        List<String> messages = new ArrayList<String>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getDefaultMessage() != null && !fieldError.getDefaultMessage().trim().isEmpty()) {
                messages.add(fieldError.getDefaultMessage());
            }
        }
        for (ObjectError objectError : bindingResult.getGlobalErrors()) {
            if (objectError.getDefaultMessage() != null && !objectError.getDefaultMessage().trim().isEmpty()) {
                messages.add(objectError.getDefaultMessage());
            }
        }
        if (messages.isEmpty()) {
            messages.add("Please correct the form errors and try again.");
        }
        modelAndView.addObject(ERROR_MSGS, messages);
    }

    private static void addMessage(String key, String msg, HttpServletRequest request) {
        if (request == null || msg == null || msg.trim().isEmpty()) {
            return;
        }
        HttpSession session = request.getSession();
        List<String> messages = (List<String>) session.getAttribute(key);
        if (messages == null) {
            messages = new ArrayList<String>();
            session.setAttribute(key, messages);
        }
        messages.add(msg);
    }

    private static List<String> consumeMessages(HttpSession session, String key) {
        if (session == null) {
            return null;
        }
        List<String> messages = (List<String>) session.getAttribute(key);
        if (messages == null || messages.isEmpty()) {
            session.removeAttribute(key);
            return null;
        }
        session.removeAttribute(key);
        return new ArrayList<String>(messages);
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }
}
