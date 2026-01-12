<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">
<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditCategories = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_CATEGORIES_SOUS_CATEGORIES);
boolean canDeleteCategories = canEditCategories && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_CATEGORIES_SOUS_CATEGORIES);
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
						<div
							class="d-flex justify-content-between align-items-center mb-3">
							<h2 class="h4 mb-1">
								<i class="fe fe-layers fe-32 align-self-center text-black"></i>
								Sous catégories de produits
							</h2>
							<div class="custom-btn-group">
								<button class="btn btn-outline-primary" id="btnAjouterCategorie"
									data-toggle="modal" data-target=".modal-cat">
									<i class="fe fe-plus fe-16"></i> Ajouter une sous-catégorie
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
											<th>Date de création</th>
											<th>Date de modification</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<%
										List<InventaireCategorie> sous_categories = (List<InventaireCategorie>) request.getAttribute("sousCategories-inventaire");
										if (sous_categories != null) {
											for (InventaireCategorie cat : sous_categories) {
										%>
										<tr>
											<td><%=cat.getNom()%></td>
											<td><%=cat.getDescription()%></td>
											<td><%=cat.getCreatedAt() != null ? cat.getCreatedAt().toString().replace('T', ' ') : "-"%></td>
											<td><%=cat.getUpdatedAt() != null ? cat.getUpdatedAt().toString().replace('T', ' ') : "-"%></td>
											<td>
												<button class="btn btn-sm dropdown-toggle more-horizontal"
													type="button" data-toggle="dropdown" aria-haspopup="true"
													aria-expanded="false">
													<span class="text-muted sr-only">Action</span>
												</button>
												<div class="dropdown-menu dropdown-menu-right">
												<% if (canEditCategories) { %>
												<a class="dropdown-item" href="InventaireCategorieServlet?action=edit&id=<%= cat.getId() %>&type=SOUS_CATEGORIE">
													    Modifier
													</a>
													<% } %>
													  <% if (canDeleteCategories) { %>
													<form method="POST" action="InventaireCategorieServlet"
														style="display: inline;"
														onsubmit="return confirm('Confirmer la suppression ?');">
														<input type="hidden" name="action" value="supprimer" /> <input
															type="hidden" name="id" value="<%=cat.getId()%>" /> <input
															type="hidden" name="type" value="SOUS_CATEGORIE" />
														<button type="submit"
															class="dropdown-item btn-delete-categorie">Supprimer</button>
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
								</table>
							</div>
						</div>
					</div>
					<!-- End table -->
				</div>
			</div>
		</div>
	</div>

<%
    List<InventaireCategorie> categories = (List<InventaireCategorie>) request.getAttribute("categories-inventaire");
    InventaireCategorie sous_categorie = (InventaireCategorie) request.getAttribute("sous-categorie-inventaire");
    boolean isEdit = (sous_categorie != null);
%>


	  <!-- Modal Ajout / Modification -->
        <div class="modal fade modal-cat modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalCategorieLabel" aria-hidden="true">
            <div class="modal-dialog modal-md" role="document">
                <div class="modal-content">
                    <form id="formCategorie" action="InventaireCategorieServlet" method="post" accept-charset="UTF-8">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalCategorieLabel">
                                <%= isEdit ? "Modifier la sous-catégorie" : "Ajouter une sous-catégorie" %>
                            </h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>

                        <div class="modal-body">
                            <input type="hidden" name="action" value="<%= isEdit ? "modifier" : "ajouter" %>">
                            <% if (isEdit) { %>
                                <input type="hidden" name="id" value="<%= sous_categorie.getId() %>">
                            <% } %>
                            <input type="hidden" name="type" value="SOUS_CATEGORIE">

                            <div class="form-group">
                                <label for="nomCategorie">Nom</label>
                                <input type="text" class="form-control" id="nomCategorie" name="nom" required value="<%= isEdit ? sous_categorie.getNom() : "" %>">
                            </div>

                            <div class="form-group">
                                <label for="parentId">Catégorie parente</label>
                                <select class="form-control" id="parentId" name="categorieId" required>
                                    <option value="">-- Sélectionner --</option>
                                    <% 
                                        if (categories != null) {
                                            for (InventaireCategorie p : categories) {
                                                if (!isEdit || p.getId() != sous_categorie.getId()) {
                                    %>
                                    <option value="<%= p.getId() %>" <%= (isEdit && p.getId() == sous_categorie.getCategorieId()) ? "selected" : "" %>>
                                        <%= p.getNom() %>
                                    </option>
                                    <% 
                                                }
                                            }
                                        } 
                                    %>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="descriptionCategorie">Description</label>
                                <textarea class="form-control" id="descriptionCategorie" name="description" rows="3"><%= isEdit ? sous_categorie.getDescription() : "" %></textarea>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary btn-block">
                                <%= isEdit ? "Modifier la catégorie" : "Ajouter la catégorie" %>
                                <i class="fe fe-send"></i>
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
    });

   
});
</script>
