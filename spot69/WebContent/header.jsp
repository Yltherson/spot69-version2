<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">


<html lang="ht">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>La Divinité de Dieu</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<link rel="icon" type="image/png" href="image/d-logo.png">

<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;500;600;700;800&display=swap"
	rel="stylesheet" />
	<!-- Swiper CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
	
<link rel="stylesheet" href="./css/index.css">

</head>
<style>
	/* .cart-button {
	    margin-left: 10px;
	    font-size: 20px;
	    color: var(--gold);
	    background-color: transparent;
	    border: none;
	    cursor: pointer;
	    position: relative;
	    text-decoration: none;
	}
	
	.cart-button:hover {
	    color: var(--gold-dark);
	} */
	
	.icon-phone{
	display: none;
    justify-content: center;
    align-items: center;
    gap: 22px
	}
/* 	.cart-count-mobile, .cart-count-pc{
		position: absolute;
    bottom: 19px;
    left: 85%;
    transform: translateX(-50%);
    background-color: var(--gold);
    color: white;
    font-size: 12px;
    /* padding: 0px 0px; */
/*     border-radius: 12px;
    font-weight: bold;
    min-width: 18px;
    text-align: center;
} */
.cart-button {
    margin-left: 10px;
    font-size: 20px;
	} */
</style>

<body>


<div id="toast" class="toast-message hidden">
	<span id="toast-text"></span> <span class="toast-close"
		onclick="hideToast()">×</span>
</div>



<!-- Formulaire de connexion -->
<div id="loginModal" class="modal" >
	<div class="login-box">
		<span class="close-login close">&times;</span>
		<div class="login-icon">
			<a href="index.jsp" class="logo"><img src="image/e-logo.png" width=40></a>
		</div>
		<h2>Connectez-vous pour passer vos commandes et réserver !</h2>

		<form action="UtilisateurServlet?action=login" method="post" >
		 <input type="text" name="whoislogin" value="CLIENT" hidden />
		 <input type="text" name="redirectTo" value="website"  hidden/>
			<div class="input-group">
				<input type="text" name="username" placeholder="Nom d'utilisateur" required />
				<i class="fas fa-user"></i>
			</div>
			<div class="input-group">
				<input type="password" name="password" placeholder="Mot de passe"
					required /> <i class="fas fa-lock"></i>
			</div>
			<button type="submit" class="login-btn">
				Login <i class="fas fa-sign-out-alt"></i>
			</button>
			<div class="login-options">

				<a href="#">Mot de passe oublié ?</a>

			</div>
			<div class="register-link">
				Vous n’avez pas encore de compte ? <a href="register.jsp">Créez-en
					une</a>
			</div>

		</form>
	</div>
</div>


<!-- Formulaire de création de compte -->
<div id="registerModal" class="modal">
	<div class="login-box">
		<span class="close-register close">&times;</span>

		<div class="login-icon">
			<a href="#" class="logo"><img src="./image/69.png" width="40"></a>
		</div>
		<h2>Création de compte !</h2>

		<form action="UtilisateurServlet?action=register" method="post" >
			<div class="input-group">
				<input type="text" name="nom" placeholder="Nom" required /> <i
					class="fas fa-user"></i>
			</div>
			<div class="input-group">
				<input type="text" name="prenom" placeholder="Prénom" required /> <i
					class="fas fa-user"></i>
			</div>
			<div class="input-group">
				<input type="email" name="email" placeholder="Email" required /> <i
					class="fas fa-envelope"></i>
			</div>
			<div class="input-group">
				<input type="text" name="username" placeholder="Nom d'utilisateur"
					required /> <i class="fas fa-user-circle"></i>
			</div>
			<div class="input-group">
				<input type="password" name="password" placeholder="Mot de passe"
					required /> <i class="fas fa-lock"></i>
			</div>

			<button type="submit" class="login-btn">Créer mon compte</button>

			<div class="register-link">
				Déjà inscrit ? <a href="#" id="backToLogin">Connectez-vous ici</a>
			</div>
		</form>
	</div>
</div>