<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Reservation,java.text.SimpleDateFormat,java.util.Date,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<style>
  .reservation-badge {
    font-size: 0.75rem;
    margin: 1px;
  }
  .type-badge-sejour { background-color: #28a745; }
  .type-badge-nuit { background-color: #007bff; }
  .type-badge-jour { background-color: #ffc107; color: #000; }
  .type-badge-moment { background-color: #6f42c1; }
  .status-badge-confirmed { background-color: #28a745; }
  .status-badge-pending { background-color: #ffc107; color: #000; }
  .status-badge-cancelled { background-color: #dc3545; }
  .date-cell {
    min-width: 150px;
  }
  
  /* Styles pour les boutons d'action */
  .dropdown-item button {
    background: none;
    border: none;
    width: 100%;
    text-align: left;
    padding: 0.25rem 1.5rem;
  }
  
  .dropdown-item form {
    margin: 0;
  }
  
  /* Styles pour les modals */
  .modal-content {
    border-radius: 10px;
  }
  
  .modal-header {
    /* background-color: #f8f9fa; */
    border-bottom: 1px solid #dee2e6;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .form-section {
    margin-bottom: 25px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
  }
  
  .form-section-title {
    font-weight: 600;
    color: #495057;
    margin-bottom: 15px;
    font-size: 1.1rem;
  }
  
  .reservation-info {
    background-color: #f8f9fa;
    padding: 15px;
    border-radius: 5px;
    margin-bottom: 15px;
  }
  
  .info-label {
    font-weight: 600;
    color: #495057;
    min-width: 150px;
  }
  
  .info-value {
    color: #212529;
  }
  
  /* Styles pour les détails de réservation */
.reservation-details .info-row {
    margin-bottom: 15px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
}
.reservation-details .info-label {
    font-weight: 600;
    color: #495057;
    min-width: 200px;
    display: inline-block;
}
.reservation-details .info-value {
    color: #212529;
}
.reservation-details .section-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #343a40;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #007bff;
}
.payment-info {
    background-color: #f8f9fa;
    padding: 15px;
    border-radius: 5px;
    margin-top: 20px;
}
.user-info {
    background-color: #e8f4fd;
    padding: 15px;
    border-radius: 5px;
    margin-top: 20px;
}
</style>

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditReservations = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_PRODUITS);
boolean canDeleteReservations = canEditReservations && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_PRODUITS);

// Récupérer les paramètres de filtres
String statutFilter = request.getParameter("statut");
String typeFilter = request.getParameter("type");
String utilisateurFilter = request.getParameter("utilisateurId");
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
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="h4 mb-1">
                <i class="fe fe-calendar fe-32 align-self-center text-black"></i>
                Gestion des Réservations
              </h2>
              <div class="d-flex flex-column align-items-end">
                <button class="btn btn-outline-primary" data-toggle="modal" data-target="#addReservationModal">
                  <i class="fe fe-plus fe-16"></i> Nouvelle réservation
                </button>
              </div>
            </div>
            
            <!-- Filtres corrigés -->
            <div class="card shadow mb-4">
              <div class="card-header">
                <h5 class="card-title mb-0">Filtres</h5>
              </div>
              <div class="card-body">
                <form method="GET" action="ReservationServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="lister">
                  
                  <div class="row">
                    <div class="col-md-3">
                      <div class="form-group">
                        <label for="statutFilter">Statut</label>
                        <select class="form-control" id="statutFilter" name="statut">
                          <option value="">Tous les statuts</option>
                          <option value="confirmé" <%= "confirmé".equals(statutFilter) || "confirmed".equals(statutFilter) ? "selected" : "" %>>Confirmé</option>
                          <option value="en cours" <%= "en cours".equals(statutFilter) || "pending".equals(statutFilter) ? "selected" : "" %>>En attente</option>
                          <option value="annulé" <%= "annulé".equals(statutFilter) || "cancelled".equals(statutFilter) ? "selected" : "" %>>Annulé</option>
                        </select>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <div class="form-group">
                        <label for="typeFilter">Type</label>
                        <select class="form-control" id="typeFilter" name="type">
                          <option value="">Tous les types</option>
                          <option value="sejour" <%= "sejour".equals(typeFilter) ? "selected" : "" %>>Séjour</option>
                          <option value="nuit" <%= "nuit".equals(typeFilter) ? "selected" : "" %>>Nuit</option>
                          <option value="jour" <%= "jour".equals(typeFilter) ? "selected" : "" %>>Jour</option>
                          <option value="moment" <%= "moment".equals(typeFilter) ? "selected" : "" %>>Moment</option>
                        </select>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <div class="form-group">
                        <label for="utilisateurFilter">Utilisateur</label>
                        <select class="form-control" id="utilisateurFilter" name="utilisateurId">
                          <option value="">Tous les utilisateurs</option>
                          <% 
                          List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
                          if (utilisateurs != null) {
                              for (Utilisateur user : utilisateurs) { 
                          %>
                              <option value="<%= user.getId() %>" 
                                      <%= (utilisateurFilter != null && utilisateurFilter.equals(String.valueOf(user.getId()))) ? "selected" : "" %>>
                                <%= user.getLogin() %>
                              </option>
                          <% 
                              }
                          }
                          %>
                        </select>
                      </div>
                    </div>
                    <div class="col-md-3 align-self-end">
                      <div class="form-group">
                        <button type="submit" class="btn btn-primary">
                          <i class="fe fe-filter fe-16"></i> Appliquer
                        </button>
                        <a href="ReservationServlet?action=lister" class="btn btn-secondary">
                          <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                        </a>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            
            <div class="card shadow">
              <div class="card-body">
                <table class="table datatables" id="dataTableReservations">
                  <thead>
                    <tr>
                      <th>Titre</th>
                      <!-- <th>Chambre</th> -->
                      <th>Période</th>
                      <th>Type</th>
                      <th>Prix Total</th>
                      <th>Statut</th>
                      <th>Détails</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <%
                    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    if (reservations != null) {
                        for (Reservation reservation : reservations) {
                    %>
                    <tr>
                      <td><strong><%= reservation.getTitle() %></strong></td>
                      <%-- <td>
                        <span class="badge badge-info reservation-badge">
                          Chambre <%= reservation.getRoomId() %>
                        </span>
                      </td> --%>
                      <td class="date-cell">
                        <small>
                          Du <%= sdf.format(reservation.getStart()) %><br>
                          Au <%= sdf.format(reservation.getEnd()) %>
                        </small>
                      </td>
                      <td>
                        <%
                          String typeClass = "";
                          switch(reservation.getType()) {
                            case "sejour": typeClass = "type-badge-sejour"; break;
                            case "nuit": typeClass = "type-badge-nuit"; break;
                            case "jour": typeClass = "type-badge-jour"; break;
                            case "moment": typeClass = "type-badge-moment"; break;
                          }
                        %>
                        <span class="badge reservation-badge">
                          <%= reservation.getType().toUpperCase() %>
                        </span>
                      </td>
                      <td><strong><%= reservation.getPrixTotal() %> HTG</strong></td>
                      <td>
                        <%
                          String statusClass = "";
                          String status = reservation.getStatus();
                          if (status != null) {
                              switch(status.toLowerCase()) {
                                case "confirmé":
                                case "confirmed":
                                    statusClass = "status-badge-confirmed"; 
                                    status = "CONFIRMÉ";
                                    break;
                                case "en cours":
                                case "pending":
                                    statusClass = "status-badge-pending"; 
                                    status = "EN COURS";
                                    break;
                                case "annulé":
                                case "cancelled":
                                    statusClass = "status-badge-cancelled"; 
                                    status = "ANNULÉ";
                                    break;
                              }
                          }
                        %>
                        <span class="badge reservation-badge <%= statusClass %>">
                          <%= status %>
                        </span>
                      </td>
                      <td>
                        <%
                          if ("sejour".equals(reservation.getType())) {
                        %>
                          <small>
                            <%= reservation.getNumberOfNights() != null ? reservation.getNumberOfNights() + " nuit(s)" : "" %><br>
                            <%= reservation.getArrivalTime() != null ? "Arrivée: " + reservation.getArrivalTime() : "" %>
                          </small>
                        <%
                          } else if ("moment".equals(reservation.getType())) {
                        %>
                          <small>
                            <%= reservation.getNumberOfSlots() != null ? reservation.getNumberOfSlots() + " créneau(x)" : "" %><br>
                            <%= reservation.getDurationHours() != null ? reservation.getDurationHours() + " heure(s)" : "" %>
                          </small>
                        <%
                          }
                        %>
                      </td>
                      <td>
                        <div class="dropdown">
                          <button class="btn btn-sm btn-secondary dropdown-toggle" type="button" data-toggle="dropdown">
                            Action
                          </button>
                          <div class="dropdown-menu dropdown-menu-right">
                            <a class="dropdown-item" href="#" onclick="showReservationDetails('<%= reservation.getId() %>')">
                              <i class="fe fe-eye fe-16 mr-1"></i> Détails
                            </a>
                            <% if (canEditReservations) { %>
                            <%-- <a class="dropdown-item" href="#" onclick="editReservation('<%= reservation.getId() %>')">
                              <i class="fe fe-edit fe-16 mr-1"></i> Modifier
                            </a> --%>
                            
                            <!-- Changement de statut -->
                            <% 
                              String currentStatus = reservation.getStatus() != null ? reservation.getStatus().toLowerCase() : "";
                            %>
                            <!-- <div class="dropdown-divider"></div> -->
                            
                            <% if (!"confirmé".equals(currentStatus) && !"confirmed".equals(currentStatus)) { %>
                            <form method="POST" action="ReservationServlet" class="dropdown-item p-0" style="display: block;">
                              <input type="hidden" name="action" value="changer-statut">
                              <input type="hidden" name="id" value="<%= reservation.getId() %>">
                              <button type="submit" name="statut" value="confirmed" class="dropdown-item btn-sm text-success" 
                                      style="background: none; border: none; width: 100%; text-align: left; padding: 0.25rem 1.5rem;">
                                <i class="fe fe-check fe-16 mr-1"></i> Confirmer
                              </button>
                            </form>
                            <% } %>
                            
                            <% if (!"annulé".equals(currentStatus) && !"cancelled".equals(currentStatus)) { %>
                            <form method="POST" action="ReservationServlet" class="dropdown-item p-0" style="display: block;">
                              <input type="hidden" name="action" value="changer-statut">
                              <input type="hidden" name="id" value="<%= reservation.getId() %>">
                              <button type="submit" name="statut" value="cancelled" class="dropdown-item btn-sm text-danger"
                                      style="background: none; border: none; width: 100%; text-align: left; padding: 0.25rem 1.5rem;">
                                <i class="fe fe-x fe-16 mr-1"></i> Annuler
                              </button>
                            </form>
                            <% } %>
                            
                            <% if ("annulé".equals(currentStatus) || "cancelled".equals(currentStatus) || 
                                   "confirmé".equals(currentStatus) || "confirmed".equals(currentStatus)) { %>
                            <form method="POST" action="ReservationServlet" class="dropdown-item p-0" style="display: block;">
                              <input type="hidden" name="action" value="changer-statut">
                              <input type="hidden" name="id" value="<%= reservation.getId() %>">
                              <button type="submit" name="statut" value="pending" class="dropdown-item btn-sm text-warning"
                                      style="background: none; border: none; width: 100%; text-align: left; padding: 0.25rem 1.5rem;">
                                <i class="fe fe-clock fe-16 mr-1"></i> Remettre en attente
                              </button>
                            </form>
                            <% } %>
                            
                            <% } %>
                            
                            <%-- <% if (canDeleteReservations) { %> --%>
								<div class="dropdown-divider"></div>
								<form method="POST" action="ReservationServlet" class="dropdown-item p-0" style="display: block;">
								  <input type="hidden" name="action" value="supprimer">
								  <input type="hidden" name="id" value="<%= reservation.getId() %>">
								  <button type="submit" class="dropdown-item btn-sm text-danger" 
								          onclick="return confirm('Êtes-vous sûr de vouloir supprimer la réservation \'<%= reservation.getTitle() %>\' ?')"
								          style="background: none; border: none; width: 100%; text-align: left; padding: 0.25rem 1.5rem;">
								    <i class="fe fe-trash-2 fe-16 mr-1"></i> Supprimer
								  </button>
								</form>
								<%-- <% } %> --%>
                          </div>
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
</main>

<!-- Modal Ajout Réservation -->
<div class="modal fade" id="addReservationModal" tabindex="-1" role="dialog" aria-labelledby="addReservationModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="addReservationModalLabel">
          <i class="fe fe-plus fe-16 mr-2"></i>Nouvelle Réservation
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="ReservationServlet" id="addReservationForm">
        <input type="hidden" name="action" value="ajouter">
        <div class="modal-body">
          <div class="form-section">
            <h6 class="form-section-title">Informations générales</h6>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addTitle">Titre *</label>
                  <input type="text" class="form-control" id="addTitle" name="title" required>
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addRoomId">Chambre *</label>
                  <input type="text" class="form-control" id="addRoomId" name="roomId" required>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addType">Type de réservation *</label>
                  <select class="form-control" id="addType" name="type" required onchange="showAdditionalFields()">
                    <option value="">Sélectionnez un type</option>
                    <option value="sejour">Séjour</option>
                    <option value="nuit">Nuit</option>
                    <option value="jour">Jour</option>
                    <option value="moment">Moment</option>
                  </select>
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addPrixTotal">Prix Total (HTG) *</label>
                  <input type="number" step="0.01" class="form-control" id="addPrixTotal" name="prixTotal" required>
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h6 class="form-section-title">Période de réservation</h6>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addStart">Date et heure de début *</label>
                  <input type="datetime-local" class="form-control" id="addStart" name="start" required>
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addEnd">Date et heure de fin *</label>
                  <input type="datetime-local" class="form-control" id="addEnd" name="end" required>
                </div>
              </div>
            </div>
          </div>

          <!-- Champs supplémentaires selon le type -->
          <div class="form-section" id="sejourFields" style="display: none;">
            <h6 class="form-section-title">Détails du séjour</h6>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addNumberOfNights">Nombre de nuits</label>
                  <input type="number" class="form-control" id="addNumberOfNights" name="numberOfNights" min="1">
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addArrivalTime">Heure d'arrivée</label>
                  <input type="time" class="form-control" id="addArrivalTime" name="arrivalTime">
                </div>
              </div>
            </div>
          </div>

          <div class="form-section" id="momentFields" style="display: none;">
            <h6 class="form-section-title">Détails du moment</h6>
            <div class="row">
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addNumberOfSlots">Nombre de créneaux</label>
                  <input type="number" class="form-control" id="addNumberOfSlots" name="numberOfSlots" min="1">
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group">
                  <label for="addDurationHours">Durée (heures)</label>
                  <input type="number" class="form-control" id="addDurationHours" name="durationHours" min="1">
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h6 class="form-section-title">Client</h6>
            <div class="form-group">
              <label for="addUtilisateurId">Utilisateur *</label>
              <select class="form-control" id="addUtilisateurId" name="utilisateurId" required>
                <option value="">Sélectionnez un utilisateur</option>
                <% 
                if (utilisateurs != null) {
                    for (Utilisateur user : utilisateurs) { 
                %>
                    <option value="<%= user.getId() %>"><%= user.getLogin() %></option>
                <% 
                    }
                }
                %>
              </select>
            </div>
          </div>

          <div class="form-section">
            <h6 class="form-section-title">Statut</h6>
            <div class="form-group">
              <label for="addStatus">Statut</label>
              <select class="form-control" id="addStatus" name="status">
                <option value="en cours" selected>En cours</option>
                <option value="confirmé">Confirmé</option>
                <option value="annulé">Annulé</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
          <button type="submit" class="btn btn-primary">Créer la réservation</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Modification Réservation -->
<div class="modal fade" id="editReservationModal" tabindex="-1" role="dialog" aria-labelledby="editReservationModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editReservationModalLabel">
          <i class="fe fe-edit fe-16 mr-2"></i>Modifier la Réservation
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="ReservationServlet" id="editReservationForm">
        <input type="hidden" name="action" value="modifier">
        <input type="hidden" id="editId" name="id">
        <div class="modal-body">
          <!-- Le contenu sera chargé dynamiquement -->
          <div class="text-center">
            <div class="spinner-border text-primary" role="status">
              <span class="sr-only">Chargement...</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
          <button type="submit" class="btn btn-primary">Mettre à jour</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Détails Réservation -->
<div class="modal fade" id="detailReservationModal" tabindex="-1" role="dialog" aria-labelledby="detailReservationModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <!-- <div class="modal-header">
        <h5 class="modal-title" id="detailReservationModalLabel">
          <i class="fe fe-eye fe-16 mr-2"></i>Détails de la Réservation
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div> -->
      <div class="modal-body" id="detailReservationContent">
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

<!-- Modal Confirmation Suppression -->
<div class="modal fade" id="deleteConfirmModal" tabindex="-1" role="dialog" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteConfirmModalLabel">Confirmer la suppression</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Êtes-vous sûr de vouloir supprimer la réservation : <strong id="reservationToDeleteTitle"></strong> ?</p>
        <p class="text-danger"><small>Cette action est irréversible.</small></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
        <a href="#" id="confirmDeleteBtn" class="btn btn-danger">Supprimer</a>
      </div>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />

<script>
$(document).ready(function () {
    // Initialisation DataTable
    $('#dataTableReservations').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json"
        },
        "pageLength": 10,
        "lengthMenu": [10, 25, 50, 100],
        "order": [[2, "desc"]], // Tri par date de début
        "responsive": true
    });

    // Afficher/masquer les champs supplémentaires selon le type
    function showAdditionalFields() {
        var type = $('#addType').val();
        $('#sejourFields').hide();
        $('#momentFields').hide();
        
        if (type === 'sejour') {
            $('#sejourFields').show();
        } else if (type === 'moment') {
            $('#momentFields').show();
        }
    }
    
    $('#addType').change(showAdditionalFields);

    // Empêcher la propagation des événements sur les boutons du dropdown
    $('.dropdown-item button').click(function(e) {
        e.stopPropagation();
    });
});

// Fonction pour afficher les détails d'une réservation
/* function showReservationDetails(reservationId) {
    $('#detailReservationContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">Chargement...</span></div></div>');
    $('#detailReservationModal').modal('show');
    
    // Charger les détails via AJAX
    $.ajax({
        url: 'ReservationServlet',
        type: 'GET',
        data: { action: 'details', id: reservationId },
        success: function(response) {
            $('#detailReservationContent').html(response);
        },
        error: function() {
            $('#detailReservationContent').html('<div class="alert alert-danger">Erreur lors du chargement des détails.</div>');
        }
    });
} */

//Fonction pour afficher les détails d'une réservation
function showReservationDetails(reservationId) {
    $('#detailReservationContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">Chargement...</span></div></div>');
    $('#detailReservationModal').modal('show');
    
    // Charger les détails via AJAX
    $.ajax({
        url: 'ReservationServlet',
        type: 'GET',
        data: { action: 'details-content', id: reservationId },
        success: function(response) {
            $('#detailReservationContent').html(response);
        },
        error: function() {
            $('#detailReservationContent').html('<div class="alert alert-danger">Erreur lors du chargement des détails.</div>');
        }
    });
}

// Fonction pour modifier une réservation
function editReservation(reservationId) {
    $('#editReservationModal').modal('show');
    
    // Charger le formulaire de modification via AJAX
    $.ajax({
        url: 'ReservationServlet',
        type: 'GET',
        data: { action: 'edit', id: reservationId },
        success: function(response) {
            $('#editReservationModal .modal-body').html(response);
            
            // Réinitialiser les événements pour les champs dynamiques
            $('#editType').off('change').on('change', function() {
                var type = $(this).val();
                $('#editSejourFields').toggle(type === 'sejour');
                $('#editMomentFields').toggle(type === 'moment');
            });
        },
        error: function() {
            $('#editReservationModal .modal-body').html('<div class="alert alert-danger">Erreur lors du chargement des données.</div>');
        }
    });
}

// Fonction pour supprimer une réservation
function deleteReservation(reservationId, reservationTitle) {
    $('#reservationToDeleteTitle').text(reservationTitle);
    $('#confirmDeleteBtn').attr('href', 'ReservationServlet?action=supprimer&id=' + reservationId);
    $('#deleteConfirmModal').modal('show');
}

// Pré-remplir la date et heure pour le formulaire d'ajout
$(document).ready(function() {
    var now = new Date();
    var nowStr = now.toISOString().slice(0, 16);
    var tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    var tomorrowStr = tomorrow.toISOString().slice(0, 16);
    
    $('#addStart').val(nowStr);
    $('#addEnd').val(tomorrowStr);
    
    // Si utilisateur connecté, sélectionner son ID par défaut
    <% if (currentUser != null) { %>
        $('#addUtilisateurId').val('<%= currentUser.getId() %>');
    <% } %>
});

// Gestion des messages de notification
$(document).ready(function() {
    <% 
    String successMsg = (String) session.getAttribute("ToastAdmSuccesNotif");
    String errorMsg = (String) session.getAttribute("ToastAdmErrorNotif");
    
    if (successMsg != null) {
        session.removeAttribute("ToastAdmSuccesNotif");
    %>
        showToast('success', '<%= successMsg %>');
    <% } 
    
    if (errorMsg != null) {
        session.removeAttribute("ToastAdmErrorNotif");
    %>
        showToast('error', '<%= errorMsg %>');
    <% } %>
});

// Fonction pour afficher les toasts
function showToast(type, message) {
    var toastClass = type === 'success' ? 'alert-success' : 'alert-danger';
    var toastHtml = '<div class="toast-alert alert ' + toastClass + ' alert-dismissible fade show" role="alert">' +
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

// Validation du formulaire d'ajout
$('#addReservationForm').submit(function(e) {
    var start = new Date($('#addStart').val());
    var end = new Date($('#addEnd').val());
    
    if (start >= end) {
        e.preventDefault();
        alert('La date de fin doit être postérieure à la date de début.');
        return false;
    }
    
    return true;
});
</script>