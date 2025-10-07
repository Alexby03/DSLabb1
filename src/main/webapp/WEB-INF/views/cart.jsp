<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Kundvagn</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin-bottom: 16px; }
        .nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; }
        .nav a { text-decoration: none; color: #007bff; }
        .nav a:hover { text-decoration: underline; }
        button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #007bff; color: white; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-success { background: #28a745; color: white; }
        .price { font-weight: 600; color: #28a745; }
        .quantity-controls { display: flex; gap: 8px; align-items: center; margin: 8px 0; }
        .qty-btn { width: 30px; height: 30px; }
        .success { color: #155724; background: #d4edda; padding: 8px; border-radius: 4px; margin-bottom: 1rem; }
        .error { color: #721c24; background: #f8d7da; padding: 8px; border-radius: 4px; margin-bottom: 1rem; }
        .cart-summary { border: 2px solid #007bff; border-radius: 8px; padding: 16px; margin-top: 20px; }
    </style>
</head>
<body>

<div class="nav">
    <a href="${pageContext.request.contextPath}/shop">← Tillbaka till butik</a>
    <a href="${pageContext.request.contextPath}/profile">Profil</a>
</div>

<h1>🛒 Kundvagn</h1>

<c:if test="${not empty successMessage}">
    <div class="success">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="error">${errorMessage}</div>
</c:if>

<c:if test="${isEmpty}">
    <div class="empty-cart">
        <h2>Din kundvagn är tom</h2>
        <p>Lägg till produkter från butiken!</p>
        <a href="${pageContext.request.contextPath}/shop">
            <button class="btn-primary">Fortsätt handla</button>
        </a>
    </div>
</c:if>


<c:if test="${not isEmpty}">
    <c:forEach var="item" items="${cartItems}">
        <div class="card">
            <h3>${item.productName()}</h3>
            <p>SKU: ${item.sku()}</p>
            <p class="price">${item.price()} kr/st</p>

            <div class="quantity-controls">
                <form method="post" action="${pageContext.request.contextPath}/cart" style="display: inline;">
                    <input type="hidden" name="action" value="updateQuantity"/>
                    <input type="hidden" name="sku" value="${item.sku()}"/>
                    <input type="hidden" name="quantity" value="${item.quantity() - 1}"/>
                    <button type="submit" class="qty-btn btn-primary" ${item.quantity() <= 1 ? 'disabled' : ''}>-</button>
                </form>

                <span><strong>${item.quantity()} st</strong></span>

                <form method="post" action="${pageContext.request.contextPath}/cart" style="display: inline;">
                    <input type="hidden" name="action" value="updateQuantity"/>
                    <input type="hidden" name="sku" value="${item.sku()}"/>
                    <input type="hidden" name="quantity" value="${item.quantity() + 1}"/>
                    <button type="submit" class="qty-btn btn-primary">+</button>
                </form>
            </div>

            <p><strong>Summa: ${item.getSubtotal()} kr</strong></p>

            <form method="post" action="${pageContext.request.contextPath}/cart" style="display: inline;">
                <input type="hidden" name="action" value="removeItem"/>
                <input type="hidden" name="sku" value="${item.sku()}"/>
                <button type="submit" class="btn-danger"
                        onclick="return confirm('Ta bort ${item.productName()}?')">
                    Ta bort
                </button>
            </form>
        </div>
    </c:forEach>

    <div class="cart-summary">
        <h3>Kundvagn total:</h3>
        <p><strong>Antal varor: ${cartItemCount} st</strong></p>
        <p><strong>Totalt pris: ${cartTotal} kr</strong></p>

        <div style="margin-top: 16px;">
            <form method="post" action="${pageContext.request.contextPath}/cart"
                  style="display: inline; margin-right: 8px;">
                <input type="hidden" name="action" value="checkout"/>
                <button type="submit" class="btn-success">Gå till kassan</button>
            </form>

            <form method="post" action="${pageContext.request.contextPath}/cart"
                  style="display: inline; margin-right: 8px;">
                <input type="hidden" name="action" value="clearCart"/>
                <button type="submit" class="btn-danger"
                        onclick="return confirm('Töm hela kundvagnen?')">
                    Töm kundvagn
                </button>
            </form>

            <a href="${pageContext.request.contextPath}/shop">
                <button class="btn-primary">Fortsätt handla</button>
            </a>
        </div>
    </div>
</c:if>
</body>
</html>