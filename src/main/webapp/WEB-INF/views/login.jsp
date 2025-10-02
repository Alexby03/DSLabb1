<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Logga in</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .card { max-width: 420px; padding: 1.25rem; border: 1px solid #ddd; border-radius: 8px; }
        .error { color: rgba(193, 114, 133, 0.93); margin-top: 0.5rem; }
        .success { color: #649b64; margin-bottom: 1rem; }
        label { display: block; margin-top: 0.75rem; }
        input[type=text], input[type=password] { width: 100%; padding: 0.5rem; }
        button { margin-top: 1rem; padding: 0.5rem 1rem; }
    </style>
</head>
<body>
<div class="card">
    <div class="success">
        ${sessionScope.customer != null ? 'Välkommen, ' : ''}${sessionScope.customer != null ? sessionScope.customer.fullName : '' }
    </div>

    <div class="error">
        ${empty requestScope.loginError ? '' : requestScope.loginError}
    </div>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <h2>Logga in</h2>
        <label for="email">E-post</label>
        <input type="text" id="email" name="email" required/>

        <label for="password">Lösenord</label>
        <input type="password" id="password" name="password" required/>

        <button type="submit">Logga in</button>
    </form>
    <form method="post" action="${pageContext.request.contextPath}/register">
        <h2>Registrera dig</h2>
        <label for="email">E-post</label>
        <input type="text" id="email" name="email" required/>

        <label for="password">Lösenord</label>
        <input type="password" id="password" name="password" required/>

        <label for="fullName">Hela ditt namn</label>
        <input type="text" id="fullName" name="fullName" required/>

        <label for="address">Din adress</label>
        <input type="text" id="address" name="address"/>

        <button type="submit">Registrera</button>
    </form>
</div>
</body>
</html>
