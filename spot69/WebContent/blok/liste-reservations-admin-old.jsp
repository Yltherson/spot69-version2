<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List,com.spot69.model.ReservationEvenement,com.spot69.model.Evenement,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<meta charset="UTF-8">
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des réservations</title>
    
    <style>
        .badge-status {
            font-size: 12px;
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: 500;
        }
        .statut-EN_ATTENTE { 
            background-color: #ffc107 !important; 
            color: #000 !important; 
        }
        .statut-CONFIRMEE { 
            background-color: #28a745 !important; 
            color: white !important; 
        }
        .statut-ANNULEE { 
            background-color: #dc3545 !important; 
            color: white !important; 
        }
        .statut-DELETED { 
            background-color: #6c757d !important; 
            color: white !important; 
        }
        .date-cell { min-width: 150px; }
        .amount-cell { min-width: 120px; }
        .action-cell { min-width: 180px; }
        .filter-container {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #5d6368;
        }
        .payment-method {
            font-size: 12px;
            padding: 3px 6px;
            border-radius: 3px;
            background-color: #6c757d;
            color: white;
            display: inline-block;
            margin-bottom: 5px;
        }
        .table-responsive {
            overflow-x: auto;
        }
        .payment-info {
            font-size: 11px;
            color: #6c757d;
            line-height: 1.3;
        }
        .payment-info strong {
            color: #495057;
        }
        .summary-total {
            font-weight: 600;
            font-size: 1.1em;
        }
        .text-warning { color: #ffc107 !important; }
        .text-success { color: #28a745 !important; }
        .text-danger { color: #dc3545 !important; }
        
        /* Style pour DataTables */
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter {
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
<%
List<ReservationEvenement> reservations = (List<ReservationEvenement>) request.getAttribute("reservations");
Evenement evenement = (Evenement) request.getAttribute("evenement");
boolean isAllReservations = evenement == null;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String today = sdf.format(new Date());
%>

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
                <i class="fe fe-users fe-32 align-self-center text-black"></i>
                <%= isAllReservations ? "Toutes les réservations" : "Réservations pour: " + evenement.getTitre() %>
              </h2>
              <div class="d-flex flex-column align-items-end">
                <% if (!isAllReservations) { %>
                <a href="EvenementServlet?action=reservations" class="btn btn-secondary mb-2">
                  <i class="fe fe-list fe-16"></i> Toutes les réservations
                </a>
                <% } else { %>
                <a href="EvenementServlet?action=lister" class="btn btn-secondary mb-2">
                  <i class="fe fe-arrow-left fe-16"></i> Retour aux événements
                </a>
                <% } %>
                <!-- Bouton export (optionnel) -->
                <a href="#" class="btn btn-outline-primary btn-sm" onclick="exportReservations()">
                  <i class="fe fe-download fe-16"></i> Exporter
                </a>
              </div>
            </div>
            
            <!-- Filtres améliorés -->
            <div class="filter-container">
              <form method="GET" action="EvenementServlet" class="row g-3 align-items-end">
                <input type="hidden" name="action" value="<%= isAllReservations ? "reservations" : "reservationsevent" %>">
                <% if (!isAllReservations) { %>
                <input type="hidden" name="id" value="<%= evenement.getId() %>">
                <% } %>
                
                <div class="col-md-2 col-sm-6">
                  <label for="statut" class="form-label">Statut</label>
                  <select class="form-control form-control-sm" id="statut" name="statut">
                    <option value="">Tous les statuts</option>
                    <option value="EN_ATTENTE" <%= "EN_ATTENTE".equals(request.getParameter("statut")) ? "selected" : "" %>>En attente</option>
                    <option value="CONFIRMEE" <%= "CONFIRMEE".equals(request.getParameter("statut")) ? "selected" : "" %>>Confirmée</option>
                    <option value="ANNULEE" <%= "ANNULEE".equals(request.getParameter("statut")) ? "selected" : "" %>>Annulée</option>
                  </select>
                </div>
                
                <div class="col-md-2 col-sm-6">
                  <label for="moyenPaiement" class="form-label">Paiement</label>
                  <select class="form-control form-control-sm" id="moyenPaiement" name="moyenPaiement">
                    <option value="">Tous</option>
                    <option value="SOLDE" <%= "SOLDE".equals(request.getParameter("moyenPaiement")) ? "selected" : "" %>>Solde</option>
                    <option value="MONCASH" <%= "MONCASH".equals(request.getParameter("moyenPaiement")) ? "selected" : "" %>>MonCash</option>
                    <option value="NATCASH" <%= "NATCASH".equals(request.getParameter("moyenPaiement")) ? "selected" : "" %>>NatCash</option>
                    <option value="VIREMENT" <%= "VIREMENT".equals(request.getParameter("moyenPaiement")) ? "selected" : "" %>>Virement</option>
                    <option value="CASH" <%= "CASH".equals(request.getParameter("moyenPaiement")) ? "selected" : "" %>>Cash</option>
                  </select>
                </div>
                
                <div class="col-md-2 col-sm-6">
                  <label for="dateDebut" class="form-label">Date début</label>
                  <input type="date" class="form-control form-control-sm" id="dateDebut" name="dateDebut" 
                         value="<%= request.getParameter("dateDebut") != null ? request.getParameter("dateDebut") : "" %>"
                         max="<%= today %>">
                </div>
                
                <div class="col-md-2 col-sm-6">
                  <label for="dateFin" class="form-label">Date fin</label>
                  <input type="date" class="form-control form-control-sm" id="dateFin" name="dateFin"
                         value="<%= request.getParameter("dateFin") != null ? request.getParameter("dateFin") : "" %>"
                         max="<%= today %>">
                </div>
                
                <div class="col-md-2 col-sm-6">
                  <label for="recherche" class="form-label">Recherche</label>
                  <input type="text" class="form-control form-control-sm" id="recherche" name="recherche"
                         placeholder="Nom, transaction..."
                         value="<%= request.getParameter("recherche") != null ? request.getParameter("recherche") : "" %>">
                </div>
                
                <div class="col-md-2 col-sm-6">
                  <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary btn-sm">
                      <i class="fe fe-filter fe-16"></i> Filtrer
                    </button>
                    <% if (isAllReservations) { %>
                    <a href="EvenementServlet?action=reservations" class="btn btn-secondary btn-sm">Réinitialiser</a>
                    <% } else { %>
                    <a href="EvenementServlet?action=reservationsevent&id=<%= evenement.getId() %>" class="btn btn-secondary btn-sm">Réinitialiser</a>
                    <% } %>
                  </div>
                </div>
              </form>
            </div>
            
            <!-- Résumé statistique -->
            <div class="row mb-3">
              <div class="col-12">
                <div class="card bg-light">
                  <div class="card-body py-2">
                    <div class="row text-center">
                      <%
                      int totalReservations = reservations != null ? reservations.size() : 0;
                      int pendingReservations = 0;
                      int confirmedReservations = 0;
                      int cancelledReservations = 0;
                      double totalAmount = 0.0;
                      
                      if (reservations != null) {
                          for (ReservationEvenement reservation : reservations) {
                              if ("EN_ATTENTE".equals(reservation.getStatut())) pendingReservations++;
                              else if ("CONFIRMEE".equals(reservation.getStatut())) confirmedReservations++;
                              else if ("ANNULEE".equals(reservation.getStatut())) cancelledReservations++;
                              
                              if (reservation.getMontantTotal() != null && "CONFIRMEE".equals(reservation.getStatut())) {
                                  totalAmount += reservation.getMontantTotal().doubleValue();
                              }
                          }
                      }
                      %>
                      <div class="col-md-3 col-sm-6">
                        <span class="d-block text-muted">Total</span>
                        <span class="h5"><%= totalReservations %></span>
                      </div>
                      <div class="col-md-3 col-sm-6">
                        <span class="d-block text-warning">En attente</span>
                        <span class="h5"><%= pendingReservations %></span>
                      </div>
                      <div class="col-md-3 col-sm-6">
                        <span class="d-block text-success">Confirmées</span>
                        <span class="h5"><%= confirmedReservations %></span>
                      </div>
                      <div class="col-md-3 col-sm-6">
                        <span class="d-block text-danger">Annulées</span>
                        <span class="h5"><%= cancelledReservations %></span>
                      </div>
                    </div>
                    <div class="row mt-2">
                      <div class="col-12 text-center">
                        <span class="summary-total">
                          Revenu total confirmé: 
                          <fmt:setLocale value="fr_HT" />
                          <fmt:formatNumber value="<%= totalAmount %>" type="currency" currencyCode="HTG" />
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Tableau des réservations -->
            <div class="card shadow">
              <div class="card-body">
                <div class="table-responsive">
                  <table class="table table-hover datatables" id="dataTableReservations">
                    <thead class="thead-dark">
                      <tr>
                        <th>ID</th>
                        <% if (isAllReservations) { %>
                        <th>Événement</th>
                        <% } %>
                        <th>Client</th>
                        <th>Tables</th>
                        <th>Montant</th>
                        <th>Paiement</th>
                        <th>Statut</th>
                        <th>Date réservation</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      <%
                      if (reservations != null && !reservations.isEmpty()) {
                          for (ReservationEvenement reservation : reservations) {
                              java.util.Date dateReservation = reservation.getDateReservationAsDate();
                      %>
                      <tr>
                        <td><strong>#<%= reservation.getId() %></strong></td>
                        <% if (isAllReservations) { %>
                        <td>
                          <% if (reservation.getEvenement() != null) { %>
                          <a href="EvenementServlet?action=details&id=<%= reservation.getEvenement().getId() %>" 
                             class="text-primary" title="Voir l'événement">
                            <strong><%= reservation.getEvenement().getTitre() %></strong>
                          </a>
                          <% } else { %>
                          <span class="text-muted">N/A</span>
                          <% } %>
                        </td>
                        <% } %>
                        <td>
                          <% if (reservation.getUtilisateur() != null) { %>
                          <%= reservation.getUtilisateur().getNom() %> <%= reservation.getUtilisateur().getPrenom() %>
                          <br><small class="text-muted"><%= reservation.getUtilisateur().getEmail() %></small>
                          <% } else if (reservation.getNomPersonne() != null && !reservation.getNomPersonne().isEmpty()) { %>
                          <%= reservation.getNomPersonne() %>
                          <br><small class="text-muted">Client externe</small>
                          <% } else { %>
                          <span class="text-muted">Client #<%= reservation.getUtilisateurId() %></span>
                          <% } %>
                        </td>
                        <td>
                          <span class="badge bg-info"><%= reservation.getQuantiteTables() %> table(s)</span>
                          <br><small><%= reservation.getCapaciteTotale() %> personne(s)</small>
                        </td>
                        <td class="amount-cell">
                          <fmt:setLocale value="fr_HT" />
                          <strong><fmt:formatNumber value="<%= reservation.getMontantTotal() %>" type="currency" currencyCode="HTG" /></strong>
                        </td>
                        <td>
                          <span class="payment-method">
                            <%= reservation.getMoyenPaiement() %>
                          </span>
                          <% 
                          if (reservation.getNumeroTransaction() != null && !reservation.getNumeroTransaction().isEmpty()) { 
                              String transfert = reservation.getNumeroTransfert() != null ? reservation.getNumeroTransfert() : "";
                              String transaction = reservation.getNumeroTransaction();
                              String compte = reservation.getNomPersonne() != null ? reservation.getNomPersonne() : "";
                          %>
                          <div class="payment-info mt-1">
                            <% if (!transfert.isEmpty()) { %>
                            <div><strong>No transfert:</strong> <%= transfert %></div>
                            <% } %>
                            <% if (!transaction.isEmpty()) { %>
                            <div><strong>No transaction:</strong> <%= transaction %></div>
                            <% } %>
                            <% if (!compte.isEmpty()) { %>
                            <div><strong>Compte:</strong> <%= compte %></div>
                            <% } %>
                          </div>
                          <% } %>
                        </td>
                        <td>
                          <span class="badge badge-status statut-<%= reservation.getStatut() %>">
                            <%= reservation.getStatut().replace("_", " ") %>
                          </span>
                        </td>
                        <td class="date-cell">
                          <% if (dateReservation != null) { %>
                          <fmt:formatDate value="<%= dateReservation %>" pattern="dd/MM/yyyy HH:mm" />
                          <% } else { %>
                          <span class="text-muted">N/A</span>
                          <% } %>
                        </td>
                        <td class="action-cell">
                          <div class="btn-group btn-group-sm" role="group">
                            <!-- Bouton Voir Détails -->
                            <a href="EvenementServlet?action=detailsreservation&id=<%= reservation.getId() %>" 
                               class="btn btn-info" title="Voir détails">
                              <i class="fe fe-eye"></i>
                            </a>
                            
                            <!-- Bouton Valider (si en attente) -->
                            <% if ("EN_ATTENTE".equals(reservation.getStatut())) { %>
                            <button type="button" class="btn btn-success" title="Valider"
                                    onclick="validerReservation(<%= reservation.getId() %>)">
                              <i class="fe fe-check"></i>
                            </button>
                            <% } %>
                            
                            <!-- Bouton Annuler (si en attente ou confirmée) -->
                            <% if ("EN_ATTENTE".equals(reservation.getStatut()) || "CONFIRMEE".equals(reservation.getStatut())) { %>
                            <button type="button" class="btn btn-danger" title="Annuler"
                                    onclick="annulerReservation(<%= reservation.getId() %>)">
                              <i class="fe fe-x"></i>
                            </button>
                            <% } %>
                            
                            <!-- Bouton Télécharger reçu (si confirmée) -->
                            <% if ("CONFIRMEE".equals(reservation.getStatut())) { %>
                            <a href="EvenementServlet?action=genererrecu&id=<%= reservation.getId() %>" 
                               class="btn btn-warning" title="Télécharger reçu">
                              <i class="fe fe-download"></i>
                            </a>
                            <% } %>
                          </div>
                        </td>
                      </tr>
                      <%
                          }
                      } else {
                      %>
                      <tr>
                        <td colspan="<%= isAllReservations ? 9 : 8 %>" class="text-center text-muted py-5">
                          <i class="fe fe-users fe-48 mb-3"></i>
                          <h5 class="text-muted">Aucune réservation trouvée</h5>
                          <p class="small">Essayez de modifier vos critères de recherche</p>
                        </td>
                      </tr>
                      <%
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
  </div>
</main>

<!-- Modal pour validation de réservation -->
<div class="modal fade" id="validerModal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Valider la réservation</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span>&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Êtes-vous sûr de vouloir valider cette réservation ?</p>
        <div class="form-group">
          <label for="notesValidation">Notes (optionnel)</label>
          <textarea class="form-control" id="notesValidation" rows="3" 
                    placeholder="Notes supplémentaires..."></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
        <button type="button" class="btn btn-success" onclick="confirmValider()">
          <i class="fe fe-check mr-1"></i> Valider
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Modal pour annulation de réservation -->
<div class="modal fade" id="annulerModal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Annuler la réservation</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span>&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Êtes-vous sûr de vouloir annuler cette réservation ?</p>
        <div class="form-group">
          <label for="raisonAnnulation">Raison de l'annulation *</label>
          <textarea class="form-control" id="raisonAnnulation" rows="3" 
                    placeholder="Raison de l'annulation..." required></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
        <button type="button" class="btn btn-danger" onclick="confirmAnnuler()">
          <i class="fe fe-x mr-1"></i> Annuler la réservation
        </button>
      </div>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />

<script>
// Variables pour stocker l'ID de réservation en cours de traitement
let currentReservationId = null;

// Fonction pour valider une réservation
function validerReservation(reservationId) {
    currentReservationId = reservationId;
    $('#validerModal').modal('show');
}

// Fonction pour annuler une réservation
function annulerReservation(reservationId) {
    currentReservationId = reservationId;
    $('#annulerModal').modal('show');
}

// Confirmer la validation
function confirmValider() {
    if (!currentReservationId) return;
    
    const notes = document.getElementById('notesValidation').value;
    
    // Créer un formulaire pour soumettre la demande
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = 'EvenementServlet';
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'validerreservation';
    form.appendChild(actionInput);
    
    const idInput = document.createElement('input');
    idInput.type = 'hidden';
    idInput.name = 'id';
    idInput.value = currentReservationId;
    form.appendChild(idInput);
    
    const notesInput = document.createElement('input');
    notesInput.type = 'hidden';
    notesInput.name = 'notes';
    notesInput.value = notes;
    form.appendChild(notesInput);
    
    document.body.appendChild(form);
    form.submit();
}

// Confirmer l'annulation
function confirmAnnuler() {
    if (!currentReservationId) return;
    
    const raison = document.getElementById('raisonAnnulation').value;
    
    if (!raison.trim()) {
        showToast('error', 'Veuillez indiquer une raison pour l\'annulation.');
        return;
    }
    
    // Créer un formulaire pour soumettre la demande
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = 'EvenementServlet';
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'annulerreservation';
    form.appendChild(actionInput);
    
    const idInput = document.createElement('input');
    idInput.type = 'hidden';
    idInput.name = 'id';
    idInput.value = currentReservationId;
    form.appendChild(idInput);
    
    const raisonInput = document.createElement('input');
    raisonInput.type = 'hidden';
    raisonInput.name = 'raison';
    raisonInput.value = raison;
    form.appendChild(raisonInput);
    
    document.body.appendChild(form);
    form.submit();
}

// Fonction d'export (exemple)
function exportReservations() {
    // Récupérer les filtres actuels
    const statut = document.getElementById('statut').value;
    const moyenPaiement = document.getElementById('moyenPaiement').value;
    const dateDebut = document.getElementById('dateDebut').value;
    const dateFin = document.getElementById('dateFin').value;
    
    let url = 'EvenementServlet?action=exportreservations';
    if (statut) url += '&statut=' + statut;
    if (moyenPaiement) url += '&moyenPaiement=' + moyenPaiement;
    if (dateDebut) url += '&dateDebut=' + dateDebut;
    if (dateFin) url += '&dateFin=' + dateFin;
    
    // Pour une page spécifique à un événement
    <% if (!isAllReservations && evenement != null) { %>
    url += '&id=<%= evenement.getId() %>';
    <% } %>
    
    window.open(url, '_blank');
}

// Initialisation DataTable avec options avancées
$(document).ready(function() {
    const table = $('#dataTableReservations').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json",
            "search": "Rechercher:",
            "lengthMenu": "Afficher _MENU_ entrées",
            "info": "Affichage de _START_ à _END_ sur _TOTAL_ entrées",
            "paginate": {
                "first": "Premier",
                "last": "Dernier",
                "next": "Suivant",
                "previous": "Précédent"
            }
        },
        "pageLength": 25,
        "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Tous"]],
        "order": [[0, "desc"]], // Tri par ID décroissant
        "responsive": true,
        "autoWidth": false,
        "dom": '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rt<"row"<"col-sm-12 col-md-6"i><"col-sm-12 col-md-6"p>>',
        "columnDefs": [
            { 
                "orderable": false, 
                "targets": [<%= isAllReservations ? 8 : 7 %>],
                "className": "text-center"
            },
            { 
                "type": "date-eu", 
                "targets": [<%= isAllReservations ? 7 : 6 %>] 
            },
            { 
                "type": "num-fmt", 
                "targets": [<%= isAllReservations ? 4 : 3 %>] 
            }
        ],
        "initComplete": function() {
            // Ajouter un filtre personnalisé pour les moyens de paiement
            this.api().columns([<%= isAllReservations ? 5 : 4 %>]).every(function() {
                const column = this;
                const select = $('<select class="form-control form-control-sm"><option value="">Tous les paiements</option></select>')
                    .appendTo($(column.header()))
                    .on('change', function() {
                        const val = $.fn.dataTable.util.escapeRegex($(this).val());
                        column.search(val ? '^' + val + '$' : '', true, false).draw();
                    });
                
                column.data().unique().sort().each(function(d) {
                    select.append('<option value="' + d + '">' + d + '</option>');
                });
            });
        }
    });
    
    // Récupérer la valeur de recherche du formulaire
    const recherche = '<%= request.getParameter("recherche") != null ? request.getParameter("recherche") : "" %>';
    if (recherche) {
        table.search(recherche).draw();
    }
    
    // Réinitialiser les modales quand elles sont fermées
    $('#validerModal, #annulerModal').on('hidden.bs.modal', function() {
        currentReservationId = null;
        $('#notesValidation').val('');
        $('#raisonAnnulation').val('');
    });
    
    // Toast notifications
    <%
    String toastMessage = (String) session.getAttribute("ToastAdmErrorNotif");
    String toastType = (String) session.getAttribute("toastType");
    if (toastMessage != null) {
    %>
        showToast('<%= toastType != null ? toastType : "error" %>', '<%= toastMessage.replace("'", "\\'") %>');
    <%
        session.removeAttribute("ToastAdmErrorNotif");
        session.removeAttribute("toastType");
    }
    %>
    
    <%
    String successMessage = (String) session.getAttribute("ToastAdmSuccesNotif");
    if (successMessage != null) {
    %>
        showToast('success', '<%= successMessage.replace("'", "\\'") %>');
    <%
        session.removeAttribute("ToastAdmSuccesNotif");
    }
    %>
});

// Fonction pour afficher les toasts
function showToast(type, message) {
    // Créer le conteneur toast s'il n'existe pas
    if ($('.toast-container').length === 0) {
        $('body').append('<div class="toast-container position-fixed top-0 end-0 p-3"></div>');
    }
    
    const toastId = 'toast-' + Date.now();
    const icon = type === 'success' ? 'fe-check-circle' : 'fe-alert-circle';
    const bgClass = type === 'success' ? 'bg-success' : type === 'warning' ? 'bg-warning' : 'bg-danger';
    
    const toast = $(`
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center">
                    <i class="fe ${icon} fe-16 me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `);
    
    $('.toast-container').append(toast);
    const bsToast = new bootstrap.Toast(toast[0], {
        delay: 5000
    });
    bsToast.show();
    
    // Supprimer après fermeture
    toast.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}
</script>
</body>
</html>