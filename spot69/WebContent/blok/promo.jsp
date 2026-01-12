<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.model.Promo,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

    <style>
        /* Styles additionnels pour les promotions */
        .video-thumbnail video {
            object-fit: cover;
            width: 100%;
            height: 100%;
        }
        
        .media-preview {
            max-width: 120px;
            max-height: 80px;
            overflow: hidden;
            border-radius: 4px;
        }
        
        .badge-light {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
        }
        
        /* Correction pour les icônes Fe */
        .fe-12 { font-size: 12px; }
        .fe-16 { font-size: 16px; }
        .fe-24 { font-size: 24px; }
        .fe-32 { font-size: 32px; }
        .fe-48 { font-size: 48px; }
        
        /* Styles pour le gradient preview */
        #gradientPreview, #editGradientPreview {
            background: linear-gradient(135deg, #8B5CF6, #EC4899, #8B5CF6);
            transition: background 0.3s ease;
        }
        
        /* Styles pour les modales */
        .modal-content {
            border-radius: 0.5rem;
        }
        
        .nav-tabs .nav-link {
            border: 1px solid transparent;
            border-top-left-radius: 0.25rem;
            border-top-right-radius: 0.25rem;
        }
        
        .nav-tabs .nav-link.active {
            color: #495057;
            background-color: #fff;
            border-color: #dee2e6 #dee2e6 #fff;
        }
    </style>
    
    <%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
// Utilisez les bonnes permissions pour les promotions
boolean canEditPromos = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
boolean canDeletePromos = canEditPromos && PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
boolean canAddPromos = canEditPromos;

List<Promo> promos = (List<Promo>) request.getAttribute("promos");
String ctx = request.getContextPath();

// Valeurs par défaut pour les statistiques
int totalPromosActives = 0;
int totalPromosVideo = 0;
int totalPromosImage = 0;
int totalPromosProgrammees = 0;

if (promos != null) {
    // Utilisation de boucles traditionnelles au lieu de lambdas pour compatibilité
    for (Promo p : promos) {
        // Promos actives
        if ("actif".equals(p.getStatut())) {
            totalPromosActives++;
        }
        
        // Promos vidéo
        if ("video".equalsIgnoreCase(p.getTypeContenu())) {
            totalPromosVideo++;
        }
        
        // Promos images
        if ("image".equalsIgnoreCase(p.getTypeContenu())) {
            totalPromosImage++;
        }
        
        // Promos programmées
        if (p.getDateDebut() != null && p.getDateFin() != null) {
            totalPromosProgrammees++;
        }
    }
}
%>

<!DOCTYPE html>
<body>
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<jsp:include page="header.jsp" />


<main role="main" class="main-content">
    <div class="container-fluid">
        <div class="row justify-content-center">
            <div class="col-12">
                <div class="row align-items-center mb-4">
                    <div class="col">
                        <h2 class="h5 page-title">
                            <i class="fe fe-video fe-24 text-primary mr-2"></i>
                            Gestion des Promotions
                        </h2>
                        <p class="text-muted mb-0">Gérez les promotions affichées dans le carrousel</p>
                    </div>
                    <div class="col-auto">
                        <% if (canAddPromos) { %>
                        <button type="button" class="btn btn-primary" data-toggle="modal" data-target=".modal-add-promo">
                            <i class="fe fe-plus fe-16 mr-2"></i> Nouvelle promotion
                        </button>
                        <% } %>
                    </div>
                </div>
                
                <!-- Statistiques -->
                <div class="row">
                    <!-- Total des promos actives -->
                    <div class="col-md-3 mb-4">
                        <div class="card shadow-sm border-0">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0"><%= totalPromosActives %></span>
                                        <p class="small text-muted mb-0">Promos actives</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-24 fe-play-circle text-success"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Promos vidéo -->
                    <div class="col-md-3 mb-4">
                        <div class="card shadow-sm border-0">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0 text-primary"><%= totalPromosVideo %></span>
                                        <p class="small text-muted mb-0">Vidéos</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-24 fe-film text-primary"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Promos images -->
                    <div class="col-md-3 mb-4">
                        <div class="card shadow-sm border-0">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0 text-info"><%= totalPromosImage %></span>
                                        <p class="small text-muted mb-0">Images</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-24 fe-image text-info"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Promos programmées -->
                    <div class="col-md-3 mb-4">
                        <div class="card shadow-sm border-0">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0 text-warning"><%= totalPromosProgrammees %></span>
                                        <p class="small text-muted mb-0">Programmées</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-24 fe-calendar text-warning"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Tableau des promos -->
                <div class="card shadow">
                    <div class="card-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="card-title mb-0">
                                <i class="fe fe-list fe-16 mr-2"></i>
                                Liste des promotions
                            </h5>
                            
                            <!-- Filtres -->
                            <form id="filterForm" method="get" action="PromoServlet" class="form-inline">
                                <input type="hidden" name="action" value="lister">
                                <div class="form-group mr-2">
                                    <select class="form-control form-control-sm" id="filterStatut" name="statut">
                                        <option value="">Tous les statuts</option>
                                        <option value="actif" <%= "actif".equals(request.getParameter("statut")) ? "selected" : "" %>>Actif</option>
                                        <option value="inactif" <%= "inactif".equals(request.getParameter("statut")) ? "selected" : "" %>>Inactif</option>
                                        <option value="supprime" <%= "supprime".equals(request.getParameter("statut")) ? "selected" : "" %>>Supprimé</option>
                                    </select>
                                </div>
                                <div class="form-group mr-2">
                                    <select class="form-control form-control-sm" id="filterType" name="type">
                                        <option value="">Tous les types</option>
                                        <option value="video" <%= "video".equals(request.getParameter("type")) ? "selected" : "" %>>Vidéo</option>
                                        <option value="image" <%= "image".equals(request.getParameter("type")) ? "selected" : "" %>>Image</option>
                                    </select>
                                </div>
                                <div class="form-group mr-2">
                                    <input type="text" class="form-control form-control-sm" id="filterSearch" 
                                           name="search" placeholder="Recherche..." 
                                           value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                                </div>
                                <button type="submit" class="btn btn-primary btn-sm mr-2">
                                    <i class="fe fe-filter fe-12"></i> Filtrer
                                </button>
                                <a href="PromoServlet?action=lister" class="btn btn-outline-secondary btn-sm">
                                    <i class="fe fe-refresh-ccw fe-12"></i>
                                </a>
                            </form>
                        </div>
                    </div>
                    
                    <div class="card-body">
                        <% if (promos != null && !promos.isEmpty()) { %>
                        <div class="table-responsive">
                            <table class="table table-hover" id="dataTablePromos">
                                <thead class="">
                                    <tr>
                                        <th width="120px">Média</th>
                                        <th>Promotion</th>
                                        <th>Type</th>
                                        <th>Dates</th>
                                        <th width="80px">Ordre</th>
                                        <th width="100px">Statut</th>
                                        <th width="100px">Vues</th>
                                        <th width="100px" class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                    for (Promo promo : promos) {
                                        String mediaUrl = promo.getCheminMedia();
                                        String mediaType = promo.getTypeContenu();
                                        String mediaPath;
                                        
                                        // Déterminer le chemin du média
                                        if (mediaUrl != null && !mediaUrl.isEmpty()) {
                                        	if ("video".equalsIgnoreCase(mediaType)) {
                                                if (mediaUrl.startsWith("uploads/promos/videos/")) {
                                                    mediaPath = ctx + "/images/videos/" + mediaUrl.substring("uploads/promos/videos".length());
                                                } else {
                                                    mediaPath = mediaUrl;
                                                }
                                            }else {
                                                if (mediaUrl.startsWith("uploads/promos/")) {
                                                    mediaPath = ctx + "/images/promos/" + mediaUrl.substring("uploads/promos/".length());
                                                } else {
                                                    mediaPath = mediaUrl;
                                                }
                                            }
                                        } else {
                                            // Image par défaut selon le type
                                            mediaPath = "video".equalsIgnoreCase(mediaType) ? 
                                                ctx + "/videos/default.mp4" : ctx + "/images/promos/default.png";
                                        }
                                        
                                        // Déterminer la classe CSS pour le statut
                                        String statutClass = "";
                                        String statutText = "";
                                        if ("actif".equals(promo.getStatut())) {
                                            statutClass = "badge badge-success";
                                            statutText = "Actif";
                                        } else if ("inactif".equals(promo.getStatut())) {
                                            statutClass = "badge badge-secondary";
                                            statutText = "Inactif";
                                        } else if ("supprime".equals(promo.getStatut())) {
                                            statutClass = "badge badge-danger";
                                            statutText = "Supprimé";
                                        } else {
                                            statutClass = "badge badge-light";
                                            statutText = promo.getStatut();
                                        }
                                        
                                        // Formater les dates
                                        String dateDebut = promo.getDateDebut() != null ? 
                                            promo.getDateDebut().toLocalDate().toString() : "Indéfinie";
                                        String dateFin = promo.getDateFin() != null ? 
                                            promo.getDateFin().toLocalDate().toString() : "Indéfinie";
                                    %>
                                    <tr>
                                        <td>
                                            <div class="media-preview">
<% if ("video".equalsIgnoreCase(mediaType)) { %>

<a href="<%= mediaPath %>" target="_blank" class="text-decoration-none">
    <div class="video-thumbnail position-relative rounded"
         style="width: 100px; height: 60px; overflow: hidden; cursor: pointer;">
        <video width="100" height="60" muted>
            <source src="<%= mediaPath %>" type="video/mp4">
        </video>

        <div class="position-absolute" style="top: 5px; right: 5px;">
            <span class="badge badge-dark px-1">
                <i class="fe fe-play-circle fe-10"></i>
            </span>
        </div>

        <% if (promo.getDureeVideo() != null) { %>
        <div class="position-absolute" style="bottom: 5px; left: 5px;">
            <small class="text-white bg-dark px-1 rounded">
                <%= promo.getDureeVideo() %>
            </small>
        </div>
        <% } %>
    </div>
</a>

<% } else { %>

<img src="<%= mediaPath %>" alt="<%= promo.getTitre() %>"
     class="rounded" style="width: 100px; height: 60px; object-fit: cover;">

<% } %>
</div>

                                        </td>
                                        <td>
                                            <div>
                                                <strong class="d-block mb-1"><%= promo.getTitre() %></strong>
                                                <% if (promo.getSousTitre() != null && !promo.getSousTitre().isEmpty()) { %>
                                                <small class="text-muted d-block mb-1"><%= promo.getSousTitre() %></small>
                                                <% } %>
                                                <small class="text-muted d-block mb-1">
                                                    <%= promo.getDescription() != null && promo.getDescription().length() > 80 ? 
                                                        promo.getDescription().substring(0, 80) + "..." : promo.getDescription() %>
                                                </small>
                                                <div class="mt-2">
                                                    <small class="text-primary mr-3">
                                                        <i class="fe fe-navigation fe-12 mr-1"></i> 
                                                        <%= promo.getRouteCible() != null ? promo.getRouteCible() : "/" %>
                                                    </small>
                                                    <small class="text-info">
                                                        <i class="fe fe-mouse-pointer fe-12 mr-1"></i> 
                                                        <%= promo.getTexteBouton() != null ? promo.getTexteBouton() : "Explorer" %>
                                                    </small>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="badge <%= "video".equalsIgnoreCase(mediaType) ? "badge-primary" : "badge-info" %>">
                                                <i class="fe fe-<%= "video".equalsIgnoreCase(mediaType) ? "film" : "image" %> fe-12 mr-1"></i>
                                                <%= "video".equalsIgnoreCase(mediaType) ? "Vidéo" : "Image" %>
                                            </span>
                                        </td>
                                        <td>
                                            <small class="d-block">
                                                <strong class="text-muted">Début:</strong> 
                                                <span class="text-dark"><%= dateDebut %></span>
                                            </small>
                                            <small class="d-block">
                                                <strong class="text-muted">Fin:</strong> 
                                                <span class="text-dark"><%= dateFin %></span>
                                            </small>
                                        </td>
                                        <td>
                                            <span class="badge badge-light border">
                                                <i class="fe fe-list fe-12 mr-1"></i> 
                                                <%= promo.getOrdreAffichage() %>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="<%= statutClass %>"><%= statutText %></span>
                                        </td>
                                        <td>
                                            <small class="text-muted">
                                                <%= promo.getVues() != null ? promo.getVues() : "0 vues" %>
                                            </small>
                                        </td>
                                        <td class="text-center">
                                            <div class="btn-group">
                                                <button class="btn btn-sm btn-outline-primary dropdown-toggle" 
                                                        type="button" data-toggle="dropdown" aria-haspopup="true" 
                                                        aria-expanded="false">
                                                    <i class="fe fe-settings fe-12"></i>
                                                </button>
                                                <div class="dropdown-menu dropdown-menu-right">
                                                    <% if (canEditPromos) { %>
                                                    <a class="dropdown-item btn-edit-promo" href="#" 
                                                       data-toggle="modal" data-target=".modal-edit-promo"
                                                       data-id="<%= promo.getId() %>"
                                                       data-titre="<%= promo.getTitre() %>"
                                                       data-soustitre="<%= promo.getSousTitre() != null ? promo.getSousTitre() : "" %>"
                                                       data-description="<%= promo.getDescription() != null ? promo.getDescription() : "" %>"
                                                       data-typecontenu="<%= promo.getTypeContenu() %>"
                                                       data-cheminmedia="<%= promo.getCheminMedia() %>"
                                                       data-couleursgradient="<%= promo.getCouleursGradientString() %>"
                                                       data-textbouton="<%= promo.getTexteBouton() != null ? promo.getTexteBouton() : "" %>"
                                                       data-route="<%= promo.getRouteCible() != null ? promo.getRouteCible() : "" %>"
                                                       data-duree="<%= promo.getDureeVideo() != null ? promo.getDureeVideo() : "" %>"
                                                       data-vues="<%= promo.getVues() != null ? promo.getVues() : "" %>"
                                                       data-ordre="<%= promo.getOrdreAffichage() %>"
                                                       data-statut="<%= promo.getStatut() %>"
                                                       data-datedebut="<%= promo.getDateDebut() != null ? promo.getDateDebut().toString() : "" %>"
                                                       data-datefin="<%= promo.getDateFin() != null ? promo.getDateFin().toString() : "" %>">
                                                        <i class="fe fe-edit fe-12 mr-2"></i> Modifier
                                                    </a>
                                                    
                                                    <%-- <a class="dropdown-item btn-change-status" href="#"
                                                       data-id="<%= promo.getId() %>"
                                                       data-current-status="<%= promo.getStatut() %>">
                                                        <% if ("actif".equals(promo.getStatut())) { %>
                                                        <i class="fe fe-pause-circle fe-12 mr-2"></i> Désactiver
                                                        <% } else { %>
                                                        <i class="fe fe-play-circle fe-12 mr-2"></i> Activer
                                                        <% } %>
                                                    </a> --%>
                                                    
                                                    <div class="dropdown-divider"></div>
                                                    <% } %>
                                                    
                                                    <%-- <a class="dropdown-item btn-preview-promo" href="#"
                                                       data-id="<%= promo.getId() %>">
                                                        <i class="fe fe-eye fe-12 mr-2"></i> Aperçu
                                                    </a> --%>
                                                    
                                                    <% if (true) { %>
                                                    <form method="POST" action="PromoServlet" style="display: inline;">
                                                        <input type="hidden" name="action" value="supprimer">
                                                        <input type="hidden" name="id" value="<%= promo.getId() %>">
                                                        <button type="submit" class="dropdown-item text-danger btn-delete-promo" 
                                                                onclick="return confirm('Voulez-vous vraiment supprimer la promotion \'<%= promo.getTitre() %>\' ?')"
                                                                style="background: none; border: none; width: 100%; text-align: left;">
                                                            <i class="fe fe-trash-2 fe-12 mr-2"></i> Supprimer
                                                        </button>
                                                    </form>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                        <% } else { %>
                        <div class="text-center py-5">
                            <div class="text-muted mb-4">
                                <i class="fe fe-video fe-48 mb-3"></i>
                                <p class="h4 mb-3">Aucune promotion disponible</p>
                                <p class="mb-4">Commencez par créer votre première promotion</p>
                                <% if (canAddPromos) { %>
                                <button type="button" class="btn btn-primary" data-toggle="modal" data-target=".modal-add-promo">
                                    <i class="fe fe-plus fe-16 mr-2"></i> Créer une promotion
                                </button>
                                <% } %>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Modal pour ajouter une promotion -->
<div class="modal fade modal-add-promo" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fe fe-plus-circle fe-24 mr-2"></i> Nouvelle Promotion
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="POST" action="PromoServlet?action=ajouter" id="formAddPromo" enctype="multipart/form-data">
                <div class="modal-body">
                    <!-- Onglets -->
                    <ul class="nav nav-tabs mb-4" id="promoTabs" role="tablist">
                        <li class="nav-item">
                            <a class="nav-link active" id="info-tab" data-toggle="tab" href="#info" role="tab">
                                <i class="fe fe-info fe-16 mr-2"></i> Informations
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="media-tab" data-toggle="tab" href="#media" role="tab">
                                <i class="fe fe-image fe-16 mr-2"></i> Média
                            </a>
                        </li>
                        <li class="nav-item" style="display:none">
                            <a class="nav-link" id="design-tab" data-toggle="tab" href="#design" role="tab">
                                <i class="fe fe-palette fe-16 mr-2"></i> Design
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="advanced-tab" data-toggle="tab" href="#advanced" role="tab">
                                <i class="fe fe-settings fe-16 mr-2"></i> Paramètres
                            </a>
                        </li>
                    </ul>
                    
                    <!-- Contenu des onglets -->
                    <div class="tab-content" id="promoTabsContent">
                        <!-- Onglet Informations -->
                        <div class="tab-pane fade show active" id="info" role="tabpanel">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addTitre" class="font-weight-bold">Titre principal <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addTitre" name="titre" required 
                                               placeholder="Ex: Découvrez Notre Établissement">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addSousTitre" class="font-weight-bold">Sous-titre</label>
                                        <input type="text" class="form-control" id="addSousTitre" name="sousTitre" 
                                               placeholder="Ex: Expérience VIP Exclusive">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="addDescription" class="font-weight-bold">Description <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="addDescription" name="description" rows="3" required 
                                          placeholder="Ex: Restaurant • Rooftop • Événements"></textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addRoute" class="font-weight-bold">Route cible <span class="text-danger">*</span></label>
                                        <select class="form-control" id="addRoute" name="routeCible" required>
                                            <option value="">-- Sélectionner une route --</option>
                                            <option value="/gallery">Galerie</option>
                                            <option value="/menu">Menu</option>
                                            <option value="/reservation">Réservation</option>
                                            <option value="/hotel">Hôtel</option>
                                            <option value="/promotions">Promotions</option>
                                            <option value="/contact">Contact</option>
                                            <option value="/">Accueil</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addTexteBouton" class="font-weight-bold">Texte du bouton <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addTexteBouton" name="texteBouton" required 
                                               placeholder="Ex: Explorer, Réserver, Voir le menu">
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Média -->
                        <div class="tab-pane fade" id="media" role="tabpanel">
                            <div class="form-group">
                                <label class="font-weight-bold">Type de contenu <span class="text-danger">*</span></label>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="custom-control custom-radio">
                                            <input type="radio" id="addTypeImage" name="typeContenu" value="image" 
                                                   class="custom-control-input" checked>
                                            <label class="custom-control-label" for="addTypeImage">
                                                <i class="fe fe-image fe-16 mr-2"></i> Image
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="custom-control custom-radio">
                                            <input type="radio" id="addTypeVideo" name="typeContenu" value="video" 
                                                   class="custom-control-input">
                                            <label class="custom-control-label" for="addTypeVideo">
                                                <i class="fe fe-film fe-16 mr-2"></i> Vidéo
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div id="imageSection">
                                <div class="form-group">
                                    <label for="addImageFile" class="font-weight-bold">Image <span class="text-danger">*</span></label>
                                    <input type="file" class="form-control-file" id="addImageFile" name="imageFile" 
                                           accept="image/*">
                                    <small class="form-text text-muted">Format recommandé: 1920x1080px, Max 5MB</small>
                                </div>
                            </div>
                            
                            <div id="videoSection" style="display: none;">
                                <div class="form-group">
                                    <label for="addVideoFile" class="font-weight-bold">Vidéo <span class="text-danger">*</span></label>
                                    <input type="file" class="form-control-file" id="addVideoFile" name="videoFile" 
                                           accept="video/*">
                                    <small class="form-text text-muted">Format recommandé: MP4, Max 50MB</small>
                                </div>
                                
                                <div class="form-group">
                                    <label for="addDureeVideo" class="font-weight-bold">Durée de la vidéo</label>
                                    <input type="text" class="form-control" id="addDureeVideo" name="dureeVideo" 
                                           placeholder="Ex: 00:30">
                                    <small class="form-text text-muted">Format: MM:SS</small>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Design -->
                        <div class="tab-pane fade" id="design" role="tabpanel">
                            <div class="form-group">
                                <label class="font-weight-bold">Couleurs de fond</label>
                                <div class="row">
                                    <div class="col-md-4">
                                        <label for="addCouleur1">Couleur 1</label>
                                        <input type="color" class="form-control" id="addCouleur1" name="couleur1" 
                                               value="#8B5CF6">
                                    </div>
                                    <div class="col-md-4">
                                        <label for="addCouleur2">Couleur 2</label>
                                        <input type="color" class="form-control" id="addCouleur2" name="couleur2" 
                                               value="#EC4899">
                                    </div>
                                    <div class="col-md-4">
                                        <label for="addCouleur3">Couleur 3</label>
                                        <input type="color" class="form-control" id="addCouleur3" name="couleur3" 
                                               value="#8B5CF6">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="font-weight-bold">Aperçu du gradient</label>
                                <div id="gradientPreview" class="rounded p-4 text-center" 
                                     style="height: 100px; background: linear-gradient(135deg, #8B5CF6, #EC4899, #8B5CF6);">
                                    <span class="text-white">Aperçu du fond de la promotion</span>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Paramètres -->
                        <div class="tab-pane fade" id="advanced" role="tabpanel">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addOrdreAffichage" class="font-weight-bold">Ordre d'affichage <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="addOrdreAffichage" name="ordreAffichage" 
                                               required min="1" value="1">
                                        <small class="form-text text-muted">Détermine la position dans le carrousel</small>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addStatut" class="font-weight-bold">Statut <span class="text-danger">*</span></label>
                                        <select class="form-control" id="addStatut" name="statut" required>
                                            <option value="actif">Actif</option>
                                            <option value="inactif">Inactif</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addDateDebut" class="font-weight-bold">Date de début</label>
                                        <input type="date" class="form-control" id="addDateDebut" name="dateDebut">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="addDateFin" class="font-weight-bold">Date de fin</label>
                                        <input type="date" class="form-control" id="addDateFin" name="dateFin">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">
                        <i class="fe fe-save fe-16 mr-2"></i> Créer la promotion
                    </button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal pour éditer une promotion -->
<div class="modal fade modal-edit-promo" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fe fe-edit fe-24 mr-2"></i> Modifier la Promotion
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="POST" action="PromoServlet?action=modifier" id="formEditPromo" enctype="multipart/form-data">
                <input type="hidden" id="editPromoId" name="id">
                
                <div class="modal-body">
                    <!-- Onglets similaires à l'ajout -->
                    <ul class="nav nav-tabs mb-4" id="editPromoTabs" role="tablist">
                        <li class="nav-item">
                            <a class="nav-link active" id="edit-info-tab" data-toggle="tab" href="#edit-info" role="tab">
                                <i class="fe fe-info fe-16 mr-2"></i> Informations
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="edit-media-tab" data-toggle="tab" href="#edit-media" role="tab">
                                <i class="fe fe-image fe-16 mr-2"></i> Média
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="edit-design-tab" data-toggle="tab" href="#edit-design" role="tab">
                                <i class="fe fe-palette fe-16 mr-2"></i> Design
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="edit-advanced-tab" data-toggle="tab" href="#edit-advanced" role="tab">
                                <i class="fe fe-settings fe-16 mr-2"></i> Paramètres
                            </a>
                        </li>
                    </ul>
                    
                    <!-- Contenu des onglets d'édition -->
                    <div class="tab-content" id="editPromoTabsContent">
                        <!-- Onglet Informations -->
                        <div class="tab-pane fade show active" id="edit-info" role="tabpanel">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editTitre" class="font-weight-bold">Titre principal <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editTitre" name="titre" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editSousTitre" class="font-weight-bold">Sous-titre</label>
                                        <input type="text" class="form-control" id="editSousTitre" name="sousTitre">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="editDescription" class="font-weight-bold">Description <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="editDescription" name="description" rows="3" required></textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editRoute" class="font-weight-bold">Route cible <span class="text-danger">*</span></label>
                                        <select class="form-control" id="editRoute" name="routeCible" required>
                                            <option value="">-- Sélectionner une route --</option>
                                            <option value="/gallery">Galerie</option>
                                            <option value="/menu">Menu</option>
                                            <option value="/reservation">Réservation</option>
                                            <option value="/hotel">Hôtel</option>
                                            <option value="/promotions">Promotions</option>
                                            <option value="/contact">Contact</option>
                                            <option value="/">Accueil</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editTexteBouton" class="font-weight-bold">Texte du bouton <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editTexteBouton" name="texteBouton" required>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Média -->
                        <div class="tab-pane fade" id="edit-media" role="tabpanel">
                            <div class="form-group">
                                <label class="font-weight-bold">Type de contenu <span class="text-danger">*</span></label>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="custom-control custom-radio">
                                            <input type="radio" id="editTypeImage" name="typeContenu" value="image" 
                                                   class="custom-control-input">
                                            <label class="custom-control-label" for="editTypeImage">
                                                <i class="fe fe-image fe-16 mr-2"></i> Image
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="custom-control custom-radio">
                                            <input type="radio" id="editTypeVideo" name="typeContenu" value="video" 
                                                   class="custom-control-input">
                                            <label class="custom-control-label" for="editTypeVideo">
                                                <i class="fe fe-film fe-16 mr-2"></i> Vidéo
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div id="editImageSection" style="display: none;">
                                <div class="form-group">
                                    <label for="editImageFile" class="font-weight-bold">Image</label>
                                    <input type="file" class="form-control-file" id="editImageFile" name="imageFile" 
                                           accept="image/*">
                                    <small class="form-text text-muted">Laissez vide pour conserver l'image actuelle</small>
                                </div>
                            </div>
                            
                            <div id="editVideoSection" style="display: none;">
                                <div class="form-group">
                                    <label for="editVideoFile" class="font-weight-bold">Vidéo</label>
                                    <input type="file" class="form-control-file" id="editVideoFile" name="videoFile" 
                                           accept="video/*">
                                    <small class="form-text text-muted">Laissez vide pour conserver la vidéo actuelle</small>
                                </div>
                                
                                <div class="form-group">
                                    <label for="editDureeVideo" class="font-weight-bold">Durée de la vidéo</label>
                                    <input type="text" class="form-control" id="editDureeVideo" name="dureeVideo">
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Design -->
                        <div class="tab-pane fade" id="edit-design" role="tabpanel">
                            <div class="form-group">
                                <label class="font-weight-bold">Couleurs de fond</label>
                                <div class="row">
                                    <div class="col-md-4">
                                        <label for="editCouleur1">Couleur 1</label>
                                        <input type="color" class="form-control" id="editCouleur1" name="couleur1">
                                    </div>
                                    <div class="col-md-4">
                                        <label for="editCouleur2">Couleur 2</label>
                                        <input type="color" class="form-control" id="editCouleur2" name="couleur2">
                                    </div>
                                    <div class="col-md-4">
                                        <label for="editCouleur3">Couleur 3</label>
                                        <input type="color" class="form-control" id="editCouleur3" name="couleur3">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="font-weight-bold">Aperçu du gradient</label>
                                <div id="editGradientPreview" class="rounded p-4 text-center" 
                                     style="height: 100px;">
                                    <span class="text-white">Aperçu du fond de la promotion</span>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Onglet Paramètres -->
                        <div class="tab-pane fade" id="edit-advanced" role="tabpanel">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editOrdreAffichage" class="font-weight-bold">Ordre d'affichage <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="editOrdreAffichage" name="ordreAffichage" 
                                               required min="1">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editStatut" class="font-weight-bold">Statut <span class="text-danger">*</span></label>
                                        <select class="form-control" id="editStatut" name="statut" required>
                                            <option value="actif">Actif</option>
                                            <option value="inactif">Inactif</option>
                                            <option value="supprime">Supprimé</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editDateDebut" class="font-weight-bold">Date de début</label>
                                        <input type="date" class="form-control" id="editDateDebut" name="dateDebut">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="editDateFin" class="font-weight-bold">Date de fin</label>
                                        <input type="date" class="form-control" id="editDateFin" name="dateFin">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">
                        <i class="fe fe-save fe-16 mr-2"></i> Enregistrer
                    </button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<!-- JavaScript -->
<script src="<%= ctx %>/assets/js/jquery.min.js"></script>
<script src="<%= ctx %>/assets/js/popper.min.js"></script>
<script src="<%= ctx %>/assets/js/moment.min.js"></script>
<script src="<%= ctx %>/assets/js/bootstrap.min.js"></script>
<script src="<%= ctx %>/assets/js/simplebar.min.js"></script>
<script src="<%= ctx %>/assets/js/daterangepicker.js"></script>
<script src="<%= ctx %>/assets/js/jquery.stickOnScroll.js"></script>
<script src="<%= ctx %>/assets/js/tinycolor-min.js"></script>
<script src="<%= ctx %>/assets/js/config.js"></script>
<script src="<%= ctx %>/assets/js/apps.js"></script>

<script>
$(document).ready(function() {
    // Initialiser DataTable seulement si le tableau existe
    if ($('#dataTablePromos').length) {
        $('#dataTablePromos').DataTable({
            autoWidth: true,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "Tous"]],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.11.5/i18n/fr-FR.json'
            },
            order: [[4, 'asc']], // Trier par ordre d'affichage par défaut
            columnDefs: [
                { orderable: false, targets: [0, 7] } // Désactiver le tri sur les colonnes média et actions
            ],
            dom: '<"row"<"col-md-6"l><"col-md-6"f>>rt<"row"<"col-md-6"i><"col-md-6"p>>',
            initComplete: function() {
                // Ajuster le style après l'initialisation
                $('.dataTables_length select').addClass('custom-select custom-select-sm');
                $('.dataTables_filter input').addClass('form-control form-control-sm');
            }
        });
    }
    
    // Gestion du type de contenu dans le formulaire d'ajout
    $('input[name="typeContenu"]').change(function() {
        if ($(this).val() === 'video') {
            $('#imageSection').hide();
            $('#videoSection').show();
            $('#addImageFile').removeAttr('required');
            $('#addVideoFile').attr('required', 'required');
        } else {
            $('#imageSection').show();
            $('#videoSection').hide();
            $('#addImageFile').attr('required', 'required');
            $('#addVideoFile').removeAttr('required');
        }
    });
    
    // Gestion du type de contenu dans le formulaire d'édition
    $('input[name="typeContenu"]').on('change', function() {
        var type = $(this).val();
        if (type === 'video') {
            $('#editImageSection').hide();
            $('#editVideoSection').show();
        } else {
            $('#editImageSection').show();
            $('#editVideoSection').hide();
        }
    });
    
    // Mise à jour de l'aperçu du gradient pour l'ajout
    function updateGradientPreview() {
        var color1 = $('#addCouleur1').val();
        var color2 = $('#addCouleur2').val();
        var color3 = $('#addCouleur3').val();
        $('#gradientPreview').css('background', 'linear-gradient(135deg, ' + color1 + ', ' + color2 + ', ' + color3 + ')');
    }
    
    // Mise à jour de l'aperçu du gradient pour l'édition
    function updateEditGradientPreview() {
        var color1 = $('#editCouleur1').val();
        var color2 = $('#editCouleur2').val();
        var color3 = $('#editCouleur3').val();
        $('#editGradientPreview').css('background', 'linear-gradient(135deg, ' + color1 + ', ' + color2 + ', ' + color3 + ')');
    }
    
    $('#addCouleur1, #addCouleur2, #addCouleur3').on('input', updateGradientPreview);
    $('#editCouleur1, #editCouleur2, #editCouleur3').on('input', updateEditGradientPreview);
    
    // Remplir le formulaire d'édition
    $(document).on('click', '.btn-edit-promo', function() {
        var promoId = $(this).data('id');
        $('#editPromoId').val(promoId);
        
        // Remplir les champs avec les données
        $('#editTitre').val($(this).data('titre'));
        $('#editSousTitre').val($(this).data('soustitre'));
        $('#editDescription').val($(this).data('description'));
        $('#editRoute').val($(this).data('route'));
        $('#editTexteBouton').val($(this).data('textbouton'));
        $('#editDureeVideo').val($(this).data('duree'));
        $('#editOrdreAffichage').val($(this).data('ordre'));
        $('#editStatut').val($(this).data('statut'));
        
        // Dates
        var dateDebut = $(this).data('datedebut');
        var dateFin = $(this).data('datefin');
        if (dateDebut) {
            $('#editDateDebut').val(dateDebut.substring(0, 10));
        }
        if (dateFin) {
            $('#editDateFin').val(dateFin.substring(0, 10));
        }
        
        // Type de contenu
        var typeContenu = $(this).data('typecontenu');
        if (typeContenu === 'video') {
            $('#editTypeVideo').prop('checked', true);
            $('#editImageSection').hide();
            $('#editVideoSection').show();
        } else {
            $('#editTypeImage').prop('checked', true);
            $('#editImageSection').show();
            $('#editVideoSection').hide();
        }
        
        // Couleurs de gradient
        var couleursGradient = $(this).data('couleursgradient');
        if (couleursGradient) {
            var colors = couleursGradient.split(',');
            if (colors.length >= 3) {
                $('#editCouleur1').val(colors[0]);
                $('#editCouleur2').val(colors[1]);
                $('#editCouleur3').val(colors[2]);
                updateEditGradientPreview();
            }
        }
    });
    
    // Gestion du changement de statut
    $(document).on('click', '.btn-change-status', function(e) {
        e.preventDefault();
        var promoId = $(this).data('id');
        var currentStatus = $(this).data('current-status');
        var newStatus = currentStatus === 'actif' ? 'inactif' : 'actif';
        
        if (confirm('Voulez-vous vraiment changer le statut de cette promotion ?')) {
            window.location.href = 'PromoServlet?action=changerStatut&id=' + promoId + '&statut=' + newStatus;
        }
    });
    
    // Gestion de la suppression
    /* $(document).on('click', '.btn-delete-promo', function(e) {
        e.preventDefault();
        var promoId = $(this).data('id');
        var promoTitre = $(this).data('titre');
        
        if (confirm('Voulez-vous vraiment supprimer la promotion "' + promoTitre + '" ?')) {
            window.location.href = 'PromoServlet?action=supprimer&id=' + promoId;
        }
    }); */
    
    // Validation du formulaire d'ajout
    $('#formAddPromo').on('submit', function(e) {
        var typeContenu = $('input[name="typeContenu"]:checked').val();
        var hasError = false;
        
        if (typeContenu === 'image' && $('#addImageFile').val() === '') {
            alert('Veuillez sélectionner une image.');
            hasError = true;
            $('#media-tab').click();
        } else if (typeContenu === 'video' && $('#addVideoFile').val() === '') {
            alert('Veuillez sélectionner une vidéo.');
            hasError = true;
            $('#media-tab').click();
        }
        
        if (hasError) {
            e.preventDefault();
        }
    });
    
    // Aperçu de la promotion
    $(document).on('click', '.btn-preview-promo', function(e) {
        e.preventDefault();
        var promoId = $(this).data('id');
        // Ouvrir un nouvel onglet avec l'aperçu
        window.open('<%= ctx %>/promo-preview.jsp?id=' + promoId, '_blank');
    });
    
    // Initialiser les aperçus de gradient
    updateGradientPreview();
});
</script>
</body>
</html>