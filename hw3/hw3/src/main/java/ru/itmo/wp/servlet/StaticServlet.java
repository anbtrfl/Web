//-----------------------------------токо А робит-----------------------------------------------------------------------
//package ru.itmo.wp.servlet;
//
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.file.Files;
//
//public class StaticServlet extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String uri = request.getRequestURI();
//        File file = new File(getServletContext().getRealPath("."), "../../src/main/webapp/static/" + uri);
//        if (!file.isFile()) {
//            file = new File(getServletContext().getRealPath("/static/" + uri));
//        }
//        if (file.isFile()) {
//            response.setContentType(getServletContext().getMimeType(file.getName()));
//            try (OutputStream outputStream = response.getOutputStream()) {
//                Files.copy(file.toPath(), outputStream);
//            }
//        } else {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//
//    }
//}
//------------------------------------робит А+В-------------------------------------------------------------------------
package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] luri = request.getRequestURI().split("\\+");
        for (String uris : luri) {
            File file = new File(getServletContext().getRealPath("."), "../../src/main/webapp/static/" + uris);
            if (!file.isFile()) {
                file = new File(getServletContext().getRealPath("/static/" + uris));
            }
            if (file.isFile()) {
                if (response.getContentType() == null) {
                    response.setContentType(getServletContext().getMimeType(file.getAbsolutePath()));
                }
                try (OutputStream outputStream = response.getOutputStream()) {
                    Files.copy(file.toPath(), outputStream);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}
