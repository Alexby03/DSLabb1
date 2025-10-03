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
<p>Du har loggat ut. Tryck <a href="${pageContext.request.contextPath}/login">här</a> för att logga in.</p>
</body>
</html>
