package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.IUser;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.bo.services.UserService;

import java.io.IOException;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        IUser user = UserService.authenticateUser(email, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            if (user.getUserType() == UserType.CUSTOMER) {
                session.setAttribute("CUSTOMER", user);
            } else if (user.getUserType() == UserType.ADMIN) {
                session.setAttribute("ADMIN", user);
            } else if (user.getUserType() == UserType.WAREHOUSEWORKER) {
                session.setAttribute("WAREHOUSEWORKER", user);
            }
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            response.sendRedirect(request.getContextPath() + "/shop");
        } else {
            request.setAttribute("loginError", "Ogiltig e-post eller lösenord.");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            rd.forward(request, response);
        }
    }
}