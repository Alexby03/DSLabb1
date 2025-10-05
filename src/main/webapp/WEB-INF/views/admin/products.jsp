<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Admin - Produkter</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 0; background: #f5f5f5; }
        .header { background: #2c3e50; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { margin: 0; font-size: 1.5rem; }
        .nav { display: flex; gap: 1.5rem; }
        .nav a { color: white; text-decoration: none; padding: 0.5rem 1rem; border-radius: 4px; }
        .nav a:hover { background: #34495e; }
        .container { max-width: 1400px; margin: 2rem auto; padding: 0 2rem; }
        .success { color: #155724; background: #d4edda; padding: 12px; border-radius: 4px; margin-bottom: 1rem; }
        .error { color: #721c24; background: #f8d7da; padding: 12px; border-radius: 4px; margin-bottom: 1rem; }
        .section { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 2rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 1rem; }
        label { display: block; margin-bottom: 0.25rem; font-weight: 500; }
        input, select, textarea { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px; }
        button { padding: 0.75rem 1.5rem; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary { background: #3498db; color: white; }
        .btn-success { background: #27ae60; color: white; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #ddd; }
        th { background: #f8f9fa; font-weight: 600; }
        .low-stock { color: #e74c3c; font-weight: bold; }
        .action-form { display: inline; margin-right: 0.5rem; }
    </style>
</head>
<body>
<div class="header">
    <h1>📦 Produkthantering</h1>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Ordrar</a>
        <a href="${pageContext.request.contextPath}/logout">Logga ut</a>
    </div>
</div>

<div class="container">
    <c:if test="${not empty successMessage}">
        <div class="success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="error">${errorMessage}</div>
    </c:if>

    <!-- Create Product Form -->
    <div class="section">
        <h2>Skapa Ny Produkt</h2>
        <form method="post" action="${pageContext.request.contextPath}/admin/products">
            <input type="hidden" name="action" value="create"/>

            <div class="form-row">
                <div>
                    <label for="sku">SKU *</label>
                    <input type="text" id="sku" name="sku" required />
                </div>
                <div>
                    <label for="name">Produktnamn *</label>
                    <input type="text" id="name" name="name" required />
                </div>
                <div>
                    <label for="category">Kategori *</label>
                    <select id="category" name="category" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}">${cat}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div>
                    <label for="price">Pris (kr) *</label>
                    <input type="number" step="0.01" id="price" name="price" required />
                </div>
                <div>
                    <label for="quantity">Lagersaldo *</label>
                    <input type="number" id="quantity" name="quantity" required />
                </div>
            </div>

            <div>
                <label for="description">Beskrivning</label>
                <textarea id="description" name="description" rows="3"></textarea>
            </div>

            <button type="submit" class="btn-success">Skapa Produkt</button>
        </form>
    </div>

    <!-- Product List -->
    <div class="section">
        <h2>Alla Produkter (${products.size()})</h2>
        <table>
            <thead>
            <tr>
                <th>SKU</th>
                <th>Namn</th>
                <th>Kategori</th>
                <th>Pris</th>
                <th>Lager</th>
                <th>Status</th>
                <th>Åtgärder</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.sku}</td>
                    <td>${product.name}</td>
                    <td>${product.category}</td>
                    <td>${product.price} kr</td>
                    <td class="${product.quantity < 10 ? 'low-stock' : ''}">${product.quantity}</td>
                    <td>${product.retired ? 'Utgått' : 'Aktiv'}</td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/products" class="action-form">
                            <input type="hidden" name="action" value="updateStock"/>
                            <input type="hidden" name="sku" value="${product.sku}"/>
                            <input type="number" name="quantity" placeholder="Lägg till" style="width: 60px; padding: 4px;" min="0"/>
                            <button type="submit" class="btn-primary" style="padding: 4px 8px;">+Lager</button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/admin/products" class="action-form">
                            <input type="hidden" name="action" value="updatePrice"/>
                            <input type="hidden" name="sku" value="${product.sku}"/>
                            <input type="number" step="0.01" name="price" placeholder="Nytt pris" style="width: 80px; padding: 4px;"/>
                            <button type="submit" class="btn-primary" style="padding: 4px 8px;">Ändra pris</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
