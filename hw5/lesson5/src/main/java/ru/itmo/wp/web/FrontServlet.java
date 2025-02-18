package ru.itmo.wp.web;

import freemarker.template.*;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FrontServlet extends HttpServlet {
    private static final String BASE_PACKAGE = FrontServlet.class.getPackage().getName() + ".page";
    private static final String DEFAULT_ACTION = "action";
//есть нюанс) мы хотим грузить темплейты из исходников в первую очередь(папка темплейтс)
//и если они не нашлись,то план Б - из задеплоеного приложения.поэтому надо иметь две конфигурации фримаркертные
    private Configuration sourceConfiguration;//чтобы грузить фримаркерные шаблончики из папки теплейтс
    private Configuration targetConfiguration;//из задеплоенного приложения

    //короче тут база,так делали уже
    private Configuration newFreemarkerConfiguration(String templateDirName, boolean debug)
            throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir="
                    + templateDir + "]");
        }
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER :
                TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setLocalizedLookup(false);

        return configuration;
    }

    @Override//создаем эти конфигурации
    public void init() throws ServletException {
        sourceConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("/") + "../../src/main/webapp/WEB-INF/templates", true);
        targetConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("WEB-INF/templates"), false);
    }
//мы для нашего сайта обрабатываем токо гет и пост запросы.одинаково их обрабатываем,поэтому просто делегируем их новому методу
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }
//вообще у нас есть соотв.запрос ,но мы то хотим найти соотв.контроллер,создать экземпляр класса этого контроллера
//и делегировать контроллеру обработку этого запроса. поэтому все что надо сначала сделать - раутинг(маршрутизацию)
//на основании реквеста получить какому конкретно контроллеру собираемся делегировать этот запрос,более того,получить метод в этом контроллере
//по поводу метода концепция что в урлах (например /миск/хелп) может еще знак вопроса стоит.типа получается /миск/хелп?экшен=тест (экшен может быть может нет)
//если он есть,у контроллера будем вызывать метод именно с таким названием. если его нет,то делегируем методу экшен(просто по дефолту)
//таким образом рез-тат раутинга - упорядоченная пара - полное имя класса вместе с пакетами,какому контроллеру надо делегировать
//и второе - экшен,который должен быть вызыван (фактически метод) в этом контроллере. переходим к Route
//теперь надо получить раут,где класснейм - ру.итмо.вп.веб.пейдж.миск.ХелпПейдж (имя класса контроллера) и экшен = тест
    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Route route = Route.newRoute(request);//наконец есть раут.теперь хотим каким то образом по имени класса получить экземпляр этого класса
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException notFoundException) {
                throw new ServletException(notFoundException);
            }
        }
    }

    private void process(Route route, HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, ServletException, IOException {
        Class<?> pageClass;//описатель класса по его имени.(магия jvm)мы попросили жвм по заданному имени класса подгрузить этот класс в жвм и вернуть описатьель класса.
        try {
            pageClass = Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();//в общем обработали ошибочку
        }
//нам нужно запустить конкретный метод в этом классе.его надо найти,а пока есть токо название,а надо найти описатель )что? этого метода.

        Method method = null;
//        //идем и смотрим в классы,до тех пор пока метод найден и пока не пришли к корню иерархии
//        for (Class<?> clazz = pageClass; method == null && clazz != null; clazz = clazz.getSuperclass()) {
//            try {//берем текуший класс(сначала это наш класс,потом вверх по иерархии).говорим гетдеклэред метод и пытаемся найти в нем соответствующий метод по имени и сигнатуре его.
//                //сигнатуры в методах контроллеров:им нужны данные,им нужно то,куда складывать те данные,которые пойдут во вью.(тот самый мэпчик)
//
//                method = clazz.getDeclaredMethod(route.getAction(), HttpServletRequest.class, Map.class);
//            } catch (NoSuchMethodException ignored) {
//                // No operations.
//            }
//        }
////этот фор закончится след.образом: либо метод нулл(на всей иерархии не нашелся метод с подходящей сигнатурой),лтбо не нулл
        for (Class<?> clazz = pageClass; method == null && clazz != null; clazz = clazz.getSuperclass()) {
            try {//возвращает массив, который содержит объекты типа Method, отражающие все объявленные методы класса или интерфейса, представленного этим объектом класса, включая публичные, приватные, по умолчанию и protected методы, но исключая унаследованные методы.
                Method foundMethod = findMethodByName(route.getAction(), clazz.getDeclaredMethods());
                if (foundMethod == null) throw new NoSuchMethodException();
                else
                    method = clazz.getDeclaredMethod(route.getAction(), foundMethod.getParameterTypes());
            } catch (NoSuchMethodException ignored) {
                // No operations.
            } catch (ClassFormatError e) {
                e.printStackTrace();
            }
        }
        if (method == null) {
            throw new NotFoundException();
        }

        Object page;
        // надо как-то создать экземпляр класса контроллера, и запустить данный метод с 2 аргументами - реквест,вью
        try {
            page = pageClass.getDeclaredConstructor().newInstance();//вызываем конструктор того клааса по описателю !(мэджик)
        } catch (InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            throw new ServletException("Can't create page [pageClass="
                    + pageClass + "]");
        }//короче 500 ошибок надо обрабатывать
        //воот,и получилось экземпляр класса например РегистерПейдж

//мэпчик с теми данными,которые попадут во Фримаркер шаблончик
        Map<String, Object> view = new HashMap<>();
        method.setAccessible(true);//тк метод приватный,нада такой мэджик
        try {
            method.invoke(page, getMethodParameters(method, request, view));
            //method.invoke(page, request, view);//говорим методу запустись
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't invoke action method [pageClass="
                    + pageClass + ", method=" + method + "]");//икспешен
        } catch (InvocationTargetException e) {//в общем я ниечго непон,какойто там эксепшен,надеюсь можно забить
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                RedirectException redirectException = (RedirectException) cause;
                response.sendRedirect(redirectException.getTarget());
                return;
            } else {
                throw new ServletException("Can't invoke action method [pageClass="
                        + pageClass + ", method=" + method + "]", cause);
            }
        }//и вся цель этого метода состоит в том чтобы в этот вью(мэпчик в котором лежат ключ данные) понапихал их туда
        //все что осталось сделат - создать шаблончик и порендерить его в браузер  ,источник вью. ну как раньше

        //сечас надо шаблончик подгрузить
        //Template template = newTemplate(pageClass.getSimpleName() + ".ftlh");//этот метод сначала с помощью сорс конфигурации грузить,в случае неудачи - таржет
        String lang = request.getParameter("lang");
        String addition;
        if (lang != null && lang.matches("[a-z]{2}")) {
            request.getSession().setAttribute("lang", lang);
        } else {
            lang = (String) request.getSession().getAttribute("lang");
        }
        addition = "_" + lang;
        Template template;
        if (!addition.equals("_en")) {
            try {
                template = newTemplate(pageClass.getSimpleName() + addition + ".ftlh");
            } catch (ServletException e) {
                template = newTemplate(pageClass.getSimpleName() + ".ftlh");
            }
        } else {
            template = newTemplate(pageClass.getSimpleName() + ".ftlh");
            //System.out.println(pageClass.getSimpleName() + ".ftlh");
        }
        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            template.process(view, response.getWriter());//порендерись используя хэшмэп вью
        } catch (TemplateException e) {
            if (sourceConfiguration == null) {
                throw new ServletException("Can't render template [pageClass="
                        + pageClass + ", action=" + method + "]", e);
            }
        }
    }

    private Object[] getMethodParameters(Method method, HttpServletRequest request, Map<String, Object> view) {
        ArrayList<Object> resultList = new ArrayList<>();
        for (Class<?> currentClass : method.getParameterTypes()) {
            if (HttpServletRequest.class.equals(currentClass)) {
                resultList.add(request);
            }
            if (Map.class.equals(currentClass)) {
                resultList.add(view);
            }
        }
        return resultList.toArray();
    }

    private Method findMethodByName(String action, Method[] currentClazzsMethods) throws ClassFormatError {
        boolean methodWasFoundEarlier = false;
        Method foundMethod = null;
        for (Method currentMethod : currentClazzsMethods) {
            if (currentMethod.getName().equals(action) && methodWasFoundEarlier) {
                throw new ClassFormatError("The method you are looking for is overridden: " + action);
            }
            if (currentMethod.getName().equals(action)) {
                methodWasFoundEarlier = true;
                foundMethod = currentMethod;
            }
        }
        return foundMethod;
    }
//подгружает темплейт и вернуть его
    private Template newTemplate(String templateName) throws ServletException {
        Template template = null;

        if (sourceConfiguration != null) {
            try {
                template = sourceConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null && targetConfiguration != null) {
            try {
                template = targetConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null) {
            throw new ServletException("Can't find template [templateName=" + templateName + "]");
        }

        return template;
    }
//
    private static class Route {
        private final String className;
        private final String action;

        private Route(String className, String action) { //конструктор
            this.className = className;
            this.action = action;
        }
//просто геттеры
        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newNotFoundRoute() {
            return new Route(NotFoundPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newIndexRoute() {
            return new Route(IndexPage.class.getName(), DEFAULT_ACTION);
        }
//дай нам новый раут
        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();//у реквеста берем ури

            List<String> classNameParts = Arrays.stream(uri.split("/"))//сплитим по слешикам
                    .filter(part -> !part.isEmpty())//айтем интересен если не пустой
                    .collect(Collectors.toList());//чето про потоки данных я непон
//в общем этот кусок кода он мы сплитнули ури.пусть ури = /миск/хелп, потом получается "" "миск" и "хелп". после этого фильтранули пустое
// и в итоге в этом листе только "миск" и "хелп"
            if (classNameParts.isEmpty()) {//есть корневая директория то просто возвращаем индексную страницу и дефолтный экшен.просто уходим в корень
                return newIndexRoute();
            }

            StringBuilder simpleClassName = new StringBuilder(classNameParts.get(classNameParts.size() - 1));//взяли последний токен
            int lastDotIndex = simpleClassName.lastIndexOf(".");
            simpleClassName.setCharAt(lastDotIndex + 1,
                    Character.toUpperCase(simpleClassName.charAt(lastDotIndex + 1)));//капитализировали первый символ (что??)
            classNameParts.set(classNameParts.size() - 1, simpleClassName.toString());//теперь в нашем листе последнее не хелп,а Хелп

            String className = BASE_PACKAGE + "." + String.join(".", classNameParts) + "Page";//соберем в строку полное имя класса. это

            String action = request.getParameter("action");//теперь надо получить экшен
            if (action == null || action.isEmpty()) {//если не ок то присваем в дефолт экшен
                action = DEFAULT_ACTION;
            }

            return new Route(className, action);
            //воот,и все это делали чтобы получить на основании реквеста полное имя класса контроллер(которму делегируем запрос. и экшен метод
        }
    }
}
