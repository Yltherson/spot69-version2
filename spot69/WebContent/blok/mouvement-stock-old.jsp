<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Produit,com.spot69.model.MouvementStock,com.spot69.model.InventaireCategorie"%>
<meta charset="UTF-8">
<style>
	small{
		font-size : 10px;
	}
</style>
<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
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
											<th>Satut</th>
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
                imagePath = ctx + "/images/produits/default-image.jpg";
            }
%>
    <tr>
        <td>
            <div style="display: flex; align-items: center;">
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
            <button class="btn btn-sm dropdown-toggle more-horizontal"
                type="button" data-toggle="dropdown" aria-haspopup="true"
                aria-expanded="false">
                <span class="text-muted sr-only">Action</span>
            </button>
            <div class="dropdown-menu dropdown-menu-right">
                <%-- <a class="dropdown-item btn-edit-produit" href="#"
                    data-toggle="modal" data-target=".modal-update-produit"
                    data-id="<%=p.getId()%>"
                    data-nom="<%=p.getNom()%>"
                    data-categorieId="<%=p.getCategorie().getId()%>"
                    data-sousCategorieId="<%=p.getSousCategorie().getId()%>"
                    data-description="<%=p.getDescription()%>"
                    data-nom="<%=p.getImageUrl()%>"
                    data-emplacement="<%=p.getEmplacement()%>"
                    data-unite="<%=p.getUniteVente()%>"
                    data-prixVente="<%=p.getPrixVente()%>"
                    data-contenu="<%=p.getContenuParUnite()%>"
                    data-seuil="<%=p.getSeuilAlerte()%>">
                    Modifier
                </a> --%>
                <a class="dropdown-item btn-show-mouvement" href="#" data-id="<%=p.getId()%>">Mouvement stock</a>
               <a class="dropdown-item btn-ajuster-stock" href="#" 
	       data-id="<%=p.getId()%>"
	       data-nom="<%=p.getNom()%>"
	       data-stock="<%=p.getQteEnStock()%>">
	        Ajustement stock
	    </a>

                <form method="POST" action="ProduitServlet?action=supprimer"
                    style="display: inline;"
                    onsubmit="return confirm('Confirmer la suppression ?');">
                    <input type="hidden" name="id" value="<%=p.getId()%>" />
                    <button type="submit" class="dropdown-item">Supprimer</button>
                </form>
            </div>
        </td>
    </tr>
<%
        }
    }
%>
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
	
<!-- Alternative plus simple -->
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

                    <!-- Catégorie -->
                    <div class="form-group">
                        <label>Catégorie *</label>
                        <select class="form-control" name="categorieId" id="editCategorie" required>
                            <option value="">Sélectionnez</option>
                            <% 
                            List<InventaireCategorie> categories = (List<InventaireCategorie>) request.getAttribute("categoriesAvecSous");
                            for (InventaireCategorie cat : categories) {
                            %>
                                <option value="<%=cat.getId()%>"><%=cat.getNom()%></option>
                            <% } %>
                        </select>
                    </div>

                    <!-- Sous-catégorie -->
                    <div class="form-group">
                        <label>Sous-catégorie *</label>
                        <select class="form-control" name="sousCategorieId" id="editSousCategorie" required disabled>
                            <option value="">Sélectionnez</option>
                            <% 
                            for (InventaireCategorie cat : categories) {
                                for (InventaireCategorie sous : cat.getSousCategories()) {
                            %>
                                <option value="<%=sous.getId()%>" data-cat="<%=cat.getId()%>"><%=sous.getNom()%></option>
                            <% } } %>
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
                    
                    <!-- Contenu -->
                    <div class="form-group">
                        <label for="editContenu">Prix de vente *</label>
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

</main>

<jsp:include page="footer.jsp" />
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
<!-- DataTable -->
<script>
	$("#dataTable-1").DataTable({
		autoWidth: true,
		lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
	});
	
</script>
<script>
document.addEventListener("DOMContentLoaded", () => {
    const table = $('#produit-table').DataTable({
        order: [[0, 'desc']]
    });

    $('#produit-table tbody').on('click', '.btn-show-mouvement', function (e) {
        e.preventDefault();

        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const produitId = $(this).data('id');

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
        } else {
            fetch('MouvementStockServlet?action=get-mouvement-produit&id=' + produitId)
                .then(res => {
                    if (!res.ok) throw new Error('Erreur serveur');
                    return res.json();
                })
                /* .then(data => {
                    if (Array.isArray(data) && data.length > 0) {
                        let html = '<table class="table table-sm table-striped mb-0">';
                        html += '<thead><tr>' +
                            '<th>Date</th>' +
                            '<th>Stock Début</th>' +
                            '<th>Entrée</th>' +
                            '<th>Sortie</th>' +
                            '<th>Stock Fin</th>' +
                            '</tr></thead><tbody>';

                        data.forEach(mvt => {
                            const date = new Date(mvt.date);
                            const formattedDate = date.toLocaleString();
                            const detail = mvt.factureDetail || {};
                            const facture = detail.facture || {};
                            const quantite = detail.quantite !== undefined ? detail.quantite : 0;
                            const qteVendu = detail.qteVendu !== undefined ? detail.qteVendu : 0;
                            const noFacture = facture.noFacture ? facture.noFacture : '-';

                            html += '<tr>';
                            html += '<td>' + formattedDate + '</td>';
                            html += '<td>' + mvt.stockDebut + '</td>';
                            html += '<td>' + (mvt.qteIn > 0 ? mvt.qteIn : '-') + '</td>';
                            html += '<td>' + (mvt.qteOut > 0 ? mvt.qteOut : '-') + '</td>';
                            html += '<td>' + mvt.stockFin + '</td>';
                            html += '</tr>';
                        });

                        html += '</tbody></table>';
                        row.child(html).show();
                        tr.addClass('shown');
                    } else {
                        row.child("<div class='p-2'>Aucun mouvement de stock trouvé pour ce produit.</div>").show();
                        tr.addClass('shown');
                    }
                }) */
                .then(data => {
                    if (Array.isArray(data) && data.length > 0) {
                        let html = '<table class="table table-sm table-striped mb-0">';
                        html += '<thead><tr>' +
                            '<th>Date</th>' +
                            '<th>Type</th>' +   // <-- nouvelle colonne
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
                            html += '<td>' + mvt.typeMouvement + '</td>'; // <-- affichage du type
                            html += '<td>' + mvt.stockDebut + '</td>';
                            html += '<td>' + (mvt.qteIn > 0 ? mvt.qteIn : '-') + '</td>';
                            html += '<td>' + (mvt.qteOut > 0 ? mvt.qteOut : '-') + '</td>';
                            html += '<td>' + mvt.stockFin + '</td>';
                            html += '</tr>';
                        });

                        html += '</tbody></table>';
                        row.child(html).show();
                        tr.addClass('shown');
                    } else {
                        row.child("<div class='p-2'>Aucun mouvement de stock trouvé pour ce produit.</div>").show();
                        tr.addClass('shown');
                    }
                })

                .catch(err => {
                    console.error(err);
                    row.child("<div class='p-2 text-danger'>Erreur lors du chargement des mouvements.</div>").show();
                    tr.addClass('shown');
                });
        }
    });
});
</script>


<!-- Script remplissage modal -->
<script>
$(document).ready(function () {
    $('.btn-edit-produit').click(function () {
        const button = $(this);

        $('#editProduitId').val(button.data('id'));
        $('#editNom').val(button.data('nom'));
        $('#editDescription').val(button.data('description'));
        $('#editEmplacement').val(button.data('emplacement'));
        $('#editUnite').val(button.data('unite'));
        $('#editContenu').val(button.data('contenu'));
        $('#editSeuil').val(button.data('seuil'));
        $('#editprixVente').val(button.data('prixvente'));

        // Récupérer catégorieId et sousCategorieId
        const catId = button.data('categorieid');       // Note : pas de camelCase en jQuery data()
        const sousCatId = button.data('souscategorieid');
        
     // Récupérer l'URL de l'image actuelle (remarque : tu as dans tes data-attributes "data-nom" pour l'image, ce n'est pas clair, change en "data-imageurl" par exemple)
        const imageUrl = button.data('imageurl'); // Assure-toi que dans le bouton tu as bien data-imageurl="..."

        if (imageUrl) {
            const contextPath = '<%=request.getContextPath()%>';
            const relativePath = imageUrl.substring("uploads/produits/".length);
            const fullImagePath = contextPath + '/blok/images/produits/' + relativePath;
            $('#editImagePreview').attr('src', fullImagePath).show();
        } else {
            $('#editImagePreview').hide();
        }

        // Réinitialiser le champ input file
        $('#editImage').val('');


        // Sélectionner la catégorie
        $('#editCategorie').val(catId);

        // Filtrer les sous-catégories visibles pour cette catégorie
        filterSousCategories(catId);

        // Préselectionner la sous-catégorie et activer le select si applicable
        if (sousCatId) {
            $('#editSousCategorie').val(sousCatId);
            $('#editSousCategorie').prop('disabled', false);
        } else {
            $('#editSousCategorie').val('');
            $('#editSousCategorie').prop('disabled', true);
        }
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

    // Au changement de catégorie, filtrer les sous-catégories
    $('#editCategorie').change(function () {
        const selectedCat = $(this).val();
        filterSousCategories(selectedCat);
    });

    function filterSousCategories(categorieId) {
        if (!categorieId) {
            $('#editSousCategorie').val('');
            $('#editSousCategorie').prop('disabled', true);
            return;
        }

        $('#editSousCategorie option').each(function () {
            const catAttr = $(this).data('cat');
            if (!catAttr) {
                // option "Sélectionnez"
                $(this).show();
            } else if (catAttr == categorieId) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });

        if ($('#editSousCategorie option:visible').length > 1) { // >1 pour exclure l'option vide
            $('#editSousCategorie').prop('disabled', false);
        } else {
            $('#editSousCategorie').prop('disabled', true);
        }
    }
});


</script>

<script>
$(document).ready(function () {
    // Gestion de la concaténation dynamique dans la modale
    const editUniteInput = document.getElementById("editUnite");
    const editContenuInput = document.getElementById("editContenu");

    function updateUniteAvecContenu() {
        const contenu = editContenuInput.value.trim();
        const uniteActuelle = editUniteInput.value;
        const uniteOriginale = editUniteInput.getAttribute("data-original") || uniteActuelle.split(" (")[0].trim();

        if (uniteOriginale && contenu) {
            editUniteInput.value = uniteOriginale + " (" + contenu + ")";
        } else {
            editUniteInput.value = uniteOriginale;
        }

        // Stocke la version de base pour éviter les accumulations
        editUniteInput.setAttribute("data-original", uniteOriginale);
    }

    // Réagit à chaque frappe dans le champ contenu
    editContenuInput.addEventListener("input", updateUniteAvecContenu);

    // Si on modifie l’unité à la main, on oublie l’ancienne version
    editUniteInput.addEventListener("input", function () {
        editUniteInput.removeAttribute("data-original");
    });

    // Lors de l’ouverture de la modale, initialise aussi le data-original
    $('.btn-edit-produit').click(function () {
        const uniteText = $(this).data('unite') || '';
        const uniteBase = uniteText.split(" (")[0].trim();
        editUniteInput.setAttribute("data-original", uniteBase);
    });
});
</script>

