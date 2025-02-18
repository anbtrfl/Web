package ru.itmo.wp.web;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class StaticFilter extends HttpFilter {
    @Override
    //этот метод делает следующее:он смотрит в запрос и говорит,что если этот запрос соответствует
    //статическому файлику из веб-ресурсов, то хорошо,сечас этот файлик отдадим
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); //работаем в кодировке утф-8

        String uri = request.getRequestURI(); // есть текущий ури
        String rootRealPath = getServletContext().getRealPath("/");

        //пытаемся сначала найти этот статический файлик
        File file = new File(new File(rootRealPath, "../../src/main/webapp"), uri);
        if (!file.isFile()) {
            file = new File(rootRealPath, uri); //getRealPath используем, чтобы транслировать ури в абсолютный путь до конкретного файла уже задеплоенного приложения
        }

        if (file.isFile()) {
            response.setContentType(getServletContext().getMimeType(file.getCanonicalPath()));
            response.setContentLengthLong(file.length());
            Files.copy(file.toPath(), response.getOutputStream()); //копируем все содержимое этого файла в респонс
        } else {
            chain.doFilter(request, response); //дальше в цепочку обработки отдаем этот запрос(не 404 как раньше). теперь оно пошло в фронтсервлет
        }
    }
}
