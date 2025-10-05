<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Admin - Orderdetaljer</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
        .header { background: #333; color: white; padding: 15px; margin: -20px -20px 20px -20px; }
        .header h1 { margin: 0; }
        .nav { margin-top: 10px; }
        .nav a { color: white; text-decoration: none; margin-right: 15px; }
        .nav a:hover { text-decoration: underline; }
        .content { max-width: 1000px; background: white; padding: 20px; border-radius: 4px; }
        .success { color: #155724; background: #d4edda; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .error { color: #721c24; background: #f8d7da; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .info-section { background: #f8f9fa; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
        .info-section h3 { margin-top: 0; }
        .info-row { margin: 8px 0; }
        .info-row strong { display: inline-block; width: 150px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { text-align: left; padding: 10px; border-bottom: 1px solid #ddd; }
        th { background: #f8f9fa; }
        button, select { padding: 8px 15px; margin-right: 10px; border: 1px solid #ccc; border-radius: 4px; cursor: pointer; }
        button { background: #007bff; color: white; border: none; }
        button:hover { background: #0056b3; }
        .btn-danger { background: #dc3545; }
        .btn-danger:hover { background: #c82333; }
    </style>
</head>
<body>
<div class="header">
    <h1>Orderdetaljer</h1>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/admin/orders">← Tillbaka</a>
        <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/logout">Logga ut</a>
    </div>
</div>

<div class="content">
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="error">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <h2>Order #${order.orderId.toString().substring(0, 8)}</h2>

    <div class="info-section">
        <h3>Kundinformation</h3>
        <div class="info-row"><strong>Namn:</strong> ${customerName}</div>
        <div class="info-row"><strong>E-post:</strong> ${customerEmail}</div>
        <div class="info-row"><strong>Adress:</strong> ${customerAddress != null ? customerAddress : 'Ej angiven'}</div>
    </div>

    <div class="info-section">
        <h3>Orderinformation</h3>
        <div class="info-row"><strong>Order ID:</strong> ${order.orderId}</div>
        <div class="info-row"><strong>Datum:</strong> ${order.dateOfPurchase.toString().substring(0, 10)}</div>
        <div class="info-row"><strong>Status:</strong> ${order.orderStatus}</div>
        <div class="info-row"><strong>Totalt belopp:</strong> ${order.totalAmount} kr</div>
    </div>

    <h3>Produkter i ordern</h3>
    <table>
        <thead>
        <tr>
            <th>SKU</th>
            <th>Produktnamn</th>
            <th>Pris/st</th>
            <th>Antal</th>
            <th>Summa</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${orderItems}">
            <tr>
                <td>${item.sku}</td>
                <td>${item.productName}</td>
                <td>${item.unitPrice} kr</td>
                <td>${item.quantity}</td>
                <td>${item.subtotal()} kr</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${order.orderStatus != 'CANCELED' && order.orderStatus != 'DELIVERED'}">
        <h3>Hantera Order</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/orders" style="display: inline;">
            <input type="hidden" name="action" value="updateStatus"/>
            <input type="hidden" name="orderId" value="${order.orderId}"/>

            <select name="newStatus" required>
                <option value="">Välj ny status</option>
                <c:forEach var="status" items="${orderStatuses}">
                    <c:if test="${status != order.orderStatus}">
                        <option value="${status}">${status}</option>
                    </c:if>
                </c:forEach>
            </select>

            <button type="submit">Uppdatera Status</button>
        </form>

        <c:if test="${order.orderStatus == 'PAID'}">
            <form method="post" action="${pageContext.request.contextPath}/admin/orders" style="display: inline;">
                <input type="hidden" name="action" value="cancelOrder"/>
                <input type="hidden" name="orderId" value="${order.orderId}"/>
                <button type="submit" class="btn-danger" onclick="return confirm('Är du säker på att du vill avbryta denna order?')">
                    Avbryt Order
                </button>
            </form>
        </c:if>
    </c:if>
</div>
</body>
</html>
