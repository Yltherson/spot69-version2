
<meta charset="UTF-8">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Utilisateur,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditComptes = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
boolean canDeleteComptes = canEditComptes && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_COMPTES);
%>


<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar2.jsp" />
<%-- <jsp:include page="sidebar.jsp" /> --%>
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
                                Liste des utilisateurs
                            </h2>
                            <div class="custom-btn-group">
                            <a href="UtilisateurServlet?action=add" class="btn btn-outline-primary">
                                    <i class="fe fe-plus fe-16"></i> Ajouter 
                                </a>
                            </div>
                        </div>
						<p class="mb-3"></p>
						<div class="card shadow">
							<div class="card-body">
								<table class="table datatables" id="dataTable-1">
									<thead>
										<tr>

											<th>Nom</th>
											<th>Email</th>
											<th>Type Compte</th>
											<!-- <th>Pourcentage</th> -->
											<th>Date Creation</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<%
										List<com.spot69.model.Utilisateur> utilisateurs = (List<com.spot69.model.Utilisateur>) request
												.getAttribute("utilisateurs");
										if (utilisateurs != null) {
											for (com.spot69.model.Utilisateur user : utilisateurs) {
										%>
										<tr>

											<td><%=user.getNom() + " " + user.getPrenom()%></td>
											<td><%=user.getEmail()%></td>
											<td><%=user.getRole().getRoleName()%></td>
											<%-- 	<td><%=user.getPourcentage()%></td> --%>
											<td><%=new java.text.SimpleDateFormat("MMM d, yyyy").format(user.getCreationDate())%></td>
											<td>
												<button class="btn btn-sm dropdown-toggle more-horizontal"
													type="button" data-toggle="dropdown" aria-haspopup="true"
													aria-expanded="false">
													<span class="text-muted sr-only">Action</span>
												</button>
												<div class="dropdown-menu dropdown-menu-right">
											
								           	
								           	<% if (canEditComptes) { %>
													<a class="dropdown-item btn-edit-user" href="#"
													    data-toggle="modal" data-target=".modal-update-user"
													    data-id="<%=user.getId()%>"
													    data-nom="<%=user.getNom()%>"
													    data-statut="<%=user.getStatut()%>"
													    data-prenom="<%=user.getPrenom()%>"
													    data-email="<%=user.getEmail()%>"
													    data-pourcentage="<%=user.getPourcentage()%>"
													    data-username="<%=user.getLogin()%>"
													    data-role="<%=user.getRole().getId()%>"
													    data-plafond="<%=user.getPlafond()%>"> Modifier </a>
												<% } %>
												
												 <% if (canDeleteComptes) { %>
													<form method="POST"
														action="UtilisateurServlet?action=supprimer"
														style="display: inline;"
														onsubmit="return confirm('Confirmer la suppression ?');">
														<input type="hidden" name="id" value="<%=user.getId()%>" />
														<button type="submit"
															class="dropdown-item btn-delete-user">Supprimer</button>
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
					<!-- customized table -->
				</div>

			</div>
			<!-- .col-12 -->
		</div>
		<!-- .row -->
	</div>

	<div class="modal fade modal-update-user modal-slide" tabindex="-1"
		role="dialog" aria-labelledby="defaultModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<!-- gardé lg pour plus de place -->
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="defaultModalLabel">
						<span class="fe fe-user fe-24"></span> Modifier l'utilisateur
					</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Fermer">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>

				<form method="POST" action="UtilisateurServlet?action=modifier"
					id="formEditUser">
					<div class="modal-body">
						<input type="hidden" value="" name="id" id="editUserId">

						<div class="form-group">
							<label for="editNom">Nom *</label> <input type="text"
								class="form-control" id="editNom" name="nom" required>
						</div>

						<div class="form-group">
							<label for="editPrenom">Prénom *</label> <input type="text"
								class="form-control" id="editPrenom" name="prenom" required>
						</div>
						
						<!-- Champ boaqer modifié en select -->
						<div class="form-group">
							<label for="editBoaqer">BLOQER</label>
							<select class="form-control" id="editStatut" name="statut">
								<option value="BLOCKED">BLOCKED</option>
								<option value="VISIBLE">Débloquer</option>
							</select>
						</div>
						
						
						<div class="form-group">
							<label for="editEmail">Email *</label> <input type="email"
								class="form-control" id="editEmail" name="email" required>
						</div>

						<div class="form-group">
							<label for="editLogin">Login *</label> <input type="text"
								class="form-control" id="editLogin" name="username" required>
						</div>
						
						

						<div class="form-group">
							<label for="editPassword">Mot de passe *</label> <input
								type="password" class="form-control" id="editPassword"
								name="password">
						</div>

						<div class="form-group">
						    <label for="editRole">Rôle *</label>
						    <select class="form-control" id="editRole" name="role" required>
						        <%
						            List<com.spot69.model.Role> roles = (List<com.spot69.model.Role>) request.getAttribute("roles");
						            if (roles != null) {
						                for (com.spot69.model.Role role : roles) {
						        %>
						            <option value="<%= role.getId() %>"><%= role.getRoleName() %></option>
						        <%
						                }
						            }
						        %>
						    </select>
						</div>

					</div>

					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">
							Enregistrer les modifications <i class="fe fe-send icon"></i> <span
								class="spinner-border spinner-border-sm d-none" role="status"
								aria-hidden="true"></span>
						</button>
						<button type="button" class="btn btn-secondary"
							data-dismiss="modal">
							Annuler <span class="spinner-border spinner-border-sm d-none"
								role="status" aria-hidden="true"></span>
						</button>
					</div>
				</form>
			</div>
		</div>
	
		
	</div>


</main>
<jsp:include page="bottombar.jsp" />
<jsp:include page="footer.jsp" />

<script>
    $("#dataTable-1").DataTable({
        autoWidth: true,
        lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
        language: {
            search: "Rechercher:",
            lengthMenu: "Afficher _MENU_ éléments",
            info: "Affichage de _START_ à _END_ sur _TOTAL_ éléments",
            paginate: {
                first: "Premier",
                last: "Dernier", 
                next: "Suivant",
                previous: "Précédent"
            },
            zeroRecords: "Aucun résultat trouvé",
            infoEmpty: "Aucun élément à afficher",
            infoFiltered: "(filtré depuis _MAX_ éléments au total)"
        }
    });

    // Utiliser la délégation d'événements pour gérer les boutons de toutes les pages
    $(document).on('click', '.btn-edit-user', function() {
        const button = $(this);
        $('#editUserId').val(button.data('id'));
        $('#editNom').val(button.data('nom'));
        $('#editPrenom').val(button.data('prenom'));
        $('#editStatut').val(button.data('statut'));
        $('#editEmail').val(button.data('email'));
        $('#editLogin').val(button.data('username'));
        $('#editPassword').val(''); // Mot de passe vide par défaut
        $('#editRole').val(button.data('role'));
        
        // Ouvrir le modal (au cas où)
        $('.modal-update-user').modal('show');
    });
</script>
