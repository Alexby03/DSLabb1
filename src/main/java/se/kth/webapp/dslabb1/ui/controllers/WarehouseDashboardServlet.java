package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.io.IOException;

@WebServlet("/warehouse/dashboard")
public class WarehouseDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("WAREHOUSEWORKER") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserInfo worker = (UserInfo) session.getAttribute("WAREHOUSEWORKER");
        request.setAttribute("worker", worker);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/warehouse/dashboard.jsp");
        rd.forward(request, response);
    }
}
