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

<h3>Domain models</h3>

```Product:``` Describes a sellable catalog entry (SKU, name, description, price, stock) that cart and order line items reference as the source of truth for product identity.

```Customer:``` Represents the shopper account that owns a shopping cart and order history in the online shopping domain.

```Cart:``` A mutable container of prospective purchases that holds product variants as line items while the shopper is still browsing and before checkout.

```CartItem:``` A line item in the cart that links to a specific product or variant and tracks the current quantity the shopper intends to buy.

```Order:``` A checkout result that captures a durable snapshot of the transaction, including items, pricing, customer and addresses, payments, and order state.

```Item:``` An order line item that records the purchased product, unit price, and quantity as they were at the moment of purchase for historical accuracy.

```OrderStatus:``` Encodes the lifecycle of an order after purchase (for example Paid, Shipped, Delivered, or Canceled) to support fulfillment and after‑sales flows.

<h2>Item vs CartItem</h2>

- A CartItem represents the quantity of a product currently in the shopper’s cart and can be freely added, removed, or edited until checkout.

- An Item represents the quantity of a product that has been purchased and is recorded on the Order with prices and details fixed at the time of purchase.

- During checkout, the platform converts a Cart into an Order and copies its line items into Items on the Order.
