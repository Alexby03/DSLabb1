<%--
  Created by IntelliJ IDEA.
  User: BOTALEX
  Date: 2025-10-03
  Time: 20:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Beställningsdetaljer</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin-bottom: 16px; }
        .nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; }
        .nav a { text-decoration: none; color: #007bff; }
        .nav a:hover { text-decoration: underline; }
        button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #007bff; color: white; }
        .btn-primary:hover { background: #1b3044; }
        .btn-danger { background: #d99aa0; color: white; }
        .btn-danger:hover { background: #c82333; }
        .text { font-weight: 600; color: #2b2b2b; }
        .product-list { width: 100%; max-width: 600px; margin: auto; border-collapse: collapse; }
        .product-list th, .product-list td { padding: 0.75rem 1rem; text-align: left; }
        .product-list th { background-color: #ffffff; color: #0062ff; }

        .order-header {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .order-info h2 { margin: 0; color: #333; }
        .order-status {
            font-size: 1.2rem;
            font-weight: bold;
            padding: 0.5rem 1rem;
            border-radius: 6px;
        }
        .status-PAID { background: #fff3cd; color: #856404; }
        .status-SHIPPED { background: #cce5ff; color: #004085; }
        .status-DELIVERED { background: #d4edda; color: #155724; }
        .status-CANCELED { background: #f8d7da; color: #721c24; }

        .success { background: #d4edda; color: #155724; padding: 1rem; border-radius: 4px; margin-bottom: 1rem; }
        .error { background: #f8d7da; color: #721c24; padding: 1rem; border-radius: 4px; margin-bottom: 1rem; }

        .order-summary {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 8px;
            margin-top: 1rem;
        }

        .action-buttons {
            margin-top: 1rem;
            display: flex;
            gap: 1rem;
        }

        .debug-info {
            background: #e2e3e5;
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
            font-family: monospace;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>

<div class="nav">
    <a href="${pageContext.request.contextPath}/orders">← Tillbaka till beställningar</a>
</div>



<!-- Success/Error Messages -->
<c:if test="${not empty successMessage}">
    <div class="success">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="error">${errorMessage}</div>
</c:if>

<!-- Order Header -->
<div class="order-header">
    <div class="order-info">
        <h2>Beställning #${order.orderId.toString().substring(0, 8)}</h2>
        <p>Datum: ${order.dateOfPurchase.toString().substring(0, 10)}</p>
    </div>
    <div class="order-status status-${order.orderStatus}">
        ${order.orderStatus}
    </div>
</div>

<!-- Customer Information -->
<div class="card">
    <h3>Leveransuppgifter</h3>
    <div class="text">${email}</div>
    <div class="text">${name}</div>
    <div class="text">${address}</div>
</div>

<!-- Order Items -->
<h3>Beställda varor</h3>
<table class="product-list">
    <tr>
        <th>SKU</th>
        <th>Namn</th>
        <th>Pris per st</th>
        <th>Antal</th>
        <th>Summa</th>
    </tr>
    <c:forEach var="item" items="${orderItems}">
        <tr>
            <td>${item.sku}</td>
            <td>${item.productName}</td>
            <td>${item.unitPrice} kr</td>
            <td>${item.quantity}</td>
            <td>${item.unitPrice * item.quantity} kr</td>
        </tr>
    </c:forEach>
</table>

<!-- Order Summary -->
<div class="order-summary">
    <h3>Beställningssammanfattning</h3>
    <p><strong>Totalt pris: ${orderTotal} kr</strong></p>
</div>

<!-- Action Buttons -->
<div class="action-buttons">
    <c:choose>
        <c:when test="${canCancel}">
            <form method="post" action="${pageContext.request.contextPath}/orders"
                  onsubmit="return confirm('Är du säker på att du vill avbeställa denna order?');">
                <input type="hidden" name="action" value="cancelOrder"/>
                <input type="hidden" name="orderId" value="${order.orderId}"/>
                <button type="submit" class="btn-danger">Avbeställ order</button>
            </form>
        </c:when>
        <c:otherwise>
            <button class="btn-primary" disabled>Kan inte avbeställas</button>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>