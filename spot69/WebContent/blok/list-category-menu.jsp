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
								Les catégories du menu
							</h2>
							<div class="custom-btn-group">
								<button class="btn btn-outline-primary" id="btnAjouterCategorie" data-toggle="modal" data-target=".modal-cat">
									<i class="fe fe-plus fe-16"></i> Ajouter une catégorie
								</button>
							</div>
						</div>

						<div class="card shadow">
							<div class="card-body">
								<table class="table datatables" id="category-datatable">
									<thead>
										<tr>
											<th>Nom</th>
											<th>Description</th>
											<th>Rayon</th>
											<th>Date de création</th>
											<th>Date de modification</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<%
											List<MenuCategorie> categories = (List<MenuCategorie>) request.getAttribute("categorie-parente");
											if (categories != null) {
												for (MenuCategorie cat : categories) {
										%>
										<tr>
											<td><%= cat.getNom() %></td>
											<td><%= cat.getDescription() %></td>
											<td>
												<% if (cat.getRayon() != null) { %>
													<%= cat.getRayon().getNom() %>
												<% } else { %>
													<span class="text-muted">-</span>
												<% } %>
											</td>
											<td><%= cat.getCreationDate().toString().replace('T', ' ') %></td>
											<td><%= cat.getUpdateDate().toString().replace('T', ' ') %></td>
											<td>
												<button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
													<span class="text-muted sr-only">Action</span>
												</button>
												<div class="dropdown-menu dropdown-menu-right">
												<% if (canEditMenuCategories) { %>
													<a class="dropdown-item" href="MenuServlet?action=editCategorie&id=<%= cat.getId() %>&page=l-cat">Modifier</a>
													<% } %>
													  <% if (canDeleteMenuCategories) { %>
													<a class="dropdown-item" href="MenuServlet?action=deleteCategorie&id=<%= cat.getId() %>&page=l-cat">Supprimer</a>
														<% } %>
													
													<a class="dropdown-item show-subcategories" href="#" data-id="<%= cat.getId() %>">Voir les sous-catégories</a>
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

	<%
		com.spot69.model.MenuCategorie categorie = (com.spot69.model.MenuCategorie) request.getAttribute("categorie");
		boolean isEdit = (categorie != null);
		Integer editCatId = (categorie != null) ? categorie.getId() : null;
		
		// Récupérer la liste des rayons
		com.spot69.dao.RayonDAO rayonDAO = new com.spot69.dao.RayonDAO();
		List<com.spot69.model.Rayon> rayons = rayonDAO.getAll();
	%>

	<div class="modal fade modal-cat modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalCategorieLabel" aria-hidden="true">
		<div class="modal-dialog modal-md" role="document">
			<div class="modal-content">
				<form id="formCategorie" action="MenuServlet" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
					<div class="modal-header">
						<h5 class="modal-title" id="modalCategorieLabel">
							<%= isEdit ? "Modifier la catégorie" : "Ajouter une catégorie" %>
						</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>

					<div class="modal-body">
						<input type="hidden" name="action" value="<%= isEdit ? "updateCategorie" : "addCategorie" %>">
						<input type="hidden" name="page" value="l-cat">
						<input type="hidden" name="utilisateurId" value="${sessionScope.userId != null ? sessionScope.userId : 1}">

						<% if (isEdit) { %>
							<input type="hidden" name="id" value="<%= categorie.getId() %>">
						<% } %>

						<div class="form-group">
							<label for="nomCategorie">Nom *</label>
							<input type="text" class="form-control" id="nomCategorie" name="nom" required value="<%= isEdit ? categorie.getNom() : "" %>">
						</div>

						<div class="form-group">
							<label for="descriptionCategorie">Description</label>
							<textarea class="form-control" id="descriptionCategorie" name="description" rows="3"><%= isEdit ? categorie.getDescription() : "" %></textarea>
						</div>

						<!-- Sélection du rayon -->
						<div class="form-group" id="rayonGroup">
							<label for="rayonId">Rayon</label>
							<select class="form-control" id="rayonId" name="rayonId">
								<option value="">-- Sélectionner un rayon --</option>
								<%
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
							<small class="form-text text-muted" id="rayonHelpText">Le rayon est obligatoire pour les catégories parentes</small>
						</div>

						<%-- <div class="form-group" id="categorieParenteGroup">
							<label for="categorie">Catégorie parente</label>
							<select class="form-control" id="categorie" name="parentId">
								<option value="">-- Sélectionner une catégorie parente --</option>
								<%
									List<MenuCategorie> allCategories = (List<MenuCategorie>) request.getAttribute("categorie-parente");
									if (allCategories != null) {
										for (MenuCategorie cat : allCategories) {
											if (editCatId != null && editCatId.equals(cat.getId())) continue;
											if (cat.getParentId() == null) {
												boolean selected = isEdit && categorie.getParentId() != null && categorie.getParentId().equals(cat.getId());
								%>
								<option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>><%= cat.getNom() %></option>
								<%
											}
										}
									}
								%>
							</select>
						</div> --%>

						<div class="form-group" id="sousCategorieGroup">
							<label for="sousCategorie">Sous-catégorie</label>
							<select class="form-control" id="sousCategorie" name="sousCategorieId" disabled>
								<option value="">-- Sélectionner une sous-catégorie --</option>
							</select>
						</div>

						<div class="form-group">
							<label for="imageCategorie">Image de la catégorie</label>
							<input type="file" class="form-control" id="imageCategorie" name="image" accept="image/*">

							<%
								if (isEdit) {
									String imageUrl = categorie.getImageUrl();
									if (imageUrl != null && !imageUrl.isEmpty()) {
							%>
								<div class="mt-2">
									<img src="<%= request.getContextPath() %>/<%= imageUrl %>" alt="<%= categorie.getNom() %>" width="100" />
									<small class="form-text text-muted">Image actuelle</small>
								</div>
							<%
									} else {
							%>
								<div class="mt-2">
									<img src="https://themewagon.github.io/yummy-red/assets/img/menu/menu-item-4.png" alt="Image par défaut" width="100" />
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
							<%= isEdit ? "Modifier la catégorie" : "Ajouter la catégorie" %>
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
		// Initialiser l'état du formulaire en mode édition
		toggleRayonRequired();
	});
</script>
<%
}
%>

<script>
$(document).ready(function() {
    // Gérer la logique entre rayon et catégorie parente
    function toggleRayonRequired() {
        const parentId = $('#categorie').val();
        const rayonSelect = $('#rayonId');
        const rayonHelpText = $('#rayonHelpText');
        
        if (!parentId) {
            // C'est une catégorie parente → rayon obligatoire
            rayonSelect.prop('required', true);
            rayonHelpText.removeClass('text-muted').addClass('text-danger');
        } else {
            // C'est une sous-catégorie → rayon non obligatoire
            rayonSelect.prop('required', false);
            rayonHelpText.removeClass('text-danger').addClass('text-muted');
        }
    }
    
    // Initialiser l'état
    toggleRayonRequired();
    
    // Écouter les changements de catégorie parente
    $('#categorie').on('change', function() {
        toggleRayonRequired();
        
        let parentId = $(this).val();
        let $sousCategorie = $('#sousCategorie');
        $sousCategorie.empty().append('<option value="">-- Sélectionner une sous-catégorie --</option>');

        if (!parentId) {
            $sousCategorie.prop('disabled', true);
            return;
        }

        $.ajax({
            url: 'MenuServlet?action=sous-categories&parentId=' + parentId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                if (data && data.sousCategories.length > 0) {
                    data.sousCategories.forEach(function(cat) {
                        $sousCategorie.append(
                            $('<option>', { value: cat.id, text: cat.nom })
                        );
                    });
                    $sousCategorie.prop('disabled', false);
                } else {
                    $sousCategorie.prop('disabled', true);
                }
            },
            error: function() {
                alert("Erreur lors du chargement des sous-catégories");
                $sousCategorie.prop('disabled', true);
            }
        });
    });

    // Gestion de l'affichage des groupes
    function initializeFormState() {
       // $('#categorieParenteGroup').show();
        $('#sousCategorieGroup').hide();
        toggleRayonRequired();
    }
    
    // Initialiser l'état du formulaire
    initializeFormState();

    // Validation du formulaire
    $('#formCategorie').on('submit', function(e) {
        const parentId = $('#categorie').val();
        const rayonId = $('#rayonId').val();
        const nom = $('#nomCategorie').val();
        
        // Validation basique
        if (!nom.trim()) {
            e.preventDefault();
            alert('Le nom de la catégorie est obligatoire.');
            return false;
        }
        
        // Validation rayon pour les catégories parentes
        if (!parentId && (!rayonId || rayonId === '')) {
            e.preventDefault();
            alert('Le rayon est obligatoire pour les catégories parentes.');
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
        initializeFormState();
    });
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

    // Construire un tableau JS des catégories côté client
    const categoriesData = [
        <%
        if (categories != null) {
            for (MenuCategorie cat : categories) {
                // Gestion sécurisée des valeurs null
                String nom = cat.getNom() != null ? cat.getNom().replace("\"", "\\\"") : "";
                String description = cat.getDescription() != null ? cat.getDescription().replace("\"", "\\\"") : "";
                String creationDate = cat.getCreationDate() != null ? cat.getCreationDate().toString().replace('T', ' ') : "";
                String updateDate = cat.getUpdateDate() != null ? cat.getUpdateDate().toString().replace('T', ' ') : "";
                String rayonNom = "";
                if (cat.getRayon() != null && cat.getRayon().getNom() != null) {
                    rayonNom = cat.getRayon().getNom().replace("\"", "\\\"");
                }
        %>
        {
            id: <%=cat.getId()%>,
            parentId: <%=cat.getParentId() == null ? "null" : cat.getParentId()%>,
            nom: "<%=nom%>",
            description: "<%=description%>",
            creationDate: "<%=creationDate%>",
            updateDate: "<%=updateDate%>",
            rayon: "<%=rayonNom%>"
        },
        <%
            }
        }
        %>
    ];

    // Gérer le clic sur le lien "Voir les sous-catégories"
    $('#category-datatable tbody').on('click', 'a.show-subcategories', function (e) {
        e.preventDefault();
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const catId = parseInt($(this).data('id'));

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
            $(this).text('Voir les sous-catégories');
        } else {
            // Afficher un indicateur de chargement
            row.child('<div class="p-2 text-center"><div class="spinner-border spinner-border-sm" role="status"></div> Chargement des sous-catégories...</div>').show();
            tr.addClass('shown');
            $(this).text('Chargement...');

            fetch("<%=request.getContextPath()%>/MenuServlet?action=sous-categories&parentId=" + catId)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Erreur HTTP: " + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (data.sousCategories && Array.isArray(data.sousCategories) && data.sousCategories.length > 0) {
                        let html = "<table class='table table-sm mb-0'>";
                        html += "<thead class='thead-light'><tr><th>Nom</th><th>Description</th><th>Date création</th><th>Date modification</th><th>Actions</th></tr></thead><tbody>";
                        
                        data.sousCategories.forEach(function (c) {
                            html += "<tr>";
                            html += "<td>" + (c.nom || "-") + "</td>";
                            html += "<td>" + (c.description || "-") + "</td>";
                            html += "<td>" + (c.creationDate ? formatDate(c.creationDate) : "-") + "</td>";
                            html += "<td>" + (c.updateDate ? formatDate(c.updateDate) : "-") + "</td>";
                            html += "<td>";
                            html += "<div class='dropdown'>";
                            html += "<button class='btn btn-sm dropdown-toggle more-horizontal' type='button' data-toggle='dropdown'>";
                            html += "<span class='text-muted sr-only'>Action</span>";
                            html += "</button>";
                            html += "<div class='dropdown-menu dropdown-menu-right'>";
                            html += "<a class='dropdown-item' href='MenuServlet?action=editCategorie&id=" + c.id + "&page=l-cat'>Modifier</a>";
                            html += "<a class='dropdown-item' href='MenuServlet?action=deleteCategorie&id=" + c.id + "&page=l-cat'>Supprimer</a>";
                            html += "</div>";
                            html += "</div>";
                            html += "</td>";
                            html += "</tr>";
                        });

                        html += "</tbody></table>";
                        row.child(html).show();
                        tr.addClass('shown');
                        $(tr).find('.show-subcategories').text('Cacher les sous-catégories');
                    } else {
                        row.child("<div class='p-3 text-muted'>Aucune sous-catégorie disponible.</div>").show();
                        tr.addClass('shown');
                        $(tr).find('.show-subcategories').text('Cacher les sous-catégories');
                    }
                })
                .catch(function (error) {
                    console.error("Erreur lors du chargement des sous-catégories :", error);
                    row.child("<div class='p-2 text-danger'>Erreur de chargement des sous-catégories.</div>").show();
                    tr.addClass('shown');
                    $(tr).find('.show-subcategories').text('Cacher les sous-catégories');
                });
        }
    });s
});
</script>

<style>
.table img {
    border: 1px solid #dee2e6;
    border-radius: 4px;
}

.shown {
    background-color: #f8f9fa !important;
}

.table-details {
    background-color: #f8f9fa;
    border-left: 4px solid #007bff;
}

.text-danger {
    color: #dc3545 !important;
    font-weight: 500;
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