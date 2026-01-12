<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Facture,com.spot69.model.MenuCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker,com.spot69.model.Rayon"%>
<meta charset="UTF-8">

<%
String username = (String) session.getAttribute("username");
String role = (String) session.getAttribute("role");
Integer userId = (Integer) session.getAttribute("userId");
%>
<!DOCTYPE html>
<!-- <meta charset="UTF-8"> -->

<!-- <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LA DIVINITE DE DIEU</title> -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
<link rel="stylesheet" href="css/feather.css">
<jsp:include page="header.jsp" />

<style>
td.numero-facture {
    cursor: pointer;
    text-decoration: underline;
  }
  td.numero-facture:hover {
    color: #b08c3e;
  }
  .table-danger, .table-danger > th, .table-danger > td{
  color:#dc3545;
  background-color: unset;}
/* --- GLOBAL STYLES & VARIABLES --- */
:root { -
	-bg-dark: #1a1a1a; -
	-bg-panel: #222222; -
	-bg-ticket: #2b2e33; -
	-text-grey: #a0a0a0; -
	-border-color: #3e3e3e;
	/* Couleurs des produits */ -
	-color-red: #ef5350; -
	-color-green: #4caf50; -
	-color-blue: #42a5f5; -
	-color-orange: #ffa726; -
	-color-purple: #ab47bc; -
	-color-dark-blue: #3f51b5; -
	-color-dark-btn: #37474f; -
	-color-dark-green: #388e3c; -
	-color-yellow: #fdd835;
}

body {
	background-color: var(- -bg-dark);
	font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
	color: white;
	height: 100vh;
	overflow: hidden;
	display: flex;
	flex-direction: column;
}

/* --- UTILITAIRES --- */
.no-gutters {
	margin-right: 0;
	margin-left: 0;
}

.no-gutters>.col, .no-gutters>[class*="col-"] {
	padding-right: 0;
	padding-left: 0;
}

.full-height {
	height: 100%;
}

.text-xs {
	font-size: 0.7rem;
}

/* --- HEADER (TOP BAR) --- */
.top-bar {
	background-color: #000;
	height: 100px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 15px;
	border-bottom: 1px solid var(- -border-color);
}

.top-icon {
	color: white;
	text-align: center;
	font-size: 0.8rem;
	margin: 0 10px;
	cursor: pointer;
}

.top-icon i {
	display: block;
	font-size: 1.2rem;
	margin-bottom: 2px;
}

.brand-logo {
	color: #ef5350;
	font-size: 1.8rem;
	font-weight: bold;
}

.sold {
	color: #66bb6a;
	font-size: 1.8rem;
	font-weight: bold;
}

/* --- MAIN LAYOUT --- */
.main-workspace {
	flex: 1;
	display: flex;
	overflow: hidden;
}

/* --- LEFT TOOLBAR (Vertical) --- */
.left-tools {
	width: 120px;
	background-color: #111;
	display: flex;
	flex-direction: column;
	border-right: 1px solid var(- -border-color);
	overflow-y: auto;
}

.tool-btn {
	height: 70px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	color: #ccc;
	border-bottom: 1px solid #333;
	cursor: pointer;
	position: relative;
}

.tool-btn:hover {
	background-color: #333;
	color: white;
}

.tool-btn i {
	font-size: 1.4rem;
	margin-bottom: 5px;
}

.tool-btn span {
	font-size: 0.65rem;
	text-transform: uppercase;
}

.tool-btn.active {
	background-color: #444;
	color: #fdd835;
	border-left: 3px solid #fdd835;
}

.tool-btn.rayon-btn {
	border-bottom: 1px solid #444;
}

.rayon-badge {
	position: absolute;
	top: 5px;
	right: 5px;
	background-color: #ef5350;
	color: white;
	font-size: 0.6rem;
	padding: 1px 3px;
	border-radius: 3px;
}

/* --- PRODUCT AREA --- */
.product-area {
	flex: 1;
	padding: 5px;
	overflow-y: auto;
	background-color: var(- -bg-panel);
	display: flex;
}

/* Sections pour catégories, sous-catégories et produits */
.section-container {
	flex: 1;
	display: flex;
	flex-direction: column;
	max-width: 13%;
	min-width: 150px;
	border-right: 1px solid #444;
	overflow-y: auto;
}

.section-container:last-child {
	border-right: none;
}

/* Conteneur principal des produits */
.products-main-container {
	flex: 3;
	display: flex;
	flex-direction: column;
	overflow-y: auto;
}

/* Style des cartes */
.category-card, .subcategory-card, .product-card {
	height: 100px;
	margin: 5px;
	border-radius: 4px;
	padding: 8px;
	position: relative;
	cursor: pointer;
	transition: transform 0.1s, opacity 0.2s;
	color: white;
	font-weight: 500;
	font-size: 0.9rem;
	line-height: 1.1;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	width: 150px;
}

.category-card:hover, .subcategory-card:hover, .product-card:hover {
	opacity: 0.9;
	transform: translateY(-2px);
}

.category-card:active, .subcategory-card:active, .product-card:active {
	transform: scale(0.98);
}

/* Image dans les cartes */
.card-image {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
	opacity: 0.3;
	border-radius: 4px;
	z-index: 1;
}

.card-content {
	position: relative;
	z-index: 2;
	height: 100%;
	width: 100%;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	text-align: center;
}

/* Style spécifique pour les cartes de catégories et sous-catégories */
.category-card .card-content, .subcategory-card .card-content {
	padding: 10px 5px;
}

/* Cartes avec images pour les produits */
.card-with-image {
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
}

.card-overlay {
	background: rgba(0, 0, 0, 0.5);
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	border-radius: 4px;
	z-index: 1;
}

/* Couleurs */
.bg-red {
	background-color: var(- -color-red);
}

.bg-green {
	background-color: var(- -color-green);
}

.bg-blue {
	background-color: var(- -color-blue);
}

.bg-orange {
	background-color: var(- -color-orange);
}

.bg-purple {
	background-color: var(- -color-purple);
}

.bg-dark-blue {
	background-color: var(- -color-dark-btn);
}

.bg-discount {
	background-color: var(- -color-dark-green);
}

.bg-yellow {
	background-color: var(- -color-yellow);
	color: #333;
}

.bg-teal {
	background-color: #009688;
}

.bg-pink {
	background-color: #ec407a;
}

.bg-cyan {
	background-color: #26c6da;
}

.bg-brown {
	background-color: #8d6e63;
}

.bg-indigo {
	background-color: #5c6bc0;
}

.bg-lime {
	background-color: #cddc39;
	color: #333;
}

.bg-deep-orange {
	background-color: #ff5722;
}

.bg-deep-purple {
	background-color: #673ab7;
}

.bg-light-blue {
	background-color: #03a9f4;
}

.bg-light-green {
	background-color: #8bc34a;
}

.card-price {
	position: absolute;
	bottom: 5px;
	right: 8px;
	font-size: 0.85rem;
	background: rgba(0, 0, 0, 0.7);
	padding: 2px 8px;
	border-radius: 12px;
	font-weight: bold;
	z-index: 3;
}

.card-code {
	position: absolute;
	bottom: 5px;
	left: 8px;
	font-size: 0.7rem;
	opacity: 0.8;
	text-transform: uppercase;
	background: rgba(0, 0, 0, 0.5);
	padding: 2px 6px;
	border-radius: 3px;
	z-index: 3;
}

.card-points {
	position: absolute;
	top: 5px;
	right: 8px;
	font-size: 0.7rem;
	background: rgba(255, 255, 255, 0.9);
	color: #333;
	padding: 2px 6px;
	border-radius: 10px;
	font-weight: bold;
	z-index: 3;
}

.card-title {
	font-weight: bold;
	font-size: 0.9rem;
	margin-bottom: 3px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	text-align: center;
	width: 100%;
}

/* Style spécifique pour les titres de catégories et sous-catégories */
.category-card .card-title, .subcategory-card .card-title {
	margin-top: 8px;
	margin-bottom: 0;
	font-size: 0.85rem;
}

.card-description {
	font-size: 0.75rem;
	opacity: 0.9;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	flex-grow: 1;
}

.card-image-icon {
	position: absolute;
	bottom: 5px;
	right: 5px;
	font-size: 1rem;
	opacity: 0.7;
	z-index: 3;
}

/* Icône centrée pour catégories et sous-catégories */
.category-icon, .subcategory-icon {
	font-size: 1.8rem;
	margin-bottom: 8px;
	opacity: 0.9;
}

.selected-card {
	border: 3px solid #fdd835 !important;
	box-shadow: 0 0 10px rgba(253, 216, 53, 0.5);
}

/* En-têtes de section */
.section-header {
	padding: 10px;
	background-color: #333;
	color: #fdd835;
	font-size: 1rem;
	font-weight: bold;
	border-bottom: 1px solid #444;
	text-align: center;
}

.section-subheader {
	padding: 8px;
	background-color: #2a2a2a;
	color: #ccc;
	font-size: 0.85rem;
	border-bottom: 1px solid #444;
	text-align: center;
}

/* --- TICKET AREA (Right Side) --- */
.ticket-area {
	width: 400px;
	background-color: var(- -bg-ticket);
	display: none;
	flex-direction: column;
	border-left: 1px solid var(- -border-color);
}

.ticket-header {
	padding: 10px;
	background-color: #222;
}

.ticket-tabs .nav-link {
	background-color: #3e3e3e;
	color: #ccc;
	border: none;
	margin-right: 2px;
	border-radius: 0;
	padding: 8px 15px;
	font-size: 0.85rem;
}

.ticket-tabs .nav-link.active {
	background-color: #5d6166;
	color: white;
}

.order-list {
	flex: 1;
	overflow-y: auto;
	font-size: 0.9rem;
}

.table-dark {
	background-color: transparent;
}

.table-dark th {
	border-top: none;
	border-bottom: 1px solid #444;
	color: #aaa;
	font-size: 0.75rem;
	font-weight: normal;
}

.table-dark td {
	border: none;
	padding: 0.5rem 0.75rem;
	vertical-align: middle;
}

.sub-item td:nth-child(3) {
	padding-left: 20px;
	color: #bbb;
	font-size: 0.85rem;
	font-style: italic;
}

.menu-header {
	background-color: #333;
}

.ticket-footer {
	background-color: #222;
	padding: 0;
}

.total-display {
	background-color: #2b2e33;
	padding: 15px;
	text-align: right;
}

.total-label {
	font-size: 0.9rem;
	color: #ccc;
}

.total-amount {
	font-size: 1.8rem;
	font-weight: bold;
	color: white;
}

.action-grid {
	display: flex;
}

.action-btn {
	flex: 1;
	background-color: #37474f;
	color: white;
	border: 1px solid #222;
	padding: 15px;
	text-align: center;
}

.action-btn:hover {
	background-color: #455a64;
}

/* --- BOTTOM NAV --- */
.bottom-nav {
	height: 120px;
	background-color: #111;
	display: flex;
	align-items: center;
	padding: 0 10px;
	border-top: 1px solid #333;
}

.user-profile {
	width: 70px;
	text-align: center;
	/* border-right: 1px solid #333; */
	/* margin-right: 10px; */
}

.avatar-img {
	width: 30px;
	height: 30px;
	border-radius: 50%;
	object-fit: cover;
	margin-bottom: 5px;
}

.category-scroll {
	display: flex;
	flex: 1;
	overflow-x: auto;
	gap: 10px;
	padding: 10px 0;
}

.cat-btn {
	min-width: 110px;
	height: 90px;
	background-color: #333;
	border-radius: 8px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	color: white;
	cursor: pointer;
}

.cat-btn i {
	font-size: 1.5rem;
	margin-bottom: 4px;
}

.cat-btn span {
	font-size: 1rem;
}

.cat-btn.active {
	background-color: #444;
	color: #fdd835;
}

.cat-btn.bg-teal {
	background-color: #009688;
}

/* États de visibilité */
.visible {
	display: block !important;
}

.flex-visible {
	display: flex !important;
}

/* Message d'état */
.state-message {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #aaa;
	font-size: 1.2rem;
}

/* Conteneur de grille de produits */
.products-grid {
	display: flex;
	flex-wrap: wrap;
	padding: 5px;
	gap: 5px;
	overflow-y: auto;
}

.product-item {
	width: calc(25% - 5px);
	min-width: 120px;
}

/* Image par défaut */
.default-image {
	background-color: #555;
	display: flex;
	align-items: center;
	justify-content: center;
}

.default-image i {
	font-size: 2rem;
	opacity: 0.5;
}

/* Barre de recherche */
.search-bar {
	padding: 8px;
	background-color: #333;
	border-bottom: 1px solid #444;
}

.search-bar input {
	width: 100%;
	padding: 6px 10px;
	border-radius: 4px;
	border: 1px solid #555;
	background-color: #222;
	color: white;
}

/* Responsive */
@media ( max-width : 1400px) {
	.product-item {
		width: calc(33.333% - 5px);
	}
}

@media ( max-width : 1200px) {
	.product-item {
		width: calc(50% - 5px);
	}
}

.vertical .main-content, .vertical.hover .main-content, .narrow.open .main-content
	{
	margin-left: unset !important;
}

.product-area .main-content {
	width: 100% !important;
}
</style>
</head>
<body>

	<header class="top-bar">
		<div class="d-flex align-items-center">
			<div class="top-icon" onclick="goBack()">
				<i class="fas fa-chevron-left"></i> Back
			</div>
			<div class="top-icon" onclick="showSearch()">
				<i class="fas fa-search"></i> SEARCH
			</div>
			<div class="top-icon" onclick="showPLU()">
				<i class="fas fa-th"></i> PLU
			</div>
		</div>

		<div class="sold">
			<i class="mr-10">124000 HTG</i>
		</div>
		<div class="brand-logo">
			<i class="fas fa-fire"></i>
		</div>
		<div class="sold">
			<i class="ml-10"></i>1060US
		</div>
		<!-- <i class="fas fa-utensils"></i> -->
		<div class="d-flex align-items-center">
			<!-- <div class="top-icon"><i class="fas fa-beer"></i> SEND-BAR</div> -->
			<div class="top-icon mt-4">SETTINGS</div>
			<div class="top-icon">
				<i class="fas fa-receipt"></i> CONFIG
			</div>
			<div class="top-icon">
				<a class="nav-link" href="RoleServlet?action=lister"> <i
					class="fas fa-user-plus"></i> <span class="ml-3 item-text">USERS</span>
				</a>
			</div>
			<div class="top-icon">
				<ul class="nav">
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle text-muted pr-0 d-flex align-items-center gap-2"
						href="#" id="navbarDropdownMenuLink" role="button"
						data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							<div class="user-profile">
								<img src="https://i.pravatar.cc/100?img=11" alt="User"
									class="avatar-img">
								<div class="text-xs font-weight-bold text-uppercase">
									<%
									if (username != null && !username.isEmpty()) {
									%>
									<%=username%>
									<%
									}
									%>
								</div>
							</div>
					</a>

						<div class="dropdown-menu dropdown-menu-right"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item d-flex align-items-center" href="#">
								<i class="fe fe-user fe-16 mr-2"></i> Profile
							</a> <a class="dropdown-item d-flex align-items-center"
								href="UtilisateurServlet?action=logout&who=ADM"> <i
								class="fe fe-log-out fe-16 mr-2"></i> Déconnexion
							</a> <a class="dropdown-item d-flex align-items-center"
								href="<%=request.getContextPath()%>/index.jsp"> <i
								class="fe fe-home fe-16 mr-2"></i> Acceder au site
							</a>
						</div></li>
				</ul>
			</div>
		</div>
	</header>

	<div class="main-workspace">

		<!-- Barre latérale gauche avec rayons -->
		<div class="left-tools" id="rayons-sidebar">
			<!-- Les rayons seront ajoutés ici dynamiquement -->
		</div>

		<div class="product-area">
		
			<main role="main" class="main-content">
	<div class="container-fluid">
		<div class="row justify-content-center">
			<div class="col-12">
				<div class="row">
					<!-- Small table -->
		
					<div class="col-md-12 my-4">
						<div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="h4 mb-1">
                                <i class="fe fe-layers fe-32 align-self-center text-black"></i>
                                Liste des factures
                            </h2>
                            <div style="display: inline-flex; align-items: center; gap: 12px;">
							    <div class="custom-btn-group">
							        <a class=" py-2 px-3 btn btn-outline-primary d-flex align-items-center" 
							           href="FournisseurServlet?action=lister"
							           style="text-decoration: none; border-radius: 4px;">
							           <!-- <i class="fe fe-package"></i> -->
							           <i class="fe fe-users"></i>
							            <span class="pl-2">Fournisseurs</span>
							        </a>
							    </div>
							    <div class="custom-btn-group">
							        <a href="FactureServlet?action=add" class="btn btn-outline-primary d-flex align-items-center">
							            <i class="fas fa-plus mr-2"></i> 
							            <span>Reapprovisionner</span>
							        </a>
							    </div>
							</div>
                        </div>
                  <% 
    List<Facture> factures = (List<Facture>) request.getAttribute("factures");
    Integer deletedInterval = (Integer) request.getAttribute("deletedInterval");
    if (deletedInterval == null) deletedInterval = 1;
%>

<div class="d-flex align-items-center mb-3 gap-2">
    <label for="deletedInterval" class="me-2 mb-0">
        Préciser le nombre de jours pour la suppression :
    </label>
    <input type="number" id="deletedInterval" style="width:70px"
           name="deletedInterval" class="form-control"
           min="1" value="<%= deletedInterval %>">
    <button id="sendDeletedInterval" class="ml-3 btn btn-sm btn-primary">
        <i class="fe fe-send fe-16"></i> Envoyer
    </button>
</div>




						<div class="card shadow">
							<div class="card-body">
			<table class="table " id="facture-table">
    <thead>
        <tr>
            <th>No Facture</th>
            <th>Fournisseur</th>
            <th>Prix Achat Total</th>
            <th>Date</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    
      <% 
if (factures != null) {
    for (Facture facture : factures) {
        boolean isAjustement = facture.getNoFacture() != null && 
                              facture.getNoFacture().startsWith("AJUST-");
%>
<tr data-id="<%= facture.getId() %>" class="<%= isAjustement ? "table-danger" : "" %>">
    <td class="numero-facture" data-id="<%= facture.getId() %>">
        <%= facture.getNoFacture() %>
    </td>
    <td><%= facture.getFournisseur() != null ? facture.getFournisseur().getNom() : "-" %></td>
    <td><%= facture.getMontantTotal() %></td>
    <td><%= facture.getCreatedAt() %></td>
    <td>
        <% if (!isAjustement) { %>
        <!-- Afficher les actions seulement pour les factures normales -->
        <div class="dropdown">
            <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" 
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="text-muted sr-only">Action</span>
            </button>
            <div class="dropdown-menu">
                <a class="dropdown-item btn-show-details" href="#">Afficher les détails</a>
                <a class="dropdown-item" href="FactureServlet?action=edit&id=<%= facture.getId() %>">Modifier</a>
                <a class="dropdown-item" href="FactureServlet?action=supprimer&id=<%= facture.getId() %>" 
                   onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette facture ?');">
                    Supprimer
                </a>
            </div>
        </div>
        <% } else { %>
        <!-- Pour les ajustements, afficher juste "Ajustement" -->
        <span class="badge badge-danger">Ajustement</span>
        <% } %>
    </td>
</tr>
<% } } %>
    </tbody>
</table>

							</div>
						</div>
					</div>
					<!-- customized table -->
				</div>
			</div>
		</div>
	</div>

</main>
		
		</div>

		<div class="ticket-area">
			<div
				class="ticket-header d-flex justify-content-between align-items-center text-white">
				<button class="btn btn-outline-light btn-sm" onclick="showActions()">
					<i class="fas fa-level-up-alt"></i> Actions
				</button>
				<span style="font-size: 1.2rem; font-weight: bold;">RECEIPT</span>
				<div class="text-right text-xs text-muted">
					<span id="current-date"></span><br> <span id="clock">4:26
						AM</span>
				</div>
			</div>

			<ul class="nav nav-tabs ticket-tabs" id="myTab" role="tablist">
				<li class="nav-item"><a class="nav-link active" href="#">Cart</a></li>
				<li class="nav-item"><a class="nav-link" href="#">Last
						Ticket</a></li>
				<li class="nav-item"><a class="nav-link" href="#">Daily
						sales</a></li>
				<!-- <li class="nav-item"><a class="nav-link" href="#">User</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Course</a></li> -->
			</ul>

			<div class="order-list p-0">
				<table class="table table-dark table-sm mb-0">
					<thead>
						<tr>
							<!-- <th scope="col" style="width: 15%">ID</th> -->
							<th scope="col" style="width: 15%">QTY</th>
							<th scope="col">NAME</th>
							<th scope="col" class="text-right">PRICE</th>
						</tr>
					</thead>
					<tbody id="order-items">
						<!-- Les articles de commande seront ajoutés ici dynamiquement -->
					</tbody>
				</table>
			</div>

			<div class="ticket-footer">
				<div class="action-grid">
					<div class="action-btn" onclick="showModifiers()">
						<i class="fas fa-sliders-h"></i><br> <small>PROFORMA</small>
					</div>
					<div class="action-btn" onclick="changeTable()">
						<i class="fas fa-chair"></i><br> <small>TICKET HTG</small>
					</div>
					<!-- <div class="flex-grow-1 bg-dark p-2"><i class="fas fa-chair"></i><br><small>VENTE HTG</small></div> -->
					<div class="action-btn" onclick="changeTable()">
						<i class="fas fa-chair"></i><br> <small>TICKET US</small>
					</div>
				</div>
				<div class="total-display">
					<span class="total-label">Total due:</span><br> <span
						class="total-amount" id="total-amount">0.00</span>
				</div>
			</div>
		</div>
	</div>

	<div class="bottom-nav">
		<div class="user-profile">
			<!-- Espace réservé pour le profil utilisateur -->
		</div>

		<div class="category-scroll" id="bottom-categories">
			<div class="cat-btn" onclick="showFavorites()">
				<i class="fas fa-star"></i> <span>Favorites</span>
			</div>
			<div class="cat-btn">
				<i class="fas fa-glass-whiskey"></i>
				<!-- <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;">
                    <span>BOARD</span>
                </a> -->
				<a href="MenuServlet?action=placer-commande"
					style="color: white; text-decoration: none;"> <!-- <i class="fe fe-clipboard fe-16"></i> -->
					<span>BOARD</span>
				</a>
			</div>
			<div class="cat-btn">
				<i class="fas fa-glass-whiskey"></i> <a
					href="ProduitServlet?action=lister"
					style="color: white; text-decoration: none;"> <span>Produits</span>
				</a>
			</div>
			<div class="cat-btn">
				<i class="fas fa-truck"></i> <a href="FactureServlet?action=lister"
					style="color: white; text-decoration: none;"> <span>Achats</span>
				</a>
			</div>
			<div class="cat-btn">
				<i class="fas fa-box-open"></i> <a class=""
					href="MouvementStockServlet?action=lister"
					style="color: white; text-decoration: none;"> <span class="">Inventaire</span>
				</a>
			</div>
			<div class="cat-btn">
				<i class="fas fa-birthday-cake"></i> <span>Rapport</span>
			</div>
			<div class="cat-btn bg-teal">
				<i class="fas fa-percent"></i> <span>Taux</span>
			</div>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>

	<script>
        // Variables globales
        let menuData = null;
        let currentRayon = null;
        let currentCategory = null;
        let currentSousCategory = null;
        let filteredCategories = [];
        let filteredSubcategories = [];
        let filteredProducts = [];
        let orderItems = [];
        let totalAmount = 0;
        
        // Couleurs disponibles
        const productColors = [
            'bg-red', 'bg-green', 'bg-blue', 'bg-orange', 'bg-purple', 
            'bg-dark-blue', 'bg-yellow', 'bg-teal', 'bg-pink', 'bg-cyan',
            'bg-brown', 'bg-indigo', 'bg-lime', 'bg-deep-orange', 
            'bg-deep-purple', 'bg-light-blue', 'bg-light-green'
        ];
        
        // Base URL pour les images
        const baseUrl = 'http://localhost:8080';
        
        // Fonction pour échapper les guillemets simples
        function escapeSingleQuotes(str) {
            if (!str) return '';
            return str.replace(/'/g, "\\'");
        }
        
        // Fonction pour formater l'heure
        function formatTime(date) {
            let hours = date.getHours();
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12;
            hours = hours ? hours : 12;
            return hours + ':' + minutes + ' ' + ampm;
        }
        
        // Charger les données du menu
        function loadMenuData() {
            $('#state-message').text('Chargement du menu...');
            
            $.ajax({
                url: baseUrl + '/spot69/api?action=getmenu',
                method: 'GET',
                dataType: 'json',
                success: function(data) {
                    if (data.status === 'ok') {
                        menuData = data.data.rayons;
                        displayRayonsInSidebar();
                        displayRayonContent();
                        $('#state-message').hide();
                    } else {
                        $('#state-message').text('Erreur de chargement du menu');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Erreur lors du chargement du menu:', error);
                    // Données de secours
/*                     menuData = [
                        {
                            "id": 3,
                            "nom": "Tabac",
                            "description": "",
                            "imageUrl": "/spot69/images/default.png",
                            "categories": [
                                {
                                    "id": 64,
                                    "nom": "Tabac et Dérivés",
                                    "description": "Tabac et produits dérivés",
                                    "imageUrl": "/spot69/images/categories/default.png",
                                    "sousCategories": []
                                },
                                {
                                    "id": 63,
                                    "nom": "Produits du Tabac",
                                    "description": "Catégorie des produits du tabac",
                                    "imageUrl": "/spot69/images/categories/6ba482d7-99e9-4b22-8462-501e9d7e8423.jpg",
                                    "sousCategories": [
                                        {
                                            "id": 64,
                                            "nom": "Tabac et Dérivés",
                                            "description": "Tabac et produits dérivés",
                                            "imageUrl": "/spot69/images/categories/default.png",
                                            "plats": [
                                                {
                                                    "id": 221,
                                                    "nom": "test",
                                                    "prix": 123,
                                                    "qtePoints": 0,
                                                    "image": "/spot69/images/plats/default.png"
                                                },
                                                {
                                                    "id": 204,
                                                    "prix": 0,
                                                    "qtePoints": 5,
                                                    "image": "/spot69/images/plats/default.png"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ];
 */                    
                    displayRayonsInSidebar();
                    displayRayonContent();
                    $('#state-message').hide();
                }
            });
        }
        
        // Afficher les rayons dans la barre latérale
        function displayRayonsInSidebar() {
            const container = $('#rayons-sidebar');
            container.empty();
            
            if (!menuData || menuData.length === 0) {
                container.append('<div class="tool-btn"><i class="fas fa-exclamation-circle"></i><span>Aucun rayon</span></div>');
                return;
            }
            
            // Ajouter une séparation
            container.append('<div style="height: 1px; background-color: #444; margin: 5px 0;"></div>');
            
            // Sélectionner le premier rayon par défaut
            currentRayon = menuData[0];
            
            // Ajouter les rayons
            menuData.forEach((rayon, index) => {
                const toolBtn = $('<div class="tool-btn rayon-btn" onclick="selectRayon(' + rayon.id + ')">' +
                    '<i class="fas fa-' + getRayonIcon(rayon.nom) + '"></i>' +
                    '<span>' + (rayon.nom.length > 8 ? rayon.nom.substring(0, 8) + '...' : rayon.nom) + '</span>' +
                    '</div>');
                
                // Calculer le nombre de catégories
                const catCount = rayon.categories ? rayon.categories.length : 0;
                if (catCount > 0) {
                    toolBtn.append('<span class="rayon-badge">' + catCount + '</span>');
                }
                
                // Marquer le premier rayon comme sélectionné
                if (index === 0) {
                    toolBtn.addClass('active');
                }
                
                container.append(toolBtn);
            });
        }
        
        // Sélectionner un rayon
        function selectRayon(rayonId) {
            const rayon = menuData.find(r => r.id === rayonId);
            if (!rayon) return;
            
            currentRayon = rayon;
            
            // Mettre à jour la classe active
            $('.tool-btn.rayon-btn').removeClass('active');
            $('[onclick="selectRayon(' + rayonId + ')"]').addClass('active');
            
            // Réinitialiser les filtres
            $('#category-search').val('');
            $('#subcategory-search').val('');
            $('#product-search').val('');
            
            // Afficher le contenu du rayon
            displayRayonContent();
        }
        
        // Afficher le contenu du rayon sélectionné
        function displayRayonContent() {
            if (!currentRayon) return;
            
            // Mettre à jour les sous-titres
            $('#categories-subheader').html('Rayon: <strong>' + currentRayon.nom + '</strong>');
            $('#subcategories-subheader').text('Sélectionnez une catégorie d\'abord');
            $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            
            // Afficher les catégories
            displayCategories(currentRayon.categories);
            
            // Vider les sous-catégories et produits
            clearSubcategories();
            clearProducts();
        }
        
        // Afficher les catégories (MODIFIÉ - sans description, icône centrée)
        function displayCategories(categories) {
            const container = $('#categories-container');
            container.empty();
            
            if (!categories || categories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune catégorie disponible</div>');
                filteredCategories = [];
                return;
            }
            
            // Stocker les catégories pour la recherche
            filteredCategories = categories;
            
            // Réinitialiser la sélection
            currentCategory = null;
            
            categories.forEach((category, index) => {
                const colorClass = productColors[index % productColors.length];
                const imageUrl = category.imageUrl && category.imageUrl !== '/spot69/images/categories/default.png' 
                    ? baseUrl + category.imageUrl 
                    : null;
                
                // Obtenir l'icône pour la catégorie
                const categoryIcon = getCategoryIcon(category.nom);
                
                let cardHtml = '<div class="category-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Icône centrée (plus grande)
                cardHtml += '<div class="category-icon">';
                cardHtml += '<i class="fas fa-' + categoryIcon + '"></i>';
                cardHtml += '</div>';
                
                // Nom de la catégorie seulement (pas de description)
                cardHtml += '<div class="card-title">' + category.nom + '</div>';
                
                cardHtml += '</div>';
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- C' + category.id + ' -->';
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-category-id', category.id);
                card.attr('data-category-name', category.nom.toLowerCase());
                card.attr('data-category-description', (category.description || '').toLowerCase());
                
                card.click(function() {
                    selectCategory(category.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une catégorie
        function selectCategory(categoryId) {
            if (!currentRayon || !currentRayon.categories) return;
            
            const category = currentRayon.categories.find(c => c.id === categoryId);
            if (!category) return;
            
            currentCategory = category;
            
            // Mettre à jour la sélection visuelle
            $('.category-card').removeClass('selected-card');
            $('.category-card[data-category-id="' + categoryId + '"]').addClass('selected-card');
            
            // Mettre à jour le sous-titre
            $('#subcategories-subheader').html('Catégorie: <strong>' + category.nom + '</strong>');
            
            // Afficher les sous-catégories
            displaySubcategories(category.sousCategories);
            
            // Vider les produits
            clearProducts();
        }
        
        // Afficher les sous-catégories (MODIFIÉ - sans description, icône centrée)
        function displaySubcategories(subcategories) {
            const container = $('#subcategories-container');
            container.empty();
            
            // Réinitialiser la sélection
            currentSousCategory = null;
            
            if (!subcategories || subcategories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune sous-catégorie disponible</div>');
                filteredSubcategories = [];
                
                // Si pas de sous-catégories, afficher les produits de la catégorie directement
                if (currentCategory && currentCategory.plats && currentCategory.plats.length > 0) {
                    displayProducts(currentCategory.plats);
                    $('#products-subheader').html('Catégorie: <strong>' + currentCategory.nom + '</strong>');
                } else {
                    clearProducts();
                }
                return;
            }
            
            // Stocker les sous-catégories pour la recherche
            filteredSubcategories = subcategories;
            
            subcategories.forEach((subcat, index) => {
                const colorClass = productColors[(index + 3) % productColors.length];
                const imageUrl = subcat.imageUrl && subcat.imageUrl !== '/spot69/images/categories/default.png' 
                    ? baseUrl + subcat.imageUrl 
                    : null;
                
                // Obtenir l'icône pour la sous-catégorie
                const subcategoryIcon = getCategoryIcon(subcat.nom);
                
                let cardHtml = '<div class="subcategory-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Icône centrée (plus grande)
                cardHtml += '<div class="subcategory-icon">';
                cardHtml += '<i class="fas fa-' + subcategoryIcon + '"></i>';
                cardHtml += '</div>';
                
                // Nom de la sous-catégorie seulement (pas de description)
                cardHtml += '<div class="card-title">' + subcat.nom + '</div>';
                
                cardHtml += '</div>';
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- SC' + subcat.id + ' -->';
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-subcategory-id', subcat.id);
                card.attr('data-subcategory-name', subcat.nom.toLowerCase());
                card.attr('data-subcategory-description', (subcat.description || '').toLowerCase());
                
                card.click(function() {
                    selectSubcategory(subcat.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une sous-catégorie
        function selectSubcategory(subcategoryId) {
            if (!currentCategory || !currentCategory.sousCategories) return;
            
            const subcategory = currentCategory.sousCategories.find(sc => sc.id === subcategoryId);
            if (!subcategory) return;
            
            currentSousCategory = subcategory;
            
            // Mettre à jour la sélection visuelle
            $('.subcategory-card').removeClass('selected-card');
            $('.subcategory-card[data-subcategory-id="' + subcategoryId + '"]').addClass('selected-card');
            
            // Mettre à jour le sous-titre
            $('#products-subheader').html('Sous-catégorie: <strong>' + subcategory.nom + '</strong>');
            
            // Afficher les produits
            if (subcategory.plats && subcategory.plats.length > 0) {
                displayProducts(subcategory.plats);
            } else {
                clearProducts();
            }
        }
        
        // Afficher les produits (inchangé - garde la description)
        function displayProducts(plats) {
            const container = $('#products-container');
            container.empty();
            
            if (!plats || plats.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Aucun produit disponible</div>');
                filteredProducts = [];
                return;
            }
            
            // Stocker les produits pour la recherche
            filteredProducts = plats;
            
            plats.forEach((plat, index) => {
                const colorClass = productColors[(index + 5) % productColors.length];
                const nom = plat.nom || (plat.produit ? plat.produit.nom : 'Produit ' + plat.id);
                const description = plat.description || '';
                let prix = plat.prix;
                
                if (prix === 0 && plat.produit) {
                    prix = plat.produit.prix;
                }
                
                let points = plat.qtePoints || 0;
                if (points === 0 && plat.produit) {
                    points = plat.produit.qtePoints || 0;
                }
                
                // Image du produit
                let imageUrl = null;
                if (plat.image && plat.image !== '/spot69/images/plats/default.png') {
                    imageUrl = baseUrl + plat.image;
                } else if (plat.produit && plat.produit.imageUrl && plat.produit.imageUrl !== '/spot69/images/produits/default.png') {
                    imageUrl = baseUrl + plat.produit.imageUrl;
                }
                
                // Obtenir l'icône pour le produit
                const productIcon = getProductIcon(nom);
                
                const escapedNom = escapeSingleQuotes(nom);
                
                // Créer la carte produit
                const productItem = $('<div class="product-item"></div>');
                let cardHtml = '<div class="product-card ';
                
                if (imageUrl) {
                    cardHtml += 'card-with-image" style="background-image: url(\'' + imageUrl + '\')">';
                    cardHtml += '<div class="card-overlay"></div>';
                } else {
                    cardHtml += colorClass + '">';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Ajouter l'icône du produit (plus grande si pas d'image)
                if (!imageUrl) {
                    cardHtml += '<div style="text-align: center; margin-top: 10px; margin-bottom: 5px; font-size: 1.8rem; opacity: 0.9;">';
                    cardHtml += '<i class="fas fa-' + productIcon + '"></i>';
                    cardHtml += '</div>';
                }
                
                cardHtml += '<div class="card-title">' + nom + '</div>';
                
                // Description pour les produits uniquement
                if (description && description.trim() !== '') {
                    cardHtml += '<div class="card-description">' + description + '</div>';
                }
                
                cardHtml += '</div>';
                
                // Prix
                if (prix > 0) {
                    cardHtml += '<span class="card-price">' + prix.toFixed(2) + '</span>';
                }
                
                // Points
                if (points > 0) {
                    cardHtml += '<span class="card-points">' + points + ' pts</span>';
                }
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- P' + plat.id + ' -->';
                
                // Icône d'image si présente
                if (imageUrl) {
                    cardHtml += '<span class="card-image-icon"><i class="fas fa-image"></i></span>';
                }
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-product-id', plat.id);
                card.attr('data-product-name', nom.toLowerCase());
                card.attr('data-product-description', description.toLowerCase());
                card.attr('data-product-price', prix);
                
                card.click(function() {
                    addToOrder(plat.id, nom, prix);
                });
                
                productItem.append(card);
                container.append(productItem);
            });
        }
        
        // Vider les sous-catégories
        function clearSubcategories() {
            $('#subcategories-container').empty();
            $('#subcategories-container').append('<div class="text-center py-5" style="color: #aaa;">Sélectionnez une catégorie d\'abord</div>');
            filteredSubcategories = [];
        }
        
        // Vider les produits
        function clearProducts() {
            $('#products-container').empty();
            $('#products-container').append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Sélectionnez une sous-catégorie d\'abord</div>');
            filteredProducts = [];
        }
        
        // Fonctions de recherche
        function searchCategories() {
            const searchTerm = $('#category-search').val().toLowerCase();
            const container = $('#categories-container');
            
            if (!searchTerm) {
                // Afficher toutes les catégories
                $('.category-card').show();
                return;
            }
            
            $('.category-card').each(function() {
                const name = $(this).attr('data-category-name') || '';
                const description = $(this).attr('data-category-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        function searchSubcategories() {
            const searchTerm = $('#subcategory-search').val().toLowerCase();
            const container = $('#subcategories-container');
            
            if (!searchTerm) {
                // Afficher toutes les sous-catégories
                $('.subcategory-card').show();
                return;
            }
            
            $('.subcategory-card').each(function() {
                const name = $(this).attr('data-subcategory-name') || '';
                const description = $(this).attr('data-subcategory-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        function searchProducts() {
            const searchTerm = $('#product-search').val().toLowerCase();
            const container = $('#products-container');
            
            if (!searchTerm) {
                // Afficher tous les produits
                $('.product-item').show();
                return;
            }
            
            $('.product-item').each(function() {
                const card = $(this).find('.product-card');
                const name = card.attr('data-product-name') || '';
                const description = card.attr('data-product-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        // Obtenir l'icône pour un rayon
        function getRayonIcon(rayonNom) {
            const icons = {
                'Boisson': 'wine-glass',
                'Nourritures': 'utensils',
                'Tabac': 'smoking',
                'Vins': 'wine-bottle',
                'Champagnes': 'wine-bottle',
                'Jus': 'glass-whiskey',
                'Alcoolisées': 'wine-bottle',
                'Salades': 'leaf',
                'Burgers': 'hamburger',
                'Sandwiches': 'bread-slice',
                'Pizzas': 'pizza-slice',
                'Pâtes': 'utensil-spoon',
                'Poulets': 'drumstick-bite',
                'Fruits de Mer': 'fish',
                'Snacks': 'cookie-bite',
                'Soupes': 'bowl-hot',
                'Combos': 'concierge-bell',
                'Spécialités': 'star',
                'Entrées': 'app-store-ios',
                'Accompagnements': 'pepper-hot',
                'Plats du Jour': 'calendar-day',
                'Cafés': 'coffee',
                'Thés': 'mug-hot',
                'Bières': 'beer',
                'Énergétiques': 'bolt',
                'Gazeuses': 'glass-cheers',
                'Eau': 'tint',
                'Sans Alcool': 'glass-martini-alt',
                'Vin': 'wine-glass-alt',
                'Champagne': 'wine-bottle',
                'Vodka': 'cocktail',
                'Wisky': 'whiskey-glass',
                'Cocktail': 'cocktail',
                'Rhum': 'glass-whiskey'
            };
            
            for (const key in icons) {
                if (rayonNom.includes(key)) {
                    return icons[key];
                }
            }
            
            return 'tag';
        }
        
        // Obtenir l'icône pour un produit
        function getProductIcon(productName) {
            const icons = {
                'cola': 'glass',
                'coca': 'glass',
                'pepsi': 'glass',
                'fanta': 'glass',
                'sprite': 'glass',
                'jus': 'glass-whiskey',
                'eau': 'tint',
                'café': 'coffee',
                'thé': 'mug-hot',
                'bière': 'beer',
                'vin': 'wine-glass-alt',
                'champagne': 'wine-bottle',
                'vodka': 'cocktail',
                'whisky': 'whiskey-glass',
                'rhum': 'glass-whiskey',
                'cocktail': 'cocktail',
                'pizza': 'pizza-slice',
                'burger': 'hamburger',
                'sandwich': 'bread-slice',
                'salade': 'leaf',
                'poulet': 'drumstick-bite',
                'steak': 'drumstick-bite',
                'poisson': 'fish',
                'frites': 'utensils',
                'pâtes': 'utensil-spoon',
                'soupe': 'bowl-hot',
                'dessert': 'birthday-cake',
                'glace': 'ice-cream',
                'gâteau': 'birthday-cake',
                'cigarette': 'smoking',
                'tabac': 'smoking',
                'chicha': 'smoking'
            };
            
            const lowerName = productName.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key)) {
                    return icons[key];
                }
            }
            
            return 'utensils';
        }
        
        // Obtenir l'icône pour une catégorie
        function getCategoryIcon(categoryName) {
            const icons = {
                'tabac': 'smoking',
                'cigarette': 'smoking',
                'cigar': 'smoking',
                'produit': 'box',
                'boisson': 'wine-bottle',
                'vin': 'wine-glass-alt',
                'bière': 'beer',
                'alcool': 'wine-bottle',
                'nourriture': 'utensils',
                'repas': 'utensils',
                'plat': 'utensil-spoon',
                'entrée': 'apple-alt',
                'dessert': 'birthday-cake',
                'salade': 'leaf',
                'burger': 'hamburger',
                'pizza': 'pizza-slice',
                'sandwich': 'bread-slice',
                'poulet': 'drumstick-bite',
                'viande': 'drumstick-bite',
                'poisson': 'fish',
                'fruits': 'apple-alt',
                'légumes': 'carrot',
                'café': 'coffee',
                'thé': 'mug-hot',
                'jus': 'glass-whiskey',
                'eau': 'tint',
                'soda': 'glass',
                'cocktail': 'cocktail',
                'spiritueux': 'whiskey-glass',
                'rhum': 'glass-whiskey',
                'vodka': 'cocktail',
                'whisky': 'whiskey-glass',
                'champagne': 'wine-bottle',
                'snack': 'cookie-bite',
                'apéritif': 'wine-glass',
                'fromage': 'cheese',
                'pain': 'bread-slice',
                'pâtes': 'utensil-spoon',
                'riz': 'utensil-spoon',
                'soupe': 'bowl-hot',
                'frites': 'french-fries',
                'accompagnement': 'pepper-hot',
                'sauce': 'pepper-hot',
                'épice': 'pepper-hot',
                'fruit de mer': 'fish',
                'crustacé': 'fish',
                'coquillage': 'fish',
                'glace': 'ice-cream',
                'gâteau': 'birthday-cake',
                'patisserie': 'birthday-cake',
                'viennoiserie': 'croissant',
                'chocolat': 'candy-cane',
                'bonbon': 'candy-cane',
                'confiserie': 'candy-cane',
                'lait': 'glass-milk',
                'produit laitier': 'glass-milk',
                'yaourt': 'glass-milk',
                'œuf': 'egg',
                'céréale': 'seedling',
                'légumineuse': 'seedling',
                'noix': 'seedling',
                'graine': 'seedling',
                'huile': 'flask',
                'vinaigre': 'flask',
                'condiment': 'pepper-hot',
                'conserves': 'jar',
                'surgelé': 'snowflake',
                'frais': 'leaf',
                'bio': 'leaf',
                'traiteur': 'concierge-bell',
                'emporter': 'shopping-bag',
                'livraison': 'motorcycle',
                'menu': 'concierge-bell',
                'formule': 'concierge-bell',
                'promotion': 'percent',
                'nouveauté': 'star',
                'spécialité': 'star',
                'maison': 'home',
                'tradition': 'history',
                'régional': 'map-marker-alt',
                'international': 'globe',
                'asiatique': 'globe-asia',
                'italien': 'pizza-slice',
                'français': 'flag',
                'mexicain': 'pepper-hot',
                'indien': 'pepper-hot',
                'japonais': 'fish',
                'chinois': 'bowl-rice',
                'thaï': 'lemon',
                'marocain': 'mortar-pestle',
                'libanais': 'leaf',
                'grec': 'lemon',
                'espagnol': 'pepper-hot',
                'portugais': 'fish'
            };
            
            const lowerName = categoryName.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key)) {
                    return icons[key];
                }
            }
            
            // Icônes par défaut selon le type
            if (lowerName.includes('sous')) return 'folder';
            if (lowerName.includes('catégorie')) return 'folder-open';
            if (lowerName.includes('rayon')) return 'tags';
            
            return 'tag'; // Icône par défaut
        }
        
        // Ajouter un produit à la commande
        function addToOrder(productId, productName, price) {
            // Effet visuel
            $('.product-card[data-product-id="' + productId + '"]')
                .css('transform', 'scale(0.95)')
                .animate({transform: 'scale(1)'}, 300);
            
            // Vérifier si le produit est déjà dans la commande
            const existingItem = orderItems.find(item => item.id === productId);
            
            if (existingItem) {
                existingItem.quantity += 1;
                existingItem.total = existingItem.quantity * price;
            } else {
                orderItems.push({
                    id: productId,
                    name: productName,
                    price: price,
                    quantity: 1,
                    total: price
                });
            }
            
            // Mettre à jour l'affichage
            updateOrderDisplay();
        }
        
        // Mettre à jour l'affichage de la commande
        function updateOrderDisplay() {
            const container = $('#order-items');
            container.empty();
            
            totalAmount = 0;
            
            if (orderItems.length === 0) {
                container.append('<tr><td colspan="4" class="text-center text-muted py-3">Aucun article ajouté</td></tr>');
            } else {
                orderItems.forEach(item => {
                    totalAmount += item.total;
                    
                    const row = $('<tr>' +
                        '<td class="text-muted">' + formatTime(new Date()) + '</td>' +
                        '<td>' + item.quantity + '</td>' +
                        '<td>' + item.name + '</td>' +
                        '<td class="text-right">' + item.total.toFixed(2) + '</td>' +
                        '</tr>');
                    
                    container.append(row);
                });
            }
            
            // Mettre à jour le total
            $('#total-amount').text(totalAmount.toFixed(2));
        }
        
        // Fonction pour mettre à jour l'horloge
        function updateClock() {
            const now = new Date();
            let hours = now.getHours();
            const minutes = String(now.getMinutes()).padStart(2, '0');
            const ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12;
            hours = hours ? hours : 12;
            $('#clock').text(hours + ':' + minutes + ' ' + ampm);
            
            // Mettre à jour la date
            const options = { day: 'numeric', month: 'short', year: 'numeric' };
            const dateStr = now.toLocaleDateString('fr-FR', options);
            $('#current-date').text(dateStr);
        }
        
        // Fonctions pour les boutons
        function goBack() {
            // Retour à l'écran précédent
            if (currentSousCategory) {
                currentSousCategory = null;
                $('.subcategory-card').removeClass('selected-card');
                clearProducts();
                $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            } else if (currentCategory) {
                currentCategory = null;
                $('.category-card').removeClass('selected-card');
                clearSubcategories();
                clearProducts();
                $('#subcategories-subheader').text('Sélectionnez une catégorie d\'abord');
                $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            }
        }
        
        function showSearch() {
            // Focus sur la recherche de produits
            $('#product-search').focus();
        }
        
        function showPLU() {
            alert('Fonctionnalité PLU à implémenter');
        }
        
        function addQuantity() {
            alert('Modification de quantité à implémenter');
        }
        
        function editPrice() {
            alert('Modification de prix à implémenter');
        }
        
        function showDetails() {
            alert('Détails à implémenter');
        }
        
        function showModifiers() {
            alert('Modificateurs à implémenter');
        }
        
        function showActions() {
            alert('Actions à implémenter');
        }
        
        function changeTable() {
            alert('Changement de table à implémenter');
        }
        
        function showFavorites() {
            alert('Favoris à implémenter');
        }
        
        // Initialisation
        $(document).ready(function() {
            // Mettre à jour l'horloge
            updateClock();
            setInterval(updateClock, 1000);
            
            // Charger les données du menu
            loadMenuData();
        });
    </script>

	<script>
	$("#dataTable-1").DataTable({
	    autoWidth: true,
	    lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
	    footerCallback: function (row, data, start, end, display) {
	        var api = this.api();

	        // Fonction pour extraire la valeur numérique de la colonne "Valeur"
	        var intVal = function (i) {
	            return typeof i === 'string' ?
	                i.replace(/[\sHTG,]/g, '')*1 :
	                typeof i === 'number' ?
	                i : 0;
	        };

	        // Total sur toutes les pages
	        var total = api
	            .column(5) // index de la colonne "Valeur"
	            .data()
	            .reduce(function (a, b) {
	                return intVal(a) + intVal(b);
	            }, 0);

	        // Affiche dans le footer
	        $(api.column(5).footer()).html(total.toLocaleString() + ' HTG');
	    }
	});
</script>

	<!-- Script remplissage modal -->
	<script>
$(document).ready(function () {
    // Variables pour stocker les IDs pendant le chargement
    let pendingCatId = null;
    let pendingSousCatId = null;

    // Gestion du clic sur le bouton modifier
    $('#produit-table tbody').on('click', '.btn-edit-produit', function () {
        const button = $(this);

        // Remplir les champs de base
        $('#editProduitId').val(button.data('id'));
        $('#editNom').val(button.data('nom'));
        $('#editDescription').val(button.data('description'));
        $('#editEmplacement').val(button.data('emplacement'));
        $('#editUnite').val(button.data('unite'));
        $('#editContenu').val(button.data('contenu'));
        $('#editSeuil').val(button.data('seuil'));
        $('#editprixVente').val(button.data('prixvente'));
        $('#editQtePoints').val(button.data('qtepoints'));
        $('#editprixAchatParUniteVente').val(button.data('prixachatparunitevente'));

        const rayonId = button.data('rayonid');
        const catId = button.data('categorieid');
        const sousCatId = button.data('souscategorieid');
        const imageUrl = button.data('imageurl');
        
        // Présélectionner le rayon
        $('#editRayon').val(rayonId);

        // Gestion de l'image
        if (imageUrl) {
            const contextPath = '<%=request.getContextPath()%>';
            const relativePath = imageUrl.substring("uploads/produits/".length);
            const fullImagePath = contextPath + '/blok/images/produits/' + relativePath;
            $('#editImagePreview').attr('src', fullImagePath).show();
        } else {
            $('#editImagePreview').hide();
        }
        $('#editImage').val('');

        // Stocker les IDs pour utilisation ultérieure
        pendingCatId = catId;
        pendingSousCatId = sousCatId;

        // Gestion de la catégorisation hiérarchique
        if (rayonId && rayonId !== "") {
            // Déclencher le chargement des catégories
            $('#editRayon').trigger('change');
        } else {
            // Réinitialiser si pas de rayon
            $('#editCategorie').empty().append('<option value="">-- Sélectionnez d\'abord un rayon --</option>').prop('disabled', true);
            $('#editSousCategorie').empty().append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>').prop('disabled', true);
        }
    });
    
    // Gérer le changement de rayon
    $('#editRayon').on('change', function() {
        const rayonId = $(this).val();
        const $categorieSelect = $('#editCategorie');
        const $sousCategorieSelect = $('#editSousCategorie');
        
        // Réinitialiser les selects
        $categorieSelect.empty().prop('disabled', true).prop('required', false);
        $sousCategorieSelect.empty().prop('disabled', true).prop('required', false);
        
        if (!rayonId) {
            $categorieSelect.append('<option value="">-- Sélectionnez d\'abord un rayon --</option>');
            $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
            return;
        }
        
        // Afficher un indicateur de chargement
        $categorieSelect.append('<option value="">Chargement des catégories...</option>');
        $categorieSelect.prop('disabled', true);
        
        // Charger les catégories du rayon sélectionné
        $.ajax({
            url: 'MenuServlet?action=categories-by-rayon&rayonId=' + rayonId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $categorieSelect.empty();
                
                if (data.categories && data.categories.length > 0) {
                    $categorieSelect.append('<option value="">-- Sélectionner une catégorie --</option>');
                    
                    data.categories.forEach(function(cat) {
                        $categorieSelect.append(
                            $('<option>', { 
                                value: cat.id, 
                                text: cat.nom 
                            })
                        );
                    });
                    
                    $categorieSelect.prop('disabled', false);
                    $categorieSelect.prop('required', true);
                    
                    // Si on a une catégorie en attente, la présélectionner
                    if (pendingCatId && pendingCatId !== "") {
                        $categorieSelect.val(pendingCatId);
                        // Déclencher le chargement des sous-catégories
                        $categorieSelect.trigger('change');
                    }
                    
                } else {
                    $categorieSelect.append('<option value="">Aucune catégorie disponible pour ce rayon</option>');
                    $categorieSelect.prop('disabled', true);
                    $categorieSelect.prop('required', false);
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des catégories:", error);
                $categorieSelect.empty();
                $categorieSelect.append('<option value="">Erreur de chargement</option>');
                $categorieSelect.prop('disabled', true);
                $categorieSelect.prop('required', false);
            }
        });
    });
    
    // Gérer le changement de catégorie
    $('#editCategorie').on('change', function() {
        const categorieId = $(this).val();
        const $sousCategorieSelect = $('#editSousCategorie');
        
        // Réinitialiser le select des sous-catégories
        $sousCategorieSelect.empty().prop('disabled', true).prop('required', false);
        
        if (!categorieId) {
            $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
            return;
        }
        
        // Afficher un indicateur de chargement
        $sousCategorieSelect.append('<option value="">Chargement des sous-catégories...</option>');
        $sousCategorieSelect.prop('disabled', true);
        
        // Charger les sous-catégories de la catégorie sélectionnée
        $.ajax({
            url: 'MenuServlet?action=sous-categories&parentId=' + categorieId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $sousCategorieSelect.empty();
                
                if (data.sousCategories && data.sousCategories.length > 0) {
                    $sousCategorieSelect.append('<option value="">-- Sélectionner une sous-catégorie --</option>');
                    
                    data.sousCategories.forEach(function(cat) {
                        $sousCategorieSelect.append(
                            $('<option>', { 
                                value: cat.id, 
                                text: cat.nom 
                            })
                        );
                    });
                    
                    $sousCategorieSelect.prop('disabled', false);
                    $sousCategorieSelect.prop('required', true);
                    
                    // Si on a une sous-catégorie en attente, la présélectionner
                    if (pendingSousCatId && pendingSousCatId !== "") {
                        $sousCategorieSelect.val(pendingSousCatId);
                    }
                    
                } else {
                    $sousCategorieSelect.append('<option value="">Aucune sous-catégorie disponible</option>');
                    $sousCategorieSelect.prop('disabled', true);
                    $sousCategorieSelect.prop('required', false);
                }
                
                // Réinitialiser les IDs en attente après utilisation
                pendingCatId = null;
                pendingSousCatId = null;
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des sous-catégories:", error);
                $sousCategorieSelect.empty();
                $sousCategorieSelect.append('<option value="">Erreur de chargement</option>');
                $sousCategorieSelect.prop('disabled', true);
                $sousCategorieSelect.prop('required', false);
            }
        });
    });

    // Réinitialiser les IDs en attente quand le modal se ferme
    $('.modal-update-produit').on('hidden.bs.modal', function () {
        pendingCatId = null;
        pendingSousCatId = null;
    });

    // Preview dynamique à la sélection d'une nouvelle image
    $('#editImage').change(function (event) {
        const input = event.target;
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#editImagePreview').attr('src', e.target.result).show();
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            $('#editImagePreview').hide();
        }
    });

    // Validation du formulaire d'édition
    $('#formEditProduit').on('submit', function(e) {
        const rayonId = $('#editRayon').val();
        const categorieId = $('#editCategorie').val();
        const sousCategorieId = $('#editSousCategorie').val();
        
        // Validation rayon
        if (!rayonId) {
            e.preventDefault();
            alert('Veuillez sélectionner un rayon.');
            return false;
        }
        
        // Validation catégorie
        if (!categorieId) {
            e.preventDefault();
            alert('Veuillez sélectionner une catégorie.');
            return false;
        }
        
        // Validation sous-catégorie
        if (!sousCategorieId) {
            e.preventDefault();
            alert('Veuillez sélectionner une sous-catégorie.');
            return false;
        }
        
        return true;
    });
});
</script>

	<!-- Script pour l'ajustement de stock -->
	<script>
$(document).ready(function() {
    $('body').on('click', '.btn-ajuster-stock', function(e) {
        e.preventDefault();
        
        const produitId = $(this).data('id');
        const produitNom = $(this).data('nom');
        const stockActuel = $(this).data('stock');
        
        $('#ajustProduitId').val(produitId);
        $('#modalProduitNom').text(produitNom);
        $('#ajustStockActuelVisuel').text(stockActuel);
        $('#ajustNouvelleQteVisuel').text(stockActuel);
        $('#ajustValeur').val('');
        $('#differenceBadge').hide().empty();
        
        $('#modalAjustement').modal('show');
    });
    
    $('#ajustValeur').on('input', function() {
        const stockActuel = parseFloat($('#ajustStockActuelVisuel').text()) || 0;
        const ajustement = parseFloat($(this).val()) || 0;
        const nouvelleQte = stockActuel + ajustement;
        
        // Mettre à jour l'affichage
        $('#ajustNouvelleQteVisuel').text(nouvelleQte);
        
        // Gérer le badge de différence
        const differenceBadge = $('#differenceBadge');
        if (ajustement !== 0) {
            let badgeClass = 'badge ';
            let badgeText = '';
            
            if (ajustement > 0) {
                badgeClass += 'badge-success';
                badgeText = `+${ajustement}`;
                $('#ajustNouvelleQteVisuel').css('color', '#28a745');
            } else {
                badgeClass += 'badge-danger ml-2';
                badgeText = ajustement.toString();
                $('#ajustNouvelleQteVisuel').css('color', '#dc3545');
                
                if (nouvelleQte < 0) {
                    $('#ajustNouvelleQteVisuel').css('color', '#ffc107');
                }
            }
            
            differenceBadge.text(badgeText)
                .removeClass()
                .addClass(badgeClass)
                .show();
        } else {
            differenceBadge.hide();
            $('#ajustNouvelleQteVisuel').css('color', '#000');
        }
    });
    
    $('#modalAjustement').on('hidden.bs.modal', function() {
        $('#formAjustement')[0].reset();
        $('#modalProduitNom').text('');
        $('#ajustNouvelleQteVisuel').css('color', '#000');
    });
});
</script>

	<!-- Script pour les mouvements de stock (clique sur nom ET bouton) -->
	<script>
document.addEventListener("DOMContentLoaded", () => {
    const table = $('#produit-table').DataTable({
        order: [[0, 'desc']]
    });

    function showMouvementStock(produitId, tr, row) {
        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
        } else {
            fetch('MouvementStockServlet?action=get-mouvement-produit&id=' + produitId)
                .then(res => {
                    if (!res.ok) throw new Error('Erreur serveur');
                    return res.json();
                })
                .then(data => {
                    if (Array.isArray(data) && data.length > 0) {
                        let html = '<div class="p-3 border bg-light">';
                        html += '<h6 class="mb-3">Mouvements de stock</h6>';
                        html += '<table class="table table-sm table-striped mb-0">';
                        html += '<thead><tr>' +
                            '<th>Date</th>' +
                            '<th>Type</th>' +
                            '<th>Stock Début</th>' +
                            '<th>Entrée</th>' +
                            '<th>Sortie</th>' +
                            '<th>Stock Fin</th>' +
                            '</tr></thead><tbody>';

                        data.forEach(mvt => {
                            const date = new Date(mvt.date);
                            const formattedDate = date.toLocaleString();

                            html += '<tr>';
                            html += '<td>' + formattedDate + '</td>';
                            html += '<td>' + mvt.typeMouvement + '</td>';
                            html += '<td>' + mvt.stockDebut + '</td>';
                            html += '<td>' + (mvt.qteIn > 0 ? mvt.qteIn : '-') + '</td>';
                            html += '<td>' + (mvt.qteOut > 0 ? mvt.qteOut : '-') + '</td>';
                            html += '<td>' + mvt.stockFin + '</td>';
                            html += '</tr>';
                        });

                        html += '</tbody></table>';
                        html += '</div>';
                        row.child(html).show();
                        tr.addClass('shown');
                    } else {
                        row.child("<div class='p-3 border bg-light'><div class='p-2'>Aucun mouvement de stock trouvé pour ce produit.</div></div>").show();
                        tr.addClass('shown');
                    }
                })
                .catch(err => {
                    console.error(err);
                    row.child("<div class='p-3 border bg-light'><div class='p-2 text-danger'>Erreur lors du chargement des mouvements.</div></div>").show();
                    tr.addClass('shown');
                });
        }
    }

    // Gestion du clic sur le nom du produit
    $('#produit-table tbody').on('click', '.btn-show-mouvement-nom', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const produitId = $(this).data('id');

        showMouvementStock(produitId, tr, row);
    });

    // Gestion du clic sur le bouton "Mouvement stock" dans le menu déroulant
    $('#produit-table tbody').on('click', '.btn-show-mouvement', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const produitId = $(this).data('id');

        showMouvementStock(produitId, tr, row);
    });
});
</script>
	<!-- </body>
</html> -->