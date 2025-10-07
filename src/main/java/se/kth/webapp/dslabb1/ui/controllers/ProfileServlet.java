package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.UserService;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.io.IOException;

@WebServlet(name = "profileServlet", value = "/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserInfo customer = (UserInfo) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/profile.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserInfo customer = (UserInfo) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        String newEmail = request.getParameter("email");
        String newFullName = request.getParameter("fullName");
        String newAddress = request.getParameter("address");
        String newPaymentMethod = request.getParameter("paymentMethod");

        Result result = UserService.updateCustomer(
                newEmail,
                newFullName,
                newAddress,
                null,
                customer,
                newPaymentMethod
        );

        if (result == Result.SUCCESS) {

            UserInfo updatedCustomer = new UserInfo(
                    customer.userId(),
                    newEmail,
                    newFullName,
                    customer.userType(),
                    customer.isActive(),
                    newAddress,
                    newPaymentMethod
            );
            session.setAttribute("CUSTOMER", updatedCustomer);
            request.setAttribute("successMessage", "Din profil har uppdaterats");
        } else {
            request.setAttribute("errorMessage", "Kunde inte uppdatera profil. Försök igen.");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/profile.jsp");
        rd.forward(request, response);
    }
}