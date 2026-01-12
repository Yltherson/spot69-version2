<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,com.spot69.model.Point,com.spot69.model.Utilisateur"%>
    <meta charset="UTF-8">
<%
List<Point> points = (List<Point>) request.getAttribute("points");
Integer totalPoints = (Integer) request.getAttribute("totalPoints");
Utilisateur targetUser = (Utilisateur) request.getAttribute("targetUser");
String selectedDateDebut = (String) request.getAttribute("selectedDateDebut");
String selectedDateFin = (String) request.getAttribute("selectedDateFin");
Integer userId = (Integer) request.getAttribute("userId");
%>
<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
    <div class="container-fluid">
        <div class="row justify-content-center">
            <div class="col-12">
                <h2 class="h5 page-title">
                    <i class="fe fe-star fe-32 align-self-center text-warning"></i>
                    Mes Points
                </h2>
                
                <!-- Carte des points totaux -->
                <div class="row mb-4">
                    <div class="col-md-4 mb-4">
                        <div class="card shadow bg-gradient-warning text-white">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0"><%= totalPoints != null ? totalPoints : 0 %></span>
                                        <p class="small mb-0">Points totaux disponibles</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-32 fe-star text-white"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4 mb-4">
                        <div class="card shadow">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0"><%= points != null ? points.size() : 0 %></span>
                                        <p class="small text-muted mb-0">Transactions</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-32 fe-list text-muted"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4 mb-4">
                        <div class="card shadow">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <span class="h2 mb-0" id="pointsValides">0</span>
                                        <p class="small text-muted mb-0">Points valides</p>
                                    </div>
                                    <div class="col-auto">
                                        <span class="fe fe-32 fe-check-circle text-success"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filtres -->
                <div class="card shadow mb-4">
                    <div class="card-body">
                        <form method="GET" action="PointServlet" class="row">
                            <input type="hidden" name="action" value="lister">
                            <input type="hidden" name="userId" value="<%= userId %>">
                            
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="dateDebut">Date début</label>
                                    <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                                           value="<%= selectedDateDebut != null ? selectedDateDebut : "" %>">
                                </div>
                            </div>
                            
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="dateFin">Date fin</label>
                                    <input type="date" class="form-control" id="dateFin" name="dateFin" 
                                           value="<%= selectedDateFin != null ? selectedDateFin : "" %>">
                                </div>
                            </div>
                            
                            <div class="col-md-4 d-flex align-items-end">
                                <div class="form-group w-100">
                                    <button type="submit" class="btn btn-primary w-100">Filtrer</button>
                                </div>
                            </div>
                        </form>
                        
                        <!-- Actions rapides -->
                        <div class="row mt-3">
                            <div class="col-md-12">
                                <div class="btn-group" role="group">
                                    <a href="PointServlet?action=utiliserPoints" class="btn btn-outline-primary">
                                        <i class="fe fe-shopping-cart"></i> Utiliser mes points
                                    </a>
                                    <a href="PointServlet?action=statistiques" class="btn btn-outline-info">
                                        <i class="fe fe-pie-chart"></i> Statistiques
                                    </a>
                                    <% if (targetUser != null && (targetUser.getRole().equals("ADMINISTRATEUR") || 
                                          targetUser.getRole().equals("MANAGEUR") || targetUser.getRole().equals("RESPONSABLE"))) { %>
                                    <a href="PointServlet?action=configurations" class="btn btn-outline-warning">
                                        <i class="fe fe-settings"></i> Configurations
                                    </a>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Table des points -->
                <div class="card shadow">
                    <div class="card-body">
                        <% if (points != null && !points.isEmpty()) { %>
                        <div class="table-responsive">
                            <table class="table table-hover" id="pointsTable">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Source</th>
                                        <th>Détails</th>
                                        <th>Points</th>
                                        <th>Statut</th>
                                        <th>Expiration</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Point point : points) { 
                                        String badgeClass = "";
                                        String statutText = "";
                                        
                                        switch(point.getStatut()) {
                                            case "VALIDE":
                                                badgeClass = "badge badge-success";
                                                statutText = "Valide";
                                                break;
                                            case "UTILISE":
                                                badgeClass = "badge badge-secondary";
                                                statutText = "Utilisé";
                                                break;
                                            case "EXPIRE":
                                                badgeClass = "badge badge-danger";
                                                statutText = "Expiré";
                                                break;
                                            default:
                                                badgeClass = "badge badge-light";
                                                statutText = point.getStatut();
                                        }
                                    %>
                                    <tr>
                                        <td>
                                            <small class="text-muted">
                                                <%= point.getDateObtention() != null ? 
                                                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(point.getDateObtention()) : "" %>
                                            </small>
                                        </td>
                                        <td>
                                            <%= point.getSourceType() != null ? point.getSourceType() : "N/A" %>
                                            <% if (point.getCommandeId() != null) { %>
                                            <br><small class="text-muted">Commande #<%= point.getCommandeId() %></small>
                                            <% } %>
                                        </td>
                                        <td>
                                            <%= point.getNotes() != null ? point.getNotes() : "" %>
                                        </td>
                                        <td>
                                            <span class="h6 <%= point.getPointsObtenus() > 0 ? "text-success" : "text-danger" %>">
                                                <%= point.getPointsObtenus() > 0 ? "+" : "" %><%= point.getPointsObtenus() %>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="<%= badgeClass %>"><%= statutText %></span>
                                        </td>
                                        <td>
                                            <% if (point.getDateExpiration() != null && point.getStatut().equals("VALIDE")) { %>
                                            <small class="text-muted">
                                                <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(point.getDateExpiration()) %>
                                            </small>
                                            <% } else { %>
                                            <small class="text-muted">-</small>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                        <% } else { %>
                        <div class="text-center py-5">
                            <div class="mb-3">
                                <i class="fe fe-star fe-64 text-muted"></i>
                            </div>
                            <h4>Aucun point trouvé</h4>
                            <p class="text-muted">Vous n'avez pas encore accumulé de points.</p>
                            <a href="CommandeServlet" class="btn btn-primary">Faire une commande</a>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
$(document).ready(function() {
    // Calcul des points valides
    function calculerPointsValides() {
        let totalValides = 0;
        $('#pointsTable tbody tr').each(function() {
            const statut = $(this).find('.badge').text();
            const pointsText = $(this).find('td:nth-child(4) .h6').text();
            const points = parseInt(pointsText) || 0;
            
            if (statut === "Valide") {
                totalValides += points;
            }
        });
        $('#pointsValides').text(totalValides);
    }
    
    calculerPointsValides();
    
    // DataTable
    if ($('#pointsTable').length) {
        $('#pointsTable').DataTable({
            order: [[0, 'desc']],
            language: {
                url: '//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json'
            }
        });
    }
});
</script>

<jsp:include page="footer.jsp" />