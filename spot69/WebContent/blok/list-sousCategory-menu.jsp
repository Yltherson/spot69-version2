<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.MenuCategorie,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditMenuCategories = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_MENU_CATEGORIES);
boolean canDeleteMenuCategories = canEditMenuCategories && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_MENU_CATEGORIES);
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

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
								Les sous-catégories du menu
							</h2>

							<button class="btn btn-outline-primary"
								id="btnAjouterSousCategorie" data-toggle="modal"
								data-target=".modal-cat">
								<i class="fe fe-plus fe-16"></i> Ajouter une sous-catégorie
							</button>
						</div>

						<div class="card shadow">
							<div class="card-body">
								<table class="table datatables" id="category-datatable">
									<thead>
										<tr>
											<th>Nom</th>
											<th>Description</th>
											<th>Catégorie Parente</th>
											<th>Date de création</th>
											<th>Date de modification</th>
											<th>Action</th>
										</tr>
									</thead>
									<%
									    List<MenuCategorie> categories = (List<MenuCategorie>) request.getAttribute("sous-categories");
									    com.spot69.model.MenuCategorie categorie = (com.spot69.model.MenuCategorie) request.getAttribute("categorie");
									    boolean isEdit = (categorie != null);
									    Integer editCatId = isEdit ? categorie.getId() : null;
									%>
									
									<!-- Liste des sous-catégories -->
									<tbody>
									<% if (categories != null) {
									       for (MenuCategorie cat : categories) { 
									           // Récupérer le nom de la catégorie parente
									           String nomParent = "";
									           if (cat.getParentId() != null) {
									               com.spot69.dao.MenuCategorieDAO catDAO = new com.spot69.dao.MenuCategorieDAO();
									               MenuCategorie parent = catDAO.getById(cat.getParentId());
									               if (parent != null) {
									                   nomParent = parent.getNom();
									               }
									           }
									%>
									    <tr>
									        <td><%= cat.getNom() %></td>
									        <td><%= cat.getDescription() != null ? cat.getDescription() : "" %></td>
									        <td><%= nomParent %></td>
									        <td><%= cat.getCreationDate().toString().replace('T', ' ') %></td>
									        <td><%= cat.getUpdateDate().toString().replace('T', ' ') %></td>
									        <td>
									            <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									                <span class="text-muted sr-only">Action</span>
									            </button>
									            <div class="dropdown-menu dropdown-menu-right">
									            <% if (canEditMenuCategories) { %>
									                <a class="dropdown-item" href="MenuServlet?action=editSousCategorie&id=<%= cat.getId() %>&page=l-subCat">Modifier</a>
									                <% } %>
									                	  <% if (canDeleteMenuCategories) { %>
									                <a class="dropdown-item" href="MenuServlet?action=deleteSousCategorie&id=<%= cat.getId() %>&page=l-subCat">Supprimer</a>
									           	<% } %>
									            </div>
									        </td>
									    </tr>
									<%     }
									   }
									%>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade modal-cat modal-slide" tabindex="-1"
	    role="dialog" aria-labelledby="modalCategorieLabel" aria-hidden="true">
	    <div class="modal-dialog modal-md" role="document">
	        <div class="modal-content">
	            <form id="formCategorie" action="MenuServlet" method="post"
	                enctype="multipart/form-data" accept-charset="UTF-8">
	                <div class="modal-header">
	                    <h5 class="modal-title" id="modalCategorieLabel">
	                        <%= isEdit ? "Modifier la sous-catégorie" : "Ajouter une sous-catégorie" %>
	                    </h5>
	                    <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
	                        <span aria-hidden="true">&times;</span>
	                    </button>
	                </div>

	                <div class="modal-body">
	                    <input type="hidden" name="action"
	                        value="<%= isEdit ? "updateCategorie" : "addCategorie" %>">
	                    <input type="hidden" name="page" value="l-subCat">
	                    <input type="hidden" name="utilisateurId" value="${sessionScope.userId != null ? sessionScope.userId : 1}">

	                    <% if (isEdit) { %>
	                        <input type="hidden" name="id" value="<%= categorie.getId() %>">
	                    <% } %>

	                    <div class="form-group">
	                        <label for="nomCategorie">Nom *</label>
	                        <input type="text" class="form-control" id="nomCategorie" name="nom" required
	                            value="<%= isEdit ? categorie.getNom() : "" %>">
	                    </div>

	                    <div class="form-group">
	                        <label for="descriptionCategorie">Description</label>
	                        <textarea class="form-control" id="descriptionCategorie" name="description" rows="3"><%= isEdit ? categorie.getDescription() : "" %></textarea>
	                    </div>

	                    <!-- Sélection du rayon -->
	                    <div class="form-group" id="rayonGroup">
	                        <label for="rayonId">Rayon *</label>
	                        <select class="form-control" id="rayonId" name="rayonId" required>
	                            <option value="">-- Sélectionner un rayon --</option>
	                            <%
	                                // Récupérer la liste des rayons
	                                com.spot69.dao.RayonDAO rayonDAO = new com.spot69.dao.RayonDAO();
	                                List<com.spot69.model.Rayon> rayons = rayonDAO.getAll();
	                                
	                                if (rayons != null) {
	                                    for (com.spot69.model.Rayon rayon : rayons) {
	                                        boolean selected = false;
	                                        if (isEdit && categorie.getRayon() != null) {
	                                            selected = categorie.getRayon().getId() == rayon.getId();
	                                        }
	                            %>
	                            <option value="<%= rayon.getId() %>" <%= selected ? "selected" : "" %>>
	                                <%= rayon.getNom() %>
	                            </option>
	                            <%
	                                    }
	                                }
	                            %>
	                        </select>
	                        <small class="form-text text-muted">Sélectionnez d'abord un rayon pour voir ses catégories</small>
	                    </div>

	                    <!-- Catégorie parente - sera rempli dynamiquement -->
	                    <div class="form-group" id="categorieParenteGroup">
	                        <label for="categorie">Catégorie parente *</label>
	                        <select class="form-control" id="categorie" name="parentId" required disabled>
	                            <option value="">-- Sélectionnez d'abord un rayon --</option>
	                            <% 
	                                // En mode édition, pré-remplir avec la catégorie actuelle
	                                if (isEdit && categorie.getParentId() != null) {
	                                    com.spot69.dao.MenuCategorieDAO catDAO = new com.spot69.dao.MenuCategorieDAO();
	                                    MenuCategorie parentCat = catDAO.getById(categorie.getParentId());
	                                    if (parentCat != null) {
	                            %>
	                            <option value="<%= parentCat.getId() %>" selected><%= parentCat.getNom() %></option>
	                            <%
	                                    }
	                                }
	                            %>
	                        </select>
	                    </div>

	                    <!-- Image -->
	                    <div class="form-group">
	                        <label for="imageCategorie">Image de la sous-catégorie</label>
	                        <input type="file" class="form-control" id="imageCategorie" name="image" accept="image/*">

	                        <%
	                            if (isEdit) {
	                                String imageUrl = categorie.getImageUrl();
	                                if (imageUrl != null && !imageUrl.isEmpty()) {
	                        %>
	                            <div class="mt-2">
	                                <img src="<%= request.getContextPath() %>/<%= imageUrl %>" 
	                                     alt="<%= categorie.getNom() %>" width="100" />
	                                <small class="form-text text-muted">Image actuelle</small>
	                            </div>
	                        <%
	                                } else {
	                        %>
	                            <div class="mt-2">
	                                <img src="https://themewagon.github.io/yummy-red/assets/img/menu/menu-item-4.png" 
	                                     alt="Image par défaut" width="100" />
	                                <small class="form-text text-muted">Image par défaut</small>
	                            </div>
	                        <%
	                                }
	                            }
	                        %>
	                    </div>
	                </div>

	                <div class="modal-footer">
	                    <button type="submit" class="btn btn-primary btn-block">
	                        <%= isEdit ? "Modifier la sous-catégorie" : "Ajouter la sous-catégorie" %>
	                        <i class="fe fe-send"></i>
	                        <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
	                    </button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>
</main>

<jsp:include page="footer.jsp" />

<%
if (isEdit) {
%>
<script>
	$(document).ready(function() {
		$('.modal-cat').modal('show');
	});
</script>
<%
}
%>

<script>
$(document).ready(function() {
    // Gérer le changement de rayon
    $('#rayonId').on('change', function() {
        const rayonId = $(this).val();
        const $categorieSelect = $('#categorie');
        
        // Réinitialiser le select des catégories
        $categorieSelect.empty();
        
        if (!rayonId) {
            // Aucun rayon sélectionné
            $categorieSelect.append('<option value="">-- Sélectionnez d\'abord un rayon --</option>');
            $categorieSelect.prop('disabled', true);
            $categorieSelect.prop('required', false);
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
                    $categorieSelect.append('<option value="">-- Sélectionner une catégorie parente --</option>');
                    
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
                    
                    // En mode édition, sélectionner la catégorie parente actuelle
                    <% if (isEdit && categorie.getParentId() != null) { %>
                        $categorieSelect.val('<%= categorie.getParentId() %>');
                    <% } %>
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
                
                alert("Erreur lors du chargement des catégories. Veuillez réessayer.");
            }
        });
    });
    
    // Validation du formulaire
    $('#formCategorie').on('submit', function(e) {
        const nom = $('#nomCategorie').val();
        const rayonId = $('#rayonId').val();
        const parentId = $('#categorie').val();
        
        // Validation basique
        if (!nom.trim()) {
            e.preventDefault();
            alert('Le nom de la sous-catégorie est obligatoire.');
            return false;
        }
        
        // Validation rayon
        if (!rayonId) {
            e.preventDefault();
            alert('Veuillez sélectionner un rayon.');
            return false;
        }
        
        // Validation catégorie parente
        if (!parentId) {
            e.preventDefault();
            alert('Veuillez sélectionner une catégorie parente.');
            return false;
        }
        
        // Afficher l'indicateur de chargement
        const submitBtn = $(this).find('button[type="submit"]');
        const spinner = submitBtn.find('.spinner-border');
        submitBtn.prop('disabled', true);
        spinner.removeClass('d-none');
        
        return true;
    });
    
    // Réinitialiser le formulaire quand le modal se ferme
    $('.modal-cat').on('hidden.bs.modal', function () {
        $('#formCategorie')[0].reset();
        const submitBtn = $('#formCategorie').find('button[type="submit"]');
        submitBtn.prop('disabled', false);
        submitBtn.find('.spinner-border').addClass('d-none');
        
        // Réinitialiser le select des catégories
        $('#categorie').empty().append('<option value="">-- Sélectionnez d\'abord un rayon --</option>')
                      .prop('disabled', true).prop('required', false);
    });
    
    // En mode édition, déclencher le chargement des catégories si rayon déjà sélectionné
    <% if (isEdit && categorie.getRayon() != null) { %>
        $(document).ready(function() {
            // Déclencher le changement pour charger les catégories du rayon
            setTimeout(function() {
                $('#rayonId').trigger('change');
            }, 500);
        });
    <% } %>
});

function formatDate(obj) {
    if (!obj || !obj.date || !obj.time) return "-";

    const d = obj.date;
    const t = obj.time;

    const year = d.year;
    const month = String(d.month).padStart(2, '0');
    const day = String(d.day).padStart(2, '0');

    const hour = String(t.hour).padStart(2, '0');
    const minute = String(t.minute).padStart(2, '0');
    const second = String(t.second).padStart(2, '0');

    return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
}

$(document).ready(function () {
    const table = $('#category-datatable').DataTable({
        autoWidth: true,
        lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json'
        }
    });
});
</script>

<style>
.table img {
    border: 1px solid #dee2e6;
    border-radius: 4px;
}

.card {
    border: none;
    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.card-header {
    background-color: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
}


</style>