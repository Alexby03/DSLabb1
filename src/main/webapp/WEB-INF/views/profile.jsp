<%--
  Created by IntelliJ IDEA.
  User: BOTALEX
  Date: 2025-10-02
  Time: 16:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Redigera Profil</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .card { max-width: 420px; padding: 1.25rem; border: 1px solid #ddd; border-radius: 8px; }
        .error { color: rgba(193, 114, 133, 0.93); margin-top: 0.5rem; }
        .success { color: #649b64; margin-bottom: 1rem; }
        .nav-links { margin-bottom: 1rem; }
        .nav-links a { text-decoration: none; color: #007bff; margin-right: 1rem; }
        .nav-links a:hover { text-decoration: underline; }
        label { display: block; margin-top: 0.75rem; }
        input[type=text], input[type=email] { width: 100%; padding: 0.5rem; }
        button { margin-top: 1rem; padding: 0.5rem 1rem; }
    </style>
</head>
<header>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/shop">Shop</a>
    </div>
</header>
<body>
<div class="card">
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/shop">← Tillbaka till butik</a>
        <a href="${pageContext.request.contextPath}/orders">Mina ordrar</a>
        <a href="${pageContext.request.contextPath}/cart">Kundvagn</a>
        <a href="${pageContext.request.contextPath}/logout">Logga ut</a>
    </div>

    <div class="success">
        ${not empty requestScope.successMessage ? requestScope.successMessage : ''}
    </div>

    <div class="error">
        ${empty requestScope.errorMessage ? '' : requestScope.errorMessage}
    </div>

    <form method="post" action="${pageContext.request.contextPath}/profile">
        <h2>Redigera Profil</h2>

        <label for="email">E-post</label>
        <input type="email" id="email" name="email" value="${sessionScope.CUSTOMER.email}" required/>

        <label for="fullName">Fullständigt namn</label>
        <input type="text" id="fullName" name="fullName" value="${sessionScope.CUSTOMER.fullName}" required/>

        <label for="address">Adress</label>
        <input type="text" id="address" name="address" value="${sessionScope.CUSTOMER.address}"/>

        <button type="submit">Uppdatera Profil</button>
    </form>
</div>
</body>
</html>
