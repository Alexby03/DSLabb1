<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Lager - Ordrar</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 0; background: #f5f5f5; }
        .header { background: #34495e; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { margin: 0; font-size: 1.5rem; }
        .nav { display: flex; gap: 1.5rem; }
        .nav a { color: white; text-decoration: none; padding: 0.5rem 1rem; border-radius: 4px; }
        .nav a:hover { background: #465a6b; }
        .container { max-width: 1400px; margin: 2rem auto; padding: 0 2rem; }
        .section { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 2rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .filter-bar { display: flex; gap: 1rem; margin-bottom: 1.5rem; align-items: center; }
        .filter-bar select { padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #ddd; }
        th { background: #f8f9fa; font-weight: 600; }
        .status-PAID { color: #ff9800; font-weight: bold; }
        .status-SHIPPED { color: #2196f3; font-weight: bold; }
        .status-DELIVERED { color: #4caf50; font-weight: bold; }
        .status-CANCELED { color: #f44336; font-weight: bold; }
        .btn { padding: 0.5rem 1rem; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; }
        .btn-primary { background: #3498db; color: white; }
    </style>
</head>
<body>
<div class="header">
    <h1>Orderhantering</h1>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/warehouse/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/logout">Logga ut</a>
    </div>
</div>

<div class="container">
    <div class="section">
        <h2>Ordrar</h2>

        <div class="filter-bar">
            <label for="statusFilter">Filtrera efter status:</label>
            <select id="statusFilter" onchange="window.location.href='${pageContext.request.contextPath}/warehouse/orders?status=' + this.value">
                <option value="ALL" ${currentFilter == 'ALL' ? 'selected' : ''}>Alla</option>
                <c:forEach var="status" items="${orderStatuses}">
                    <option value="${status}" ${currentFilter == status.toString() ? 'selected' : ''}>${status}</option>
                </c:forEach>
            </select>
        </div>

        <table>
            <thead>
            <tr>
                <th>Order ID</th>
                <th>Datum</th>
                <th>Belopp</th>
                <th>Status</th>
                <th>Åtgärd</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="order" items="${orders}">
                <tr>
                    <td>${order.orderId().toString().substring(0, 8)}...</td>
                    <td>${order.dateOfPurchase()}</td>
                    <td>${order.totalAmount()} kr</td>
                    <td class="status-${order.orderStatus()}">${order.orderStatus()}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/warehouse/orders?orderId=${order.orderId()}" class="btn btn-primary">Visa detaljer</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
