<%-- <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<jsp:include page="header.jsp" />

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="./css/login.css">

<!-- Formulaire de connexion -->
<div class="login-box">
	<div class="login-icon">
		<a href="#" class="logo"><img src="./image/e-logo.png" width="50"
			alt="logo"></a>
	</div>
	<h2>Connexion</h2>

	<form action="UtilisateurServlet?action=login" method="post"  enctype="multipart/form-data" accept-charset="UTF-8">
		<input type="text" name="whoislogin" value="ADM" hidden />
		<div class="input-group">
			<input type="text" name="username" placeholder="Nom d'utilisateur"
				required /> <i class="fe fe-16 fe-user text-black mb-0"></i>
		</div>
		<div class="input-group">
			<input type="password" name="password" placeholder="Mot de passe"
				required /> <i class="fe fe-16 fe-lock text-black mb-0"></i>
		</div>
		<button type="submit" class="login-btn">
			Login <i class="fe fe-16 fe-log-in text-white mb-0"></i>
		</button>

		<div class="register-link">
			<!--  Vous n’avez pas encore de compte ? <a href="register.jsp">Créez-en une</a> -->
		</div>
		<div class="login-options">
			<a href="#">Mot de passe oublié ?</a>
		</div>
	</form>
</div>
 --%>
 
 <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<%@ include file="header.jsp"%>

<div class="login-page-container">
    <div class="login-box standalone">
        <div class="login-icon">
            <a href="index.jsp" class="logo">
                <!-- Logo ajouté ici -->
                <img src="./images/e-logo.png" alt="Logo" style="max-width: 150px; height: auto;">
            </a>
        </div>
        <h2>Connectez-vous pour passer vos commandes et réserver !</h2>

        <form action="UtilisateurServlet?action=login" method="post">
            <input type="text" name="whoislogin" value="CLIENT" hidden />
            <input type="text" name="redirectTo" value="website" hidden/>
            <div class="input-group">
                <input type="text" name="username" placeholder="Nom d'utilisateur" required />
                <i class="fas fa-user"></i>
            </div>
            <div class="input-group">
                <input type="password" name="password" placeholder="Mot de passe" required />
                <i class="fas fa-lock"></i>
            </div>
            <button type="submit" class="login-btn">
                Login <i class="fas fa-sign-out-alt"></i>
            </button>
            <div class="login-options">
                <a href="#">Mot de passe oublié ?</a>
            </div>
           <!--  <div class="register-link">
                Vous n'avez pas encore de compte ? 
                <a href="register.jsp">Créez-en une</a>
            </div> -->
           
        </form>
        
        <!-- Section d'informations ajoutée ici -->
        <div class="login-info-section" style="margin-top: 30px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; text-align: center;">
            <h3 style="color: #333; margin-bottom: 15px;">Informations de contact</h3>
            <p style="margin: 8px 0; color: #555;">
                <strong>Nom :</strong> La divinité de Dieu
            </p>
            <p style="margin: 8px 0; color: #555;">
                <strong>Adresse :</strong> St Marc Rue Bonet
            </p>
            <p style="margin: 8px 0; color: #555;">
                <strong>Téléphone :</strong> 40388773
            </p>
        </div>
    </div>
</div>

<%
String toastMessage = (String) session.getAttribute("toastMessage");
String toastType = (String) session.getAttribute("toastType");

if (toastMessage != null && toastType != null) {
%>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        showToast("<%=toastMessage%>", "<%=toastType%>");
    });
</script>
<%
session.removeAttribute("toastMessage");
session.removeAttribute("toastType");
}
%>

<script src="./js/login.js"></script>