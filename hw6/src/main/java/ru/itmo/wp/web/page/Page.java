package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.EventService;

import ru.itmo.wp.model.service.UserService;

public class Page {
    protected final static UserService userService = new UserService();
    protected final static EventService eventService = new EventService();


    private HttpServletRequest currentRequest;

    void before(HttpServletRequest request, Map<String, Object> view) {
        currentRequest = request;

        view.put("userCount", userService.findCount());

        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }

        String message = (String) request.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    void after(HttpServletRequest request, Map<String, Object> view) {

    }

    protected void action(HttpServletRequest request, Map<String, Object> view) {}

    public void setMessage(String message) {
        currentRequest.getSession().setAttribute("message", message);
    }

    public void setUser(User user) {
        currentRequest.getSession().setAttribute("user", user);
    }
    public User getUser() {
        return (User) currentRequest.getSession().getAttribute("user");
    }
}