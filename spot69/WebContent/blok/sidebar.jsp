<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.spot69.model.Utilisateur, com.spot69.model.Permissions, com.spot69.utils.PermissionChecker, com.spot69.dao.UtilisateurDAO" %>
<meta charset="UTF-8">

<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
if (currentUser != null) {
    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    Utilisateur freshUser = utilisateurDAO.findByIdWithPermissions(currentUser.getId());
    if (freshUser != null) {
        currentUser = freshUser;
    }
    session.setAttribute("user", currentUser);
}
%>

<!-- SIDEBAR -->
<aside class="sidebar-left border-right bg-white shadow" id="leftSidebar" data-simplebar>
    <a href="#" class="btn collapseSidebar toggle-btn d-lg-none text-muted ml-2 mt-3" data-toggle="toggle">
        <i class="fe fe-x"><span class="sr-only"></span></i>
    </a>
    <nav class="vertnav navbar navbar-light">
        <!-- nav bar -->
        <div class="w-100 mb-4 d-flex">
            <a class="navbar-brand mx-auto mt-2 flex-fill text-center" href="index.jsp">
                <img src="./image/d-logo.png" style="width: 60%;" alt="">
            </a>
        </div>
        <ul class="navbar-nav flex-fill w-100 mb-2">
            <li class="nav-item w-100">
                <a class="nav-link" href="index.jsp">
                    <i class="fe fe-home fe-16"></i>
                    <span class="ml-3 item-text">Tableau de bord</span>
                </a>
            </li>
        </ul>

        <!-- ==================== GESTION DE STOCK ==================== -->
        <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_STOCK) || 
              PermissionChecker.hasPermission(currentUser, Permissions.GESTION_PRODUITS) ||
              PermissionChecker.hasPermission(currentUser, Permissions.VOIR_LES_PRODUITS) ||
              PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FOURNISSEURS) ||
              PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FACTURES)) { %>
        <p class="text-muted nav-heading mt-4 mb-1">
            <span>Gestion de stock</span>
        </p>

        <ul class="navbar-nav flex-fill w-100 mb-2">
        
            <!-- Catégories, Rayons et Sous-catégories -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_RAYONS_CATEGORIES_SOUS_CATEGORIES) || 
                   PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_RAYONS_CATEGORIES_SOUS_CATEGORIES) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.GESTION_CATEGORIES_SOUS_CATEGORIES)) { %>
            <li class="nav-item dropdown">
                <a href="#catSouCat" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-clipboard fe-16"></i>
                    <span class="ml-3 item-text">Gestion des catégories</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="catSouCat">
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_RAYONS_CATEGORIES_SOUS_CATEGORIES)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="MenuServlet?action=liste-rayons">
                            <span class="ml-1 item-text">Rayons</span>
                        </a>
                    </li>
                    <% } %>
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_CATEGORIES_SOUS_CATEGORIES)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="MenuServlet?action=menu-c">
                            <span class="ml-1 item-text">Catégories</span>
                        </a>
                    </li>
                    <% } %>
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_CATEGORIES_SOUS_CATEGORIES)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="MenuServlet?action=menu-sc">
                            <span class="ml-1 item-text">Sous-catégories</span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>

            <!-- Produits -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_PRODUITS) || 
                   PermissionChecker.hasPermission(currentUser, Permissions.VOIR_LES_PRODUITS) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_PRODUITS)) { %>
            <li class="nav-item dropdown w-100">
                <a href="#produits" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-package fe-16"></i>
                    <span class="ml-3 item-text">Produits</span>
                </a>
                <ul class="collapse list-unstyled pl-4" id="produits">
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_LES_PRODUITS) ||
                           PermissionChecker.hasPermission(currentUser, Permissions.GESTION_PRODUITS)) { %>
                    <li class="nav-item w-100">
                        <a class="nav-link" href="ProduitServlet?action=lister">
                            <span class="ml-2 item-text">Liste des produits</span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>

            <!-- Approvisionnement -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FOURNISSEURS) || 
                   PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FACTURES) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_FOURNISSEURS) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_FACTURES)) { %>
            <li class="nav-item dropdown w-100">
                <a href="#fournisseurs" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-truck fe-16"></i>
                    <span class="ml-3 item-text">Approvisionnement</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="fournisseurs">
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FOURNISSEURS)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="FournisseurServlet?action=lister">
                            <span class="ml-1 item-text">Fournisseurs</span>
                        </a>
                    </li>
                    <% } %>
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FACTURES)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="FactureServlet?action=lister">
                            <span class="ml-1 item-text">Achats</span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>

            <!-- Inventaire -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_STOCK)) { %>
            <li class="nav-item dropdown">
                <a href="#inventaire" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-box fe-16"></i>
                    <span class="ml-3 item-text">Inventaire</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="inventaire">
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="MouvementStockServlet?action=lister">
                            <span class="ml-1 item-text">Mouvements de stock</span>
                        </a>
                    </li>
                </ul>
            </li>
            <% } %>
        </ul>
        <% } %>

        <!-- ==================== ADMINISTRATION ==================== -->
        <%
        if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES) || 
            PermissionChecker.hasPermission(currentUser, Permissions.GESTION_ROLES_ET_PERMISSIONS) ||
            PermissionChecker.hasPermission(currentUser, Permissions.GESTION_MENU_CATEGORIES) ||
            PermissionChecker.hasPermission(currentUser, Permissions.VOIR_LE_MENU)) {
        %>
        <p class="text-muted nav-heading mt-4 mb-1">
            <span>Administration</span>
        </p>
        <ul class="navbar-nav flex-fill w-100 mb-2">
            <!-- Gestion des utilisateurs -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_COMPTES)) { %>
            <li class="nav-item dropdown">
                <a href="#ui-elements" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-users fe-16"></i>
                    <span class="ml-3 item-text">Gestion d'utilisateur</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="ui-elements">
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="UtilisateurServlet?action=lister">
                            <span class="ml-1 item-text">Liste</span>
                        </a>
                    </li>
                </ul>
            </li>
            <% } %>
            
            <!-- Gestion du menu -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_STOCK) ){ %>
            <li class="nav-item dropdown">
                <a href="#menu" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-clipboard fe-16"></i>
                    <span class="ml-3 item-text">Gestion du menu</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="menu">
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_STOCK)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="MenuServlet?action=liste-plat">
                            <span class="ml-1 item-text">Menu</span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>
        </ul>
        <% } %>

        <!-- ==================== GESTION DE VENTE ==================== -->
        <% if (PermissionChecker.hasPermission(currentUser, Permissions.PLACER_DES_COMMANDES) || 
              PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMMANDES) || 
              PermissionChecker.hasPermission(currentUser, Permissions.VOIR_HISTORIQUE_COMMANDES) ||
              PermissionChecker.hasPermission(currentUser, Permissions.MODIFIER_STATUT_COMMANDES)) { %>
        <p class="text-muted nav-heading mt-4 mb-1">
            <span>Gestion de vente</span>
        </p>
        <ul class="navbar-nav flex-fill w-100 mb-2">
         
            <!-- Placer des commandes -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.PLACER_DES_COMMANDES)) { %>
            <li class="nav-item w-100">
                <a class="nav-link" href="MenuServlet?action=placer-commande">
                    <i class="fe fe-clipboard fe-16"></i>
                    <span class="ml-3 item-text">Placer des commandes</span>
                </a>
            </li>
            <% } %>
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_LE_MENU)) { %>
                    <li class="nav-item w-100">
                        <a class="nav-link" href="CommandeServlet?action=liste-commande-par-staff">
                         <i class="fe fe-clipboard fe-16"></i>
                            <span class="ml-3 item-text">Mes commandes</span>
                        </a>
                    </li>
                    <% } %>
            
            <!-- Rapport -->
            <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_HISTORIQUES_COMMANDES) || 
                   PermissionChecker.hasPermission(currentUser, Permissions.VOIR_RAPPORT_CAISSE) ||
                   PermissionChecker.hasPermission(currentUser, Permissions.VOIR_RAPPORT_PAR_PRODUITS)) { %>
            <li class="nav-item dropdown">
                <a href="#historique" data-toggle="collapse" aria-expanded="false" class="dropdown-toggle nav-link">
                    <i class="fe fe-clipboard fe-16"></i>
                    <span class="ml-3 item-text">Rapport</span>
                </a>
                <ul class="collapse list-unstyled pl-4 w-100" id="historique">
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_HISTORIQUES_COMMANDES)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="CommandeServlet?action=liste-toutes-commande">
                            <span class="ml-1 item-text">Historique des commandes</span>
                        </a>
                    </li>
                    <% } %>
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_RAPPORT_CAISSE)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="CommandeServlet?action=caissiere-commandes-cashed">
                            <span class="ml-1 item-text">Rapport par caissier</span>
                        </a>
                    </li>
                    <% } %>
                    <% if (PermissionChecker.hasPermission(currentUser, Permissions.VOIR_RAPPORT_PAR_PRODUITS)) { %>
                    <li class="nav-item">
                        <a class="nav-link pl-3" href="CommandeServlet?action=rapport-commandes-by-filtres">
                            <span class="ml-1 item-text">Rapport par produits</span>
                        </a>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>
        </ul>
        <% } %>

        <!-- ==================== CONFIGURATION ==================== -->
      <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_ROLES_ET_PERMISSIONS) ||
    		  PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES)) { %> 
        <p class="text-muted nav-heading mt-4 mb-1">
            <span>Configuration</span>
        </p>
        <ul class="navbar-nav flex-fill w-100 mb-2">
             <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_ROLES_ET_PERMISSIONS)) { %>
                    <li class="nav-item w-100">
                <a class="nav-link" href="RoleServlet?action=lister">
                    <i class="fe fe-shield fe-16"></i>
                    <span class="ml-3 item-text">Rôles</span>
                </a>
            </li>
             <% } %>
             <% if (PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES)) { %>
                    <li class="nav-item w-100">
                <a class="nav-link" href="TauxServlet?action=lister">
                    <i class="fe fe-shield fe-16"></i>
                    <span class="ml-3 item-text">Taux</span>
                </a>
            </li>
             <% } %>
        </ul>
       <% } %>
    </nav>
</aside>
<!-- SIDEBAR END -->