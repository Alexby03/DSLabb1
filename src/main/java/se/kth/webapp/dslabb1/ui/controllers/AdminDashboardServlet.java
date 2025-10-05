package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.Admin;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ADMIN") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Admin admin = (Admin) session.getAttribute("ADMIN");
        request.setAttribute("admin", admin);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
        rd.forward(request, response);
    }
}
