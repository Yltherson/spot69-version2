<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.List,com.spot69.model.PointConfig"%>
<meta charset="UTF-8">
<%
List<PointConfig> configurations = (List<PointConfig>) request.getAttribute("configurations");
String typeFilter = (String) request.getAttribute("typeFilter");
String statutFilter = (String) request.getAttribute("statutFilter");
%>


<!-- CONFIGURATION TOTALE -->
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
            <div class="col-12">
                <h2 class="h5 page-title">
                    <i class="fe fe-settings fe-32 align-self-center text-warning"></i>
                    Configuration des Points
                </h2>
                </div>
                </div>
                  <!-- Onglets de navigation -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="nav-wrapper">
                            <ul class="nav nav-tabs" id="configTabs" role="tablist">
                                <li class="nav-item">
                                    <a class="nav-link active" id="produit-tab" data-toggle="tab" href="#produit" role="tab">
                                        <i class="fe fe-package"></i> Produits
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" id="categorie-tab" data-toggle="tab" href="#categorie" role="tab">
                                        <i class="fe fe-layers"></i> Catégories
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" id="commande-tab" data-toggle="tab" href="#commande" role="tab">
                                        <i class="fe fe-shopping-cart"></i> Commandes
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" id="montant-tab" data-toggle="tab" href="#montant" role="tab">
                                        <i class="fe fe-dollar-sign"></i> Montants
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" id="liste-tab" data-toggle="tab" href="#liste" role="tab">
                                        <i class="fe fe-list"></i> Toutes les configurations
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                
                <!-- Contenu des onglets -->
                <div class="tab-content" id="configTabsContent">
                    
                    <!-- Onglet Produits -->
                    <div class="tab-pane fade show active" id="produit" role="tabpanel">
                        <div class="card shadow">
                            <div class="card-header">
                                <h4 class="card-title">Configuration des points par produit</h4>
                                <p class="card-text">Attribuez des points spécifiques à des produits</p>
                            </div>
                            <div class="card-body">
                                <!-- Recherche et sélection de produit -->
                                <div class="row mb-4">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label>Rechercher un produit</label>
                                            <input type="text" class="form-control" id="searchProduit" 
                                                   placeholder="Tapez le nom du produit...">
                                            <div id="produit-suggestions" class="autocomplete-suggestions" 
                                                 style="display: none;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="form-group">
                                            <label>Points par unité</label>
                                            <input type="number" class="form-control" id="pointsProduit" 
                                                   placeholder="Points" min="0">
                                        </div>
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end">
                                        <button type="button" class="btn btn-primary w-100" 
                                                onclick="ajouterProduitConfiguration()">
                                            <i class="fe fe-plus"></i> Ajouter
                                        </button>
                                    </div>
                                </div>
                                
                                <!-- Liste des produits sélectionnés -->
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="tableProduitsConfig">
                                        <thead>
                                            <tr>
                                                <th width="50px">#</th>
                                                <th>Produit</th>
                                                <th>Image</th>
                                                <th>Points par unité</th>
                                                <th width="100px">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="produitsConfigBody">
                                            <!-- Les lignes seront ajoutées dynamiquement -->
                                        </tbody>
                                    </table>
                                </div>
                                
                                <!-- Bouton d'enregistrement -->
                                <div class="text-right mt-3">
                                    <button type="button" class="btn btn-success" 
                                            onclick="enregistrerConfigProduits()">
                                        <i class="fe fe-save"></i> Enregistrer les configurations
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Onglet Catégories -->
                    <div class="tab-pane fade" id="categorie" role="tabpanel">
                        <div class="card shadow">
                            <div class="card-header">
                                <h4 class="card-title">Configuration des points par catégorie</h4>
                                <p class="card-text">Attribuez des points à des catégories ou sous-catégories</p>
                            </div>
                            <div class="card-body">
                                <form id="formCategorieConfig">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label>Type de catégorie</label>
                                                <select class="form-control" id="typeCategorie" 
                                                        onchange="chargerCategories()">
                                                    <option value="CATEGORIE">Catégorie principale</option>
                                                    <option value="SOUS_CATEGORIE">Sous-catégorie</option>
                                                </select>
                                            </div>
                                        </div>
                                        
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label id="labelCategorie">Catégorie</label>
                                                <select class="form-control" id="selectCategorie" required>
                                                    <option value="">-- Chargement --</option>
                                                </select>
                                            </div>
                                        </div>
                                        
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label>Points par unité</label>
                                                <input type="number" class="form-control" id="pointsCategorie" 
                                                       placeholder="Points" min="0" required>
                                            </div>
                                        </div>
                                        
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label>Date d'expiration (optionnel)</label>
                                                <input type="date" class="form-control" id="dateFinCategorie">
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="text-right">
                                        <button type="button" class="btn btn-success" 
                                                onclick="enregistrerConfigCategorie()">
                                            <i class="fe fe-save"></i> Enregistrer
                                        </button>
                                    </div>
                                </form>
                                
                                <!-- Liste des configurations existantes -->
                                <div class="mt-4">
                                    <h5>Configurations existantes</h5>
                                    <div id="listeConfigCategories"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Onglet Commandes -->
                    <div class="tab-pane fade" id="commande" role="tabpanel">
                        <div class="card shadow">
                            <div class="card-header">
                                <h4 class="card-title">Configuration des points par quantité de commandes</h4>
                                <p class="card-text">Bonus de points basés sur le nombre de commandes journalières</p>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="tableCommandesConfig">
                                        <thead>
                                            <tr>
                                                <th>Nombre minimum de commandes</th>
                                                <th>Points bonus</th>
                                                <th width="100px">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="commandesConfigBody">
                                            <tr>
                                                <td>
                                                    <input type="number" class="form-control" name="conditionValeur[]" 
                                                           placeholder="Ex: 2" min="1">
                                                </td>
                                                <td>
                                                    <input type="number" class="form-control" name="points[]" 
                                                           placeholder="Points" min="0">
                                                </td>
                                                <td>
                                                    <button type="button" class="btn btn-danger btn-sm" 
                                                            onclick="supprimerLigneCommande(this)">
                                                        <i class="fe fe-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-12">
                                        <button type="button" class="btn btn-primary" 
                                                onclick="ajouterLigneCommande()">
                                            <i class="fe fe-plus"></i> Ajouter une condition
                                        </button>
                                    </div>
                                </div>
                                
                                <div class="text-right mt-3">
                                    <button type="button" class="btn btn-success" 
                                            onclick="enregistrerConfigCommandes()">
                                        <i class="fe fe-save"></i> Enregistrer
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Onglet Montants -->
                    <div class="tab-pane fade" id="montant" role="tabpanel">
                        <div class="card shadow">
                            <div class="card-header">
                                <h4 class="card-title">Configuration des points par montant total</h4>
                                <p class="card-text">Bonus de points basés sur le montant total de la commande</p>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="tableMontantsConfig">
                                        <thead>
                                            <tr>
                                                <th>Montant minimum</th>
                                                <th>Points bonus</th>
                                                <th width="100px">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="montantsConfigBody">
                                            <tr>
                                                <td>
                                                    <input type="number" class="form-control" name="conditionValeur[]" 
                                                           placeholder="Ex: 1000" min="0" step="0.01">
                                                </td>
                                                <td>
                                                    <input type="number" class="form-control" name="points[]" 
                                                           placeholder="Points" min="0">
                                                </td>
                                                <td>
                                                    <button type="button" class="btn btn-danger btn-sm" 
                                                            onclick="supprimerLigneMontant(this)">
                                                        <i class="fe fe-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-12">
                                        <button type="button" class="btn btn-primary" 
                                                onclick="ajouterLigneMontant()">
                                            <i class="fe fe-plus"></i> Ajouter une condition
                                        </button>
                                    </div>
                                </div>
                                
                                <div class="text-right mt-3">
                                    <button type="button" class="btn btn-success" 
                                            onclick="enregistrerConfigMontants()">
                                        <i class="fe fe-save"></i> Enregistrer
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Onglet Liste complète -->
                    <div class="tab-pane fade" id="liste" role="tabpanel">
                        <div class="card shadow">
                            <div class="card-body">
                                <!-- Filtres -->
                                <div class="row mb-3">
                                    <div class="col-md-4">
                                        <div class="form-group">
                                            <label>Filtrer par type</label>
                                            <select class="form-control" id="filterType" onchange="filtrerConfigurations()">
                                                <option value="">Tous les types</option>
                                                <option value="PRODUIT">Produit</option>
                                                <option value="CATEGORIE">Catégorie</option>
                                                <option value="SOUS_CATEGORIE">Sous-catégorie</option>
                                                <option value="QTE_COMMANDE_JOURNALIERE">Commandes journalières</option>
                                                <option value="MONTANT_TOTAL">Montant total</option>
                                            </select>
                                        </div>
                                    </div>
                                    
                                    <div class="col-md-4">
                                        <div class="form-group">
                                            <label>Filtrer par statut</label>
                                            <select class="form-control" id="filterStatut" onchange="filtrerConfigurations()">
                                                <option value="">Tous les statuts</option>
                                                <option value="ACTIF">Actif</option>
                                                <option value="INACTIF">Inactif</option>
                                                <option value="DELETED">Supprimé</option>
                                            </select>
                                        </div>
                                    </div>
                                    
                                    <div class="col-md-4 d-flex align-items-end">
                                        <button type="button" class="btn btn-primary w-100" 
                                                onclick="chargerToutesConfigurations()">
                                            <i class="fe fe-refresh-cw"></i> Actualiser
                                        </button>
                                    </div>
                                </div>
                                
                                <!-- Table des configurations -->
                                <div class="table-responsive">
                                    <table class="table table-hover" id="tableAllConfigs">
                                        <thead>
                                            <tr>
                                                <th>Type</th>
                                                <th>Référence</th>
                                                <th>Points</th>
                                                <th>Condition</th>
                                                <th>Date début</th>
                                                <th>Date fin</th>
                                                <th>Statut</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="allConfigsBody">
                                            <% if (configurations != null && !configurations.isEmpty()) { 
                                                for (PointConfig Theconfig : configurations) { 
                                                    String badgeClass = Theconfig.getStatut().equals("ACTIF") ? "badge-success" : 
                                                    	Theconfig.getStatut().equals("INACTIF") ? "badge-warning" : "badge-danger";
                                            %>
                                            <tr data-type="<%= Theconfig.getTypeConfig() %>" 
                                                data-statut="<%= Theconfig.getStatut() %>">
                                                <td><span class="badge badge-primary"><%= Theconfig.getTypeConfig() %></span></td>
                                                <td>
                                                    <%= Theconfig.getRefId() != null ? Theconfig.getRefId() : "N/A" %>
                                                    <br><small class="text-muted">
                                                        <%= getNomReference(Theconfig.getTypeConfig(), Theconfig.getRefId()) %>
                                                    </small>
                                                </td>
                                                <td><strong><%= Theconfig.getPoints() %></strong></td>
                                                <td>
                                                    <% if (Theconfig.getConditionValeur() != null) { %>
                                                    <%= Theconfig.getConditionType() %> <%= Theconfig.getConditionValeur() %>
                                                    <% } else { %>
                                                    -
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <%= Theconfig.getDateDebut() != null ? 
                                                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(Theconfig.getDateDebut()) : "-" %>
                                                </td>
                                                <td>
                                                    <%= Theconfig.getDateFin() != null ? 
                                                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(Theconfig.getDateFin()) : "-" %>
                                                </td>
                                                <td><span class="badge <%= badgeClass %>"><%= Theconfig.getStatut() %></span></td>
                                                <td>
                                                    <div class="btn-group" role="group">
                                                        <a href="PointServlet?action=detailConfig&id=<%= Theconfig.getId() %>" 
                                                           class="btn btn-sm btn-outline-info">
                                                            <i class="fe fe-eye"></i>
                                                        </a>
                                                        <a href="PointServlet?action=formConfig&id=<%= Theconfig.getId() %>" 
                                                           class="btn btn-sm btn-outline-warning">
                                                            <i class="fe fe-edit"></i>
                                                        </a>
                                                        <% if (!Theconfig.getStatut().equals("DELETED")) { %>
                                                        <form method="POST" action="PointServlet" 
                                                              onsubmit="return confirm('Supprimer cette configuration?')" 
                                                              style="display: inline;">
                                                            <input type="hidden" name="action" value="supprimerConfig">
                                                            <input type="hidden" name="id" value="<%= Theconfig.getId() %>">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                <i class="fe fe-trash"></i>
                                                            </button>
                                                        </form>
                                                        <% } %>
                                                    </div>
                                                </td>
                                            </tr>
                                            <% } 
                                            } %>
                                        </tbody>
                                    </table>
                                </div>
                                
                                <% if (configurations == null || configurations.isEmpty()) { %>
                                <div class="text-center py-5">
                                    <i class="fe fe-settings fe-64 text-muted"></i>
                                    <h4 class="mt-3">Aucune configuration</h4>
                                    <p class="text-muted">Commencez par créer des configurations dans les onglets ci-dessus.</p>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
  
      </div>
</main>
<!-- Modal pour confirmation -->
<div class="modal fade" id="confirmationModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmation</h5>
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="confirmationMessage">
                <!-- Message dynamique -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                <button type="button" class="btn btn-primary" id="confirmActionBtn">Confirmer</button>
            </div>
        </div>
    </div>
</div>


<script>
// Variables globales
let produitsSelectionnes = [];
let configurationsChargees = [];
const ctx = "<%= request.getContextPath() %>";

// Initialisation
$(document).ready(function() {
    // Charger les configurations existantes
    chargerConfigurations();
    
    // Initialiser l'autocomplete produits
    initAutocompleteProduits();
    
    // Charger les catégories
    chargerCategories();
});

// Fonction pour obtenir le nom d'une référence
<%!
    public String getNomReference(String type, Integer refId) {
        if (refId == null) return "N/A";
        
        try {
            switch(type) {
                case "PRODUIT":
                    com.spot69.dao.ProduitDAO produitDAO = new com.spot69.dao.ProduitDAO();
                    com.spot69.model.Produit produit = produitDAO.chercherParId(refId);
                    return produit != null ? produit.getNom() : "Produit #" + refId;
                    
                case "CATEGORIE":
                case "SOUS_CATEGORIE":
                    com.spot69.dao.MenuCategorieDAO categorieDAO = new com.spot69.dao.MenuCategorieDAO();
                    com.spot69.model.MenuCategorie categorie = categorieDAO.getById(refId);
                    return categorie != null ? categorie.getNom() : "Catégorie #" + refId;
                    
                default:
                    return refId.toString();
            }
        } catch (Exception e) {
            return "Erreur";
        }
    }
%>

// Autocomplete produits
function initAutocompleteProduits() {
    const inputProduit = document.getElementById('searchProduit');
    const suggestionsDiv = document.getElementById('produit-suggestions');
    
    inputProduit.addEventListener('input', function() {
        const val = this.value.toLowerCase();
        if (val.length < 2) {
            suggestionsDiv.style.display = 'none';
            return;
        }
        
        // Recherche AJAX des produits
        $.ajax({
            url: 'ProduitServlet?action=recherche&term=' + encodeURIComponent(val),
            type: 'GET',
            dataType: 'json',
            success: function(produits) {
                suggestionsDiv.innerHTML = '';
                
                if (produits && produits.length > 0) {
                    produits.forEach(function(produit) {
                        // Vérifier si le produit est déjà sélectionné
                        if (produitsSelectionnes.find(p => p.id === produit.id)) {
                            return; // Passer au suivant
                        }
                        
                        const div = document.createElement('div');
                        div.className = 'autocomplete-suggestion';
                        div.style.display = 'flex';
                        div.style.alignItems = 'center';
                        div.style.padding = '8px';
                        div.style.cursor = 'pointer';
                        div.style.borderBottom = '1px solid #333';
                        
                        // Image du produit
                        const img = document.createElement('img');
                        let imagePath = '';
                        if (produit.imageUrl && produit.imageUrl.startsWith("uploads/produits/")) {
                            imagePath = ctx + '/images/produits/' + produit.imageUrl.substring("uploads/produits/".length);
                        } else {
                            imagePath = ctx + '/images/default/default.jpg';
                        }
                        img.src = imagePath;
                        img.alt = produit.nom;
                        img.style.width = '40px';
                        img.style.height = '40px';
                        img.style.objectFit = 'cover';
                        img.style.marginRight = '10px';
                        img.style.borderRadius = '4px';
                        
                        // Nom et code
                        const infoDiv = document.createElement('div');
                        infoDiv.style.flex = '1';
                        
                        const nomSpan = document.createElement('span');
                        nomSpan.textContent = produit.nom;
                        nomSpan.style.fontWeight = 'bold';
                        nomSpan.style.display = 'block';
                        
                        const codeSpan = document.createElement('span');
                        codeSpan.textContent = 'Code: ' + (produit.codeProduit || 'N/A');
                        codeSpan.style.fontSize = '12px';
                        codeSpan.style.color = '#666';
                        
                        infoDiv.appendChild(nomSpan);
                        infoDiv.appendChild(codeSpan);
                        
                        // Bouton d'ajout
                        const addBtn = document.createElement('button');
                        addBtn.type = 'button';
                        addBtn.className = 'btn btn-sm btn-success';
                        addBtn.innerHTML = '<i class="fe fe-plus"></i>';
                        addBtn.onclick = function(e) {
                            e.stopPropagation();
                            ajouterProduitAListe(produit);
                            inputProduit.value = '';
                            suggestionsDiv.style.display = 'none';
                        };
                        
                        div.appendChild(img);
                        div.appendChild(infoDiv);
                        div.appendChild(addBtn);
                        suggestionsDiv.appendChild(div);
                    });
                    
                    if (suggestionsDiv.children.length > 0) {
                        const rect = inputProduit.getBoundingClientRect();
                        suggestionsDiv.style.position = 'absolute';
                        suggestionsDiv.style.width = rect.width + 'px';
                        suggestionsDiv.style.top = (rect.bottom + window.scrollY) + 'px';
                        suggestionsDiv.style.left = (rect.left + window.scrollX) + 'px';
                        suggestionsDiv.style.zIndex = '1000';
                        suggestionsDiv.style.display = 'block';
                        suggestionsDiv.style.maxHeight = '300px';
                        suggestionsDiv.style.overflowY = 'auto';
                        suggestionsDiv.style.backgroundColor = '#fff';
                        suggestionsDiv.style.border = '1px solid #ddd';
                        suggestionsDiv.style.borderRadius = '4px';
                    } else {
                        suggestionsDiv.style.display = 'none';
                    }
                } else {
                    suggestionsDiv.style.display = 'none';
                }
            }
        });
    });
    
    // Cacher les suggestions quand on clique ailleurs
    document.addEventListener('click', function(e) {
        if (e.target !== inputProduit && !suggestionsDiv.contains(e.target)) {
            suggestionsDiv.style.display = 'none';
        }
    });
}

function ajouterProduitAListe(produit) {
    // Vérifier si déjà présent
    if (produitsSelectionnes.find(p => p.id === produit.id)) {
        showAlert('Ce produit est déjà dans la liste', 'warning');
        return;
    }
    
    produitsSelectionnes.push(produit);
    mettreAJourTableProduits();
}

function mettreAJourTableProduits() {
    const tbody = document.getElementById('produitsConfigBody');
    tbody.innerHTML = '';
    
    produitsSelectionnes.forEach((produit, index) => {
        const imagePath = produit.imageUrl && produit.imageUrl.startsWith("uploads/produits/") 
            ? ctx + '/images/produits/' + produit.imageUrl.substring("uploads/produits/".length)
            : ctx + '/images/default/default.jpg';
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>
                <div style="display: flex; align-items: center;">
                    <img src="${imagePath}" alt="${produit.nom}" 
                         style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px; margin-right: 10px;">
                    <div>
                        <strong>${produit.nom}</strong><br>
                        <small class="text-muted">${produit.codeProduit ? produit.codeProduit : 'N/A'}</small>
                    </div>
                </div>
            </td>
            <td>
                <img src="${imagePath}" alt="${produit.nom}" 
                     style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;">
            </td>
            <td>
                <input type="number" class="form-control points-input" 
                       data-id="${produit.id}" placeholder="Points" min="0" 
                       value="${produit.points != null ? produit.points : 0}">
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm" 
                        onclick="supprimerProduitListe(${produit.id})">
                    <i class="fe fe-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
function supprimerProduitListe(produitId) {
    produitsSelectionnes = produitsSelectionnes.filter(p => p.id !== produitId);
    mettreAJourTableProduits();
}

function ajouterProduitConfiguration() {
    const produitNom = document.getElementById('searchProduit').value;
    const points = document.getElementById('pointsProduit').value;
    
    if (!produitNom || !points) {
        showAlert('Veuillez sélectionner un produit et saisir des points', 'warning');
        return;
    }
}

function enregistrerConfigProduits() {
    if (produitsSelectionnes.length === 0) {
        showAlert('Aucun produit sélectionné', 'warning');
        return;
    }
    
    const configs = [];
    document.querySelectorAll('.points-input').forEach(input => {
        const produitId = input.getAttribute('data-id');
        const points = input.value;
        
        if (points && points > 0) {
            const produit = produitsSelectionnes.find(p => p.id == produitId);
            configs.push({
                produitId: produitId,
                produitNom: produit.nom,
                points: points
            });
        }
    });
    
    if (configs.length === 0) {
        showAlert('Veuillez saisir des points pour au moins un produit', 'warning');
        return;
    }
    
    // Envoyer au serveur
    $.ajax({
        url: 'PointServlet',
        type: 'POST',
        data: {
            action: 'mettreAJourPointsProduit',
            produitIds: configs.map(c => c.produitId).join(','),
            points: configs[0].points, // Même points pour tous
            typeConfig: 'PRODUIT'
        },
        success: function(response) {
            showAlert('Configurations enregistrées avec succès', 'success');
            produitsSelectionnes = [];
            document.getElementById('searchProduit').value = '';
            document.getElementById('pointsProduit').value = '';
            mettreAJourTableProduits();
            chargerConfigurations();
        },
        error: function() {
            showAlert('Erreur lors de l\'enregistrement', 'error');
        }
    });
}

// Gestion des catégories
function chargerCategories() {
    const type = document.getElementById('typeCategorie').value;
    const label = type === 'CATEGORIE' ? 'Catégorie' : 'Sous-catégorie';
    document.getElementById('labelCategorie').textContent = label;
    
    let url = 'MenuServlet?action=getCategories';
    if (type === 'SOUS_CATEGORIE') {
        url = 'MenuServlet?action=getSousCategories';
    }
    
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            const select = document.getElementById('selectCategorie');
            select.innerHTML = '<option value="">-- Sélectionner --</option>';
            
            if (data && data.length > 0) {
                data.forEach(function(item) {
                    const option = document.createElement('option');
                    option.value = item.id;
                    option.textContent = item.nom;
                    select.appendChild(option);
                });
            }
        }
    });
}

function enregistrerConfigCategorie() {
    const type = document.getElementById('typeCategorie').value;
    const categorieId = document.getElementById('selectCategorie').value;
    const points = document.getElementById('pointsCategorie').value;
    const dateFin = document.getElementById('dateFinCategorie').value;
    
    if (!categorieId || !points) {
        showAlert('Veuillez remplir tous les champs obligatoires', 'warning');
        return;
    }
    
    $.ajax({
        url: 'PointServlet',
        type: 'POST',
        data: {
            action: 'mettreAJourPointsCategorie',
            categorieId: categorieId,
            sousCategorieId: type === 'SOUS_CATEGORIE' ? categorieId : '',
            points: points,
            typeConfig: type,
            dateFin: dateFin
        },
        success: function(response) {
            showAlert('Configuration enregistrée avec succès', 'success');
            document.getElementById('pointsCategorie').value = '';
            document.getElementById('dateFinCategorie').value = '';
            chargerConfigurations();
        },
        error: function() {
            showAlert('Erreur lors de l\'enregistrement', 'error');
        }
    });
}

// Gestion des commandes
function ajouterLigneCommande() {
    const tbody = document.getElementById('commandesConfigBody');
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td>
            <input type="number" class="form-control" name="conditionValeur[]" 
                   placeholder="Ex: 2" min="1">
        </td>
        <td>
            <input type="number" class="form-control" name="points[]" 
                   placeholder="Points" min="0">
        </td>
        <td>
            <button type="button" class="btn btn-danger btn-sm" 
                    onclick="supprimerLigneCommande(this)">
                <i class="fe fe-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(tr);
}

function supprimerLigneCommande(button) {
    const tr = button.closest('tr');
    if (document.getElementById('commandesConfigBody').children.length > 1) {
        tr.remove();
    }
}

function enregistrerConfigCommandes() {
    const lignes = document.querySelectorAll('#commandesConfigBody tr');
    const configs = [];
    
    lignes.forEach(tr => {
        const condition = tr.querySelector('input[name="conditionValeur[]"]').value;
        const points = tr.querySelector('input[name="points[]"]').value;
        
        if (condition && points) {
            configs.push({
                conditionValeur: condition,
                points: points
            });
        }
    });
    
    if (configs.length === 0) {
        showAlert('Veuillez remplir au moins une condition', 'warning');
        return;
    }
    
    // Enregistrer chaque configuration
    let succes = 0;
    configs.forEach((config, index) => {
        $.ajax({
            url: 'PointServlet',
            type: 'POST',
            async: false,
            data: {
                action: 'creerConfig',
                typeConfig: 'QTE_COMMANDE_JOURNALIERE',
                conditionValeur: config.conditionValeur,
                conditionType: '>=',
                points: config.points,
                statut: 'ACTIF'
            },
            success: function() {
                succes++;
            }
        });
    });
    
    if (succes > 0) {
        showAlert(succes + ' configuration(s) enregistrée(s) avec succès', 'success');
        chargerConfigurations();
    }
}

// Gestion des montants (similaire aux commandes)
function ajouterLigneMontant() {
    const tbody = document.getElementById('montantsConfigBody');
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td>
            <input type="number" class="form-control" name="conditionValeur[]" 
                   placeholder="Ex: 1000" min="0" step="0.01">
        </td>
        <td>
            <input type="number" class="form-control" name="points[]" 
                   placeholder="Points" min="0">
        </td>
        <td>
            <button type="button" class="btn btn-danger btn-sm" 
                    onclick="supprimerLigneMontant(this)">
                <i class="fe fe-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(tr);
}

function supprimerLigneMontant(button) {
    const tr = button.closest('tr');
    if (document.getElementById('montantsConfigBody').children.length > 1) {
        tr.remove();
    }
}

function enregistrerConfigMontants() {
    const lignes = document.querySelectorAll('#montantsConfigBody tr');
    const configs = [];
    
    lignes.forEach(tr => {
        const condition = tr.querySelector('input[name="conditionValeur[]"]').value;
        const points = tr.querySelector('input[name="points[]"]').value;
        
        if (condition && points) {
            configs.push({
                conditionValeur: condition,
                points: points
            });
        }
    });
    
    if (configs.length === 0) {
        showAlert('Veuillez remplir au moins une condition', 'warning');
        return;
    }
    
    // Enregistrer chaque configuration
    let succes = 0;
    configs.forEach(config => {
        $.ajax({
            url: 'PointServlet',
            type: 'POST',
            async: false,
            data: {
                action: 'creerConfig',
                typeConfig: 'MONTANT_TOTAL',
                conditionValeur: config.conditionValeur,
                conditionType: '>=',
                points: config.points,
                statut: 'ACTIF'
            },
            success: function() {
                succes++;
            }
        });
    });
    
    if (succes > 0) {
        showAlert(succes + ' configuration(s) enregistrée(s) avec succès', 'success');
        chargerConfigurations();
    }
}

// Chargement et filtrage des configurations
function chargerConfigurations() {
    $.ajax({
        url: 'PointServlet?action=getConfigurationsJSON',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            configurationsChargees = data;
            afficherConfigurationsFiltrees();
        }
    });
}

function filtrerConfigurations() {
    afficherConfigurationsFiltrees();
}

function afficherConfigurationsFiltrees() {
    const tbody = document.getElementById('allConfigsBody');
    const typeFilter = document.getElementById('filterType').value;
    const statutFilter = document.getElementById('filterStatut').value;
    
    tbody.innerHTML = '';
    
    const configsFiltrees = configurationsChargees.filter(configItem => {
        return (!typeFilter || configItem.typeConfig === typeFilter) &&
               (!statutFilter || configItem.statut === statutFilter);
    });
    
    if (configsFiltrees.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center py-4">
                    <i class="fe fe-search fe-32 text-muted"></i>
                    <p class="mt-2">Aucune configuration ne correspond aux filtres</p>
                </td>
            </tr>
        `;
        return;
    }
    
    configsFiltrees.forEach(configItem => {
        const tr = document.createElement('tr');
        const badgeClass = configItem.statut === 'ACTIF' ? 'badge-success' : 
                          configItem.statut === 'INACTIF' ? 'badge-warning' : 'badge-danger';
        
        // Affichage de la condition
        const conditionDisplay = configItem.conditionValeur ? 
                                configItem.conditionType + ' ' + configItem.conditionValeur : 
                                '-';
        
        // Formulaire de suppression (uniquement si non supprimé)
        let deleteForm = '';
        if (configItem.statut !== 'DELETED') {
            deleteForm = `
                <form method="POST" action="PointServlet" 
                      onsubmit="return confirm('Supprimer cette configuration?')" 
                      style="display: inline;">
                    <input type="hidden" name="action" value="supprimerConfig">
                    <input type="hidden" name="id" value="${configItem.id}">
                    <button type="submit" class="btn btn-sm btn-outline-danger">
                        <i class="fe fe-trash"></i>
                    </button>
                </form>
            `;
        }
        
        // CORRECTION : Affichez simplement l'ID de référence sans le nom
        tr.innerHTML = `
            <td><span class="badge badge-primary">${configItem.typeConfig}</span></td>
            <td>
                ${configItem.refId != null ? configItem.refId : 'N/A'}
                <!-- Supprimez l'appel à la fonction inexistante -->
            </td>
            <td><strong>${configItem.points}</strong></td>
            <td>${conditionDisplay}</td>
            <td>${configItem.dateDebut ? new Date(configItem.dateDebut).toLocaleDateString('fr-FR') : '-'}</td>
            <td>${configItem.dateFin ? new Date(configItem.dateFin).toLocaleDateString('fr-FR') : '-'}</td>
            <td><span class="badge ${badgeClass}">${configItem.statut}</span></td>
            <td>
                <div class="btn-group" role="group">
                    <a href="PointServlet?action=detailConfig&id=${configItem.id}" 
                       class="btn btn-sm btn-outline-info">
                        <i class="fe fe-eye"></i>
                    </a>
                    <a href="PointServlet?action=formConfig&id=${configItem.id}" 
                       class="btn btn-sm btn-outline-warning">
                        <i class="fe fe-edit"></i>
                    </a>
                    ${deleteForm}
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getNomReferenceClient(type, refId) {
    // Cette fonction devrait être implémentée côté serveur
    // Pour l'instant, retourne simplement l'ID
    return refId ? '#' + refId : 'N/A';
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR');
}

function showAlert(message, type) {
    const alertClass = type === 'success' ? 'alert-success' : 
                      type === 'warning' ? 'alert-warning' : 'alert-danger';
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert">
            <span aria-hidden="true">&times;</span>
        </button>
    `;
    
    // Insérer en haut du contenu principal
    const mainContent = document.querySelector('.main-content .container-fluid');
    if (mainContent.children.length > 0) {
        mainContent.insertBefore(alertDiv, mainContent.children[0]);
    } else {
        mainContent.appendChild(alertDiv);
    }
    
    // Auto-dismiss après 5 secondes
    setTimeout(() => {
        $(alertDiv).alert('close');
    }, 5000);
}

function chargerToutesConfigurations() {
    document.getElementById('filterType').value = '';
    document.getElementById('filterStatut').value = '';
    chargerConfigurations();
}
</script>



<jsp:include page="footer.jsp" />
