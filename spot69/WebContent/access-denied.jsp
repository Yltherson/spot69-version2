<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accès Refusé</title>
    <style>
        :root {
            --gold: #daaf5a;
            --gold-dark: #b08c3e;
            --dark: #121212;
            --darker: #0a0a0a;
            --light: #ffffff;
            --gray: #2d2d2d;
            --light-gray: #aaaaaa;
        }

        body {
            margin: 0;
            padding: 0;
            background-color: var(--dark);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: var(--light-gray);
            text-align: center;
        }

        .container {
            max-width: 500px;
            margin: auto;
            background-color: var(--gray);
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.5);
        }

        h1 {
            font-size: 96px;
            margin: 0;
            color: var(--gold);
        }

        h2 {
            font-size: 32px;
            margin: 10px 0;
            color: var(--light);
        }

        p {
            font-size: 16px;
            color: var(--light-gray);
        }

        .btn {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 30px;
            font-size: 16px;
            background-color: var(--gold);
            color: var(--dark);
            border: none;
            border-radius: 6px;
            text-decoration: none;
            transition: background-color 0.3s ease;
        }

        .btn:hover {
            background-color: var(--gold-dark);
            color: var(--light);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>403</h1>
        <h2>Accès Refusé</h2>
        <p>Vous n'avez pas les autorisations nécessaires pour accéder à cette page.</p>
        <a href="<%=request.getContextPath()%>/index.jsp" class="btn">Retour à l'accueil</a>
    </div>
</body>
</html>
