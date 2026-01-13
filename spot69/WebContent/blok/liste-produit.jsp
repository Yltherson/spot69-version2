<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Produit,com.spot69.model.MenuCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker,com.spot69.model.Rayon"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditProduits = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_PRODUITS);
boolean canDeleteProduits = canEditProduits && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_PRODUITS);

List<MenuCategorie> menuCategories = (List<MenuCategorie>) request.getAttribute("menuCategories");
%>
<style>
	small{
		font-size : 10px;
	}
</style>
<!DOCTYPE html>
<jsp:include page="header.jsp"/>
<jsp:include page="topbar2.jsp"/>
<%-- <jsp:include page="sidebar.jsp" /> --%>
<main role="main" class="main-content">
	<div class="container-fluid">
	<div class="row justify-content-center">
            <div class="col-12">
              <!-- <h2>Section title</h2> -->
              <h2 class="h5 page-title">
              	 <i class="fe fe-layers fe-32 align-self-center text-black"></i>
                                Gestion de stock
              </h2>
              <%
										List<Produit> produits = (List<Produit>) request.getAttribute("produits");
										
										%>
            <!-- info small box -->
          <div class="row">
  <!-- Total des produits -->
  <div class="col-md-4 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <span class="h2 mb-0"><%= request.getAttribute("qteTotaleProduits") != null ? request.getAttribute("qteTotaleProduits") : 0 %></span>
          <p class="small text-muted mb-0">Quantité totale de produits</p>
       </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-package text-muted mb-0"></span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Produits en rupture -->
  <div class="col-md-4 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <span class="h2 mb-0 text-danger"><%= request.getAttribute("nbProduitsEnRupture") != null ? request.getAttribute("nbProduitsEnRupture") : 0 %></span>
          <p class="small text-muted mb-0">Produits en rupture</p>
        </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-alert-triangle text-danger mb-0"></span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Valeur totale en stock -->
  <div class="col-md-4 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <span class="h2 mb-0"><%= request.getAttribute("GrandTotal") != null ? request.getAttribute("GrandTotal") : 0 %></span>
            <p class="small text-muted mb-0">Valeur totale du stock</p>
          </div>
          <div class="col-auto">
            <span class="fe fe-32  text-muted mb-0">HTG</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

        </div>
          </div> 
		<div class="row justify-content-center">
			<div class="col-12">
				<div class="row">
					<!-- Small table -->
					<div class="col-md-12 my-4">
						<div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="h4 mb-1">
                                Liste des produits
                            </h2>
                            <div class="custom-btn-group">
                            <a href="ProduitServlet?action=add" class="btn btn-outline-primary">
                                    <i class="fe fe-plus fe-16"></i> Ajouter 
                                </a>
                            </div>
                        </div>
						<div class="card shadow">
							<div class="card-body">
								<table id="produit-table" class="table datatables" id="dataTable-1">
									<thead>
										<tr>
											<th>Produit</th>
											<th>Catégorie</th>
											<th>Qté en stock</th>
											<th>Stock Min</th>
											<th>Prix unitaire</th>
											<th>Valeur</th>
											<th>Statut</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
<%
    String ctx = request.getContextPath();
    if (produits != null) {
        for (Produit p : produits) {
            String imageUrl = p.getImageUrl();
            String imagePath;
            if (imageUrl != null && imageUrl.startsWith("uploads/produits/")) {
                imagePath = ctx + "/images/produits/" + imageUrl.substring("uploads/produits/".length());
            } else {
                imagePath = ctx + "/images/default/default.jpg";
            }
            
            // Récupérer le rayon via la catégorie pour l'édition
            Integer rayonId = null;
            String rayonNom = "";
            if (p.getCategorie() != null && p.getCategorie().getRayon() != null) {
                rayonId = p.getCategorie().getRayon().getId();
                rayonNom = p.getCategorie().getRayon().getNom();
            }
%>
    <tr>
        <td>
            <div style="display: flex; align-items: center; cursor: pointer;" class="btn-show-mouvement-nom" data-id="<%=p.getId()%>">
                <img src="<%= imagePath %>" alt="<%= p.getNom() %>" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; margin-right: 10px;" />
                <div>
                    <strong><%= p.getNom() %></strong><br>
                    <small>CODE : <%= p.getCodeProduit() %></small>
                </div>
            </div>
        </td>

        <td><%=p.getCategorie() != null ? p.getCategorie().getNom() : ""%> / 
            <%=p.getSousCategorie() != null ? p.getSousCategorie().getNom() : ""%></td>

        <td><%=p.getQteEnStock() %></td>
        <td><%=p.getSeuilAlerte() %></td>
        <td><%= (p.getPrixVente() != null) ? p.getPrixVente() + " HTG" : "0 HTG" %></td>
        <td><%=p.getPrixTotal() + " HTG"%></td>
        <td>
            <%
                String statut = p.getStatutStock();
                String badgeClass = "";
                String texte = "";

                switch (statut) {
                    case "STOCK_OK":
                        badgeClass = "badge badge-success";
                        texte = "Stock OK";
                        break;
                    case "STOCK_BAS":
                        badgeClass = "badge badge-warning";
                        texte = "Stock bas";
                        break;
                    case "RUPTURE":
                        badgeClass = "badge badge-danger";
                        texte = "Rupture";
                        break;
                }
            %>
            <span class="<%= badgeClass %>"><%= texte %></span>
        </td>

        <td>
            <button class="btn btn-sm btn-outline-primary dropdown-toggle"
                type="button" data-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
                <i class="fe fe-plus fe-16"></i>
                <span class="sr-only">Actions</span>
            </button>
            <div class="dropdown-menu dropdown-menu-right">
            <% if (canEditProduits) { %>
                <a class="dropdown-item btn-edit-produit" href="#"
                    data-toggle="modal" data-target=".modal-update-produit"
                    data-id="<%=p.getId()%>"
                    data-nom="<%=p.getNom()%>"
                    data-rayonid="<%=p.getRayonId() != null ? p.getRayonId() : ""%>"
                    data-rayonnom="<%=rayonNom%>"
                    data-categorieId="<%=p.getCategorie() != null ? p.getCategorie().getId() : ""%>"
                    data-sousCategorieId="<%=p.getSousCategorie() != null ? p.getSousCategorie().getId() : ""%>"
                    data-description="<%=p.getDescription()%>"
                    data-imageurl="<%=p.getImageUrl()%>"
                    data-emplacement="<%=p.getEmplacement()%>"
                    data-unite="<%=p.getUniteVente()%>"
                    data-prixVente="<%=p.getPrixVente()%>"
                    data-contenu="<%=p.getContenuParUnite()%>"
                    data-seuil="<%=p.getSeuilAlerte()%>"
                    data-qtePoints="<%=p.getQtePoints()%>"
                    data-prixachatparunitevente="<%=p.getPrixAchatParUniteVente()%>">
                    Modifier
                </a>
                <% } %>
                
                <a class="dropdown-item btn-show-mouvement" href="#" data-id="<%=p.getId()%>">Mouvement stock</a>
               <a class="dropdown-item btn-ajuster-stock" href="#" 
	       data-id="<%=p.getId()%>"
	       data-nom="<%=p.getNom()%>"
	       data-stock="<%=p.getQteEnStock()%>">
	        Ajustement stock
	    </a>

                <% if (canDeleteProduits) { %>
                <form method="POST" action="ProduitServlet?action=supprimer"
                    style="display: inline;"
                    onsubmit="return confirm('Confirmer la suppression ?');">
                    <input type="hidden" name="id" value="<%=p.getId()%>" />
                    <button type="submit" class="dropdown-item">Supprimer</button>
                </form>
                <% } %>
            </div>
        </td>
    </tr>
<%
        }
    }
%>
</tbody>
<tfoot>
    <tr>
        <th colspan="5" style="text-align:right">Total :</th>
        <th id="grandTotalFooter"></th>
        <th colspan="2"></th>
    </tr>
</tfoot>


								</table>
							</div>
						</div>
					</div>
					<!-- customized table -->
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Modification Produit -->
<div class="modal fade modal-update-produit modal-slide" tabindex="-1" role="dialog"
     aria-labelledby="defaultModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <span class="fe fe-edit fe-24"></span> Modifier le produit
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="POST" action="ProduitServlet?action=modifier" id="formEditProduit" enctype="multipart/form-data">
                <div class="modal-body">
                    <input type="hidden" name="id" id="editProduitId">

                    <!-- Nom -->
                    <div class="form-group">
                        <label for="editNom">Nom *</label>
                        <input type="text" class="form-control" id="editNom" name="nom" required>
                    </div>

                    <!-- Description -->
                    <div class="form-group">
                        <label for="editDescription">Description</label>
                        <textarea class="form-control" id="editDescription" name="description"></textarea>
                    </div>
                    
                    <!-- Image (upload + preview) -->
					<div class="form-group">
					    <label for="editImage">Image du produit</label>
					    <input type="file" class="form-control-file" id="editImage" name="image" accept="image/*">
					
					    <div style="margin-top: 10px;">
					        <img id="editImagePreview" src="" alt="Aperçu image produit" style="max-width: 200px; max-height: 150px; display: none; border: 1px solid #ddd; padding: 4px;">
					    </div>
					</div>

                            <div class="form-group">
                                <label>Rayon *</label>
                                <select class="form-control" name="rayonId" id="editRayon" required>
                                    <option value="">-- Sélectionner un rayon --</option>
                                    <%
                                        // Récupérer la liste des rayons
                                        com.spot69.dao.RayonDAO rayonDAO = new com.spot69.dao.RayonDAO();
                                        List<com.spot69.model.Rayon> rayons = rayonDAO.getAll();
                                        
                                        if (rayons != null) {
                                            for (com.spot69.model.Rayon rayon : rayons) {
                                    %>
                                        <option value="<%= rayon.getId() %>"><%= rayon.getNom() %></option>
                                    <% 
                                            }
                                        } 
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Catégorie *</label>
                                <select class="form-control" name="categorieId" id="editCategorie" required disabled>
                                    <option value="">-- Sélectionnez d'abord un rayon --</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Sous-catégorie *</label>
                                <select class="form-control" name="sousCategorieId" id="editSousCategorie" required disabled>
                                    <option value="">-- Sélectionnez d'abord une catégorie --</option>
                                </select>
                            </div>

                    <!-- Emplacement -->
                    <div class="form-group">
                        <label>Emplacement</label>
                        <input type="text" class="form-control" name="emplacement" id="editEmplacement">
                    </div>

                    <!-- Unité -->
                    <div class="form-group">
                        <label for="editUnite">Unité de vente *</label>
                        <input type="text" class="form-control" id="editUnite" name="uniteVente" required>
                        <small class="text-muted">Sépare les unités par des virgules si nécessaire</small>
                    </div>

                    <!-- Contenu -->
                    <div class="form-group">
                        <label for="editContenu">Contenu par unité *</label>
                        <input type="number" class="form-control" id="editContenu" name="contenuParUnite" required>
                    </div>
                    
                                        
                    <!-- Prix d'achat -->
                    <div class="form-group">
                        <label for="editprixAchatParUniteVente">Prix d'achat *</label>
                        <input type="text" class="form-control" id="editprixAchatParUniteVente" name="prixAchatParUniteVente" required>
                        <small class="text-muted">Prix d'achat de l'unité de vente (caisse ou demi-caisse)</small>
                    </div>
                    
                    <!-- Prix de vente -->
                    <div class="form-group">
                        <label for="editprixVente">Prix de vente *</label>
                        <input type="number" class="form-control" id="editprixVente" name="prixVente" required>
                    </div>

                    <!-- Seuil -->
                    <div class="form-group">
                        <label for="editSeuil">Seuil d'alerte *</label>
                        <input type="number" class="form-control" id="editSeuil" name="seuilAlerte" required>
                    </div>
                    
                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Enregistrer <i class="fe fe-save"></i></button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Ajustement Stock -->
<div class="modal fade" id="modalAjustement" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Ajustement : <span id="modalProduitNom"></span></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form id="formAjustement" method="POST" action="MouvementStockServlet">
                <input type="hidden" name="action" value="ajuster-stock">
                <input type="hidden" name="produitId" id="ajustProduitId">
                
                <div class="modal-body">
                    <!-- Stock actuel -->
                    <div class="form-group">
                        <label>Stock actuel</label>
                        <div class="d-flex align-items-center mb-3">
                            <span class="h4 mb-0 text-primary" id="ajustStockActuelVisuel">0</span>
                            <span class="ml-2 text-muted">unités</span>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Valeur d'ajustement *</label>
                        <input type="number" class="form-control" id="ajustValeur" name="quantite" required 
                               placeholder="Positif pour entrée, Négatif pour sortie">
                        <small class="form-text text-muted">
                            Entrez une valeur positive pour ajouter du stock, négative pour en retirer.
                        </small>
                    </div>
                    
                    <!-- Nouvelle quantité -->
                    <div class="form-group">
                        <label>Nouvelle quantité après ajustement</label>
                        <div class="d-flex align-items-center">
                            <span class="h4 mb-0" id="ajustNouvelleQteVisuel">0</span>
                            <span class="ml-2 text-muted">unités</span>
                            <span class="ml-3" id="differenceBadge" style="display: none;"></span>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-primary">Appliquer</button>
                </div>
            </form>
        </div>
    </div>
</div>

</main>
<jsp:include page="bottombar.jsp" />
<jsp:include page="footer.jsp" />

<!-- DataTable -->
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