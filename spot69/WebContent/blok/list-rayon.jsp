<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List,com.spot69.model.Rayon,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditRayons = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_RAYONS_CATEGORIES_SOUS_CATEGORIES);
boolean canDeleteRayons = canEditRayons && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_RAYONS_CATEGORIES_SOUS_CATEGORIES);
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
                                <i class="fe fe-grid fe-32 align-self-center text-black"></i>
                                Gestion des Rayons
                            </h2>
                            <div class="custom-btn-group">
                                <button class="btn btn-outline-primary" id="btnAjouterRayon" data-toggle="modal" data-target=".modal-rayon">
                                    <i class="fe fe-plus fe-16"></i> Ajouter un rayon
                                </button>
                            </div>
                        </div>

                        <div class="card shadow">
                            <div class="card-body">
                                <table class="table datatables" id="rayon-datatable">
                                    <thead>
                                        <tr>
                                            <th>Nom</th>
                                            <th>Description</th>
                                            <th>Image</th>
                                            <th>Date de création</th>
                                            <th>Date de modification</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            List<Rayon> rayons = (List<Rayon>) request.getAttribute("rayons");
                                            if (rayons != null) {
                                                for (Rayon rayon : rayons) {
                                        %>
                                        <tr>
                                            <td><%= rayon.getNom() %></td>
                                            <td><%= rayon.getDescription() != null ? rayon.getDescription() : "" %></td>
                                            <td>
                                                <% if (rayon.getImageUrl() != null && !rayon.getImageUrl().isEmpty()) { %>
                                                    <img src="<%= request.getContextPath() %>/<%= rayon.getImageUrl() %>" 
                                                         alt="<%= rayon.getNom() %>" 
                                                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                                                <% } else { %>
                                                    <span class="text-muted">Aucune image</span>
                                                <% } %>
                                            </td>
                                            <td><%= rayon.getCreationDate().toString().replace('T', ' ') %></td>
                                            <td><%= rayon.getUpdateDate().toString().replace('T', ' ') %></td>
                                            <td>
                                                <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                    <span class="text-muted sr-only">Action</span>
                                                </button>
                                                <div class="dropdown-menu dropdown-menu-right">
                                                    <% if (canEditRayons) { %>
                                                        <a class="dropdown-item" href="MenuServlet?action=editRayon&id=<%= rayon.getId() %>">Modifier</a>
                                                    <% } %>
                                                    <% if (canDeleteRayons) { %>
                                                        <a class="dropdown-item" href="MenuServlet?action=deleteRayon&id=<%= rayon.getId() %>" 
                                                           onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce rayon ?')">
                                                            Supprimer
                                                        </a>
                                                    <% } %>
                                                    <a class="dropdown-item show-categories" href="#" data-id="<%= rayon.getId() %>">Voir les catégories</a>
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
        Rayon rayon = (Rayon) request.getAttribute("rayon");
        boolean isEdit = (rayon != null);
    %>

    <div class="modal fade modal-rayon modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalRayonLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">
                <form id="formRayon" action="MenuServlet" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalRayonLabel">
                            <%= isEdit ? "Modifier le rayon" : "Ajouter un rayon" %>
                        </h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" name="action" value="<%= isEdit ? "updateRayon" : "addRayon" %>">
                        <input type="hidden" name="utilisateurId" value="${sessionScope.userId}">

                        <% if (isEdit) { %>
                            <input type="hidden" name="id" value="<%= rayon.getId() %>">
                        <% } %>

                        <div class="form-group">
                            <label for="nomRayon">Nom du rayon *</label>
                            <input type="text" class="form-control" id="nomRayon" name="nom" required 
                                   value="<%= isEdit ? rayon.getNom() : "" %>">
                        </div>

                        <div class="form-group">
                            <label for="descriptionRayon">Description</label>
                            <textarea class="form-control" id="descriptionRayon" name="description" rows="3"><%= isEdit && rayon.getDescription() != null ? rayon.getDescription() : "" %></textarea>
                        </div>

                        <div class="form-group">
                            <label for="imageRayon">Image du rayon</label>
                            <input type="file" class="form-control" id="imageRayon" name="image" accept="image/*">

                            <%
                                if (isEdit && rayon.getImageUrl() != null && !rayon.getImageUrl().isEmpty()) {
                            %>
                                <div class="mt-2">
                                    <img src="<%= request.getContextPath() %>/<%= rayon.getImageUrl() %>" 
                                         alt="<%= rayon.getNom() %>" 
                                         style="width: 100px; height: 100px; object-fit: cover; border-radius: 4px;">
                                    <small class="form-text text-muted">
                                        Image actuelle
                                    </small>
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary btn-block">
                            <%= isEdit ? "Modifier le rayon" : "Ajouter le rayon" %>
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
        $('.modal-rayon').modal('show');
    });
</script>
<%
}
%>

<script>
$(document).ready(function () {
    const table = $('#rayon-datatable').DataTable({
        autoWidth: true,
        lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json'
        }
    });

    // Gérer le clic sur le lien "Voir les catégories"
    $('#rayon-datatable tbody').on('click', 'a.show-categories', function (e) {
        e.preventDefault();
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const rayonId = parseInt($(this).data('id'));

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
            $(this).text('Voir les catégories');
        } else {
            // Afficher un indicateur de chargement
            row.child('<div class="p-2 text-center"><div class="spinner-border spinner-border-sm" role="status"></div> Chargement des catégories...</div>').show();
            tr.addClass('shown');
            $(this).text('Chargement...');

            // Charger les catégories du rayon
            fetch("<%=request.getContextPath()%>/MenuServlet?action=categories-by-rayon&rayonId=" + rayonId)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Erreur HTTP: " + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (data.categories && Array.isArray(data.categories) && data.categories.length > 0) {
                        let html = "<div class='p-3'>";
                        html += "<h6 class='mb-3'>Catégories associées à ce rayon :</h6>";
                        html += "<table class='table table-sm mb-0 table-bordered'>";
                        html += "<thead class='thead-light'><tr><th>Nom</th><th>Description</th><th>Date création</th><th>Statut</th></tr></thead><tbody>";
                        
                        data.categories.forEach(function (cat) {
                            html += "<tr>";
                            html += "<td>" + (cat.nom || "-") + "</td>";
                            html += "<td>" + (cat.description || "-") + "</td>";
                            html += "<td>" + (cat.creationDate ? cat.creationDate.replace('T', ' ') : "-") + "</td>";
                            html += "<td><span class='badge badge-" + (cat.statut === 'VISIBLE' ? 'success' : 'secondary') + "'>" + cat.statut + "</span></td>";
                            html += "</tr>";
                        });

                        html += "</tbody></table>";
                        html += "</div>";
                        row.child(html).show();
                        tr.addClass('shown');
                        $(tr).find('.show-categories').text('Cacher les catégories');
                    } else {
                        row.child("<div class='p-3 text-muted'>Aucune catégorie associée à ce rayon.</div>").show();
                        tr.addClass('shown');
                        $(tr).find('.show-categories').text('Cacher les catégories');
                    }
                })
                .catch(function (error) {
                    console.error("Erreur lors du chargement des catégories :", error);
                    row.child("<div class='p-2 text-danger'>Erreur de chargement des catégories.</div>").show();
                    tr.addClass('shown');
                    $(tr).find('.show-categories').text('Cacher les catégories');
                });
        }
    });

    // Gestion de la soumission du formulaire
    $('#formRayon').on('submit', function() {
        const submitBtn = $(this).find('button[type="submit"]');
        const spinner = submitBtn.find('.spinner-border');
        const btnText = submitBtn.find('i').prev().text();
        
        submitBtn.prop('disabled', true);
        spinner.removeClass('d-none');
        submitBtn.find('i').prev().text('Traitement...');
    });

    // Réinitialiser le formulaire quand le modal se ferme
    $('.modal-rayon').on('hidden.bs.modal', function () {
        $('#formRayon')[0].reset();
        const submitBtn = $('#formRayon').find('button[type="submit"]');
        submitBtn.prop('disabled', false);
        submitBtn.find('.spinner-border').addClass('d-none');
        submitBtn.find('i').prev().text('Ajouter le rayon');
    });
});
</script>

<style>
.table img {
    border: 1px solid #dee2e6;
}

.shown {
    background-color: #f8f9fa !important;
}

.table-details {
    background-color: #f8f9fa;
    border-left: 4px solid #007bff;
}

.badge-success {
    background-color: #28a745;
}

.badge-secondary {
    background-color: #6c757d;
}
</style>