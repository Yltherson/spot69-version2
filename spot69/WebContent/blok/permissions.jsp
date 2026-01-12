<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.List,com.spot69.model.Role"%>
<meta charset="UTF-8">


<link rel="stylesheet" href="css/permissions.css">

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<main role="main" class="main-content">
	<div class="container-fluid">
		<div class="page-wrapper">
			<div class="content">
				<div class="d-flex justify-content-between align-items-center mb-3">
					<h2 class="h4 mb-1">Permissions</h2>

					<div class="custom-btn-group">
						<a class="btn btn-outline-primary"
							href="RoleServlet?action=lister"> <i
							class="fe fe-arrow-left me-2"></i> Retour à la liste
						</a>
					</div>
				</div>


				<div class="card">
					<div class="card-body">
						<div class="permission-container mt-4">
							<!-- Colonne des rôles -->
							<div class="roles-column">
								<h5>Les Rôles</h5>
								<ul class="role-list">
									<%
									List<Role> roles = (List<Role>) request.getAttribute("roles");
									if (roles != null && !roles.isEmpty()) {
										for (Role role : roles) {
									%>
									<li class="role-item" data-id="<%=role.getId()%>"
										data-permissions="<%=role.getDroits() != null ? role.getDroits() : ""%>">
										<%=role.getRoleName()%>
									</li>
									<%
									}
									} else {
									%>
									<li class="text-muted">Aucun rôle disponible</li>
									<%
									}
									%>
								</ul>
							</div>

							<!-- Colonne des permissions -->
							<div class="permissions-column">
								<div class="productdetails product-respon">
									<ul class="permission-list">
										<!-- Utilisateurs -->
										<li>
											<h4>Utilisateurs</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
														title="Gérer les comptes utilisateurs"> Gestion
															comptes <input type="checkbox" name="permissions"
															value="GESTION_COMPTES" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Supprimer des comptes"> Suppr. comptes <input
															type="checkbox" name="permissions"
															value="SUPPRESSION_COMPTES" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Gérer les permissions"> Gestion Roles &
															Permissions <input type="checkbox" name="permissions"
															value="GESTION_ROLES_ET_PERMISSIONS" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
												</ul>
											</div>
										</li>

										<!-- Commandes -->
										<li>
											<h4>Commandes</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
														title="Gérer toutes les actions sur les commandes sauf suppression">
															Gestion commandes <input type="checkbox"
															name="permissions" value="GESTION_COMMANDES"
															class="permission-check" /> <span class="checkmark"></span>
													</label></li>
													
													<li><label class="inputcheck"
														title="Supprimer des commandes"> Suppression <input
															type="checkbox" name="permissions"
															value="SUPPRESSION_COMMANDES" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Valider le statut d'une commande">
															Validation <input type="checkbox" name="permissions"
															value="VALIDATION_COMMANDES" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck" title="Afficher la liste de mes commandes">
												        Modifier le statut des commandes 
												        <input type="checkbox" name="permissions" value="MODIFIER_STATUT_COMMANDES" class="permission-check" /> 
												        <span class="checkmark"></span>
												    </label></li>
												    
												    <li><label class="inputcheck" title="Consulter l'historique des commandes">
												      Placer des commandes
												        <input type="checkbox" name="permissions" value="PLACER_DES_COMMANDES" class="permission-check" /> 
												        <span class="checkmark"></span>
												    </label></li>
												     <li><label class="inputcheck" title="Consulter l'historique des commandes">
												      Voir l'historique des commandes
												        <input type="checkbox" name="permissions" value="VOIR_HISTORIQUE_COMMANDES" class="permission-check" /> 
												        <span class="checkmark"></span>
												    </label></li>
												   
												</ul>
											</div>
										</li>

										<!-- Approvisionnement -->
										<li>
											<h4>Approvisionnement & Inventaire</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
														title="Gérer les fournisseurs (création, modification)">
															Gestion fournisseurs <input type="checkbox"
															name="permissions" value="GESTION_FOURNISSEURS"
															class="permission-check" /> <span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Supprimer des fournisseurs"> Suppr.
															fournisseurs <input type="checkbox" name="permissions"
															value="SUPPRESSION_FOURNISSEURS" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Gérer les factures (création, modification)">
															Gestion factures <input type="checkbox"
															name="permissions" value="GESTION_FACTURES"
															class="permission-check" /> <span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck" title="Gérer les stocks">
															Gestion stock <input type="checkbox" name="permissions"
															value="GESTION_STOCK" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Supprimer des factures"> Suppr. factures <input
															type="checkbox" name="permissions"
															value="SUPPRESSION_FACTURES" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
												</ul>
											</div>
										</li>

										<!-- Gestion des Produits -->
										<li>
											<h4>Produits</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
														title="Gérer les produits (ajout, modification)">
															Gestion produits <input type="checkbox"
															name="permissions" value="GESTION_PRODUITS"
															class="permission-check" /> <span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Supprimer des produits"> Suppr. produits <input
															type="checkbox" name="permissions"
															value="SUPPRESSION_PRODUITS" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Gérer les catégories (ajout, modification)">
															Gestion catégories/sous-categories <input type="checkbox"
															name="permissions" value="GESTION_CATEGORIES_SOUS_CATEGORIES"
															class="permission-check" /> <span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Supprimer des catégories"> Suppr.
															catégories/sous-categories <input type="checkbox" name="permissions"
															value="SUPPRESSION_CATEGORIES_SOUS_CATEGORIES" class="permission-check" />
															<span class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck" title="Consulter l'historique des commandes">
												       Voir les produits
												        <input type="checkbox" name="permissions" value="VOIR_LES_PRODUITS" class="permission-check" /> 
												        <span class="checkmark"></span>
												    </label></li>
												</ul>
											</div>
										</li>
										
										<!-- Gestion des Produits -->
										<li>
											<h4>Rayons & Categories & Sous-categorie</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
										                title="Gérer les rayons (ajout, modification)">
										                    Gestion rayons, categories, sous-categories <input type="checkbox"
										                    name="permissions" value="GESTION_RAYONS_CATEGORIES_SOUS_CATEGORIES"
										                    class="permission-check" /> <span class="checkmark"></span>
										            </label></li>
										            <li><label class="inputcheck"
										                title="Supprimer des rayons"> Suppr. rayons & categories & sous-categorie <input
										                    type="checkbox" name="permissions"
										                    value="SUPPRESSION_RAYONS_CATEGORIES_SOUS_CATEGORIES" class="permission-check" />
										                    <span class="checkmark"></span>
										            </label></li>
												</ul>
											</div>
										</li>
										
									
										<!-- Ventes -->
										<li>
											<h4>Rapport</h4>
											<div class="input-checkset">
												<ul>
													<li><label class="inputcheck"
														title="Gérer le panier et les commandes"> Voir l'historique des commandes <input type="checkbox" name="permissions"
															value="VOIR_HISTORIQUES_COMMANDES" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Gérer le panier et les commandes"> Voir les rapports par caisse <input type="checkbox" name="permissions"
															value="VOIR_RAPPORT_CAISSE" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
													<li><label class="inputcheck"
														title="Gérer le panier et les commandes"> Voir les rapports par produits <input type="checkbox" name="permissions"
															value="VOIR_RAPPORT_PAR_PRODUITS" class="permission-check" /> <span
															class="checkmark"></span>
													</label></li>
												</ul>
											</div>
										</li>
									</ul>
								</div>
							</div>
						</div>

						<div class="row mt-3">
							<div class="col-12 text-end">
								<button type="button" class="btn btn-primary me-2"
									id="savePermissionsBtn">Enregistrer</button>
								<button type="button" class="btn btn-secondary" id="cancelBtn">Annuler</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</main>
<jsp:include page="footer.jsp" />
<script>
    $(document).ready(function() {
        // Variables pour suivre l'état
        var currentRoleId = null;
        var initialPermissions = [];

        // Activer le premier rôle par défaut
        if ($('.role-item').length > 0) {
            $('.role-item').first().addClass('active');
            currentRoleId = $('.role-item').first().data('id');
            loadPermissionsForRole(currentRoleId);
        }

        // Gestion du clic sur un rôle
        $('.role-list').on('click', '.role-item', function() {
            $('.role-item').removeClass('active');
            $(this).addClass('active');

            currentRoleId = $(this).data('id');
            loadPermissionsForRole(currentRoleId);
        });

        // Charger les permissions pour un rôle
        function loadPermissionsForRole(roleId) {
            var roleItem = $('.role-item[data-id="' + roleId + '"]');
            var permissions = roleItem.data('permissions');

            // Décocher toutes les cases
            $('.permission-check').prop('checked', false);

            if (permissions) {
                var permissionList = permissions.split(',');
                for (var i = 0; i < permissionList.length; i++) {
                    var perm = permissionList[i];
                    $('input[value="' + perm + '"]').prop('checked', true);
                }
            }

            // Sauvegarder l'état initial pour annulation
            initialPermissions = getSelectedPermissions();
        }

        // Récupérer les permissions sélectionnées
        function getSelectedPermissions() {
            var checkboxes = $('input[name="permissions"]:checked');
            var permissions = [];
            checkboxes.each(function() {
                permissions.push($(this).val());
            });
            return permissions;
        }

        // Enregistrer les permissions
        $('#savePermissionsBtn').click(function() {
            if (!currentRoleId) {
                alert('Veuillez sélectionner un rôle');
                return;
            }

            var permissions = getSelectedPermissions().join(',');
            console.log(permissions);
            $.ajax({
                url: 'RoleServlet',
                type: 'POST',
                data: {
                    action: 'update-droits',
                    roleId: currentRoleId,
                    permissions: permissions
                },
                success: function(response) {
                    if (response.success) {
                        showToastMessage("Succès", "Permissions mises à jour avec succès", "success");
                        // mise à jour données...
                    } else {
                        showToastMessage("Erreur", response.message || "Échec de la mise à jour", "error");
                    }
                },
                error: function() {
                    showToastMessage("Erreur", "Erreur lors de la communication avec le serveur", "error");
                }
            });

        });

        // Annuler les modifications
        $('#cancelBtn').click(function() {
            if (!currentRoleId) return;

            // Recharger les permissions initiales
            var roleItem = $('.role-item[data-id="' + currentRoleId + '"]');
            var permissions = roleItem.data('permissions') || '';

            $('.permission-check').prop('checked', false);
            if (permissions) {
                var permissionList = permissions.split(',');
                for (var i = 0; i < permissionList.length; i++) {
                    var perm = permissionList[i];
                    $('input[value="' + perm + '"]').prop('checked', true);
                }
            }
        });
    });
</script>