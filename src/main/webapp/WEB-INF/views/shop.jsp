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
        .nav a { text-decoration: none; color: #333; padding: 0.5rem 1rem; }
        .nav a:hover { background: #f0f0f0; border-radius: 4px; }
        .cart-link:hover { background: #0056b3 !important; }
        .cart-count { background: #dc3545; color: white; border-radius: 50%; padding: 0.25rem 0.5rem; font-size: 0.8rem; min-width: 1.5rem; text-align: center; }
        .grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap:16px; }
        .card { border:1px solid #ddd; border-radius:8px; padding:12px; }
        .card img { width:100%; height:140px; object-fit:cover; border-radius:6px; background:#f4f4f4; }
        .toolbar { display:flex; gap:12px; margin: 12px 0 20px; align-items:center; }
        input[type=text], select { padding:8px; }
        button { padding:8px 14px; border: none; border-radius: 4px; cursor: pointer; }
        .price { font-weight:600; margin-top:6px; color: #ff0000; }
        .add-to-cart-form { margin-top: 8px; display: flex; gap: 8px; align-items: center; }
        .add-to-cart-form input[type=number] { width: 60px; padding: 4px; }
        .add-to-cart-btn { background: #333333; color: white; border: none; padding: 6px 8px; border-radius: 4px; cursor: pointer; font-size: 0.8rem; }
        .add-to-cart-btn:hover { background: #218838; }
        .add-to-cart-btn:disabled { background: #ccc; cursor: not-allowed; }
        .out-of-stock { color: #dc3545; font-weight: bold; }
    </style>
</head>
<body>

<header>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/profile">Profil</a>
    </div>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/cart" class="cart-link">
            🛒 <span class="cart-count">${cartItemCount > 0 ? cartItemCount : 0}</span>
        </a>
    </div>
</header>
<div class="error">
    ${empty requestScope.errorMessage ? '' : requestScope.errorMessage}
</div>
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
        <div class="card">
            <div class="title">${p.name}</div>
            <div class="price">${p.price} kr</div>
            <div class="desc">${p.description}</div>

            <c:choose>
                <c:when test="${p.quantity > 0 && !p.retired}">
                    <form method="post" action="${pageContext.request.contextPath}/shop" class="add-to-cart-form">
                        <input type="hidden" name="action" value="addToCart"/>
                        <input type="hidden" name="sku" value="${p.sku}"/>
                        <input type="number" name="quantity" value="1" min="1" max="${p.quantity}"/>
                        <button type="submit" class="add-to-cart-btn">Lägg i kundvagn</button>
                    </form>
                    <div style="font-size: 0.8rem; color: #6c757d; margin-top: 4px;">
                        Lager: ${p.quantity} st
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="out-of-stock" style="margin-top: 8px;">
                            ${p.retired ? 'Utgått' : 'Slut i lager'}
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>

    <c:if test="${empty products}">
        <div style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: #6c757d;">
            Inga produkter matchar din sökning.
        </div>
    </c:if>
</section>

</body>
</html>
