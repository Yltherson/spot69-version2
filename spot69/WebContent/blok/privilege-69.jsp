<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<style>
  /* Badges pour les niveaux de privilège */
 /*  .badge-vip {
    background: linear-gradient(45deg, #FFD700, #FFA500);
    color: #000;
    font-weight: bold;
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
  .badge-gold {
    background: linear-gradient(45deg, #FFD700, #daa520);
    color: #000;
    font-weight: bold;
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
  .badge-silver {
    background: linear-gradient(45deg, #C0C0C0, #a9a9a9);
    color: #000;
    font-weight: bold;
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
  .badge-bronze {
    background: linear-gradient(45deg, #CD7F32, #8b4513);
    color: white;
    font-weight: bold;
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  } */
  
  .badge-points {
     /* CHANGÉ: fond gris clair au lieu de bleu */
    color: #daaf5a; /* CHANGÉ: texte gris foncé */
    border: 1px solid #daaf5a; /* AJOUTÉ: bordure */
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
  
  .badge-reduction {
    background-color: #1cc88a;
    color: white;
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }
  
  /* Avatar */
  .avatar-circle {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 1rem;
  }
  
  /* Filtres */
  .filter-card {
    margin-bottom: 1.5rem;
  }
  
  /* Table */
.badge-primary {
    color: #ffffff;
    background-color: #daaf5a !important;
}
  
  /* Modal */
  .modal-content {
    border-radius: 10px;
    border: none;
  }
  
  .modal-header {
    background-color: #4e73df;
    color: white;
    border-radius: 10px 10px 0 0;
  }
  
  .form-section {
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e3e6f0;
  }
  
  .form-section-title {
    font-weight: 600;
    color: #4e73df;
    margin-bottom: 1rem;
    font-size: 1rem;
  }
  
  /* Bouton de mise à jour multiple */
  .btn-update-multiple {
    background: linear-gradient(45deg, #4e73df, #224abe);
    border: none;
    transition: all 0.3s;
  }
  
  .btn-update-multiple:hover {
    background: linear-gradient(45deg, #224abe, #4e73df);
    transform: translateY(-1px);
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  }
  
  /* Checkbox */
  .checkbox-cell {
    width: 40px;
  }
  
  /* Stats */
  .stats-card {
    border-left: 4px solid #4e73df;
  }
</style>

<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canManagePrivilege = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);

// NE PAS charger les données directement ici !!!
// Laisser le Servlet gérer le chargement des données
List<Utilisateur> clients = (List<Utilisateur>) request.getAttribute("clients");

// Compter les stats seulement si les clients sont chargés
int vipCount = 0, goldCount = 0, silverCount = 0, bronzeCount = 0;
int totalClients = 0;

if (clients != null && !clients.isEmpty()) {
    totalClients = clients.size();
    for(Utilisateur client : clients) {
        String privilege = client.getPrivilege() != null ? client.getPrivilege().toUpperCase() : "BRONZE";
        switch(privilege) {
            case "VIP": vipCount++; break;
            case "GOLD": goldCount++; break;
            case "SILVER": silverCount++; break;
            default: bronzeCount++; break;
        }
    }
}
%>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <div class="row">
          <div class="col-md-12 my-4">
            <!-- En-tête -->
            <div class="d-flex justify-content-between align-items-center mb-4">
              <div>
                <h2 class="h4 mb-1">
                  <i class="fe fe-star fe-24 align-self-center text-warning"></i>
                  Programme Privilège 69
                </h2>
                <p class="text-muted mb-0">Gestion des niveaux de privilège des clients</p>
              </div>
              <div class="d-flex align-items-center">
                <span class="badge badge-primary badge-pill p-2 mr-3">
                  <i class="fe fe-users fe-16 mr-1"></i>
                  <%= totalClients %> Clients
                </span>
                <% if(canManagePrivilege) { %>
                <button class="btn btn-outline-primary" data-toggle="modal" data-target="#updateMultipleModal">
                  <i class="fe fe-edit fe-16 mr-2"></i> Mise à jour multiple
                </button>
                <% } %>
              </div>
            </div>
            
            <!-- Statistiques -->
            <div class="row mb-4">
              <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-primary shadow h-100 py-2 stats-card">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">VIP</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= vipCount %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-award fe-2x text-warning"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-success shadow h-100 py-2 stats-card">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">GOLD</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= goldCount %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-star fe-2x text-warning"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-info shadow h-100 py-2 stats-card">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">SILVER</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= silverCount %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-star fe-2x text-secondary"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="col-xl-3 col-md-6 mb-4">
                <div class="card border-left-warning shadow h-100 py-2 stats-card">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">BRONZE</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= bronzeCount %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-star fe-2x text-brown"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Filtres -->
            <div class="card shadow mb-4 filter-card">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-filter fe-16 mr-2"></i>Filtres
                </h5>
              </div>
              <div class="card-body">
                <form method="GET" action="PrivilegeServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="lister">
                  
                  <div class="row">
                    <div class="col-md-4">
                      <div class="form-group">
                        <label for="privilegeFilter">Niveau de privilège</label>
                        <select class="form-control" id="privilegeFilter" name="privilege">
                          <option value="">Tous les niveaux</option>
                          <option value="PREMIUM">PREMIUN</option>
                          <option value="EXCLUSIVE">EXCLUSIVE</option>
                          <option value="CLASSIC">CLASSIC</option>
                          <!-- <option value="BRONZE">BRONZE</option> -->
                        </select>
                      </div>
                    </div>
                    <div class="col-md-4">
                      <div class="form-group">
                        <label for="pointsMinFilter">Points minimum</label>
                        <input type="number" class="form-control" id="pointsMinFilter" name="pointsMin" 
                               placeholder="0" min="0">
                      </div>
                    </div>
                    <div class="col-md-4 align-self-end">
                      <div class="form-group">
                        <button type="submit" class="btn btn-primary">
                          <i class="fe fe-filter fe-16"></i> Appliquer
                        </button>
                        <a href="PrivilegeServlet?action=lister" class="btn btn-secondary">
                          <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                        </a>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            
            <!-- Table principale -->
            <div class="card shadow">
              <div class="card-body">
                <div class="table-responsive ">
                  <table class="table datatables" id="dataTablePrivilege">
                    <thead>
                      <tr>
                        <th class="checkbox-cell">
                          <input type="checkbox" id="selectAll">
                        </th>
                        <th>Nom Complet</th>
                        <th>Email</th>
                        <th>Niveau/Privilège</th>
                        <th>Points</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      <% 
                      if (clients != null && !clients.isEmpty()) {
                        for(Utilisateur client : clients) { 
                          String privilege = client.getPrivilege() != null ? client.getPrivilege().toUpperCase() : "BRONZE";
                          String badgeClass = "";
                          switch(privilege) {
                            case "VIP": badgeClass = "badge-vip"; break;
                            case "GOLD": badgeClass = "badge-gold"; break;
                            case "SILVER": badgeClass = "badge-silver"; break;
                            default: badgeClass = "badge-bronze"; break;
                          }
                          
                          // Initiales pour l'avatar
                          String initials = "";
                          if(client.getPrenom() != null && !client.getPrenom().isEmpty() && 
                             client.getNom() != null && !client.getNom().isEmpty()) {
                            initials = client.getPrenom().charAt(0) + "" + client.getNom().charAt(0);
                          }
                      %>
                      <tr class="user-row">
                        <td class="checkbox-cell">
                          <input type="checkbox" class="user-checkbox" 
                                 name="selectedUsers" value="<%= client.getId() %>">
                        </td>
                        <td>
                          <div class="d-flex align-items-center">
                            <div class="avatar-circle bg-primary mr-3">
                              <%= initials.toUpperCase() %>
                            </div>
                            <div>
                              <strong><%= client.getPrenom() %> <%= client.getNom() %></strong>
                              <div class="text-muted small">
                                <i class="fe fe-user fe-12 mr-1"></i>Client
                              </div>
                            </div>
                          </div>
                        </td>
                        <td>
                          <%= client.getEmail() != null ? client.getEmail() : "N/A" %>
                        </td>
                        <td>
                          <span class="badge <%= badgeClass %>">
                            <i class="fe fe-award fe-12 mr-1"></i>
                            <%= privilege %>
                          </span>
                        </td>
                        <td>
                          <span class="badge badge-points">
                            <i class="fe fe-dollar-sign fe-12 mr-1"></i>
                            <%= client.getPoint() != null ? client.getPoint() : 0 %> pts
                          </span>
                        </td>
                        <td>
                          <div class="dropdown">
                            <button class="btn btn-sm btn-secondary dropdown-toggle" type="button" data-toggle="dropdown">
                              <i class="fe fe-more-vertical fe-12"></i>
                            </button>
                            <div class="dropdown-menu dropdown-menu-right">
                              <a class="dropdown-item" href="#" onclick="viewUserDetails('<%= client.getId() %>')">
                                <i class="fe fe-eye fe-16 mr-1"></i> Détails
                              </a>
                              <% if(canManagePrivilege) { %>
                              <a class="dropdown-item" href="#" onclick="editUserPrivilege('<%= client.getId() %>')">
                                <i class="fe fe-edit fe-16 mr-1"></i> Modifier
                              </a>
                              <div class="dropdown-divider"></div>
                              <a class="dropdown-item text-warning" href="#" onclick="resetPoints('<%= client.getId() %>')">
                                <i class="fe fe-refresh-cw fe-16 mr-1"></i> Réinitialiser points
                              </a>
                              <% } %>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <% 
                        }
                      } else { 
                      %>
                      <tr>
                        <td colspan="6" class="text-center">
                          <div class="alert alert-info">
                            <i class="fe fe-info fe-16 mr-2"></i>
                            Aucun client trouvé
                          </div>
                        </td>
                      </tr>
                      <% } %>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<!-- Modal Détails Utilisateur -->
<div class="modal fade" id="detailUserModal" tabindex="-1" role="dialog" aria-labelledby="detailUserModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="detailUserModalLabel">
          <i class="fe fe-user fe-16 mr-2"></i>Détails du Client
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="detailUserContent">
        <!-- Le contenu sera chargé dynamiquement -->
        <div class="text-center">
          <div class="spinner-border text-primary" role="status">
            <span class="sr-only">Chargement...</span>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Fermer</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal Modification Privilège -->
<div class="modal fade" id="editPrivilegeModal" tabindex="-1" role="dialog" aria-labelledby="editPrivilegeModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editPrivilegeModalLabel">
          <i class="fe fe-edit fe-16 mr-2"></i>Modifier le Privilège
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="PrivilegeServlet" id="editPrivilegeForm">
        <input type="hidden" name="action" value="update">
        <input type="hidden" id="editUserId" name="userId">
        <div class="modal-body">
          <div class="form-section">
            <h6 class="form-section-title">Informations du client</h6>
            <div class="form-group">
              <label id="clientNameLabel" class="font-weight-bold"></label>
            </div>
          </div>
          
          <div class="form-section">
            <h6 class="form-section-title">Niveau de privilège</h6>
            <div class="form-group">
              <label for="editPrivilege">Niveau</label>
              <select class="form-control" id="editPrivilege" name="privilege" required>
                <option value="CLASSIC">CLASSIC</option>
                <option value="EXCLUSIVE">EXCLUSIVE</option>
                <option value="PREMIUM">PREMIUM</option>
                <!-- <option value="VIP">VIP</option> -->
              </select>
            </div>
          </div>
          
          <div class="form-section">
            <h6 class="form-section-title">Points et réduction</h6>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="editPoints">Points</label>
                  <input type="number" class="form-control" id="editPoints" name="points" 
                         min="0" step="1">
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="editPourcentage">Pourcentage de réduction (%)</label>
                  <input type="number" class="form-control" id="editPourcentage" name="pourcentage" 
                         min="0" max="100" step="1">
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
          <button type="submit" class="btn btn-primary">Enregistrer</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Mise à jour Multiple -->
<div class="modal fade" id="updateMultipleModal" tabindex="-1" role="dialog" aria-labelledby="updateMultipleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="updateMultipleModalLabel">
          <i class="fe fe-users fe-16 mr-2"></i>Mise à jour multiple
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="PrivilegeServlet" id="updateMultipleForm">
        <input type="hidden" name="action" value="updateMultiple">
        <div class="modal-body">
          <div class="alert alert-info">
            <i class="fe fe-info fe-16 mr-2"></i>
            Sélectionnez un nouveau niveau de privilège pour les utilisateurs cochés.
          </div>
          
          <div class="form-group">
            <label for="newPrivilege">Nouveau niveau de privilège *</label>
            <select class="form-control" id="newPrivilege" name="newPrivilege" required>
              <option value="">Choisir niveau...</option>
              <option value="VIP">VIP</option>
              <option value="GOLD">GOLD</option>
              <option value="SILVER">SILVER</option>
              <option value="BRONZE">BRONZE</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Nombre d'utilisateurs sélectionnés : <span id="selectedCount" class="badge badge-primary">0</span></label>
          </div>
          
          <div class="alert alert-warning">
            <i class="fe fe-alert-triangle fe-16 mr-2"></i>
            Cette action affectera tous les utilisateurs cochés dans le tableau.
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
          <button type="submit" class="btn btn-primary btn-update-multiple">
            <i class="fe fe-check fe-16 mr-2"></i> Appliquer à tous les sélectionnés
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Confirmation Réinitialisation Points -->
<div class="modal fade" id="resetPointsModal" tabindex="-1" role="dialog" aria-labelledby="resetPointsModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="resetPointsModalLabel">Confirmer la réinitialisation</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Êtes-vous sûr de vouloir réinitialiser les points de ce client ?</p>
        <p class="text-warning"><small>Les points seront remis à zéro. Cette action ne peut être annulée.</small></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
        <a href="#" id="confirmResetBtn" class="btn btn-warning">Réinitialiser</a>
      </div>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />

<script>
$(document).ready(function () {
    // Initialisation DataTable
    $('#dataTablePrivilege').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json"
        },
        "pageLength": 10,
        "lengthMenu": [10, 25, 50, 100],
        "order": [[1, "asc"]],
        "responsive": true,
        "columnDefs": [
            {
                "targets": 0,
                "orderable": false,
                "searchable": false
            },
            {
                "targets": 5, // CHANGÉ: était 6, maintenant 5 car il y a une colonne en moins
                "orderable": false,
                "searchable": false
            }
        ]
    });

    // Gestion de la sélection multiple
    $('#selectAll').click(function() {
        $('.user-checkbox').prop('checked', this.checked);
        updateSelectedCount();
    });
    
    $('.user-checkbox').click(function() {
        if($('.user-checkbox:checked').length == $('.user-checkbox').length) {
            $('#selectAll').prop('checked', true);
        } else {
            $('#selectAll').prop('checked', false);
        }
        updateSelectedCount();
    });
    
    // Mettre à jour le compteur
    function updateSelectedCount() {
        var count = $('.user-checkbox:checked').length;
        $('#selectedCount').text(count);
    }

    // Valider le formulaire de mise à jour multiple
    $('#updateMultipleForm').submit(function(e) {
        var selectedCount = $('.user-checkbox:checked').length;
        var newPrivilege = $('#newPrivilege').val();
        
        if(selectedCount === 0) {
            e.preventDefault();
            showToast('error', 'Veuillez sélectionner au moins un utilisateur');
            return false;
        }
        
        if(!newPrivilege) {
            e.preventDefault();
            showToast('error', 'Veuillez sélectionner un nouveau niveau de privilège');
            return false;
        }
        
        // Ajouter les IDs sélectionnés au formulaire
        $('.user-checkbox:checked').each(function() {
            var userId = $(this).val();
            $(this).clone().attr('type', 'hidden').appendTo('#updateMultipleForm');
        });
        
        return confirm('Êtes-vous sûr de vouloir modifier le niveau de privilège de ' + selectedCount + ' utilisateur(s) ?');
    });
});

// Fonction pour afficher les détails d'un utilisateur
function viewUserDetails(userId) {
    $('#detailUserContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">Chargement...</span></div></div>');
    $('#detailUserModal').modal('show');
    
    // Charger les détails via AJAX
    $.ajax({
        url: 'PrivilegeServlet',
        type: 'GET',
        data: { action: 'details', id: userId },
        success: function(response) {
            $('#detailUserContent').html(response);
        },
        error: function() {
            $('#detailUserContent').html('<div class="alert alert-danger">Erreur lors du chargement des détails.</div>');
        }
    });
}

// Fonction pour modifier le privilège d'un utilisateur
function editUserPrivilege(userId) {
    // Charger les données de l'utilisateur via AJAX
    $.ajax({
        url: 'PrivilegeServlet',
        type: 'GET',
        data: { action: 'getUser', id: userId },
        dataType: 'json',
        success: function(user) {
            // Remplir le formulaire
            $('#editUserId').val(user.id);
            $('#clientNameLabel').text(user.prenom + ' ' + user.nom);
            $('#editPrivilege').val(user.privilege || 'BRONZE');
            $('#editPoints').val(user.point || 0);
            
            // Convertir le pourcentage (0.10 -> 10%)
            var pourcentage = user.pourcentage ? parseFloat(user.pourcentage) * 100 : 0;
            $('#editPourcentage').val(pourcentage);
            
            $('#editPrivilegeModal').modal('show');
        },
        error: function() {
            showToast('error', 'Erreur lors du chargement des données');
        }
    });
}

// Fonction pour réinitialiser les points
function resetPoints(userId) {
    $('#confirmResetBtn').attr('href', 'PrivilegeServlet?action=resetPoints&id=' + userId);
    $('#resetPointsModal').modal('show');
}

// Fonction pour afficher les toasts
function showToast(type, message) {
    var toastClass = type === 'success' ? 'alert-success' : 'alert-danger';
    var toastHtml = '<div class="toast-alert alert ' + toastClass + ' alert-dismissible fade show" role="alert">' +
                    '<i class="fe fe-' + (type === 'success' ? 'check' : 'alert-triangle') + ' fe-16 mr-2"></i>' +
                    message +
                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                    '<span aria-hidden="true">&times;</span>' +
                    '</button>' +
                    '</div>';
    
    $('main').prepend(toastHtml);
    
    // Supprimer le toast après 5 secondes
    setTimeout(function() {
        $('.toast-alert').alert('close');
    }, 5000);
}

// Validation du formulaire d'édition
$('#editPrivilegeForm').submit(function(e) {
    var pourcentage = $('#editPourcentage').val();
    
    if(pourcentage < 0 || pourcentage > 100) {
        e.preventDefault();
        showToast('error', 'Le pourcentage doit être entre 0 et 100');
        return false;
    }
    
    var points = $('#editPoints').val();
    if(points < 0) {
        e.preventDefault();
        showToast('error', 'Les points ne peuvent pas être négatifs');
        return false;
    }
    
    return true;
});

// Gestion des messages de notification
$(document).ready(function() {
    <% 
    String successMsg = (String) session.getAttribute("ToastAdmSuccesNotif");
    String errorMsg = (String) session.getAttribute("ToastAdmErrorNotif");
    String warningMsg = (String) session.getAttribute("ToastAdmWarningNotif");
    
    if(successMsg != null) {
        session.removeAttribute("ToastAdmSuccesNotif");
    %>
        showToast('success', '<%= successMsg %>');
    <% } 
    
    if(errorMsg != null) {
        session.removeAttribute("ToastAdmErrorNotif");
    %>
        showToast('error', '<%= errorMsg %>');
    <% } 
    
    if(warningMsg != null) {
        session.removeAttribute("ToastAdmWarningNotif");
    %>
        showToast('warning', '<%= warningMsg %>');
    <% } %>
});

// Exporter en Excel
function exportToExcel() {
    var table = $('#dataTablePrivilege').DataTable();
    table.button('.buttons-excel').trigger();
}

// Activer le bouton de mise à jour multiple seulement si des utilisateurs sont sélectionnés
$(document).on('change', '.user-checkbox', function() {
    var hasSelection = $('.user-checkbox:checked').length > 0;
    $('#updateMultipleModal').find('button[type="submit"]').prop('disabled', !hasSelection);
});
</script>