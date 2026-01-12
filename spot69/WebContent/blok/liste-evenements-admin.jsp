<%-- <%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Evenement,java.text.SimpleDateFormat,java.util.Date,com.spot69.model.Utilisateur"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<meta charset="UTF-8">

<style>
    .event-image {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 5px;
    }
    .badge-status {
        font-size: 12px;
        padding: 4px 8px;
    }
    .statut-VISIBLE { background-color: #28a745; color: white; }
    .statut-HIDDEN { background-color: #6c757d; color: white; }
    .statut-DELETED { background-color: #dc3545; color: white; }
    .date-cell { min-width: 150px; }
    .price-cell { min-width: 100px; }
    .action-cell { min-width: 200px; }
    .filter-container {
        background: #f8f9fa;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
    }
</style>

<%
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
String today = sdf.format(new Date());
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
                Gestion des événements
              </h2>
              <div class="d-flex flex-column align-items-end">
                <a href="EvenementServlet?action=ajouter" class="btn btn-primary">
                  <i class="fe fe-plus fe-16"></i> Nouvel événement
                </a>
                <a href="EvenementServlet?action=reservations" class="btn btn-info mt-2">
                  <i class="fe fe-list fe-16"></i> Voir les réservations
                </a>
              </div>
            </div>
            
            <!-- Filtres -->
            <div class="filter-container">
              <form method="GET" action="EvenementServlet" class="row g-3">
                <input type="hidden" name="action" value="lister">
                
                <div class="col-md-3">
                  <label for="statut" class="form-label">Statut</label>
                  <select class="form-control" id="statut" name="statut">
                    <option value="">Tous les statuts</option>
                    <option value="VISIBLE" <%= "VISIBLE".equals(request.getParameter("statut")) ? "selected" : "" %>>Actif</option>
                    <option value="HIDDEN" <%= "HIDDEN".equals(request.getParameter("statut")) ? "selected" : "" %>>Masqué</option>
                    <option value="DELETED" <%= "DELETED".equals(request.getParameter("statut")) ? "selected" : "" %>>Supprimé</option>
                  </select>
                </div>
                
                <div class="col-md-3">
                  <label for="dateDebut" class="form-label">Date début</label>
                  <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                         value="<%= request.getParameter("dateDebut") != null ? request.getParameter("dateDebut") : "" %>">
                </div>
                
                <div class="col-md-3">
                  <label for="dateFin" class="form-label">Date fin</label>
                  <input type="date" class="form-control" id="dateFin" name="dateFin"
                         value="<%= request.getParameter("dateFin") != null ? request.getParameter("dateFin") : "" %>">
                </div>
                
                <div class="col-md-3">
                  <label class="form-label">&nbsp;</label>
                  <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">
                      <i class="fe fe-filter fe-16"></i> Filtrer
                    </button>
                    <a href="EvenementServlet?action=lister" class="btn btn-secondary">Réinitialiser</a>
                  </div>
                </div>
              </form>
            </div>
            
            <div class="card shadow">
              <div class="card-body">
                <div class="table-responsive">
                  <table class="table datatables" id="dataTableEvenements">
                    <thead>
                      <tr>
                        <th>Image</th>
                        <th>Titre</th>
                        <th>Artiste/Groupe</th>
                        <th>Date</th>
                        <th>Prix Ticket</th>
                        <th>Statut</th>
                        <th>Créé le</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      <%
                      List<Evenement> evenements = (List<Evenement>) request.getAttribute("evenements");
                      if (evenements != null && !evenements.isEmpty()) {
                          for (Evenement event : evenements) {
                              String imageUrl = event.getFullMediaPath();
                              if (imageUrl == null) {
                                  imageUrl = request.getContextPath() + "/images/default/event-default.png";
                              }
                      %>
                      <tr>
                        <td>
                          <img src="<%= imageUrl %>" alt="<%= event.getTitre() %>" class="event-image">
                        </td>
                        <td><strong><%= event.getTitre() %></strong></td>
                        <td><%= event.getArtisteGroupe() != null ? event.getArtisteGroupe() : "-" %></td>
                        <td class="date-cell">
                          <fmt:formatDate value="<%= event.getDateEventAsDate() %>" pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td class="price-cell">
                          <%
                          if (event.getPrixTicket() != null) {
                          %>
                            <strong><fmt:formatNumber value="<%= event.getPrixTicket() %>" type="currency" currencyCode="HTG" /></strong>
                          <%
                          } else {
                          %>
                            <span class="text-muted">Gratuit</span>
                          <%
                          }
                          %>
                        </td>
                        <td>
                          <span class="badge badge-status statut-<%= event.getStatut() %>">
                            <%= event.getStatut() %>
                          </span>
                        </td>
                        <td class="date-cell">
                          <fmt:formatDate value="<%= event.getCreatedAtAsDate() %>" pattern="dd/MM/yyyy" />
                        </td>
                        <td class="action-cell">
                          <div class="btn-group" role="group">
                            <!-- Bouton Voir Détails -->
                            <a href="EvenementServlet?action=details&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-info" title="Voir détails">
                              <i class="fe fe-eye fe-12"></i>
                            </a>
                            
                            <!-- Bouton Modifier (sauf DELETED) -->
                            <% if (!"DELETED".equals(event.getStatut())) { %>
                            <a href="EvenementServlet?action=modifier&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-primary" title="Modifier">
                              <i class="fe fe-edit fe-12"></i>
                            </a>
                            <% } %>
                            
                            <!-- Boutons Statut -->
                            <% if ("VISIBLE".equals(event.getStatut())) { %>
                            <a href="EvenementServlet?action=desactiver&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-warning" title="Masquer"
                               onclick="return confirm('Masquer cet événement ?')">
                              <i class="fe fe-eye-off fe-12"></i>
                            </a>
                            <% } else if ("HIDDEN".equals(event.getStatut())) { %>
                            <a href="EvenementServlet?action=reactiver&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-success" title="Réactiver"
                               onclick="return confirm('Réactiver cet événement ?')">
                              <i class="fe fe-eye fe-12"></i>
                            </a>
                            <% } %>
                            
                            <!-- Bouton Supprimer (logique) -->
                            <% if (!"DELETED".equals(event.getStatut())) { %>
                            <a href="EvenementServlet?action=supprimer&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-danger" title="Supprimer"
                               onclick="return confirm('Supprimer définitivement cet événement ?')">
                              <i class="fe fe-trash-2 fe-12"></i>
                            </a>
                            <% } %>
                            
                            <!-- Bouton Réservations -->
                            <a href="EvenementServlet?action=reservationsevent&id=<%= event.getId() %>" 
                               class="btn btn-sm btn-secondary" title="Voir réservations">
                              <i class="fe fe-users fe-12"></i>
                            </a>
                          </div>
                        </td>
                      </tr>
                      <%
                          }
                      } else {
                      %>
                      <tr>
                        <td colspan="8" class="text-center text-muted py-4">
                          <i class="fe fe-calendar fe-48"></i><br>
                          Aucun événement trouvé
                        </td>
                      </tr>
                      <%
                      }
                      %>
                    </tbody>
                    <tfoot>
                      <tr>
                        <th colspan="4" style="text-align:right">Total :</th>
                        <th colspan="4">
                          <%
                          int totalEvents = evenements != null ? evenements.size() : 0;
                          int visibleEvents = 0;
                          int hiddenEvents = 0;
                          if (evenements != null) {
                              for (Evenement event : evenements) {
                                  if ("VISIBLE".equals(event.getStatut())) visibleEvents++;
                                  else if ("HIDDEN".equals(event.getStatut())) hiddenEvents++;
                              }
                          }
                          %>
                          <%= totalEvents %> événement(s) | 
                          <span class="text-success"><%= visibleEvents %> visible(s)</span> | 
                          <span class="text-warning"><%= hiddenEvents %> masqué(s)</span>
                        </th>
                      </tr>
                    </tfoot>
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

<!-- Modal confirmation suppression -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Confirmation de suppression</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span>&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Êtes-vous sûr de vouloir supprimer cet événement ?</p>
        <p class="text-danger"><small>Cette action est irréversible. Toutes les réservations associées seront également supprimées.</small></p>
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
$(document).ready(function() {
    // Initialisation DataTable
    $('#dataTableEvenements').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json"
        },
        "pageLength": 25,
        "order": [[3, "desc"]], // Tri par date décroissante
        "responsive": true,
        "columnDefs": [
            { "orderable": false, "targets": [0, 7] }, // Désactiver tri sur image et actions
            { "type": "date", "targets": 3 } // Type date pour tri correct
        ]
    });
    
    // Gestion suppression avec confirmation
    $('.btn-delete-event').click(function(e) {
        e.preventDefault();
        var deleteUrl = $(this).attr('href');
        $('#confirmDeleteBtn').attr('href', deleteUrl);
        $('#confirmDeleteModal').modal('show');
    });
    
    // Toast notifications
    <%
    String toastMessage = (String) session.getAttribute("ToastAdmErrorNotif");
    String toastType = (String) session.getAttribute("toastType");
    if (toastMessage != null) {
    %>
        showToast('<%= toastType %>', '<%= toastMessage %>');
    <%
        // Nettoyer les attributs de session
        session.removeAttribute("ToastAdmErrorNotif");
        session.removeAttribute("toastType");
    }
    %>
});

function showToast(type, message) {
    const toast = $(`
        <div class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `);
    $('.toast-container').append(toast);
    const bsToast = new bootstrap.Toast(toast[0]);
    bsToast.show();
    
    // Supprimer après fermeture
    toast.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}
</script> --%> --%>