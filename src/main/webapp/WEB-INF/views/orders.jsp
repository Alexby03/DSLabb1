<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Mina ordrar</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; }
        .nav a { text-decoration: none; color: #007bff; }
        .nav a:hover { text-decoration: underline; }
        button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #007bff; color: white; }
        .btn-primary:hover { background: #1b3044 }
        .product-list th, .product-list td { padding: 0.75rem 1rem; text-align: left; }
        .product-list th { background-color: #ffffff; color: #0062ff; }
        .no-orders { text-align: center; padding: 3rem; color: #6c757d; }
        .order-card {
            background: white;
            border: 2px solid #ddd;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .order-card:hover { border-color: #007bff; box-shadow: 0 4px 8px rgba(0,123,255,0.2); }
        .order-header { display: flex; justify-content: space-between; align-items: flex-start; }
        .order-info { flex: 1; }
        .order-id { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 8px; }
        .order-date { color: #666; font-size: 16px; }
        .order-status { font-size: 16px; font-weight: bold; color: #333; text-align: right; }
        .status-PAID { color: #ff9800; }
        .status-SHIPPED { color: #2196f3; }
        .status-DELIVERED { color: #4caf50; }
        .status-CANCELED { color: #f44336; }
        .error { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 8px; margin-bottom: 20px; }
        .click-hint { font-size: 0.9rem; color: #007bff; margin-top: 5px; }
    </style>
</head>
<body>
<div class="nav">
    <a href="${pageContext.request.contextPath}/profile">Bakåt</a>
</div>

<c:if test="${not empty errorMessage}">
    <div class="error">${errorMessage}</div>
</c:if>

<c:choose>
    <c:when test="${hasOrder}">
        <h2>Dina beställningar (${orderCount} st)</h2>
        <c:forEach var="order" items="${orders}" varStatus="status">
            <a href="${pageContext.request.contextPath}/orders?orderId=${order.orderId()}"
               style="text-decoration: none; color: inherit;">
                <div class="order-card">
                    <div class="order-header">
                        <div class="order-info">
                            <div class="order-id">
                                order id: ${order.orderId().toString().substring(0, 8)}...
                            </div>
                            <div class="order-date">
                                date: ${order.dateOfPurchase().toString().substring(0, 10)}
                            </div>
                            <div class="click-hint">
                                Klicka för detaljer
                            </div>
                        </div>
                        <div class="order-status status-${order.orderStatus()}">
                            Status: ${order.orderStatus()}
                        </div>
                    </div>
                </div>
            </a>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="no-orders">
            <h2>Inga ordrar än...</h2>
            <p>Här kommer du se dina ordrar när du beställt något.</p>
            <a href="${pageContext.request.contextPath}/shop">
                <button class="btn-primary">Fortsätt handla</button>
            </a>
        </div>
    </c:otherwise>
</c:choose>
</body>
</html>