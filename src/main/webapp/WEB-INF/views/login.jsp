<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Logga in</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        .container { display: flex; gap: 2rem; flex-wrap: wrap; }
        .card { max-width: 420px; padding: 1.25rem; border: 1px solid #ddd; border-radius: 8px; }
        .error { color: rgba(193, 114, 133, 0.93); margin-top: 0.5rem; }
        label { display: block; margin-top: 0.75rem; }
        input[type="text"], input[type="password"], input[type="email"] { width: 100%; padding: 0.5rem; }
        button { margin-top: 1rem; padding: 0.5rem 1rem; cursor: pointer; }
    </style>
</head>
<body>
<h1>Välkommen till Webshop</h1>

<div class="container">
    <div class="card">
        <div class="error">${empty requestScope.loginError ? "" : requestScope.loginError}</div>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <h2>Logga in (Kund/Personal)</h2>
            <label for="email">E-post</label>
            <input type="text" id="email" name="email" required />

            <label for="password">Lösenord</label>
            <input type="password" id="password" name="password" required />

            <button type="submit">Logga in</button>
        </form>
    </div>

    <!-- Customer Registration -->
    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/register">
            <h2>Registrera dig (Kund)</h2>
            <label for="email">E-post</label>
            <input type="text" id="email" name="email" required />

            <label for="password">Lösenord</label>
            <input type="password" id="password" name="password" required />

            <label for="fullName">Hela ditt namn</label>
            <input type="text" id="fullName" name="fullName" required />

            <label for="address">Din adress</label>
            <input type="text" id="address" name="address" />

            <button type="submit">Registrera</button>
        </form>
    </div>
</div>
</body>
</html>
