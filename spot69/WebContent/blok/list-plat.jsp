<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.google.gson.Gson,java.util.List,com.spot69.model.Plat,com.spot69.model.Produit,
            com.spot69.model.MenuCategorie,com.spot69.model.Rayon,com.spot69.model.RayonHierarchique,
            java.util.Map,java.util.HashMap"%>
<meta charset="UTF-8">

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<style>
.plat-mini-card { display: flex; align-items: center; gap: 10px; }
.plat-mini-image img { width: 50px; height: 50px; object-fit: cover; border-radius: 6px; }
.plat-mini-info strong { font-size: 14px; }
.plat-mini-info small { font-size: 12px; }
.no-image { width: 50px; height: 50px; display: flex; justify-content: center; align-items: center; 
           background: #ddd; border-radius: 6px; font-weight: bold; font-size: 14px; color: #999; }
.filtre-group { display: flex; gap: 10px; flex-wrap: wrap; align-items: end; margin-bottom: 20px; }
.filtre-item { min-width: 200px; }
.btn-filtre { height: 38px; }
/* Style pour les produits sélectionnés */
.selected-product-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    margin: 4px 0;
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 4px;
}

.selected-product-info {
    flex-grow: 1;
}

.selected-product-name {
    font-weight: 600;
    font-size: 14px;
    color: #333;
}

.selected-product-details {
    font-size: 12px;
    color: #666;
}

.selected-product-remove {
    color: #dc3545;
    cursor: pointer;
    margin-left: 10px;
    font-size: 16px;
}

.selected-product-remove:hover {
    color: #c82333;
}

/* Masquer les sections qu'on ne veut plus utiliser */
#categorieSection, #platFields {
    display: none;
}
</style>

<main role="main" class="main-content">
<div class="container-fluid">
    <div class="row justify-content-center">
        <div class="col-12">
            <div class="row">
                <div class="col-md-12 my-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="h4 mb-1">
                            <i class="fe fe-clipboard fe-32 align-self-center text-black"></i>
                            Le Menu
                        </h2>
                        <div class="custom-btn-group">
                            <button class="btn btn-outline-primary" type="button"
                                data-toggle="modal" data-target=".modal-plat">
                                <i class="fe fe-plus fe-16"></i> Ajouter un élément
                            </button>
                        </div>
                    </div>

                    <!-- FILTRES HIÉRARCHIQUES : Rayon → Catégorie → Sous-catégorie -->
                    <div class="filtre-group">
                        <div class="form-group filtre-item">
                            <label for="filtreRayon">Rayon</label>
                            <select class="form-control" id="filtreRayon">
                                <option value="">-- Tous les rayons --</option>
                                <% 
                                    List<Rayon> rayons = (List<Rayon>) request.getAttribute("rayons");
                                    if (rayons != null) {
                                        for (Rayon rayon : rayons) {
                                %>
                                    <option value="<%=rayon.getId()%>"><%=rayon.getNom()%></option>
                                <% 
                                        }
                                    } 
                                %>
                            </select>
                        </div>
                        
                        <div class="form-group filtre-item">
                            <label for="filtreCategorie">Catégorie</label>
                            <select class="form-control" id="filtreCategorie" disabled>
                                <option value="">-- Toutes les catégories --</option>
                            </select>
                        </div>

                        <div class="form-group filtre-item">
                            <label for="filtreSousCategorie">Sous-catégorie</label>
                            <select class="form-control" id="filtreSousCategorie" disabled>
                                <option value="">-- Toutes les sous-catégories --</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <button type="button" id="btnResetFiltres" class="btn btn-secondary btn-filtre">
                                <i class="fe fe-refresh-cw fe-12"></i> Réinitialiser
                            </button>
                        </div>
                    </div>

                    <div class="card shadow">
                        <div class="card-body">
                            <table class="table datatables" id="plat-datatable">
                                <thead>
                                    <tr>
                                        <th>Plat</th>
                                        <th>Prix</th>
                                        <th>Rayon</th>
                                        <th>Catégorie</th>
                                        <th>Sous-catégorie</th>
                                        <th>Date création</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
<%
List<Plat> plats = (List<Plat>) request.getAttribute("plats");
Map<Integer, MenuCategorie> categorieMap = (Map<Integer, MenuCategorie>) request.getAttribute("categorieMap");
Map<Integer, Rayon> rayonMap = (Map<Integer, Rayon>) request.getAttribute("rayonMap");

if (plats != null) {
    for (Plat plat : plats) {
        // Variables pour l'affichage
        String nomCategorie = "";
        String nomSousCategorie = "";
        String nomRayon = "";
        Integer categorieIdForFilter = 0;
        Integer sousCategorieIdForFilter = 0;
        Integer rayonIdForFilter = 0;
        
        // Récupérer la sous-catégorie
        MenuCategorie sousCategorie = null;
        if (plat.getSousCategorieId() != null && plat.getSousCategorieId() > 0) {
            sousCategorieIdForFilter = plat.getSousCategorieId();
            sousCategorie = categorieMap.get(plat.getSousCategorieId());
            if (sousCategorie != null) {
                nomSousCategorie = sousCategorie.getNom();
            }
        }
        
        // Récupérer la catégorie
        MenuCategorie categorie = null;
        if (plat.getCategorieId() != null && plat.getCategorieId() > 0) {
            categorieIdForFilter = plat.getCategorieId();
            categorie = categorieMap.get(plat.getCategorieId());
            if (categorie != null) {
                nomCategorie = categorie.getNom();
                
                // Si c'est une sous-catégorie, trouver la catégorie parente
                if (categorie.getParentId() != null) {
                    categorieIdForFilter = categorie.getParentId();
                    MenuCategorie parentCat = categorieMap.get(categorie.getParentId());
                    if (parentCat != null) {
                        nomCategorie = parentCat.getNom();
                    }
                }
            }
        }
        
        // Récupérer le rayon
        if (plat.getRayonId() != null && plat.getRayonId() > 0) {
            rayonIdForFilter = plat.getRayonId();
            Rayon rayon = rayonMap.get(plat.getRayonId());
            if (rayon != null) {
                nomRayon = rayon.getNom();
            }
        } else if (categorie != null && categorie.getRayon() != null) {
            rayonIdForFilter = categorie.getRayon().getId();
            nomRayon = categorie.getRayon().getNom();
        }

        String nomAffiche = plat.getProduit() != null && plat.getProduit().getNom() != null
                ? plat.getProduit().getNom()
                : plat.getNom();
        String descriptionAffiche = plat.getProduit() != null && plat.getProduit().getDescription() != null
                ? plat.getProduit().getDescription()
                : plat.getDescription();
        double prixAffiche = plat.getProduit() != null && plat.getProduit().getPrixVente() != null
                ? plat.getProduit().getPrixVente().doubleValue()
                : plat.getPrix();

        String imagePath = null;
        if (plat.getImage() != null && !plat.getImage().isEmpty()) {
            imagePath = plat.getImage();
        } else if (plat.getProduit() != null && plat.getProduit().getImageUrl() != null && !plat.getProduit().getImageUrl().isEmpty()) {
            imagePath = plat.getProduit().getImageUrl();
        }
%>
<tr data-rayon-id="<%=rayonIdForFilter%>" 
    data-categorie-id="<%=categorieIdForFilter%>" 
    data-sous-categorie-id="<%=sousCategorieIdForFilter%>">
    <td>
        <div class="plat-mini-card">
            <div class="plat-mini-image">
                <% if (imagePath != null && !imagePath.isEmpty()) {
                    String imgFile = imagePath;
                    if (imgFile.startsWith("uploads/plats/")) imgFile = "plats/" + imgFile.substring("uploads/plats/".length());
                    else if (imgFile.startsWith("uploads/produits/")) imgFile = "produits/" + imgFile.substring("uploads/produits/".length());
                %>
                <img src="<%=request.getContextPath()%>/images/<%= imgFile %>" alt="<%=nomAffiche%>">
                <% } else { %>
                <div class="no-image">?</div>
                <% } %>
            </div>
            <div class="plat-mini-info">
                <strong><%=nomAffiche%></strong><br>
                <small><%=descriptionAffiche%></small>
            </div>
        </div>
    </td>
    <td><%=String.format("%.2f", prixAffiche)%> HTG</td>
    <td>
        <% if (plat.getProduit() != null && plat.getProduit().getRayon() != null && plat.getProduit().getRayon().getNom() != null) { %>
            <%=plat.getProduit().getRayon().getNom()%>
        <% } else if (plat.getRayon() != null && plat.getRayon().getNom() != null) { %>
            <%=plat.getRayon().getNom()%>
        <% } else { %>
            —
        <% } %>
    </td>
    <td>
        <% if (plat.getProduit() != null && plat.getProduit().getCategorie() != null && plat.getProduit().getCategorie().getNom() != null) { %>
            <%=plat.getProduit().getCategorie().getNom()%>
        <% } else if (plat.getCategorie() != null && plat.getCategorie().getNom() != null) { %>
            <%=plat.getCategorie().getNom()%>
        <% } else { %>
            —
        <% } %>
    </td>
    <td>
        <% if (plat.getProduit() != null && plat.getProduit().getSousCategorie() != null && plat.getProduit().getSousCategorie().getNom() != null) { %>
            <%=plat.getProduit().getSousCategorie().getNom()%>
        <% } else if (plat.getSousCategorie() != null && plat.getSousCategorie().getNom() != null) { %>
            <%=plat.getSousCategorie().getNom()%>
        <% } else { %>
            —
        <% } %>
    </td>
    <td><%=plat.getCreationDate() != null ? plat.getCreationDate().toString().replace('T', ' ') : ""%></td>
    <td>
        <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown">
            <span class="text-muted sr-only">Action</span>
        </button>
        <div class="dropdown-menu dropdown-menu-right">
        <%  if (plat.getProduit() == null ){ %>
    <a class="dropdown-item edit-plat-btn" href="#" 
       data-id="<%=plat.getId()%>"
       data-nom="<%=plat.getNom() != null ? plat.getNom() : ""%>"
       data-description="<%=plat.getDescription() != null ? plat.getDescription() : ""%>"
       data-prix="<%=plat.getPrix()%>"
       data-qtepoints="<%=plat.getQtePoints()%>"
       data-rayonid="<%=plat.getRayonId() != null ? plat.getRayonId() : ""%>"
       data-categorieid="<%=plat.getCategorieId() != null ? plat.getCategorieId() : ""%>"
       data-souscategorieid="<%=plat.getSousCategorieId() != null ? plat.getSousCategorieId() : ""%>"
       data-image="<%=plat.getImage() != null ? plat.getImage() : ""%>"
       data-produitid="<%=plat.getProductId() != null ? plat.getProductId() : 0%>">
       Modifier
    </a>
<% }   %>
            <a class="dropdown-item" href="MenuServlet?action=deletePlat&id=<%=plat.getId()%>" 
               onclick="return confirm('Supprimer ce plat ?');">Supprimer</a>
        </div>
    </td>
</tr>
<% }} %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%
Plat plat = (Plat) request.getAttribute("plat");
boolean isEdit = (plat != null);
List<Produit> produits = (List<Produit>) request.getAttribute("produits");
List<RayonHierarchique> hierarchieRayons = (List<RayonHierarchique>) request.getAttribute("hierarchieRayons");

// Créer une liste des IDs de produits déjà dans le menu
java.util.Set<Integer> produitsDejaDansMenu = new java.util.HashSet<>();
if (plats != null) {
    for (Plat p : plats) {
        if (p.getProductId() != null && p.getProductId() > 0) {
            produitsDejaDansMenu.add(p.getProductId());
        }
    }
}
%>

<!-- MODAL AJOUT/MODIFICATION (SIMPLIFIÉ) -->
<div class="modal fade modal-plat modal-slide" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <form id="formPlat" action="MenuServlet" method="post" enctype="multipart/form-data">
                <div class="modal-header">
                    <h5 class="modal-title"><%=isEdit ? "Modifier " : "Ajouter "%>un plat</h5>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="isUsingExistingProduct" name="isUsingExistingProduct" value="true">
                    <input type="hidden" name="action" value="<%=isEdit ? "updatePlat" : "addPlat"%>">
                    <input type="hidden" id="thePlatId" name="id" value="<%=isEdit ? plat.getId() : ""%>">
                    
                    <!-- Zone pour afficher les produits sélectionnés -->
                    <div id="selectedProductsContainer" class="mb-3">
                        <label>Produits sélectionnés :</label>
                        <div id="selectedProductsList" class="border rounded p-2" style="min-height: 50px; max-height: 200px; overflow-y: auto;">
                            <!-- Les produits sélectionnés apparaîtront ici -->
                        </div>
                        <input type="hidden" id="selectedProductIds" name="selectedProductIds" value="">
                    </div>

                    <!-- Container pour la sélection simple de produits -->
                    <div class="form-group" id="selectProduitContainer">
                        <label for="produitExistante">Choisir un produit</label>
                        <select class="form-control" id="produitExistante" name="produitExistanteId">
                            <option value="">-- Sélectionner un produit --</option>
                            <% if (produits != null) {
                                for (Produit p : produits) { 
                                    // Ne pas afficher les produits déjà dans le menu
                                    if (produitsDejaDansMenu.contains(p.getId())) {
                                        continue;
                                    }
                                    boolean selected = isEdit && plat.getProduit() != null && plat.getProduit().getId() == p.getId();
                            %>
                                <option value="<%=p.getId()%>" 
                                        data-categorie-id="<%=p.getCategorie() != null ? p.getCategorie().getId() : ""%>"
                                        data-sous-categorie-id="<%=p.getSousCategorie() != null ? p.getSousCategorie().getId() : ""%>"
                                        data-rayon-id="<%=p.getCategorie().getRayon() != null ? p.getCategorie().getRayon().getId() : ""%>"
                                        data-nom="<%=p.getNom()%>"
                                        data-prix="<%=p.getPrixVente()%>"
                                        data-stock="<%=p.getQteEnStock()%>"
                                        <%=selected ? "selected" : ""%>>
                                    <%=p.getNom()%> - <%=p.getPrixVente()%> HTG (Stock: <%=p.getQteEnStock()%>)
                                </option>
                            <% }} %>
                        </select>
                    </div>

                    <!-- SECTION HIÉRARCHIE COMPLÈTE (masquée) -->
                    <div id="categorieSection" style="display: none;">
                        <div class="form-group">
                            <label>Rayon *</label>
                            <select class="form-control" name="rayonId" id="rayonPlat">
                                <option value="">-- Sélectionner un rayon --</option>
                                <%
                                    if (rayons != null) {
                                        for (Rayon rayon : rayons) {
                                            boolean selected = isEdit && plat.getRayonId() != null && plat.getRayonId() == rayon.getId();
                                %>
                                    <option value="<%= rayon.getId() %>" <%=selected ? "selected" : ""%>><%= rayon.getNom() %></option>
                                <% 
                                        }
                                    } 
                                %>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label>Catégorie *</label>
                            <select class="form-control" name="categorieId" id="categoriePlat" disabled>
                                <option value="">-- Sélectionnez d'abord un rayon --</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Sous-catégorie *</label>
                            <select class="form-control" name="sousCategorieId" id="sousCategoriePlat" disabled>
                                <option value="">-- Sélectionnez d'abord une catégorie --</option>
                            </select>
                        </div>
                    </div>

                    <!-- SECTION POUR NOUVEAU PLAT (masquée) -->
                    <div id="platFields" style="display: none;">
                        <div class="form-group">
                            <label for="nomPlat">Nom*</label>
                            <input type="text" class="form-control" id="nomPlat" name="nom" 
                                   value="<%=isEdit ? plat.getNom() : ""%>">
                        </div>
                        <div class="form-group">
                            <label for="descriptionPlat">Description</label>
                            <textarea class="form-control" id="descriptionPlat" name="description" rows="3"><%=isEdit ? plat.getDescription() : ""%></textarea>
                        </div>
                        <div class="form-group">
                            <label for="prixPlat">Prix (HTG)*</label>
                            <input type="number" step="0.01" class="form-control" id="prixPlat" name="prix" 
                                   value="<%=isEdit ? plat.getPrix() : ""%>">
                        </div>
                        <div class="form-group" style="display:none">
                            <label for="qtePointsPlat">Quantité points</label>
                            <input type="number" class="form-control" id="qtePointsPlat" name="qtePoints" 
                                   value="<%=isEdit ? plat.getQtePoints() : ""%>">
                        </div>
                        <div class="form-group">
                            <label for="imagePlat">Image</label>
                            <input type="file" class="form-control-file" id="imagePlat" name="image" accept="image/*">
                            <div style="margin-top: 10px;">
                                <img id="imagePreviewPlat" src="" alt="Aperçu image plat" 
                                     style="max-width: 200px; max-height: 150px; display: none; border: 1px solid #ddd; padding: 4px;">
                            </div>
                            <% if (isEdit && plat.getImage() != null) { %>
                                <small>Image actuelle : <%=plat.getImage()%></small>
                            <% } %>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><%=isEdit ? "Modifier" : "Ajouter"%></button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                </div>
            </form>
        </div>
    </div>
</div>
</main>

<jsp:include page="footer.jsp" />
<script>
// Fonction pour ajouter un produit sélectionné à la liste
function addSelectedProduct(produitId, produitNom, produitPrix, produitStock) {
    const container = $('#selectedProductsList');
    const productIdsInput = $('#selectedProductIds');
    
    console.log("Ajout produit:", {produitId: produitId, produitNom: produitNom, produitPrix: produitPrix, produitStock: produitStock});
    
    // Vérifier si le produit est déjà sélectionné
    if ($('#selectedProductItem_' + produitId).length > 0) {
        console.log("Produit déjà sélectionné:", produitId);
        return;
    }
    
    // Formater le prix avec 2 décimales
    const prixFormate = parseFloat(produitPrix).toFixed(2);
    
    // Créer l'élément HTML
    const productItem = '<div id="selectedProductItem_' + produitId + '" class="selected-product-item">' +
        '<div class="selected-product-info">' +
            '<div class="selected-product-name">' + produitNom + '</div>' +
            '<div class="selected-product-details">' + prixFormate + ' HTG | Stock: ' + produitStock + '</div>' +
        '</div>' +
        '<div class="selected-product-remove" onclick="removeSelectedProduct(' + produitId + ')">' +
            '<i class="fe fe-x"></i>' +
        '</div>' +
    '</div>';
    
    // Ajouter à la liste
    container.append(productItem);
    
    // Mettre à jour le champ caché
    let currentIds = productIdsInput.val();
    if (currentIds) {
        currentIds += ',' + produitId;
    } else {
        currentIds = produitId;
    }
    productIdsInput.val(currentIds);
    
    // Afficher le conteneur
    $('#selectedProductsContainer').show();
    
    // Réinitialiser le select à vide
    $('#produitExistante').val('');
    
    console.log("Produit ajouté avec succès. IDs actuels:", currentIds);
}

// Fonction pour retirer un produit de la liste
function removeSelectedProduct(produitId) {
    console.log("Retrait produit:", produitId);
    $('#selectedProductItem_' + produitId).remove();
    
    // Mettre à jour le champ caché
    let currentIds = $('#selectedProductIds').val().split(',');
    currentIds = currentIds.filter(function(id) {
        return id != produitId && id !== '';
    });
    $('#selectedProductIds').val(currentIds.join(','));
    
    // Cacher le conteneur si vide
    if (currentIds.length === 0) {
        $('#selectedProductsContainer').hide();
    }
    
    console.log("Produit retiré. IDs restants:", currentIds);
}

// Fonction pour charger les catégories d'un rayon (pour les filtres)
function loadCategoriesForFilter(rayonId) {
    const $categorieSelect = $('#filtreCategorie');
    const $sousCategorieSelect = $('#filtreSousCategorie');
    
    // Réinitialiser les selects
    $categorieSelect.empty().prop('disabled', true);
    $sousCategorieSelect.empty().prop('disabled', true);
    
    if (!rayonId) {
        $categorieSelect.append('<option value="">-- Toutes les catégories --</option>');
        $sousCategorieSelect.append('<option value="">-- Toutes les sous-catégories --</option>');
        $categorieSelect.prop('disabled', false);
        return;
    }
    
    // Afficher un indicateur de chargement
    $categorieSelect.append('<option value="">Chargement...</option>');
    $categorieSelect.prop('disabled', true);
    
    // Charger les catégories du rayon sélectionné
    $.ajax({
        url: 'MenuServlet?action=categories-by-rayon&rayonId=' + rayonId,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            $categorieSelect.empty();
            
            if (data.categories && data.categories.length > 0) {
                $categorieSelect.append('<option value="">-- Toutes les catégories --</option>');
                
                data.categories.forEach(function(cat) {
                    $categorieSelect.append(
                        $('<option>', { 
                            value: cat.id, 
                            text: cat.nom 
                        })
                    );
                });
                
                $categorieSelect.prop('disabled', false);
                
            } else {
                $categorieSelect.append('<option value="">Aucune catégorie disponible</option>');
                $categorieSelect.prop('disabled', true);
            }
            
            // Réinitialiser la sous-catégorie
            $sousCategorieSelect.empty().append('<option value="">-- Toutes les sous-catégories --</option>').prop('disabled', true);
        },
        error: function(xhr, status, error) {
            console.error("Erreur lors du chargement des catégories:", error);
            $categorieSelect.empty();
            $categorieSelect.append('<option value="">Erreur de chargement</option>');
            $categorieSelect.prop('disabled', true);
        }
    });
}

// Fonction pour charger les sous-catégories d'une catégorie (pour les filtres)
function loadSousCategoriesForFilter(parentId) {
    const $sousCategorieSelect = $('#filtreSousCategorie');
    
    // Réinitialiser le select
    $sousCategorieSelect.empty().prop('disabled', true);
    
    if (!parentId) {
        $sousCategorieSelect.append('<option value="">-- Toutes les sous-catégories --</option>');
        return;
    }
    
    // Afficher un indicateur de chargement
    $sousCategorieSelect.append('<option value="">Chargement...</option>');
    $sousCategorieSelect.prop('disabled', true);
    
    // Charger les sous-catégories
    $.ajax({
        url: 'MenuServlet?action=sous-categories&parentId=' + parentId,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            $sousCategorieSelect.empty();
            
            if (data.sousCategories && data.sousCategories.length > 0) {
                $sousCategorieSelect.append('<option value="">-- Toutes les sous-catégories --</option>');
                
                data.sousCategories.forEach(function(cat) {
                    $sousCategorieSelect.append(
                        $('<option>', { 
                            value: cat.id, 
                            text: cat.nom 
                        })
                    );
                });
                
                $sousCategorieSelect.prop('disabled', false);
                
            } else {
                $sousCategorieSelect.append('<option value="">Aucune sous-catégorie disponible</option>');
                $sousCategorieSelect.prop('disabled', true);
            }
        },
        error: function(xhr, status, error) {
            console.error("Erreur lors du chargement des sous-catégories:", error);
            $sousCategorieSelect.empty();
            $sousCategorieSelect.append('<option value="">Erreur de chargement</option>');
            $sousCategorieSelect.prop('disabled', true);
        }
    });
}

// Fonction pour charger les catégories d'un rayon (pour le modal d'édition)
function loadCategoriesByRayon(rayonId, selectedCatId, callback) {
    if (selectedCatId === undefined) selectedCatId = null;
    if (callback === undefined) callback = null;
    
    const $categorieSelect = $('#categoriePlat');
    const $sousCategorieSelect = $('#sousCategoriePlat');
    
    // Réinitialiser les selects
    $categorieSelect.empty().prop('disabled', true);
    $sousCategorieSelect.empty().prop('disabled', true);
    
    if (!rayonId) {
        $categorieSelect.append('<option value="">-- Sélectionnez d\'abord un rayon --</option>');
        $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
        if (callback) callback();
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
                
                // Si une catégorie est spécifiée, la présélectionner
                if (selectedCatId && selectedCatId !== "") {
                    setTimeout(function() {
                        $categorieSelect.val(selectedCatId);
                        if ($categorieSelect.val() == selectedCatId) {
                            $categorieSelect.trigger('change');
                        }
                        if (callback) callback();
                    }, 300);
                } else {
                    if (callback) callback();
                }
                
            } else {
                $categorieSelect.append('<option value="">Aucune catégorie disponible pour ce rayon</option>');
                $categorieSelect.prop('disabled', true);
                if (callback) callback();
            }
        },
        error: function(xhr, status, error) {
            console.error("Erreur lors du chargement des catégories:", error);
            $categorieSelect.empty();
            $categorieSelect.append('<option value="">Erreur de chargement</option>');
            $categorieSelect.prop('disabled', true);
            if (callback) callback();
        }
    });
}

// Fonction pour charger les sous-catégories d'une catégorie (pour le modal d'édition)
function loadSousCategories(parentId, selectedSousCatId, callback) {
    if (selectedSousCatId === undefined) selectedSousCatId = null;
    if (callback === undefined) callback = null;
    
    const $sousCategorieSelect = $('#sousCategoriePlat');
    
    // Réinitialiser le select
    $sousCategorieSelect.empty().prop('disabled', true);
    
    if (!parentId) {
        $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
        if (callback) callback();
        return;
    }
    
    // Afficher un indicateur de chargement
    $sousCategorieSelect.append('<option value="">Chargement des sous-catégories...</option>');
    $sousCategorieSelect.prop('disabled', true);
    
    // Charger les sous-catégories
    $.ajax({
        url: 'MenuServlet?action=sous-categories&parentId=' + parentId,
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
                if (selectedSousCatId && selectedSousCatId !== "") {
                    setTimeout(function() {
                        $sousCategorieSelect.val(selectedSousCatId);
                        if (callback) callback();
                    }, 300);
                } else {
                    if (callback) callback();
                }
                
            } else {
                $sousCategorieSelect.append('<option value="">Aucune sous-catégorie disponible</option>');
                $sousCategorieSelect.prop('disabled', true);
                if (callback) callback();
            }
        },
        error: function(xhr, status, error) {
            console.error("Erreur lors du chargement des sous-catégories:", error);
            $sousCategorieSelect.empty();
            $sousCategorieSelect.append('<option value="">Erreur de chargement</option>');
            $sousCategorieSelect.prop('disabled', true);
            if (callback) callback();
        }
    });
}

// Gestion du clic sur le bouton Modifier - VERSION SIMPLIFIÉE (uniquement pour les plats manuels)
$(document).on('click', '.edit-plat-btn', function(e) {
    e.preventDefault();
    const button = $(this);
    
    console.log("=== CLICK SUR MODIFIER ===");
    
    // Récupérer toutes les données du bouton
    const platId = button.data('id');
    const nomPlat = button.data('nom') || '';
    const descriptionPlat = button.data('description') || '';
    const prixPlat = button.data('prix') || '';
    const qtePointsPlat = button.data('qtepoints') || 0;
    const rayonId = button.data('rayonid') || '';
    const catId = button.data('categorieid') || '';
    const sousCatId = button.data('souscategorieid') || '';
    const imagePath = button.data('image') || '';
    
    console.log("Données récupérées:", { 
        platId: platId,
        nom: nomPlat, 
        description: descriptionPlat, 
        prix: prixPlat,
        qtePoints: qtePointsPlat,
        rayonId: rayonId,
        catId: catId,
        sousCatId: sousCatId
    });
    
    // ÉTAPE 1: REMPLIR LES CHAMPS TEXTUELS
    $('#thePlatId').val(platId);
    $('#nomPlat').val(nomPlat);
    $('#descriptionPlat').val(descriptionPlat);
    $('#prixPlat').val(prixPlat);
    $('#qtePointsPlat').val(qtePointsPlat);
    
    // Pour l'édition, on affiche les champs manuels (mode ancien système)
    $('#categorieSection').show();
    $('#platFields').show();
    $('#selectProduitContainer').hide();
    $('#selectedProductsContainer').hide();
    $('#isUsingExistingProduct').val('false');
    
    // Pré-remplir la hiérarchie
    if (rayonId && rayonId !== "") {
        $('#rayonPlat').val(rayonId);
        
        // Charger les catégories
        setTimeout(function() {
            if ($('#rayonPlat').val() == rayonId) {
                loadCategoriesByRayon(rayonId, catId, function() {
                    if (catId && catId !== "") {
                        loadSousCategories(catId, sousCatId);
                    }
                });
            }
        }, 300);
    }
    
    // Gérer l'image
    if (imagePath && imagePath !== "") {
        const contextPath = '<%=request.getContextPath()%>';
        const imgSrc = contextPath + '/blok/images/' + imagePath;
        $('#imagePreviewPlat').attr('src', imgSrc).show();
        
        $('#imagePreviewPlat').on('error', function() {
            $(this).attr('src', contextPath + '/blok/images/default-plat.jpg');
        });
    } else {
        $('#imagePreviewPlat').hide();
    }
    
    // Ouvrir le modal
    $('input[name="action"]').val('updatePlat');
    $('.modal-title').text('Modifier le plat');
    $('button[type="submit"]').text('Modifier');
    $('.modal-plat').modal('show');
});

// Document ready
$(document).ready(function() {
    console.log("Document ready - initialisation");
    
    // Initialiser les événements des filtres
    $('#filtreRayon').on('change', function() {
        const rayonId = $(this).val();
        loadCategoriesForFilter(rayonId);
        $('#filtreSousCategorie').val('').trigger('change');
        filterTable();
    });
    
    $('#filtreCategorie').on('change', function() {
        const categorieId = $(this).val();
        loadSousCategoriesForFilter(categorieId);
        filterTable();
    });
    
    $('#filtreSousCategorie').on('change', function() {
        filterTable();
    });
    
    // Bouton réinitialiser
    $('#btnResetFiltres').on('click', function() {
        $('#filtreRayon').val('');
        $('#filtreCategorie').val('');
        $('#filtreSousCategorie').val('');
        $('#filtreCategorie').prop('disabled', true);
        $('#filtreSousCategorie').prop('disabled', true);
        filterTable();
    });
    
    // Événement pour la sélection d'un produit
    $('#produitExistante').on('change', function() {
        const produitId = $(this).val();
        const selectedOption = $(this).find('option:selected');
        
        if (produitId && produitId !== "") {
            const produitNom = selectedOption.data('nom');
            const produitPrix = selectedOption.data('prix');
            const produitStock = selectedOption.data('stock');
            
            console.log("Produit sélectionné:", produitId, produitNom, produitPrix, produitStock);
            
            if (produitNom) {
                addSelectedProduct(produitId, produitNom, produitPrix, produitStock);
            } else {
                console.error("Données manquantes pour le produit:", {
                    nom: produitNom,
                    prix: produitPrix,
                    stock: produitStock
                });
            }
        }
    });
    
    // Réinitialisation modal
    $('.modal-plat').on('hidden.bs.modal', function() {
        console.log("Modal fermé - réinitialisation");
        $('#formPlat')[0].reset();
        
        // Réinitialiser les produits sélectionnés
        $('#selectedProductIds').val('');
        $('#selectedProductsList').empty();
        $('#selectedProductsContainer').hide();
        
        // Réinitialiser le select
        $('#produitExistante').val('');
        
        // Masquer les anciennes sections
        $('#categorieSection, #platFields').hide();
        
        // Afficher la sélection de produits
        $('#selectProduitContainer').show();
        
        // Réinitialiser les valeurs
        $('input[name="action"]').val('addPlat');
        $('#thePlatId').val('');
        $('.modal-title').text('Ajouter un plat');
        $('button[type="submit"]').text('Ajouter');
        $('#isUsingExistingProduct').val('true');
    });
    
    // Configuration DataTable
    const table = $("#plat-datatable").DataTable({
        autoWidth: true,
        paging: true,
        pageLength: 16,
        lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
        searching: true,
        ordering: true,
        order: [[0, 'asc']],
        language: {
            search: "Rechercher:",
            lengthMenu: "Afficher _MENU_ éléments",
            info: "Affichage de _START_ à _END_ sur _TOTAL_ éléments",
            paginate: { first: "Premier", last: "Dernier", next: "Suivant", previous: "Précédent" },
            zeroRecords: "Aucun résultat trouvé",
            infoEmpty: "Aucun élément à afficher",
            infoFiltered: "(filtré depuis _MAX_ éléments au total)"
        }
    });
    
    // Fonction de filtrage
    function filterTable() {
        const rayonId = $('#filtreRayon').val();
        const categorieId = $('#filtreCategorie').val();
        const sousCategorieId = $('#filtreSousCategorie').val();
        
        $.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
            if (settings.nTable.id !== 'plat-datatable') return true;
            
            const rowNode = settings.aoData[dataIndex].nTr;
            const rowRayonId = $(rowNode).attr('data-rayon-id');
            const rowCategorieId = $(rowNode).attr('data-categorie-id');
            const rowSousCategorieId = $(rowNode).attr('data-sous-categorie-id');
            
            // Filtre par rayon
            if (rayonId && rayonId !== "" && rowRayonId !== rayonId) {
                return false;
            }
            
            // Filtre par catégorie
            if (categorieId && categorieId !== "" && rowCategorieId !== categorieId) {
                return false;
            }
            
            // Filtre par sous-catégorie
            if (sousCategorieId && sousCategorieId !== "" && rowSousCategorieId !== sousCategorieId) {
                return false;
            }
            
            return true;
        });
        
        table.draw();
        
        // Retirer le filtre après l'application pour éviter les doublons
        $.fn.dataTable.ext.search.pop();
    }
    
    // Validation du formulaire
    $('#formPlat').on('submit', function(e) {
        // Toujours en mode produit existant pour les nouveaux ajouts
        const productIds = $('#selectedProductIds').val();
        
        if (!$('#thePlatId').val() && (!productIds || productIds.trim() === '')) {
            // Nouvel ajout - besoin de produits sélectionnés
            e.preventDefault();
            alert('Veuillez sélectionner au moins un produit.');
            return false;
        }
        
        // Pour l'édition (mode manuel), valider les champs nécessaires
        if ($('#thePlatId').val() && $('#isUsingExistingProduct').val() === 'false') {
            const rayonId = $('#rayonPlat').val();
            const categorieId = $('#categoriePlat').val();
            const sousCategorieId = $('#sousCategoriePlat').val();
            const nom = $('#nomPlat').val();
            const prix = $('#prixPlat').val();
            
            if (!rayonId || !categorieId || !sousCategorieId || !nom || !prix) {
                e.preventDefault();
                alert('Veuillez remplir tous les champs obligatoires.');
                return false;
            }
        }
        
        return true;
    });
    
    // Initialiser l'état du modal si en mode édition
    <% if (isEdit && plat != null) { %>
        console.log("Mode édition détecté");
        $('.modal-plat').modal('show');
    <% } %>
    
    // Initialiser les filtres
    loadCategoriesForFilter('');
});
</script>