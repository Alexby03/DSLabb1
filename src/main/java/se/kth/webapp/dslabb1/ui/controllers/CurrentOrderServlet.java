package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.io.IOException;


@WebServlet("/currentOrder")
public class CurrentOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        UserInfo customer = (UserInfo) session.getAttribute("CUSTOMER");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/currentOrder.jsp").forward(request, response);
    }
}
