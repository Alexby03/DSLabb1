<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Admin Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
        .header { background: #333; color: white; padding: 15px; margin: -20px -20px 20px -20px; }
        .header h1 { margin: 0; }
        .nav { margin-top: 10px; }
        .nav a { color: white; text-decoration: none; margin-right: 15px; }
        .nav a:hover { text-decoration: underline; }
        .content { max-width: 900px; }
        h2 { color: #333; }
        .button { display: inline-block; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; margin-right: 10px; margin-top: 10px; }
        .button:hover { background: #0056b3; }
    </style>
</head>
<body>
<div class="header">
    <h1>Admin Dashboard</h1>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/logout">Logga ut</a>
    </div>
</div>

<div class="content">
    <h2>Admin Dashboard</h2>

    <div>
        <a href="${pageContext.request.contextPath}/admin/products" class="button">Hantera Produkter</a>
        <a href="${pageContext.request.contextPath}/admin/orders" class="button">Visa Ordrar</a>
    </div>
</div>
</body>
</html>
