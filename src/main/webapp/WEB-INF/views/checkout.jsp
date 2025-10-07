<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Checkout</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin-bottom: 16px; }
        .nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; }
        .nav a { text-decoration: none; color: #007bff; }
        .nav a:hover { text-decoration: underline; }
        button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #007bff; color: white; }
        .btn-primary:hover { background: #1b3044 }
        .btn-success { background: #6e9877; color: white; }
        .text { font-weight: 600; color: #2b2b2b; }
        .product-list { width: 100%; max-width: 600px; margin: auto; border-collapse: collapse; }
        .product-list th, .product-list td { padding: 0.75rem 1rem; text-align: left; }
        .product-list th { background-color: #ffffff; color: #0062ff; }
        .summary { border: 2px solid #007bff; border-radius: 8px; padding: 16px; margin-top: 20px; }
    </style>
</head>
<body>

<div class="nav">
    <a href="${pageContext.request.contextPath}/cart">← Tillbaka till kundvagnen</a>
</div>

<div class="card">
    <div class="text">${email}</div>
    <div class="text">${name}</div>
    <div class="text">${address}</div>
</div>

<table class="product-list">
    <tr>
        <th>SKU</th>
        <th>Namn</th>
        <th>Kr</th>
        <th>Antal</th>
    </tr>
    <c:forEach var="item" items="${cartItems}">
        <tr>
            <th>${item.sku()}</th>
            <th>${item.productName()}</th>
            <th>${item.price()}</th>
            <th>${item.quantity()}</th>
        </tr>
    </c:forEach>
</table>

<div class="error">
    ${empty requestScope.errorMessage ? '' : requestScope.errorMessage}
</div>
<form method="post" action="${pageContext.request.contextPath}/checkout">
    <select name="paymentMethodSlot" required>
        <option value="">Välj Betalningsmetod</option>
        <c:if test="${not empty paymentMethod}">
            <option value="${paymentMethod}">${paymentMethod}</option>
        </c:if>
    </select>
    <input type="hidden" name="action" value="order"/>
    <button type="submit" class="btn-primary">Betala</button>
</form>

<c:if test="${empty paymentMethod}">
    <form method="post" action="${pageContext.request.contextPath}/checkout"
          style="display: inline; margin-right: 8px;">
        <input type="hidden" name="action" value="addPaymentMethod"/>
        <button type="submit" class="btn-success">Ändra betalningsmetod</button>
    </form>
</c:if>

<div class="summary">
    <h3>Checkout total:</h3>
    <p><strong>Antal varor: ${cartItemCount} st</strong></p>
    <p><strong>Totalt pris: ${cartTotal} kr</strong></p>
</div>
</body>
</html>