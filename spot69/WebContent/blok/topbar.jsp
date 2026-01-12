<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">


<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
%>
    
<!DOCTYPE html>
   <nav class="topnav navbar navbar-light">
        <button type="button" class="navbar-toggler text-muted mt-2 p-0 mr-3 collapseSidebar">
          <i class="fe fe-menu navbar-toggler-icon"></i>
        </button>
        <!-- <form class="form-inline mr-auto searchform text-muted">
          <input class="form-control mr-sm-2 bg-transparent border-0 pl-4 text-muted" type="search" placeholder="Type something..." aria-label="Search">
        </form> -->
        <ul class="nav">
          <li class="nav-item">
           <!--  <a class="nav-link text-muted my-2" href="#" id="modeSwitcher" data-mode="dark">
              <i class="fe fe-sun fe-16"></i>
            </a> -->
          </li>
          <li class="nav-item">
            <a class="nav-link text-muted my-2" href="./#" data-toggle="modal" data-target=".modal-shortcut">
              <!-- <span class="fe fe-grid fe-16"></span> -->
            </a>
          </li>
          <li class="nav-item nav-notif">
            <!-- <a class="nav-link text-muted my-2" href="./#" data-toggle="modal" data-target=".modal-notif">
              <span class="fe fe-bell fe-16"></span>
              <span class="dot dot-md bg-success"></span> -->
            </a>
          </li>
          <li class="nav-item dropdown">
			  <a class="nav-link dropdown-toggle text-muted pr-0 d-flex align-items-center gap-2" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			 <%
    if (username != null && !username.isEmpty()) {
%>
    <span class="mr-2"><%= username %></span>
<%
    }
%>

			    <span class="avatar avatar-sm">
			      <img src="default/images/user.webp" alt="..." class="avatar-img rounded-circle" style="width:32px; height:32px;">
			    </span>
			  </a>
			
			 <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
			  <a class="dropdown-item d-flex align-items-center" href="#">
			    <i class="fe fe-user fe-16 mr-2"></i> Profile
			  </a>
			  <a class="dropdown-item d-flex align-items-center" href="UtilisateurServlet?action=logout&who=ADM">
			    <i class="fe fe-log-out fe-16 mr-2"></i> Déconnexion
			  </a>
			  <a class="dropdown-item d-flex align-items-center" href="<%=request.getContextPath()%>/index.jsp">
			    <i class="fe fe-home fe-16 mr-2"></i> Acceder au site
			  </a>
			  
			</div>

			</li>

        </ul>
      </nav>
      <%-- Dans header.jsp ou dans une page template --%>
<script>
// Script pour maintenir la session active
(function keepSessionAlive() {
    // Ne fonctionne que si l'utilisateur est connecté
    if (document.cookie.indexOf('JSESSIONID') !== -1) {
        // Toutes les 30 minutes, effectuer une requête légère au serveur
        setInterval(function() {
            fetch('${pageContext.request.contextPath}/KeepAliveServlet', {
                method: 'GET',
                credentials: 'include' // Inclut les cookies de session
            }).then(function(response) {
                return response.json();
            }).then(function(data) {
                if (data.status === 'no_session') {
                    console.log('Session expirée, redirection...');
                    // Optionnel : rediriger vers la page de login
                    // window.location.href = '${pageContext.request.contextPath}/index.jsp';
                }
            }).catch(function(error) {
                console.log('Erreur de keep-alive:', error);
            });
        }, 30 * 60 * 1000); // 30 minutes
        
        // Garder la session active lors des interactions utilisateur
        document.addEventListener('click', function() {
            fetch('${pageContext.request.contextPath}/KeepAliveServlet', {
                method: 'POST',
                credentials: 'include'
            }).catch(function() {
                // Ignorer les erreurs silencieusement
            });
        }, { passive: true });
    }
})();
</script>