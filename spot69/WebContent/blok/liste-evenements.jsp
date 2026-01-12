<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
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
    .capacity-cell { min-width: 100px; }
    .action-cell { min-width: 220px; }
    .filter-container {
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
    }
    .badge-tables {
        background-color: #17a2b8;
        color: white;
        font-size: 11px;
        padding: 2px 6px;
        border-radius: 10px;
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
                        <th>Capacité</th>
                        <th>Tables</th>
                        <th>Statut</th>
                        <th>Créé le</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      <%
                      String ctx = request.getContextPath();
                      List<Evenement> evenements = (List<Evenement>) request.getAttribute("evenements");
                      if (evenements != null && !evenements.isEmpty()) {
                          for (Evenement event : evenements) {
                              String imageUrl = event.getFullMediaPath();
                              String imagePath;
                              
                              if (imageUrl != null && imageUrl.startsWith("/uploads/evenements/")) {
                                  imagePath = ctx + "/images/evenements/" + imageUrl.substring("uploads/evenements/".length());
                              } else {
                                  imagePath = ctx + "/images/default/default.jpg";
                              }
                              
                      %>
                      <tr>
                        <td>
                          <img src="<%= imagePath %>" alt="<%= event.getTitre() %>" class="event-image">
                        </td>
                        <td><strong><%= event.getTitre() %></strong></td>
                        <td><%= event.getArtisteGroupe() != null ? event.getArtisteGroupe() : "-" %></td>
                        <td class="date-cell">
                          <fmt:formatDate value="<%= event.getDateEventAsDate() %>" pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td class="capacity-cell">
                          <%
                          if (event.getCapaciteTotale() > 0) {
                          %>
                            <span class="badge bg-info">
                              <%= event.getCapaciteTotale() %> places
                            </span>
                          <%
                          } else {
                          %>
                            <span class="text-muted">-</span>
                          <%
                          }
                          %>
                        </td>
                        <td>
                          <%
                          if (event.getTypesTables() != null && !event.getTypesTables().isEmpty()) {
                          %>
                            <span class="badge-tables">
                              <%= event.getTypesTables().size() %> types
                            </span>
                          <%
                          } else {
                          %>
                            <span class="text-muted">Aucune</span>
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
    <div class="dropdown">
        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" 
                id="dropdownMenuButton-<%= event.getId() %>" data-toggle="dropdown" 
                aria-haspopup="true" aria-expanded="false">
            <i class="fe fe-more-vertical fe-12"></i>
        </button>
        <div class="dropdown-menu" aria-labelledby="dropdownMenuButton-<%= event.getId() %>">
            <!-- Voir Détails -->
            <a class="dropdown-item" href="EvenementServlet?action=details&id=<%= event.getId() %>">
                <i class="fe fe-eye fe-12 mr-2"></i> Voir détails
            </a>
           
            <!-- Modifier -->
            <% if (!"DELETED".equals(event.getStatut())) { %>
            <a class="dropdown-item" href="EvenementServlet?action=modifier&id=<%= event.getId() %>">
                <i class="fe fe-edit fe-12 mr-2"></i> Modifier
            </a>
            <% } %>
            
            <!-- Voir Réservations -->
            <a class="dropdown-item" href="EvenementServlet?action=reservationsevent&id=<%= event.getId() %>">
                <i class="fe fe-users fe-12 mr-2"></i> Voir réservations
            </a>
            
            <div class="dropdown-divider"></div>
            
            <!-- Actions de statut -->
            <% if ("VISIBLE".equals(event.getStatut())) { %>
            <a class="dropdown-item text-warning" 
               href="EvenementServlet?action=desactiver&id=<%= event.getId() %>"
               onclick="return confirm('Masquer cet événement ?')">
                <i class="fe fe-eye-off fe-12 mr-2"></i> Masquer
            </a>
            <% } else if ("HIDDEN".equals(event.getStatut())) { %>
            <a class="dropdown-item text-success" 
               href="EvenementServlet?action=reactiver&id=<%= event.getId() %>"
               onclick="return confirm('Réactiver cet événement ?')">
                <i class="fe fe-eye fe-12 mr-2"></i> Rendre visible
            </a>
            <% } %>
            
            <div class="dropdown-divider"></div>
            
            <!-- Supprimer -->
            <% if (!"DELETED".equals(event.getStatut())) { %>
            <a class="dropdown-item text-danger" 
               href="EvenementServlet?action=supprimer&id=<%= event.getId() %>"
               onclick="return confirmSuppression('<%= event.getTitre() %>')">
                <i class="fe fe-trash-2 fe-12 mr-2"></i> Supprimer
            </a>
            <% } %>
            
            <!-- Si déjà supprimé -->
            <% if ("DELETED".equals(event.getStatut())) { %>
            <span class="dropdown-item text-muted disabled">
                <i class="fe fe-trash-2 fe-12 mr-2"></i> Déjà supprimé
            </span>
            <% } %>
        </div>
    </div>
</td></tr>
                      <%
                          }
                      } else {
                      %>
                      <tr>
                        <td colspan="9" class="text-center text-muted py-4">
                          <i class="fe fe-calendar fe-48"></i><br>
                          Aucun événement trouvé
                        </td>
                      </tr>
                      <%
                      }
                      %>
                    </tbody>
                  <%--   <tfoot>
                      <tr>
                        <th colspan="4" style="text-align:right">Total :</th>
                        <th colspan="5">
                          <%
                          int totalEvents = evenements != null ? evenements.size() : 0;
                          int visibleEvents = 0;
                          int hiddenEvents = 0;
                          int totalCapacity = 0;
                          int totalTables = 0;
                          if (evenements != null) {
                              for (Evenement event : evenements) {
                                  if ("VISIBLE".equals(event.getStatut())) visibleEvents++;
                                  else if ("HIDDEN".equals(event.getStatut())) hiddenEvents++;
                                  totalCapacity += event.getCapaciteTotale();
                                  if (event.getTypesTables() != null) {
                                      totalTables += event.getTypesTables().size();
                                  }
                              }
                          }
                          %>
                          <%= totalEvents %> événement(s) | 
                          <span class="text-success"><%= visibleEvents %> visible(s)</span> | 
                          <span class="text-warning"><%= hiddenEvents %> masqué(s)</span><br>
                          Capacité totale: <span class="text-info"><%= totalCapacity %> places</span> | 
                          Tables: <span class="text-primary"><%= totalTables %> types</span>
                        </th>
                      </tr>
                    </tfoot> --%>
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
        <p class="text-danger"><small>Cette action est irréversible. Toutes les tables et réservations associées seront également supprimées.</small></p>
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
            { "orderable": false, "targets": [0, 8] }, // Désactiver tri sur image et actions
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
</script>