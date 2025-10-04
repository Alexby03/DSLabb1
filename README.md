<h1>Web Shop Project</h1>

**Overview**

This Web Shop MVP is a Java-based web shop application built using the three-tier architecture. 
In this project we try to demonstrate a clear separation between the presentation layer (servlets and JSP files), 
the business logic layer (domain models and services), and the data access layer (DAOs 
for database interaction, as well as the database manager). The application supports user registration, product browsing, 
cart management, order placement, and order history viewing/cancellation.

<h3>Three-Tier Architecture</h3>

1. Presentation Layer

    ```Servlets``` (e.g., OrderServlet) handle HTTP requests, manage sessions, and control navigation.

    ```JSP pages``` render dynamic HTML using JSTL and EL, displaying data passed from servlets.

2. Business Logic Layer

    ```Domain models``` (e.g., Product, Order, Item, Cart, CartItem, Customer) represent the core business entities.

    ```Service classes``` (e.g., OrderService) encapsulate business rules, orchestrate DAOs, and manage transactions.

3. Data Access Layer

    ```DAO classes``` (e.g., ProductDAO, OrderDAO, ItemDAO, CartDAO, UserDAO) interact directly with the database using JDBC, mapping SQL results to domain objects.

- The servlet never queries the database directly; it calls a service, which in turn uses DAOs. The service returns domain objects, which the servlet passes to the JSP for rendering.
