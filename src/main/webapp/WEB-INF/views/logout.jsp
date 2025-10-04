<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="sv">
<head>
    <meta charset="UTF-8"/>
    <title>Logga in</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; }
        label { display: block; margin-top: 0.75rem; }
        input[type=text], input[type=password] { width: 100%; padding: 0.5rem; }
        button { margin-top: 1rem; padding: 0.5rem 1rem; }
    </style>
</head>
<body>
<p>Du har loggat ut. Tryck <a href="${pageContext.request.contextPath}/login">här</a> för att logga in.</p>
</body>
</html>
