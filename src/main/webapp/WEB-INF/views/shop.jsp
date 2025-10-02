<%--
  Created by IntelliJ IDEA.
  User: BOTALEX
  Date: 2025-10-02
  Time: 16:08
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- JSTL 1.2/2.0 core -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Shop</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        header { display:flex; align-items:center; justify-content:space-between; margin-bottom:1rem; }
        .nav { display:flex; gap:1rem; align-items:center; }
        .grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap:16px; }
        .card { border:1px solid #ddd; border-radius:8px; padding:12px; }
        .card img { width:100%; height:140px; object-fit:cover; border-radius:6px; background:#f4f4f4; }
        .toolbar { display:flex; gap:12px; margin: 12px 0 20px; align-items:center; }
        input[type=text], select { padding:8px; }
        button { padding:8px 14px; }
        .price { font-weight:600; margin-top:6px; }
    </style>
</head>
<body>

<header>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/profile">Profil</a>
    </div>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/cart">Kundvagn</a>
    </div>
</header>

<section class="toolbar">
    <form method="get" action="${pageContext.request.contextPath}/shop" style="display:flex; gap:12px; align-items:center;">
        <input type="text" name="searchText" placeholder="Sök produkter" value="${searchText}"/>
        <select name="category">
            <option value="">Alla kategorier</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
            </c:forEach>
        </select>
        <button type="submit">Sök</button>
    </form>
</section>

<section class="grid">
    <c:forEach var="p" items="${products}">
        <a href="${pageContext.request.contextPath}/product/${p.sku}" style="text-decoration:none; color:inherit;">
            <div class="card" style="cursor:pointer;">
                <!-- befintlig bild/titel/pris -->
                <div class="title">${p.name}</div>
                <div class="price">${p.price} kr</div>
                <div class="desc">${p.description}</div>
            </div>
        </a>
    </c:forEach>


    <c:if test="${empty products}">
        Inga produkter matchar din sökning.
    </c:if>
</section>

</body>
</html>
