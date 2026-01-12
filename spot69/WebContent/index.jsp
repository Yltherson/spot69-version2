<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<%@ include file="header.jsp"%>

<div class="login-page-container">
    <!-- Ajout du titre "La Divinité de Dieu" -->
    <div class="page-header" style="text-align: center; margin-top: 40px; margin-bottom: 20px;">
        <h1 style="color: var(--gold); font-size: 2.5rem; font-weight: bold; text-shadow: 2px 2px 4px rgba(0,0,0,0.3);">
            La Divinité de Dieu
        </h1>
        <p style="margin: 5px 0; font-size: 14px;">
            <i class="fas fa-map-marker-alt" style="margin-right: 8px; "></i>
            St Marc, Rue Bonet
        </p>
        <p style="margin: 5px 0; font-size: 14px;">
            <i class="fas fa-phone" style="margin-right: 8px; "></i>
            Téléphone : 40388773
        </p>
        <!-- <p style="color: #666; font-size: 1.2rem; margin-top: 10px;">
            Accédez à votre espace personnel
        </p> -->
    </div>
    
    
    <div class="login-box standalone" style="margin-top: 100px !important">
        <div class="login-icon">
			<a href="index.jsp" class="logo"><img src="image/d-logo.png" width=90></a>
		</div>
        <h2>Connexion</h2>

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

<!-- Footer -->
<footer style="
    padding-top: 100px;
    text-align: center;
    /* margin-top: 10px; */
    font-family: Arial, sans-serif;
    color: var(--gold);
">
    <div style="max-width: 800px; margin: 0 auto;">
        <!-- <h3 style="color: #2c3e50; margin-bottom: 15px; color: var(--gold);">La Divinité de Dieu</h3>
        <p style="margin: 5px 0; font-size: 14px;">
            <i class="fas fa-map-marker-alt" style="margin-right: 8px; "></i>
            St Marc, Rue Bonet
        </p>
        <p style="margin: 5px 0; font-size: 14px;">
            <i class="fas fa-phone" style="margin-right: 8px; "></i>
            Téléphone : 40388773
        </p> -->
        <!-- <p style="margin: 5px 0; font-size: 14px;">
            <i class="fas fa-envelope" style="margin-right: 8px;"></i>
            Email : contact@ladivinite.com
        </p> -->
        <div class="login-icon">
			<a href="index.jsp" class="logo"><img src="image/e-logo.png" width=90></a>
		</div>
        <p style="margin-top: 24px; font-size: 12px; color: #7f8c8d;">
            &copy; <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %> E-MANAGEMENT. Tous droits réservés.
        </p>
    </div>
</footer>