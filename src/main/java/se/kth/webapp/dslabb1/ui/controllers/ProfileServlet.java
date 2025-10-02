package se.kth.webapp.dslabb1.ui.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.services.UserService;

import java.io.IOException;

@WebServlet(name = "profileServlet", value = "/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // DEBUG: Print customer data to console
        System.out.println("=== DEBUG CUSTOMER DATA ===");
        System.out.println("Email: " + customer.getEmail());
        System.out.println("FullName: " + customer.getFullName());
        System.out.println("Address: " + customer.getAddress());
        System.out.println("========================");


        // Forward to profile edit page
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/profile.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get form parameters
        String newEmail = request.getParameter("email");
        String newFullName = request.getParameter("fullName");
        String newAddress = request.getParameter("address");
        String newPaymentMethod = request.getParameter("paymentMethod");

        // Update customer using your updateCustomer method
        Result result = UserService.updateCustomer(
                newEmail,
                newFullName,
                newAddress,
                null,           // null = keep existing password (don't update password in profile)
                customer,       // existing customer from session
                newPaymentMethod
        );

        if (result == Result.SUCCESS) {
            // Update session with new customer data
            Customer updatedCustomer = new Customer(
                    customer.getId(),
                    newEmail,
                    newAddress,
                    newFullName,
                    newPaymentMethod,
                    customer.isActive()
            );
            session.setAttribute("CUSTOMER", updatedCustomer);
            request.setAttribute("successMessage", "Profil uppdaterad framgångsrikt!");
        } else {
            request.setAttribute("errorMessage", "Kunde inte uppdatera profil. Försök igen.");
        }



        // Forward back to profile page with message
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/profile.jsp");
        rd.forward(request, response);
    }
}