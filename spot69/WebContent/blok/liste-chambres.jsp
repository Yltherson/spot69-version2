<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Chambre,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditChambres = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
boolean canDeleteChambres = canEditChambres && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_PRODUITS);
%>
<style>
	small{
		font-size : 10px;
	}
	.chambre-image {
		width: 80px;
		height: 60px;
		object-fit: cover;
		border-radius: 4px;
		margin-right: 10px;
	}
	.installations-badge {
		font-size: 0.7rem;
		margin: 1px;
	}
	.price-badge {
		font-size: 0.75rem;
		margin: 1px;
	}
	.price-container {
		min-width: 120px;
	}
</style>
<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<main role="main" class="main-content">
	<div class="container-fluid">
	<div class="row justify-content-center">
            <div class="col-12">
              <h2 class="h5 page-title">
              	 <i class="fe fe-home fe-32 align-self-center text-black"></i>
                                Gestion des Chambres
              </h2>
              <%
				List<Chambre> chambres = (List<Chambre>) request.getAttribute("chambres");
				%>
            <!-- info small box -->
          <div class="row">
  <!-- Total des chambres -->
  <div class="col-md-3 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <span class="h2 mb-0"><%= request.getAttribute("totalChambres") != null ? request.getAttribute("totalChambres") : 0 %></span>
          <p class="small text-muted mb-0">Total des chambres</p>
       </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-home text-muted mb-0"></span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Chambres disponibles -->
  <div class="col-md-3 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <span class="h2 mb-0 text-success"><%= request.getAttribute("nbChambresDisponibles") != null ? request.getAttribute("nbChambresDisponibles") : 0 %></span>
          <p class="small text-muted mb-0">Chambres disponibles</p>
        </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-check-circle text-success mb-0"></span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Chambres occupées -->
  <div class="col-md-3 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <%
            	int totalChambres = request.getAttribute("totalChambres") != null ? (Integer) request.getAttribute("totalChambres") : 0;
            	int chambresDisponibles = request.getAttribute("nbChambresDisponibles") != null ? (Integer) request.getAttribute("nbChambresDisponibles") : 0;
            	int chambresOccupees = totalChambres - chambresDisponibles;
            %>
            <span class="h2 mb-0 text-warning"><%= chambresOccupees %></span>
            <p class="small text-muted mb-0">Chambres occupées</p>
          </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-user text-warning mb-0"></span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Prix moyen -->
  <div class="col-md-3 mb-4">
    <div class="card shadow">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col">
            <%
              double prixMoyen = 0;
              int count = 0;
              if (chambres != null) {
                for (Chambre chambre : chambres) {
                  if (chambre.getPrixSejour() != null) {
                    prixMoyen += chambre.getPrixSejour().doubleValue();
                    count++;
                  }
                }
                if (count > 0) prixMoyen /= count;
              }
            %>
            <span class="h2 mb-0 text-info"><%= String.format("%.0f", prixMoyen) %> HTG</span>
            <p class="small text-muted mb-0">Prix moyen/nuit</p>
          </div>
          <div class="col-auto">
            <span class="fe fe-32 fe-dollar-sign text-info mb-0"></span>
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
                                Liste des chambres
                            </h2>
                            <div class="custom-btn-group">
                            <a href="ChambreServlet?action=add" class="btn btn-outline-primary">
                                    <i class="fe fe-plus fe-16"></i> Ajouter une chambre
                                </a>
                            </div>
                        </div>
						<div class="card shadow">
							<div class="card-body">
								<table class="table datatables" id="dataTable-1">
									<thead>
										<tr>
											<th>Chambre</th>
											<th>Description</th>
											<th>Capacité</th>
											<th>Installations</th>
											<th>Prix</th>
											<th>Disponibilité</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
<%
    String ctx = request.getContextPath();
    if (chambres != null) {
        for (Chambre chambre : chambres) {
            String mediaUrls = chambre.getMedia();
            String firstImagePath;
            if (mediaUrls != null && !mediaUrls.isEmpty()) {
                String firstImage = mediaUrls.split(",")[0];
                if (firstImage.startsWith("uploads/chambres/")) {
                    firstImagePath = ctx + "/images/chambres/" + firstImage.substring("uploads/chambres/".length());
                } else {
                    firstImagePath = ctx + "/images/default/chambre-default.png";
                }
            } else {
                firstImagePath = ctx + "/images/default/chambre-default.png";
            }
            
            List<String> installations = chambre.getInstallationsList();
%>
    <tr>
        <td>
            <div style="display: flex; align-items: center;">
                <img src="<%= firstImagePath %>" alt="<%= chambre.getNomChambre() %>" class="chambre-image" />
                <div>
                    <strong><%= chambre.getNomChambre() %></strong><br>
                    <small>ID: <%= chambre.getId() %></small>
                </div>
            </div>
        </td>

        <td><%= chambre.getDescriptionChambre() != null ? chambre.getDescriptionChambre() : "Aucune description" %></td>

        <td>
        	<span class="badge badge-info">
        		<%= chambre.getCapacite() %> personne(s)
        	</span>
        </td>
        
        <td>
        	<%
        	if (installations != null && !installations.isEmpty()) {
        		for (String installation : installations) {
        			if (!installation.trim().isEmpty()) {
        	%>
        		<span class="badge badge-secondary installations-badge"><%= installation %></span>
        	<%
        			}
        		}
        	} else {
        	%>
        		<small class="text-muted">Aucune</small>
        	<%
        	}
        	%>
        </td>
        
        <td class="price-container">
            <div class="d-flex flex-column">
                <span class="badge price-badge mb-1">
                    Séjour: <%= chambre.getPrixSejour() != null ? chambre.getPrixSejour() + " HTG" : "N/A" %>
                </span>
                <span class="badge price-badge mb-1">
                    Nuit: <%= chambre.getPrixNuit() != null ? chambre.getPrixNuit() + " HTG" : "N/A" %>
                </span>
                <span class="badge  price-badge mb-1">
                    Jour: <%= chambre.getPrixJour() != null ? chambre.getPrixJour() + " HTG" : "N/A" %>
                </span>
                <span class="badge  price-badge">
                    Moment: <%= chambre.getPrixMoment() != null ? chambre.getPrixMoment() + " HTG" : "N/A" %>
                </span>
            </div>
        </td>
        
        <td>
            <%
                String badgeClass = chambre.isDisponible() ? "badge badge-success" : "badge badge-danger";
                String texte = chambre.isDisponible() ? "Disponible" : "Occupée";
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
            <% if (canEditChambres) { %>
                <a class="dropdown-item btn-edit-chambre" href="#"
                    data-toggle="modal" data-target=".modal-update-chambre"
                    data-id="<%= chambre.getId() %>"
                    data-nomchambre="<%= chambre.getNomChambre() %>"
                    data-descriptionchambre="<%= chambre.getDescriptionChambre() != null ? chambre.getDescriptionChambre() : "" %>"
                    data-capacite="<%= chambre.getCapacite() %>"
                    data-installations="<%= chambre.getInstallations() %>"
                    data-prixmoment="<%= chambre.getPrixMoment() != null ? chambre.getPrixMoment() : "" %>"
                    data-prixnuit="<%= chambre.getPrixNuit() != null ? chambre.getPrixNuit() : "" %>"
                    data-prixjour="<%= chambre.getPrixJour() != null ? chambre.getPrixJour() : "" %>"
                    data-prixsejour="<%= chambre.getPrixSejour() != null ? chambre.getPrixSejour() : "" %>"
                    data-disponible="<%= chambre.isDisponible() %>"
                    data-media="<%= chambre.getMedia() %>">
                    Modifier
                </a>
                <% } %>
                
                <% if (canDeleteChambres) { %>
                <form method="POST" action="ChambreServlet?action=supprimer"
                    style="display: inline;"
                    onsubmit="return confirm('Confirmer la suppression de cette chambre ?');">
                    <input type="hidden" name="id" value="<%= chambre.getId() %>" />
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

								</table>
							</div>
						</div>
					</div>
					<!-- customized table -->
				</div>
			</div>
		</div>
	</div>

	<!-- Modal Modification Chambre -->
<div class="modal fade modal-update-chambre modal-slide" tabindex="-1" role="dialog"
     aria-labelledby="defaultModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <span class="fe fe-edit fe-24"></span> Modifier la chambre
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form method="POST" action="ChambreServlet?action=modifier" id="formEditChambre" enctype="multipart/form-data">
                <div class="modal-body">
                    <input type="hidden" name="id" id="editChambreId">

                    <!-- Nom de la chambre -->
                    <div class="form-group">
                        <label for="editNomChambre">Nom de la chambre *</label>
                        <input type="text" class="form-control" id="editNomChambre" name="nomChambre" required>
                    </div>

                    <!-- Description -->
                    <div class="form-group">
                        <label for="editDescriptionChambre">Description</label>
                        <textarea class="form-control" id="editDescriptionChambre" name="descriptionChambre" rows="3"></textarea>
                    </div>
                    
                    <!-- Capacité -->
                    <div class="form-group">
                        <label for="editCapacite">Capacité (nombre de personnes) *</label>
                        <input type="number" class="form-control" id="editCapacite" name="capacite" min="1" max="10" required>
                    </div>

                    <!-- Installations -->
                    <div class="form-group">
                        <label>Installations</label>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editWifi" name="installations" value="Wi-Fi">
                                    <label class="custom-control-label" for="editWifi">Wi-Fi</label>
                                </div>
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editTV" name="installations" value="TV">
                                    <label class="custom-control-label" for="editTV">Télévision</label>
                                </div>
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editClimatisation" name="installations" value="Climatisation">
                                    <label class="custom-control-label" for="editClimatisation">Climatisation</label>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editMinibar" name="installations" value="Minibar">
                                    <label class="custom-control-label" for="editMinibar">Minibar</label>
                                </div>
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editJacuzzi" name="installations" value="Jacuzzi">
                                    <label class="custom-control-label" for="editJacuzzi">Jacuzzi</label>
                                </div>
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="editServiceChambre" name="installations" value="Service en chambre">
                                    <label class="custom-control-label" for="editServiceChambre">Service en chambre</label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Prix - 4 types -->
                    <div class="form-group">
                        <label class="d-block">Tarification</label>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editPrixSejour">Prix par séjour (HTG) *</label>
                                    <input type="number" class="form-control" id="editPrixSejour" name="prixSejour" min="0" step="0.01" required>
                                    <small class="text-muted">Prix par nuit pour un séjour</small>
                                </div>
                                <div class="form-group">
                                    <label for="editPrixNuit">Prix pour une nuit (HTG)</label>
                                    <input type="number" class="form-control" id="editPrixNuit" name="prixNuit" min="0" step="0.01">
                                    <small class="text-muted">20h-7h</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editPrixJour">Prix pour une journée (HTG)</label>
                                    <input type="number" class="form-control" id="editPrixJour" name="prixJour" min="0" step="0.01">
                                    <small class="text-muted">8h-19h</small>
                                </div>
                                <div class="form-group">
                                    <label for="editPrixMoment">Prix par créneau (HTG)</label>
                                    <input type="number" class="form-control" id="editPrixMoment" name="prixMoment" min="0" step="0.01">
                                    <small class="text-muted">Créneau de 2h</small>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Disponibilité -->
                    <div class="form-group">
                        <div class="custom-control custom-switch">
                            <input type="checkbox" class="custom-control-input" id="editDisponible" name="disponible" value="true">
                            <label class="custom-control-label" for="editDisponible">Chambre disponible</label>
                        </div>
                    </div>

                    <!-- Images (upload multiple + preview) -->
                    <div class="form-group">
                        <label for="editMedia">Images de la chambre</label>
                        <input type="file" class="form-control-file" id="editMedia" name="media" accept="image/*" multiple>
                        <small class="text-muted">Vous pouvez sélectionner plusieurs images</small>
                    
                        <div id="editMediaPreview" class="d-flex flex-wrap mt-3" style="gap: 10px;">
                            <!-- Les aperçus d'images seront ajoutés ici dynamiquement -->
                        </div>
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

<!-- DataTable -->
<script>
	$("#dataTable-1").DataTable({
	    autoWidth: true,
	    lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
	    language: {
	        search: "Rechercher:",
	        lengthMenu: "Afficher _MENU_ chambres",
	        info: "Affichage de _START_ à _END_ sur _TOTAL_ chambres",
	        paginate: {
	            first: "Premier",
	            last: "Dernier",
	            next: "Suivant",
	            previous: "Précédent"
	        }
	    }
	});
</script>

<!-- Script remplissage modal -->
<script>
$(document).ready(function () {
    // Gestion du clic sur le bouton modifier
    $('#dataTable-1 tbody').on('click', '.btn-edit-chambre', function () {
        const button = $(this);

        // Remplir les champs basiques
        $('#editChambreId').val(button.data('id'));
        $('#editNomChambre').val(button.data('nomchambre'));
        $('#editDescriptionChambre').val(button.data('descriptionchambre'));
        $('#editCapacite').val(button.data('capacite'));
        
        // Remplir les 4 types de prix
        $('#editPrixMoment').val(button.data('prixmoment'));
        $('#editPrixNuit').val(button.data('prixnuit'));
        $('#editPrixJour').val(button.data('prixjour'));
        $('#editPrixSejour').val(button.data('prixsejour'));
        
        // Gestion de la disponibilité
        const disponible = button.data('disponible');
        $('#editDisponible').prop('checked', disponible);

        // Gestion des installations (checkboxes)
        const installations = button.data('installations');
        if (installations) {
            const installationsArray = installations.split(',');
            $('input[name="installations"]').each(function() {
                const value = $(this).val();
                $(this).prop('checked', installationsArray.includes(value));
            });
        } else {
            $('input[name="installations"]').prop('checked', false);
        }

        // Gestion des images (prévisualisation des images existantes)
        const mediaUrls = button.data('media');
        const previewContainer = $('#editMediaPreview');
        previewContainer.empty();

        if (mediaUrls) {
            const mediaArray = mediaUrls.split(',');
            const contextPath = '<%=request.getContextPath()%>';
            
            mediaArray.forEach(function(mediaUrl) {
                if (mediaUrl.trim() !== '') {
                    let imagePath;
                    if (mediaUrl.startsWith("uploads/chambres/")) {
                        const relativePath = mediaUrl.substring("uploads/chambres/".length);
                        imagePath = contextPath + '/images/chambres/' + relativePath;
                    } else {
                        imagePath = contextPath + '/images/default/chambre-default.png';
                    }
                    
                    const imgElement = $('<img>')
                        .attr('src', imagePath)
                        .attr('alt', 'Image chambre')
                        .css({
                            'width': '100px',
                            'height': '75px',
                            'object-fit': 'cover',
                            'border-radius': '4px',
                            'border': '1px solid #ddd'
                        });
                    
                    previewContainer.append(imgElement);
                }
            });
        }

        // Réinitialiser le champ input file
        $('#editMedia').val('');
    });

    // Preview dynamique à la sélection de nouvelles images
    $('#editMedia').change(function (event) {
        const input = event.target;
        const previewContainer = $('#editMediaPreview');
        
        // Vider le conteneur de preview
        previewContainer.empty();
        
        if (input.files && input.files.length > 0) {
            for (let i = 0; i < input.files.length; i++) {
                const file = input.files[i];
                const reader = new FileReader();
                
                reader.onload = function(e) {
                    const imgElement = $('<img>')
                        .attr('src', e.target.result)
                        .attr('alt', 'Nouvelle image')
                        .css({
                            'width': '100px',
                            'height': '75px',
                            'object-fit': 'cover',
                            'border-radius': '4px',
                            'border': '1px solid #ddd',
                            'margin': '5px'
                        });
                    
                    previewContainer.append(imgElement);
                }
                
                reader.readAsDataURL(file);
            }
        }
    });

    // Validation du formulaire
    $('#formEditChambre').submit(function(e) {
        // Validation des prix
        const prixSejour = parseFloat($('#editPrixSejour').val());
        if (prixSejour < 0) {
            e.preventDefault();
            alert('Le prix par séjour ne peut pas être négatif');
            return false;
        }
        
        const prixNuit = parseFloat($('#editPrixNuit').val());
        if (prixNuit < 0) {
            e.preventDefault();
            alert('Le prix pour une nuit ne peut pas être négatif');
            return false;
        }
        
        const prixJour = parseFloat($('#editPrixJour').val());
        if (prixJour < 0) {
            e.preventDefault();
            alert('Le prix pour une journée ne peut pas être négatif');
            return false;
        }
        
        const prixMoment = parseFloat($('#editPrixMoment').val());
        if (prixMoment < 0) {
            e.preventDefault();
            alert('Le prix par créneau ne peut pas être négatif');
            return false;
        }
        
        const capacite = parseInt($('#editCapacite').val());
        if (capacite < 1 || capacite > 10) {
            e.preventDefault();
            alert('La capacité doit être entre 1 et 10 personnes');
            return false;
        }
        
        return true;
    });
});
</script>