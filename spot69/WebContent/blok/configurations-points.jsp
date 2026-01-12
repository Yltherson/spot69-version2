<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,com.spot69.model.PointConfig,java.text.SimpleDateFormat"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<meta charset="UTF-8">
<%
List<PointConfig> configurations = (List<PointConfig>) request.getAttribute("configurations");
String typeFilter = (String) request.getAttribute("typeFilter");
String statutFilter = (String) request.getAttribute("statutFilter");
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
%>
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
                            <ul class="nav nav-tabs" id="configTabs" role="tablist" >
                                <li class="nav-item" hidden>
                                    <a class="nav-link " id="produit-tab" data-toggle="tab" href="#produit" role="tab">
                                        <i class="fe fe-package"></i> Produits
                                    </a>
                                </li>
                                <li class="nav-item" hidden>
                                    <a class="nav-link" id="categorie-tab" data-toggle="tab" href="#categorie" role="tab">
                                        <i class="fe fe-layers"></i> Catégories
                                    </a>
                                </li>
                                <li class="nav-item" hidden>
                                    <a class="nav-link" id="commande-tab" data-toggle="tab" href="#commande" role="tab">
                                        <i class="fe fe-shopping-cart"></i> Commandes
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link active" id="montant-tab" data-toggle="tab" href="#montant" role="tab">
                                        <i class="fe fe-dollar-sign"></i> Transactions
                                    </a>
                                </li>
                                <li class="nav-item">
									    <a class="nav-link" id="points-tab" data-toggle="tab" href="#points" role="tab">
									        <i class="fe fe-dollar-sign"></i> Points
									    </a>
									</li>
                                <li class="nav-item" hidden>
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
                    <div class="tab-pane fade  " id="produit" role="tabpanel">
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
                                
                                <!-- <div class="row">
                                    <div class="col-md-12">
                                        <button type="button" class="btn btn-primary" 
                                                onclick="ajouterLigneCommande()">
                                            <i class="fe fe-plus"></i> Ajouter une condition
                                        </button>
                                    </div>
                                </div> -->
                                
                                <div class="text-right mt-3">
                                    <button type="button" class="btn btn-success" 
                                            onclick="enregistrerConfigCommandes()">
                                        <i class="fe fe-save"></i> Enregistrer
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Onglet Points -->
					<div class="tab-pane fade" id="points" role="tabpanel">
					    <div class="card shadow">
					        <div class="card-header">
					            <h4 class="card-title">Configuration de conversion Points ↔ Gourdes</h4>
					            <p class="card-text">Définir la valeur des points en gourdes (ex: 1 point = 10 gourdes)</p>
					        </div>
					        <div class="card-body">
					            <div class="table-responsive">
					                <table class="table table-bordered" id="tablePointsConfig">
					                    <thead>
					                        <tr>
					                            <th>Nombre de Points</th>
					                            <th>Valeur en Gourdes</th>
					                            <th width="100px">Actions</th>
					                        </tr>
					                    </thead>
					                    <tbody id="pointsConfigBody">
					                        <!-- Les lignes seront chargées dynamiquement -->
					                    </tbody>
					                </table>
					            </div>
					            
					            <div class="row mt-3">
					                <div class="col-md-6">
					                    <!-- <button type="button" class="btn btn-primary" 
					                            onclick="ajouterLignePoint()">
					                        <i class="fe fe-plus"></i> Ajouter une conversion
					                    </button> -->
					                </div>
					                <div class="col-md-6 text-right">
					                    <button type="button" class="btn btn-success" 
					                            onclick="enregistrerConfigPoints()">
					                        <i class="fe fe-save"></i> Enregistrer
					                    </button>
					                </div>
					            </div>
					            
					            <!-- Aide et informations -->
					            <div class=" mt-3">
					                <i class="fe fe-info"></i>
					                <strong>Information :</strong> Cette configuration permet de définir la valeur des points.
					                Exemple : Si vous définissez "10" points avec "100" gourdes, cela signifie que
					                10 points valent 100 gourdes (soit 1 point = 10 gourdes).
					            </div>
					        </div>
					    </div>
					</div>
						                    
                    <!-- Onglet Montants -->
                    <div class="tab-pane fade show active" id="montant" role="tabpanel">
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
                                            <!-- Les lignes seront chargées dynamiquement -->
                                        </tbody>
                                    </table>
                                </div>
                                
                                <div class="row mt-3">
                                   <!--   <div class="col-md-6">
                                        <button type="button" class="btn btn-secondary" 
                                                onclick="chargerConfigurationsMontants()">
                                            <i class="fe fe-refresh-cw"></i> Réinitialiser
                                        </button>
                                       <button type="button" class="btn btn-primary ml-2" 
                                                onclick="ajouterLigneMontant()">
                                            <i class="fe fe-plus"></i> Ajouter une condition
                                        </button> -->
                                    </div>
                                    <div class="col-md-6 text-right">
                                        <button type="button" class="btn btn-success" 
                                                onclick="enregistrerConfigMontants()">
                                            <i class="fe fe-save"></i> Enregistrer
                                        </button>
                                    </div>
                                </div>
                                <!-- Aide et informations -->
                                <div class=" m-3">
                                    <i class="fe fe-info"></i>
                                    <strong>Information :</strong> Les points seront attribués lorsque le montant total 
                                    de la commande atteint ou dépasse le seuil spécifié. Exemple : Si vous définissez 
                                    "1000" avec "50 points", le client recevra 50 points bonus pour toute commande 
                                    d'un montant de 1000 ou plus.
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
                                                for (PointConfig configItem : configurations) { 
                                                    String badgeClass = configItem.getStatut().equals("ACTIF") ? "badge-success" : 
                                                        configItem.getStatut().equals("INACTIF") ? "badge-warning" : "badge-danger";
                                            %>
                                            <tr data-type="<%= configItem.getTypeConfig() %>" 
                                                data-statut="<%= configItem.getStatut() %>">
                                                <td><span class="badge badge-primary"><%= configItem.getTypeConfig() %></span></td>
                                                <td>
                                                    <%= configItem.getRefId() != null ? configItem.getRefId() : "N/A" %>
                                                    <br><small class="text-muted">
                                                        <%= getNomReference(configItem.getTypeConfig(), configItem.getRefId()) %>
                                                    </small>
                                                </td>
                                                <td><strong><%= configItem.getPoints() %></strong></td>
                                                <td>
                                                    <% if (configItem.getConditionValeur() != null) { %>
                                                    <%= configItem.getConditionType() %> <%= configItem.getConditionValeur() %>
                                                    <% } else { %>
                                                    -
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <%= configItem.getDateDebut() != null ? 
                                                        dateFormat.format(configItem.getDateDebut()) : "-" %>
                                                </td>
                                                <td>
                                                    <%= configItem.getDateFin() != null ? 
                                                        dateFormat.format(configItem.getDateFin()) : "-" %>
                                                </td>
                                                <td><span class="badge <%= badgeClass %>"><%= configItem.getStatut() %></span></td>
                                                <td>
                                                    <div class="btn-group" role="group">
                                                        <a href="PointServlet?action=detailConfig&id=<%= configItem.getId() %>" 
                                                           class="btn btn-sm btn-outline-info">
                                                            <i class="fe fe-eye"></i>
                                                        </a>
                                                        <a href="PointServlet?action=formConfig&id=<%= configItem.getId() %>" 
                                                           class="btn btn-sm btn-outline-warning">
                                                            <i class="fe fe-edit"></i>
                                                        </a>
                                                        <% if (!configItem.getStatut().equals("DELETED")) { %>
                                                        <form method="POST" action="PointServlet" 
                                                              onsubmit="return confirm('Supprimer cette configuration?')" 
                                                              style="display: inline;">
                                                            <input type="hidden" name="action" value="supprimerConfig">
                                                            <input type="hidden" name="id" value="<%= configItem.getId() %>">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                <i class="fe fe-trash"></i>
                                                            </button>
                                                        </form>
                                                        <% } %>
                                                    </div>
                                                </td>
                                            </tr>
                                            <% } 
                                            } else { %>
                                            <tr>
                                                <td colspan="8" class="text-center py-5">
                                                    <i class="fe fe-settings fe-64 text-muted"></i>
                                                    <h4 class="mt-3">Aucune configuration</h4>
                                                    <p class="text-muted">Commencez par créer des configurations dans les onglets ci-dessus.</p>
                                                </td>
                                            </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
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



<jsp:include page="footer.jsp" />
<script>
//Variables globales
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
    
    // Charger les configurations de montant au démarrage
    chargerConfigurationsMontants();
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
        tr.innerHTML = '<td>' + (index + 1) + '</td>' +
            '<td>' +
                '<div style="display: flex; align-items: center;">' +
                    '<img src="' + imagePath + '" alt="' + produit.nom + '" ' +
                         'style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px; margin-right: 10px;">' +
                    '<div>' +
                        '<strong>' + produit.nom + '</strong><br>' +
                        '<small class="text-muted">' + (produit.codeProduit ? produit.codeProduit : 'N/A') + '</small>' +
                    '</div>' +
                '</div>' +
            '</td>' +
            '<td>' +
                '<img src="' + imagePath + '" alt="' + produit.nom + '" ' +
                     'style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;">' +
            '</td>' +
            '<td>' +
                '<input type="number" class="form-control points-input" ' +
                       'data-id="' + produit.id + '" placeholder="Points" min="0" ' +
                       'value="' + (produit.points != null ? produit.points : 0) + '">' +
            '</td>' +
            '<td>' +
                '<button type="button" class="btn btn-danger btn-sm" ' +
                        'onclick="supprimerProduitListe(' + produit.id + ')">' +
                    '<i class="fe fe-trash"></i>' +
                '</button>' +
            '</td>';
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
            action: 'creerConfig',
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
    tr.innerHTML = '<td>' +
            '<input type="number" class="form-control" name="conditionValeur[]" ' +
                   'placeholder="Ex: 2" min="1">' +
        '</td>' +
        '<td>' +
            '<input type="number" class="form-control" name="points[]" ' +
                   'placeholder="Points" min="0">' +
        '</td>' +
        '<td>' +
            '<button type="button" class="btn btn-danger btn-sm" ' +
                    'onclick="supprimerLigneCommande(this)">' +
                '<i class="fe fe-trash"></i>' +
            '</button>' +
        '</td>';
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

// Gestion des montants
// Gestion des montants - CORRIGÉ
function chargerConfigurationsMontants() {
    console.log('Début du chargement des configurations montant...');
    
    $.ajax({
        url: ctx + '/PointServlet?action=getConfigurationsByType&type=MONTANT_TOTAL',
        type: 'GET',
        dataType: 'json',
        success: function(configs, status, xhr) {
            console.log('Requête réussie!');
            console.log('Nombre de configs:', configs ? configs.length : 0);
            
            const tbody = document.getElementById('montantsConfigBody');
            if (!tbody) {
                console.error('Élément montantsConfigBody non trouvé');
                return;
            }
            tbody.innerHTML = '';
            
            if (configs && configs.length > 0) {
                console.log('Configurations trouvées:', configs);
                
                // Trier par montant croissant
                configs.sort((a, b) => {
                    const valA = a.conditionValeur || 0;
                    const valB = b.conditionValeur || 0;
                    return valA - valB;
                });
                
                configs.forEach((config, index) => {
                    console.log('Ajout config:', config);
                    const tr = document.createElement('tr');
                    const conditionValue = config.conditionValeur || '';
                    const pointsValue = config.points || '';
                    
                    tr.innerHTML = '<td>' +
                            '<input type="number" class="form-control" name="conditionValeur[]" ' +
                                   'value="' + conditionValue + '" ' +
                                   'placeholder="Ex: 1000" min="0" step="0.01"' +
                                   'onblur="validerLigneMontant(this)">' +
                            '<small class="text-muted">DA</small>' +
                        '</td>' +
                        '<td>' +
                            '<input type="number" class="form-control" name="points[]" ' +
                                   'value="' + pointsValue + '" ' +
                                   'placeholder="Points" min="0">' +
                        '</td>' +
                        '<td>' +
                            '<button type="button" class="btn btn-danger btn-sm" ' +
                                    'onclick="supprimerLigneMontant(this)">' +
                                '<i class="fe fe-trash"></i>' +
                            '</button>' +
                        '</td>';
                    tbody.appendChild(tr);
                });
                
                console.log('Table HTML générée avec succès');
            } else {
                console.log('Aucune configuration trouvée, ajout d\'une ligne vide par défaut');
                // Ligne vide par défaut
                ajouterLigneMontant();
            }
        },
        error: function(xhr, status, error) {
            console.error('=== ERREUR AJAX ===');
            console.error('URL:', xhr.responseURL || 'PointServlet?action=getConfigurationsByType&type=MONTANT_TOTAL');
            console.error('Status:', status);
            console.error('Statut HTTP:', xhr.status);
            console.error('Message d\'erreur:', error);
            console.error('Réponse texte:', xhr.responseText ? xhr.responseText.substring(0, 500) + '...' : 'Aucune');
            
            showAlert('Erreur lors du chargement des configurations de montant. Voir console pour détails.', 'error');
            ajouterLigneMontant();
        },
        complete: function(xhr, status) {
            console.log('Requête terminée avec statut:', status);
        }
    });
}

function ajouterLigneMontant() {
    const tbody = document.getElementById('montantsConfigBody');
    const tr = document.createElement('tr');
    tr.innerHTML = '<td>' +
            '<input type="number" class="form-control" name="conditionValeur[]" ' +
                   'placeholder="Ex: 1000" min="0" step="0.01"' +
                   'onblur="validerLigneMontant(this)">' +
            '<small class="text-muted">DA</small>' +
        '</td>' +
        '<td>' +
            '<input type="number" class="form-control" name="points[]" ' +
                   'placeholder="Points" min="0">' +
        '</td>' +
        '<td>' +
            '<button type="button" class="btn btn-danger btn-sm" ' +
                    'onclick="supprimerLigneMontant(this)">' +
                '<i class="fe fe-trash"></i>' +
            '</button>' +
        '</td>';
    tbody.appendChild(tr);
}

function supprimerLigneMontant(button) {
    const tr = button.closest('tr');
    if (document.getElementById('montantsConfigBody').children.length > 1) {
        tr.remove();
    } else {
        // Si c'est la dernière ligne, la vider
        tr.querySelector('input[name="conditionValeur[]"]').value = '';
        tr.querySelector('input[name="points[]"]').value = '';
    }
}

function validerLigneMontant(input) {
    const ligne = input.closest('tr');
    const condition = ligne.querySelector('input[name="conditionValeur[]"]').value;
    const points = ligne.querySelector('input[name="points[]"]').value;
    
    if (condition) {
        const montant = parseFloat(condition);
        if (montant < 0) {
            showAlert('Le montant ne peut pas être négatif', 'warning');
            input.value = '';
            input.focus();
        }
        
        // Vérifier les doublons
        const toutesLignes = document.querySelectorAll('#montantsConfigBody tr');
        const montants = [];
        toutesLignes.forEach(l => {
            const val = l.querySelector('input[name="conditionValeur[]"]').value;
            if (val && l !== ligne) {
                montants.push(parseFloat(val));
            }
        });
        
        if (montants.includes(montant)) {
            showAlert('Ce montant existe déjà dans la liste', 'warning');
            input.focus();
        }
    }
}


//CORRIGER la fonction enregistrerConfigMontants
function enregistrerConfigMontants() {
 const lignes = document.querySelectorAll('#montantsConfigBody tr');
 const configs = [];
 
 lignes.forEach(tr => {
     const condition = tr.querySelector('input[name="conditionValeur[]"]').value;
     const points = tr.querySelector('input[name="points[]"]').value;
     
     if (condition && points) {
         configs.push({
             conditionValeur: parseFloat(condition),
             points: parseInt(points)
         });
     }
 });
 
 if (configs.length === 0) {
     showAlert('Veuillez remplir au moins une condition', 'warning');
     return;
 }
 
 // Trier par montant croissant
 configs.sort((a, b) => a.conditionValeur - b.conditionValeur);
 
 // Validation: vérifier qu'il n'y a pas de doublons
 const montants = configs.map(c => c.conditionValeur);
 const montantsUniques = [...new Set(montants)];
 if (montants.length !== montantsUniques.length) {
     showAlert('Attention: Il y a des montants en doublon', 'warning');
     return;
 }
 
 // Afficher une confirmation
 let message = '<p>Vous êtes sur le point d\'enregistrer ' + configs.length + ' condition(s) de montant :</p><ul>';
 configs.forEach(c => {
     message += '<li>&ge; ' + c.conditionValeur + ' DA : ' + c.points + ' points</li>';
 });
 message += '</ul><p>Les configurations existantes de type MONTANT_TOTAL seront remplacées.</p>' +
             '<div class="alert alert-warning">' +
                 '<i class="fe fe-alert-triangle"></i>' +
                 'Cette action ne peut pas être annulée.' +
             '</div>';
 
 showConfirmationModal(
     'Confirmer l\'enregistrement',
     message,
     function() {
    	    let succes = 0;
    	    let erreurs = 0;
    	    let total = configs.length;
    	    let processed = 0;
    	    
    	    // Désactiver le bouton pendant l'opération
    	    $('#saveAllBtn').prop('disabled', true).text('Enregistrement en cours...');
    	    
    	    configs.forEach((config, index) => {
    	        $.ajax({
    	            url: ctx + '/PointServlet',
    	            type: 'POST',
    	            data: {
    	                action: 'creerConfig',
    	                typeConfig: 'MONTANT_TOTAL',
    	                conditionValeur: config.conditionValeur,
    	                conditionType: '>=',
    	                points: config.points,
    	                statut: 'ACTIF'
    	            },
    	            success: function(response) {
    	                succes++;
    	                processed++;
    	                
    	                // Vérifier si toutes les requêtes sont terminées
    	                if (processed === total) {
    	                    finaliserOperation(succes, erreurs);
    	                }
    	            },
    	            error: function(xhr, status, error) {
    	                erreurs++;
    	                processed++;
    	                
    	                // Vérifier si toutes les requêtes sont terminées
    	                if (processed === total) {
    	                    finaliserOperation(succes, erreurs);
    	                }
    	            }
    	        });
    	    });
    	    
    	    // Fonction pour finaliser l'opération
    	    function finaliserOperation(succes, erreurs) {
    	        // Réactiver le bouton
    	        $('#saveAllBtn').prop('disabled', false).text('Enregistrer toutes les configurations');
    	        
    	        // Mettre le message dans la session via une requête spéciale
    	        $.ajax({
    	            url: ctx + '/PointServlet',
    	            type: 'POST',
    	            async: false,
    	            data: {
    	                action: 'setSessionMessage',
    	                message: succes + ' configuration(s) enregistrée(s) avec succès' + 
    	                       (erreurs > 0 ? ' (' + erreurs + ' erreur(s))' : ''),
    	                type: erreurs > 0 ? 'warning' : 'success'
    	            },
    	            success: function() {
    	                // Recharger la page pour afficher le message de session
    	                setTimeout(function() {
    	                    window.location.reload();
    	                }, 500); // Petit délai pour s'assurer que la session est mise à jour
    	            },
    	            error: function() {
    	                // En cas d'erreur, recharger quand même
    	                setTimeout(function() {
    	                    window.location.reload();
    	                }, 500);
    	            }
    	        });
    	    }
    	}
 );
}

// Chargement et filtrage des configurations
// CORRIGER la fonction chargerConfigurations
function chargerConfigurations() {
    $.ajax({
        url: ctx + '/PointServlet?action=getConfigurationsJSON',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            configurationsChargees = data;
            afficherConfigurationsFiltrees();
        },
        error: function(xhr) {
            console.error('Erreur chargement configurations:', xhr.status, xhr.responseText);
            showAlert('Erreur lors du chargement des configurations', 'error');
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
        tbody.innerHTML = '<tr>' +
                '<td colspan="8" class="text-center py-4">' +
                    '<i class="fe fe-search fe-32 text-muted"></i>' +
                    '<p class="mt-2">Aucune configuration ne correspond aux filtres</p>' +
                '</td>' +
            '</tr>';
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
        
        // Formatage des dates
        const dateDebutDisplay = configItem.dateDebut ? formatDate(configItem.dateDebut) : '-';
        const dateFinDisplay = configItem.dateFin ? formatDate(configItem.dateFin) : '-';
        
        // Formulaire de suppression (uniquement si non supprimé)
        let deleteForm = '';
        if (configItem.statut !== 'DELETED') {
            deleteForm = '<form method="POST" action="PointServlet" ' +
                      'onsubmit="return confirm(\'Supprimer cette configuration?\')" ' +
                      'style="display: inline;">' +
                    '<input type="hidden" name="action" value="supprimerConfig">' +
                    '<input type="hidden" name="id" value="' + configItem.id + '">' +
                    '<button type="submit" class="btn btn-sm btn-outline-danger">' +
                        '<i class="fe fe-trash"></i>' +
                    '</button>' +
                '</form>';
        }
        
        tr.innerHTML = '<td><span class="badge badge-primary">' + configItem.typeConfig + '</span></td>' +
            '<td>' +
                (configItem.refId != null ? configItem.refId : 'N/A') +
            '</td>' +
            '<td><strong>' + configItem.points + '</strong></td>' +
            '<td>' + conditionDisplay + '</td>' +
            '<td>' + dateDebutDisplay + '</td>' +
            '<td>' + dateFinDisplay + '</td>' +
            '<td><span class="badge ' + badgeClass + '">' + configItem.statut + '</span></td>' +
            '<td>' +
                '<div class="btn-group" role="group">' +
                    '<a href="PointServlet?action=detailConfig&id=' + configItem.id + '" ' +
                       'class="btn btn-sm btn-outline-info">' +
                        '<i class="fe fe-eye"></i>' +
                    '</a>' +
                    '<a href="PointServlet?action=formConfig&id=' + configItem.id + '" ' +
                       'class="btn btn-sm btn-outline-warning">' +
                        '<i class="fe fe-edit"></i>' +
                    '</a>' +
                    deleteForm +
                '</div>' +
            '</td>';
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
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return dateString; // Retourner la chaîne d'origine si elle n'est pas valide
        }
        // Formatage en français
        return date.toLocaleDateString('fr-FR');
    } catch (e) {
        return dateString;
    }
}

function showAlert(message, type) {
    const alertClass = type === 'success' ? 'alert-success' : 
                      type === 'warning' ? 'alert-warning' : 'alert-danger';
    
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert ' + alertClass + ' alert-dismissible fade show';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = message +
        '<button type="button" class="close" data-dismiss="alert">' +
            '<span aria-hidden="true">&times;</span>' +
        '</button>';
    
    // Insérer en haut du contenu principal
    const mainContent = document.querySelector('.main-content .container-fluid');
    if (mainContent.children.length > 0) {
        mainContent.insertBefore(alertDiv, mainContent.children[0]);
    } else {
        mainContent.appendChild(alertDiv);
    }
    
    // Auto-dismiss après 5 secondes
    setTimeout(function() {
        $(alertDiv).alert('close');
    }, 5000);
}

function showConfirmationModal(title, message, confirmCallback) {
    document.getElementById('confirmationMessage').innerHTML = message;
    $('#confirmationModal').modal('show');
    
    const confirmBtn = document.getElementById('confirmActionBtn');
    confirmBtn.onclick = function() {
        $('#confirmationModal').modal('hide');
        if (confirmCallback) confirmCallback();
    };
}

function chargerToutesConfigurations() {
    document.getElementById('filterType').value = '';
    document.getElementById('filterStatut').value = '';
    chargerConfigurations();
}

// Ajouter un event listener pour le changement d'onglet
document.addEventListener('DOMContentLoaded', function() {
    const montantTab = document.getElementById('montant-tab');
    if (montantTab) {
        montantTab.addEventListener('click', function() {
            // Rafraîchir les configurations de montant quand on clique sur l'onglet
            setTimeout(function() {
                chargerConfigurationsMontants();
            }, 300);
        });
    }
});

//Gestion des points
function chargerConfigurationsPoints() {
    console.log('Chargement des configurations points...');
    
    $.ajax({
        url: ctx + '/PointServlet?action=getConfigurationsByType&type=VALEUR_POINT',
        type: 'GET',
        dataType: 'json',
        success: function(configs, status, xhr) {
            console.log('Configurations points trouvées:', configs);
            
            const tbody = document.getElementById('pointsConfigBody');
            tbody.innerHTML = '';
            
            if (configs && configs.length > 0) {
                configs.sort((a, b) => a.conditionValeur - b.conditionValeur);
                
                configs.forEach((config, index) => {
                    const tr = document.createElement('tr');
                    const pointsValue = config.points || '';
                    const valeurGourdes = config.conditionValeur || '';
                    
                    tr.innerHTML = '<td>' +
                            '<input type="number" class="form-control" name="points[]" ' +
                                   'value="' + pointsValue + '" ' +
                                   'placeholder="Ex: 10" min="1" step="1"' +
                                   'onblur="validerLignePoint(this)">' +
                            '<small class="text-muted">points</small>' +
                        '</td>' +
                        '<td>' +
                            '<input type="number" class="form-control" name="conditionValeur[]" ' +
                                   'value="' + valeurGourdes + '" ' +
                                   'placeholder="Gourdes" min="0" step="0.01">' +
                            '<small class="text-muted">gourdes</small>' +
                        '</td>' +
                        '<td>' +
                            '<button type="button" class="btn btn-danger btn-sm" ' +
                                    'onclick="supprimerLignePoint(this)">' +
                                '<i class="fe fe-trash"></i>' +
                            '</button>' +
                        '</td>';
                    tbody.appendChild(tr);
                });
            } else {
                ajouterLignePoint();
            }
        },
        error: function(xhr, status, error) {
            console.error('Erreur lors du chargement des configurations points:', error);
            ajouterLignePoint();
        }
    });
}

function ajouterLignePoint() {
    const tbody = document.getElementById('pointsConfigBody');
    const tr = document.createElement('tr');
    tr.innerHTML = '<td>' +
            '<input type="number" class="form-control" name="points[]" ' +
                   'placeholder="Ex: 10" min="1" step="1"' +
                   'onblur="validerLignePoint(this)">' +
            '<small class="text-muted">points</small>' +
        '</td>' +
        '<td>' +
            '<input type="number" class="form-control" name="conditionValeur[]" ' +
                   'placeholder="Gourdes" min="0" step="0.01">' +
            '<small class="text-muted">gourdes</small>' +
        '</td>' +
        '<td>' +
            '<button type="button" class="btn btn-danger btn-sm" ' +
                    'onclick="supprimerLignePoint(this)">' +
                '<i class="fe fe-trash"></i>' +
            '</button>' +
        '</td>';
    tbody.appendChild(tr);
}

function supprimerLignePoint(button) {
    const tr = button.closest('tr');
    if (document.getElementById('pointsConfigBody').children.length > 1) {
        tr.remove();
    } else {
        tr.querySelector('input[name="conditionValeur[]"]').value = '';
        tr.querySelector('input[name="points[]"]').value = '';
    }
}

function validerLignePoint(input) {
    const ligne = input.closest('tr');
    const points = ligne.querySelector('input[name="conditionValeur[]"]').value;
    const gourdes = ligne.querySelector('input[name="points[]"]').value;
    
    if (points) {
        const pointsValue = parseInt(points);
        if (pointsValue < 1) {
            showAlert('Le nombre de points doit être au moins 1', 'warning');
            input.value = '';
            input.focus();
        }
        
        // Vérifier les doublons
        const toutesLignes = document.querySelectorAll('#pointsConfigBody tr');
        const valeursPoints = [];
        toutesLignes.forEach(l => {
            const val = l.querySelector('input[name="conditionValeur[]"]').value;
            if (val && l !== ligne) {
                valeursPoints.push(parseInt(val));
            }
        });
        
        if (valeursPoints.includes(pointsValue)) {
            showAlert('Ce nombre de points existe déjà dans la liste', 'warning');
            input.focus();
        }
    }
}

function enregistrerConfigPoints() {
    const lignes = document.querySelectorAll('#pointsConfigBody tr');
    const configs = [];
    
    lignes.forEach(tr => {
        const points = tr.querySelector('input[name="conditionValeur[]"]').value;
        const gourdes = tr.querySelector('input[name="points[]"]').value;
        
        if (points && gourdes) {
            configs.push({
                conditionValeur: parseInt(points),
                points: parseFloat(gourdes)
            });
        }
    });
    
    if (configs.length === 0) {
        showAlert('Veuillez remplir au moins une conversion', 'warning');
        return;
    }
    
    // Trier par points croissant
    configs.sort((a, b) => a.conditionValeur - b.conditionValeur);
    
    // Validation: vérifier qu'il n'y a pas de doublons
    const pointsValues = configs.map(c => c.conditionValeur);
    const pointsUniques = [...new Set(pointsValues)];
    if (pointsValues.length !== pointsUniques.length) {
        showAlert('Attention: Il y a des valeurs de points en doublon', 'warning');
        return;
    }
    
    // Afficher une confirmation
    let message = '<p>Vous êtes sur le point d\'enregistrer ' + configs.length + ' conversion(s) :</p><ul>';
    configs.forEach(c => {
        const valeurUnitaire = (c.points / c.conditionValeur).toFixed(2);
        message += '<li>' + c.points + ' points = ' + c.conditionValeur + ' gourdes ';
    });
    message += '</ul><p>Les configurations existantes de type VALEUR_POINT seront remplacées.</p>' +
                '<div class="alert alert-warning">' +
                    '<i class="fe fe-alert-triangle"></i>' +
                    'Cette action ne peut pas être annulée.' +
                '</div>';
    
    showConfirmationModal(
        'Confirmer l\'enregistrement',
        message,
        function() {
            let succes = 0;
            let erreurs = 0;
            let total = configs.length;
            let processed = 0;
            
            configs.forEach((config, index) => {
                $.ajax({
                    url: ctx + '/PointServlet',
                    type: 'POST',
                    data: {
                        action: 'creerConfig',
                        typeConfig: 'VALEUR_POINT',
                        conditionValeur: config.conditionValeur,
                        conditionType: '=',
                        points: config.points,
                        statut: 'ACTIF'
                    },
                    success: function(response) {
                        succes++;
                        processed++;
                        
                        if (processed === total) {
                            finaliserOperation(succes, erreurs);
                        }
                    },
                    error: function(xhr, status, error) {
                        erreurs++;
                        processed++;
                        
                        if (processed === total) {
                            finaliserOperation(succes, erreurs);
                        }
                    }
                });
            });
            
            function finaliserOperation(succes, erreurs) {
                $.ajax({
                    url: ctx + '/PointServlet',
                    type: 'POST',
                    async: false,
                    data: {
                        action: 'setSessionMessage',
                        message: succes + ' conversion(s) enregistrée(s) avec succès' + 
                               (erreurs > 0 ? ' (' + erreurs + ' erreur(s))' : ''),
                        type: erreurs > 0 ? 'warning' : 'success'
                    },
                    success: function() {
                        setTimeout(function() {
                            window.location.reload();
                        }, 500);
                    },
                    error: function() {
                        setTimeout(function() {
                            window.location.reload();
                        }, 500);
                    }
                });
            }
        }
    );
}

// Ajouter un event listener pour le changement d'onglet Points
document.addEventListener('DOMContentLoaded', function() {
    const pointsTab = document.getElementById('points-tab');
    if (pointsTab) {
        pointsTab.addEventListener('click', function() {
            setTimeout(function() {
                chargerConfigurationsPoints();
            }, 300);
        });
    }
});
</script>