<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    import="java.util.List,com.spot69.model.PrivilegeNiveau,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<style>
    .badge-vip {
        background: linear-gradient(45deg, #FFD700, #FFA500);
        color: #000;
        font-weight: bold;
        padding: 0.5rem 0.75rem;
    }
    .badge-gold {
        background: linear-gradient(45deg, #FFD700, #daa520);
        color: #000;
        font-weight: bold;
        padding: 0.5rem 0.75rem;
    }
    .badge-silver {
        background: linear-gradient(45deg, #C0C0C0, #a9a9a9);
        color: #000;
        font-weight: bold;
        padding: 0.5rem 0.75rem;
    }
    .badge-bronze {
        background: linear-gradient(45deg, #CD7F32, #8b4513);
        color: white;
        font-weight: bold;
        padding: 0.5rem 0.75rem;
    }
    
    .color-preview {
        display: inline-block;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        margin-right: 5px;
        vertical-align: middle;
        border: 1px solid #ddd;
    }
    
    .progress-bar-seuil {
        height: 0px;
        border-radius: 0px;
        margin-top: 0px;
    }
    
    .seuil-container {
        min-width: 150px;
    }
    
    .niveau-card {
        transition: all 0.3s;
        border-left: 4px solid transparent;
    }
    
    .niveau-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
</style>

<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canManageNiveaux = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
boolean canDeleteNiveaux = canManageNiveaux && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_PRODUITS);

List<PrivilegeNiveau> niveaux = (List<PrivilegeNiveau>) request.getAttribute("niveaux");
int totalActifs = request.getAttribute("totalActifs") != null ? (Integer) request.getAttribute("totalActifs") : 0;
int totalNiveaux = request.getAttribute("totalNiveaux") != null ? (Integer) request.getAttribute("totalNiveaux") : 0;
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
                                    <i class="fe fe-layers fe-24 align-self-center text-primary"></i>
                                    Configuration des Niveaux de Privilège
                                </h2>
                                <p class="text-muted mb-0">Gérez les seuils de points et avantages pour chaque niveau</p>
                            </div>
                            <div class="d-flex align-items-center">
                                <span class="badge badge-primary badge-pill p-2 mr-3">
                                    <i class="fe fe-layers fe-16 mr-1"></i>
                                    <%= totalActifs %>/<%= totalNiveaux %> Actifs
                                </span>
                                <% if(canManageNiveaux) { %>
                                <a href="PrivilegeNiveauServlet?action=add" class="btn btn-outline-primary">
                                    <i class="fe fe-plus fe-16 mr-2"></i> Nouveau niveau
                                </a>
                                <% } %>
                            </div>
                        </div>
                        
                        <!-- Statistiques -->
                        <div class="row mb-4">
                            <div class="col-xl-3 col-md-6 mb-4">
                                <div class="card border-left-primary shadow h-100 py-2">
                                    <div class="card-body">
                                        <div class="row no-gutters align-items-center">
                                            <div class="col mr-2">
                                                <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                                    Niveaux actifs
                                                </div>
                                                <div class="h5 mb-0 font-weight-bold text-gray-800"><%= totalActifs %></div>
                                            </div>
                                            <div class="col-auto">
                                                <i class="fe fe-check-circle fe-2x text-success"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-xl-3 col-md-6 mb-4">
                                <div class="card border-left-warning shadow h-100 py-2">
                                    <div class="card-body">
                                        <div class="row no-gutters align-items-center">
                                            <div class="col mr-2">
                                                <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                                    Réduction moyenne
                                                </div>
                                                <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                    <%
                                                        double reductionMoyenne = 0;
                                                        if (niveaux != null && !niveaux.isEmpty()) {
                                                            double totalReduction = 0;
                                                            int count = 0;
                                                            for (PrivilegeNiveau niveau : niveaux) {
                                                                if ("ACTIF".equals(niveau.getStatut())) {
                                                                    totalReduction += niveau.getPourcentageReduction().doubleValue();
                                                                    count++;
                                                                }
                                                            }
                                                            if (count > 0) reductionMoyenne = totalReduction / count;
                                                        }
                                                    %>
                                                    <%= String.format("%.1f", reductionMoyenne) %>%
                                                </div>
                                            </div>
                                            <div class="col-auto">
                                                <i class="fe fe-percent fe-2x text-warning"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-xl-3 col-md-6 mb-4">
                                <div class="card border-left-info shadow h-100 py-2">
                                    <div class="card-body">
                                        <div class="row no-gutters align-items-center">
                                            <div class="col mr-2">
                                                <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                                    Points maximum
                                                </div>
                                                <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                    <%
                                                        int pointsMax = 0;
                                                        if (niveaux != null) {
                                                            for (PrivilegeNiveau niveau : niveaux) {
                                                                if (niveau.getSeuilPoints() > pointsMax) {
                                                                    pointsMax = niveau.getSeuilPoints();
                                                                }
                                                            }
                                                        }
                                                    %>
                                                    <%= pointsMax %> pts
                                                </div>
                                            </div>
                                            <div class="col-auto">
                                                <i class="fe fe-dollar-sign fe-2x text-info"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-xl-3 col-md-6 mb-4">
                                <div class="card border-left-success shadow h-100 py-2">
                                    <div class="card-body">
                                        <div class="row no-gutters align-items-center">
                                            <div class="col mr-2">
                                                <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                                    Niveau le plus élevé
                                                </div>
                                                <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                    <%
                                                        String niveauMax = "BRONZE";
                                                        if (niveaux != null && !niveaux.isEmpty()) {
                                                            PrivilegeNiveau maxNiveau = niveaux.get(0);
                                                            for (PrivilegeNiveau niveau : niveaux) {
                                                                if (niveau.getSeuilPoints() > maxNiveau.getSeuilPoints() && 
                                                                    "ACTIF".equals(niveau.getStatut())) {
                                                                    maxNiveau = niveau;
                                                                }
                                                            }
                                                            niveauMax = maxNiveau.getNom();
                                                        }
                                                    %>
                                                    <%= niveauMax %>
                                                </div>
                                            </div>
                                            <div class="col-auto">
                                                <i class="fe fe-award fe-2x text-success"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Table des niveaux -->
                        <div class="card shadow">
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table datatables" id="dataTableNiveaux">
                                        <thead>
                                            <tr>
                                                <th>Niveau</th>
                                                <th>Seuil de points</th>
                                                <th>Réduction</th>
                                                <th>Description</th>
                                                <th>Statut</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (niveaux != null) {
                                                for (PrivilegeNiveau niveau : niveaux) { 
                                            %>
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <!--  <div class="color-preview" style="background-color: <%= niveau.getCouleur() %>"></div>-->
                                                        <div>
                                                            <span class="badge <%= niveau.getBadgeClass() %>">
                                                                <!--  <i class="fe fe-award fe-12 mr-1"></i>-->
                                                                <%= niveau.getNom() %>
                                                            </span>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="seuil-container">
                                                    <div>
                                                        <strong><%= niveau.getSeuilPoints() %> pts</strong>
                                                        <!--  <div class="progress progress-bar-seuil">
                                                            <div class="progress-bar" role="progressbar" 
                                                                 style="width: <%= Math.min(100, (niveau.getSeuilPoints() * 100) / 3000) %>%"
                                                                 aria-valuenow="<%= niveau.getSeuilPoints() %>" 
                                                                 aria-valuemin="0" 
                                                                 aria-valuemax="3000">
                                                            </div>
                                                        </div>-->
                                                    </div>
                                                </td>
                                                <td>
                                                    <!--  <span class="badge bg-success text-white p-2">
                                                        <i class="fe fe-percent fe-12 mr-1"></i>
                                                        <%= niveau.getPourcentageReduction() %>%
                                                    </span>-->
                                                    <span class="badge text-white p-2">
                                                        <%= niveau.getPourcentageReduction() %>%
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="text-truncate" style="max-width: 250px;" 
                                                         title="<%= niveau.getDescription() != null ? niveau.getDescription() : "Aucune description" %>">
                                                        <%= niveau.getDescription() != null && niveau.getDescription().length() > 50 ? 
                                                            niveau.getDescription().substring(0, 50) + "..." : 
                                                            (niveau.getDescription() != null ? niveau.getDescription() : "Aucune description") %>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge <%= "ACTIF".equals(niveau.getStatut()) ? "badge-success" : "badge-secondary" %>">
                                                        <%= niveau.getStatut() %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="dropdown">
                                                        <button class="btn btn-sm btn-secondary dropdown-toggle" type="button" 
                                                                data-toggle="dropdown">
                                                            <i class="fe fe-more-vertical fe-12"></i>
                                                        </button>
                                                        <div class="dropdown-menu dropdown-menu-right">
                                                            <a class="dropdown-item" href="#" 
                                                               onclick="showNiveauDetails('<%= niveau.getId() %>')">
                                                                <i class="fe fe-eye fe-16 mr-1"></i> Détails
                                                            </a>
                                                            <% if(true) { %>
                                                            <a class="dropdown-item" 
                                                               href="PrivilegeNiveauServlet?action=edit&id=<%= niveau.getId() %>">
                                                                <i class="fe fe-edit fe-16 mr-1"></i> Modifier
                                                            </a>
                                                            <div class="dropdown-divider"></div>
                                                            <% if("ACTIF".equals(niveau.getStatut())) { %>
                                                            <form method="POST" action="PrivilegeNiveauServlet" class="dropdown-item p-0">
                                                                <input type="hidden" name="action" value="changer-statut">
                                                                <input type="hidden" name="id" value="<%= niveau.getId() %>">
                                                                <button type="submit" name="statut" value="INACTIF" 
                                                                        class="dropdown-item btn-sm text-warning">
                                                                    <i class="fe fe-pause fe-16 mr-1"></i> Désactiver
                                                                </button>
                                                            </form>
                                                            <% } else { %>
                                                            <form method="POST" action="PrivilegeNiveauServlet" class="dropdown-item p-0">
                                                                <input type="hidden" name="action" value="changer-statut">
                                                                <input type="hidden" name="id" value="<%= niveau.getId() %>">
                                                                <button type="submit" name="statut" value="ACTIF" 
                                                                        class="dropdown-item btn-sm text-success">
                                                                    <i class="fe fe-play fe-16 mr-1"></i> Activer
                                                                </button>
                                                            </form>
                                                            <% } %>
                                                            <% } %>
                                                            
                                                            <% if(true) { %>
                                                            <div class="dropdown-divider"></div>
                                                            <form method="POST" action="PrivilegeNiveauServlet" 
                                                                  onsubmit="return confirm('Confirmer la suppression de ce niveau ?');"
                                                                  class="dropdown-item p-0">
                                                                <input type="hidden" name="action" value="supprimer">
                                                                <input type="hidden" name="id" value="<%= niveau.getId() %>">
                                                                <button type="submit" class="dropdown-item btn-sm text-danger">
                                                                    <i class="fe fe-trash-2 fe-16 mr-1"></i> Supprimer
                                                                </button>
                                                            </form>
                                                            <% } %>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                            <% }
                                            } else { %>
                                            <tr>
                                                <td colspan="6" class="text-center">
                                                    <div class="alert alert-info">
                                                        <i class="fe fe-info fe-16 mr-2"></i>
                                                        Aucun niveau de privilège configuré
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

<!-- Modal Détails Niveau -->
<div class="modal fade" id="detailNiveauModal" tabindex="-1" role="dialog" 
     aria-labelledby="detailNiveauModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="detailNiveauModalLabel">
                    <i class="fe fe-eye fe-16 mr-2"></i>Détails du Niveau
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="detailNiveauContent">
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

<jsp:include page="footer.jsp" />

<!-- DataTable -->
<script>
    $(document).ready(function () {
        // Initialisation DataTable
        $('#dataTableNiveaux').DataTable({
            autoWidth: true,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "Tous"]],
            language: {
                search: "Rechercher:",
                lengthMenu: "Afficher _MENU_ niveaux",
                info: "Affichage de _START_ à _END_ sur _TOTAL_ niveaux",
                paginate: {
                    first: "Premier",
                    last: "Dernier",
                    next: "Suivant",
                    previous: "Précédent"
                }
            },
            order: [[1, 'asc']] // Tri par seuil de points par défaut
        });
    });

    // Fonction pour afficher les détails d'un niveau
    function showNiveauDetails(niveauId) {
        $('#detailNiveauContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">Chargement...</span></div></div>');
        $('#detailNiveauModal').modal('show');
        
        // Charger les détails via AJAX
        $.ajax({
            url: 'PrivilegeNiveauServlet',
            type: 'GET',
            data: { action: 'details', id: niveauId },
            success: function(response) {
                $('#detailNiveauContent').html(response);
            },
            error: function() {
                $('#detailNiveauContent').html('<div class="alert alert-danger">Erreur lors du chargement des détails.</div>');
            }
        });
    }

    // Gestion des messages de notification
    $(document).ready(function() {
        <% 
        String successMsg = (String) session.getAttribute("ToastAdmSuccesNotif");
        String errorMsg = (String) session.getAttribute("ToastAdmErrorNotif");
        
        if(successMsg != null) {
            session.removeAttribute("ToastAdmSuccesNotif");
        %>
            showToast('success', '<%= successMsg %>');
        <% } 
        
        if(errorMsg != null) {
            session.removeAttribute("ToastAdmErrorNotif");
        %>
            showToast('error', '<%= errorMsg %>');
        <% } %>
    });

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

    // Empêcher la propagation des événements sur les boutons du dropdown
    $('.dropdown-item button').click(function(e) {
        e.stopPropagation();
    });
</script>