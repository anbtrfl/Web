package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.Type;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.model.service.EventService;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class EnterPage extends Page{
    private final UserService userService = new UserService();
    private final EventService eventRepository = new EventService();
//    private void action(HttpServletRequest request, Map<String, Object> view) {
//        // No operations.
//    }

    private void enter(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        String loginOrEmail = request.getParameter("loginOrEmail");
        String password = request.getParameter("password");

        userService.validateEnter(loginOrEmail, password);



        User user = userService.findByLoginOrEmailAndPassword(loginOrEmail, password);
        setUser(user);

        //request.getSession().setAttribute("user", user);

        setMessage("Hello, " + user.getLogin());

        //request.getSession().setAttribute("message", "Hello, " + user.getLogin());

        Event event = new Event(null, user.getId(), Type.ENTER, null);
        eventRepository.save(event);
        throw new RedirectException("/index");
    }
}
