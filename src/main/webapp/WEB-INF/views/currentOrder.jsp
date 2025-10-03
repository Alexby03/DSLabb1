<%--
  Created by IntelliJ IDEA.
  User: BOTALEX
  Date: 2025-10-03
  Time: 15:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Logga in</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; text-align: center; }
        .success { color: #28a745; font-size: 2rem; margin: 2rem 0; }
        button { padding: 1rem 2rem; margin: 0.5rem; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #007bff; color: white; }
        .btn-secondary { background: #6c757d; color: white; }
    </style>
</head>
<body>

<div class="success">
    <h3>Grattis!  Din beställning är slutförd. </h3>
</div>

<a href="${pageContext.request.contextPath}/shop">Fortsätt handla</a>

<a href="${pageContext.request.contextPath}/profile">Min profil</a>
</body>
</html>