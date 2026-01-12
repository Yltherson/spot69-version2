
<meta charset="UTF-8">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.MenuCategorie,com.spot69.model.Role,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">


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
                                <i class="fe fe-users fe-32 align-self-center text-black"></i>
                                Gestion des rôles
                            </h2>
                             
                            <div class="custom-btn-group">
                            <a class="btn btn-outline-primary" href="RoleServlet?action=permissions">
                                    <i class="fe fe-settings fe-16"></i> Permissions
                                </a>
                                <button class="btn btn-outline-primary" id="btnAjouterRole" data-toggle="modal" data-target=".modal-role">
                                    <i class="fe fe-plus fe-16"></i> Ajouter un rôle
                                </button>
                            </div>
                        </div>

                        <div class="card shadow">
                            <div class="card-body">
                                <table class="table datatables" id="role-datatable">
                                    <thead>
                                        <tr>
                                            <th>Nom du rôle</th>
                                            <th>Date de création</th>
                                            <th>Date de modification</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            List<Role> roles = (List<Role>) request.getAttribute("roles");
                                            if (roles != null) {
                                                for (Role role : roles) {
                                        %>
                                        <tr>
                                            <td><%= role.getRoleName() %></td>
                                            <td><%= role.getCreatedAt() != null ? role.getCreatedAt().toString().replace('T', ' ') : "-" %></td>
                                            <td><%= role.getUpdatedAt() != null ? role.getUpdatedAt().toString().replace('T', ' ') : "-" %></td>
                                            <td>
                                                <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                    <span class="text-muted sr-only">Action</span>
                                                </button>
                                                <div class="dropdown-menu dropdown-menu-right">
                                                    <a class="dropdown-item modifier-role" href="#" data-id="<%= role.getId() %>" data-nom="<%= role.getRoleName() %>">Modifier</a>
                                                    <a class="dropdown-item voir-utilisateurs" href="#" data-id="<%= role.getId() %>">Voir les utilisateurs</a>
                                                    <a class="dropdown-item supprimer-role" href="#" data-id="<%= role.getId() %>">Supprimer</a>
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
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Ajout/Modification de rôle -->
    <div class="modal fade modal-role modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalRoleLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">
                <form id="formRole" action="RoleServlet" method="post">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalRoleLabel">Ajouter un rôle</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" value="ajouter">
                        <input type="hidden" id="roleId" name="id" value="">
                        
                        <div class="form-group">
                            <label for="nomRole">Nom du rôle</label>
                            <input type="text" class="form-control" id="nomRole" name="roleName" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary btn-block">
                            <span id="submitBtnText">Ajouter</span>
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
<script>$(document).ready(function() {
	// Initialisation DataTable
  
    // Initialisation de DataTable
   const table = $('#role-datatable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json'
        }
    });

    // Gestion des dropdowns
    $('.dropdown-toggle').dropdown();

    // Gestion du bouton Voir les utilisateurs (child table)
    $('#role-datatable tbody').on('click', 'a.voir-utilisateurs', function(e) {
        e.preventDefault();
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const roleId = $(this).data('id');

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
            $(this).text('Voir les utilisateurs');
        } else {
            $(this).text('Cacher les utilisateurs');
            
            // Afficher le loader
            row.child('<div class="text-center p-3"><div class="spinner-border text-primary" role="status"></div></div>').show();
            tr.addClass('shown');

            // Récupérer les utilisateurs via AJAX
            $.ajax({
                url: 'RoleServlet?action=get_utilisateurs_by_role&id=' + roleId,
                type: 'GET',
                dataType: 'json',
                success: function(data) {
                	console.log(data)
                    let html = '<table class="table table-sm mb-0">';
                    html += '<thead><tr><th>Nom</th><th>Email</th><th>Date création</th></tr></thead><tbody>';
                    
                    if (data && data.length > 0) {
                        data.forEach(function(user) {
                            html += '<tr>';
                            html += '<td>' + (user.nom + " " + user.prenom  || '-') + '</td>';
                            html += '<td>' + (user.email || '-') + '</td>';
                            html += '<td>' + (user.creationDate || '-') + '</td>';
                            html += '</tr>';
                        });
                    } else {
                        html += '<tr><td colspan="4" class="text-center">Aucun utilisateur avec ce rôle</td></tr>';
                    }
                    
                    html += '</tbody></table>';
                    
                    // Mettre à jour le child row
                    row.child(html).show();
                    tr.addClass('shown');
                },
                error: function() {
                    row.child('<div class="p-2 text-danger">Erreur de chargement des utilisateurs</div>').show();
                    tr.addClass('shown');
                }
            });
        }
    });

    // Gestion du clic sur "Supprimer"
    $(document).on('click', '.supprimer-role', function(e) {
        e.preventDefault();
        var roleId = $(this).data('id');
        if (confirm('Êtes-vous sûr de vouloir supprimer ce rôle ?')) {
            window.location.href = 'RoleServlet?action=supprimer&id=' + roleId;
        }
    });

    // Gestion du formulaire d'ajout/modification
    $('#formRole').on('submit', function(e) {
        e.preventDefault();
        var $form = $(this);
        var $submitBtn = $form.find('button[type="submit"]');
        var $spinner = $submitBtn.find('.spinner-border');
        var $btnText = $submitBtn.find('#submitBtnText');
        
        $spinner.removeClass('d-none');
        $btnText.text('En cours...');
        
        $.ajax({
            url: $form.attr('action'),
            type: 'POST',
            data: $form.serialize(),
            success: function(response) {
                window.location.reload();
            },
            error: function() {
                alert('Erreur lors de l\'enregistrement');
                $spinner.addClass('d-none');
                $btnText.text($form.find('input[name="action"]').val() === 'ajouter' ? 'Ajouter' : 'Modifier');
            }
        });
    });

    // Initialisation de la modal d'ajout/modification
    $('#btnAjouterRole').on('click', function() {
        $('#formRole')[0].reset();
        $('#formRole input[name="action"]').val('ajouter');
        $('#formRole #roleId').val('');
        $('#modalRoleLabel').text('Ajouter un rôle');
        $('#submitBtnText').text('Ajouter');
    });

    // Gestion du clic sur "Modifier" pour pré-remplir la modal
    $(document).on('click', '.modifier-role', function(e) {
        e.preventDefault();
        var roleId = $(this).data('id');
        var roleName = $(this).data('nom');
        
        $('#formRole input[name="action"]').val('modifierRoleName');
        $('#formRole #roleId').val(roleId);
        $('#formRole #nomRole').val(roleName);
        $('#modalRoleLabel').text('Modifier le rôle');
        $('#submitBtnText').text('Modifier');
        $('.modal-role').modal('show');
    });
});
</script>