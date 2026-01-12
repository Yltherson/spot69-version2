<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal,java.text.SimpleDateFormat,com.spot69.dao.*,java.util.Date"%>
<meta charset="UTF-8">
<%
// Récupérer les données du servlet
List<CaisseCaissiere> caisses = (List<CaisseCaissiere>) request.getAttribute("caisses");
List<Utilisateur> caissieres = (List<Utilisateur>) request.getAttribute("caissieres");

// Récupérer les paramètres de filtres
String selectedCaissiereIdStr = (String) request.getAttribute("selectedCaissiereId");
String selectedStatut = (String) request.getAttribute("selectedStatut");
String selectedDateDebut = (String) request.getAttribute("selectedDateDebut");
String selectedDateFin = (String) request.getAttribute("selectedDateFin");

Integer selectedCaissiereId = null;
if (selectedCaissiereIdStr != null && !selectedCaissiereIdStr.isEmpty()) {
    selectedCaissiereId = Integer.parseInt(selectedCaissiereIdStr);
}

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat jsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Format pour JavaScript

// Statistiques
int nombreCaisses = caisses != null ? caisses.size() : 0;
int nombreOuvertes = 0;
int nombreFermees = 0;
int nombreShot = 0;
BigDecimal totalSoldeFinal = BigDecimal.ZERO;

if (caisses != null) {
    for (CaisseCaissiere caisse : caisses) {
        if ("OUVERTE".equals(caisse.getStatut())) {
            nombreOuvertes++;
        } else {
            nombreFermees++;
            if (caisse.isShot()) {
                nombreShot++;
            }
            if (caisse.getSoldeFinal() != null) {
                totalSoldeFinal = totalSoldeFinal.add(caisse.getSoldeFinal());
            }
        }
    }
}
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <h2 class="h5 page-title">
          <i class="fe fe-briefcase fe-32 align-self-center text-primary"></i>
          Liste des Caisses
        </h2>
      </div>
    </div>
    
    <!-- Cartes de statistiques -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-primary">
                  <i class="fe fe-briefcase text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Total caisses</p>
                <span class="h3"><%= nombreCaisses %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-success">
                  <i class="fe fe-unlock text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Ouvertes</p>
                <span class="h3"><%= nombreOuvertes %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-warning">
                  <i class="fe fe-lock text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Fermées</p>
                <span class="h3"><%= nombreFermees %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-danger">
                  <i class="fe fe-alert-triangle text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Caisses SHOT</p>
                <span class="h3"><%= nombreShot %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Filtres -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <form method="GET" action="CaisseCaissiereServlet" class="row">
              <input type="hidden" name="action" value="listeCaisses">
              
              <div class="col-md-3">
                <div class="form-group">
                  <label for="caissiereId">Caissière</label>
                  <select class="form-control" id="caissiereId" name="caissiereId">
                    <option value="">Toutes les caissières</option>
                    <%
                    if (caissieres != null && !caissieres.isEmpty()) {
                        for (Utilisateur caissiere : caissieres) {
                            boolean isSelected = (selectedCaissiereId != null && 
                                selectedCaissiereId.equals(caissiere.getId()));
                            String nomComplet = caissiere.getNom() + " " + caissiere.getPrenom();
                    %>
                    <option value="<%= caissiere.getId() %>" <%= isSelected ? "selected" : "" %>>
                      <%= nomComplet %>
                    </option>
                    <%
                        }
                    }
                    %>
                  </select>
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="statut">Statut</label>
                  <select class="form-control" id="statut" name="statut">
                    <option value="" <%= selectedStatut == null || selectedStatut.isEmpty() ? "selected" : "" %>>Tous</option>
                    <option value="OUVERTE" <%= "OUVERTE".equals(selectedStatut) ? "selected" : "" %>>Ouvertes</option>
                    <option value="FERMEE" <%= "FERMEE".equals(selectedStatut) ? "selected" : "" %>>Fermées</option>
                  </select>
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="dateDebut">Date début</label>
                  <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                         value="<%= selectedDateDebut != null ? selectedDateDebut : "" %>">
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="dateFin">Date fin</label>
                  <input type="date" class="form-control" id="dateFin" name="dateFin" 
                         value="<%= selectedDateFin != null ? selectedDateFin : "" %>">
                </div>
              </div>
              
              <div class="col-md-3 d-flex align-items-end">
                <div class="btn-group w-100">
                  <button type="submit" class="btn btn-primary w-50">
                    <i class="fe fe-search"></i> Filtrer
                  </button>
                  <a href="CaisseCaissiereServlet?action=listeCaisses" class="btn btn-secondary w-50">
                    <i class="fe fe-refresh-cw"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Table des caisses -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover" id="tableCaisses">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Caissière</th>
                    <th>Statut</th>
                    <th>Ouverture</th>
                    <th>Fermeture</th>
                    <th>Solde initial</th>
                    <th>Solde final</th>
                    <th>Shot</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <% if (caisses != null && !caisses.isEmpty()) { 
                    for (CaisseCaissiere caisse : caisses) { 
                      String statutClass = "OUVERTE".equals(caisse.getStatut()) ? "badge-success" : "badge-warning";
                      String shotClass = caisse.isShot() ? "badge-danger" : "badge-success";
                      String shotText = caisse.isShot() ? 
                          String.format("SHOT: %.2f HTG", caisse.getMontantShot()) : "OK";
                  %>
                  <tr>
                    <td><%= caisse.getId() %></td>
                    <td>
                      <strong>
                        <%= caisse.getCaissiere().getNom() %> <%= caisse.getCaissiere().getPrenom() %>
                      </strong><br>
                      <small class="text-muted">ID: <%= caisse.getCaissiereId() %></small>
                    </td>
                    <td>
                      <span class="badge <%= statutClass %>">
                        <%= caisse.getStatut() %>
                      </span>
                    </td>
                    <td>
                      <% if (caisse.getOuverture() != null) { %>
                      <i class="fe fe-calendar"></i> <%= sdf.format(caisse.getOuverture()) %>
                      <% } else { %>
                      <span class="text-muted">N/A</span>
                      <% } %>
                    </td>
                    <td>
                      <% if (caisse.getFermeture() != null) { %>
                      <i class="fe fe-calendar"></i> <%= sdf.format(caisse.getFermeture()) %>
                      <% } else { %>
                      <span class="text-muted">---</span>
                      <% } %>
                    </td>
                    <td>
                      <span class="text-primary">
                        <strong><%= String.format("%.2f HTG", caisse.getSoldeInitial()) %></strong>
                      </span>
                    </td>
                    <td>
                      <% if (caisse.getSoldeFinal() != null) { %>
                      <span class="<%= caisse.isShot() ? "text-danger" : "text-success" %>">
                        <strong><%= String.format("%.2f HTG", caisse.getSoldeFinal()) %></strong>
                      </span>
                      <% } else { %>
                      <span class="text-muted">---</span>
                      <% } %>
                    </td>
                    <td>
                      <span class="badge <%= shotClass %>">
                        <%= shotText %>
                      </span>
                    </td>
                    <td>
                      <div class="btn-group" role="group">
                        <!-- Bouton Eye pour voir l'état -->
                        <a href="CaisseCaissiereServlet?action=etatCaisse&caisseId=<%= caisse.getId() %>" 
                           class="btn btn-sm btn-outline-info" title="Voir l'état de la caisse">
                          <i class="fe fe-eye"></i>
                        </a>
                        
                        <!-- Bouton détail (si caisse fermée) -->
                        <% if (!"OUVERTE".equals(caisse.getStatut())) { %>
                        <a href="CaisseCaissiereServlet?action=detailCaisse&caisseId=<%= caisse.getId() %>" 
                           class="btn btn-sm btn-outline-primary" title="Détails">
                          <i class="fe fe-info"></i>
                        </a>
                        <% } %>
                        
                        <!-- Bouton fermer (si caisse ouverte et admin/manageur) -->
                        <% if ("OUVERTE".equals(caisse.getStatut())) { 
                           // Vérifier si l'utilisateur est admin/manageur
                           HttpSession userSession = request.getSession(false);
                           Integer userId = (userSession != null) ? (Integer) userSession.getAttribute("userId") : null;
                           boolean isAdminOrManager = false;
                           
                           if (userId != null) {
                               UtilisateurDAO userDAO = new UtilisateurDAO();
                               Utilisateur user = userDAO.findById(userId);
                               if (user != null) {
                                   String role = user.getRole().getRoleName();
                                   isAdminOrManager = "ADMINISTRATEUR".equals(role) || "MANAGEUR".equals(role);
                               }
                           }
                           
                           if (isAdminOrManager) {
                        %>
                        <button type="button" class="btn btn-sm btn-outline-danger" 
                                title="Fermer la caisse"
                                onclick="fermerCaisseAdmin(<%= caisse.getId() %>)">
                          <i class="fe fe-lock"></i>
                        </button>
                        <% } } %>
                        
                        <!-- Bouton imprimer -->
                        <a href="CaisseCaissiereServlet?action=printRapport&caisseId=<%= caisse.getId() %>&rapportType=simple" 
                           target="_blank" class="btn btn-sm btn-outline-secondary" title="Imprimer rapport">
                          <i class="fe fe-printer"></i>
                        </a>
                      </div>
                    </td>
                  </tr>
                  <% } 
                  } else { %>
                  <tr>
                    <td colspan="9" class="text-center py-5">
                      <i class="fe fe-briefcase fe-64 text-muted"></i>
                      <h4 class="mt-3">Aucune caisse trouvée</h4>
                      <p class="text-muted">Utilisez les filtres pour afficher les caisses.</p>
                    </td>
                  </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
            
            <% if (caisses != null && !caisses.isEmpty()) { %>
            <!-- Informations de synthèse -->
            <div class="row mt-4">
              <div class="col-md-12">
                <div class="alert alert-info">
                  <div class="row">
                    <div class="col-md-4">
                      <i class="fe fe-info"></i>
                      <strong><%= nombreCaisses %></strong> caisses trouvées
                    </div>
                    <div class="col-md-4">
                      <i class="fe fe-dollar-sign"></i>
                      <strong>Total solde final: <%= String.format("%.2f HTG", totalSoldeFinal) %></strong>
                    </div>
                    <div class="col-md-4 text-right">
                      <a href="javascript:window.print()" class="btn btn-sm btn-outline-primary">
                        <i class="fe fe-printer"></i> Imprimer la liste
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<!-- Modal pour fermer une caisse (admin) -->
<div class="modal fade" id="fermetureAdminModal" tabindex="-1" role="dialog">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Fermer la caisse (Admin)</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="CaisseCaissiereServlet" onsubmit="return validateFermetureAdmin()">
        <input type="hidden" name="action" value="fermerCaisse">
        <input type="hidden" id="adminCaisseId" name="caisseId" value="">
        
        <div class="modal-body">
          <div class="alert alert-warning">
            <i class="fe fe-alert-triangle"></i>
            <strong>Attention:</strong> Vous êtes sur le point de fermer une caisse en tant qu'administrateur.
            Assurez-vous d'avoir vérifié l'état de la caisse et d'avoir l'accord de la caissière.
          </div>
          
          <div id="caisseInfo" class="mb-3">
            <!-- Les informations seront chargées via JavaScript -->
          </div>
          
          <div class="form-group">
            <label>Solde réel compté *</label>
            <div class="input-group">
              <input type="number" class="form-control" id="adminSoldeFinal" name="soldeFinal" 
                     required min="0" step="0.01" placeholder="0.00">
              <div class="input-group-append">
                <span class="input-group-text">HTG</span>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <div class="form-check">
              <input type="checkbox" class="form-check-input" id="adminShot" name="shot" 
                     onclick="toggleAdminShotFields()">
              <label class="form-check-label" for="adminShot">
                <strong class="text-danger">Déclarer la caisse comme SHOT (déficit)</strong>
              </label>
            </div>
          </div>
          
          <div id="adminShotFields" style="display: none;">
            <div class="form-group">
              <label for="adminMontantShot">Montant du déficit (HTG) *</label>
              <div class="input-group">
                <input type="number" class="form-control" id="adminMontantShot" name="montantShot" 
                       step="0.01" min="0.01" placeholder="Montant du shot">
                <div class="input-group-append">
                  <span class="input-group-text">HTG</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="adminNotes">Notes (optionnel)</label>
            <textarea class="form-control" id="adminNotes" name="notes" rows="3" 
                      placeholder="Raison de la fermeture, observations..."></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
          <button type="submit" class="btn btn-danger">Confirmer la fermeture</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
// Fonction pour formater une date en français
function formatDateFr(date) {
    if (!date) return 'N/A';
    
    const d = new Date(date);
    const jour = d.getDate().toString().padStart(2, '0');
    const mois = (d.getMonth() + 1).toString().padStart(2, '0');
    const annee = d.getFullYear();
    const heures = d.getHours().toString().padStart(2, '0');
    const minutes = d.getMinutes().toString().padStart(2, '0');
    
    return `${jour}/${mois}/${annee} ${heures}:${minutes}`;
}

// Fonction pour fermer une caisse en tant qu'admin
function fermerCaisseAdmin(caisseId) {
    // Charger les informations de la caisse via AJAX
    fetch('CaisseCaissiereServlet?action=getCaisseOuverteJSON&caisseId=' + caisseId)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.caisse) {
                let caisse = data.caisse;
                let caisseInfoDiv = document.getElementById('caisseInfo');
                
                // Formater la date d'ouverture
                let ouvertureFormatted = formatDateFr(caisse.ouverture);
                
                // Afficher les informations
                caisseInfoDiv.innerHTML = `
                    <div class="card card-body">
                        <h6>Informations de la caisse</h6>
                        <div class="row">
                            <div class="col-md-6">
                                <small class="text-muted">Caissière:</small><br>
                                <strong>${caisse.caissiere.nom} ${caisse.caissiere.prenom}</strong>
                            </div>
                            <div class="col-md-6">
                                <small class="text-muted">Solde initial:</small><br>
                                <strong>${parseFloat(caisse.soldeInitial).toFixed(2)} HTG</strong>
                            </div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-md-6">
                                <small class="text-muted">Ouverte le:</small><br>
                                <strong>${ouvertureFormatted}</strong>
                            </div>
                            <div class="col-md-6">
                                <small class="text-muted">Solde théorique:</small><br>
                                <strong class="text-primary">${parseFloat(data.soldeTheorique || 0).toFixed(2)} HTG</strong>
                            </div>
                        </div>
                    </div>
                `;
                
                // Pré-remplir le solde théorique
                document.getElementById('adminSoldeFinal').value = data.soldeTheorique || caisse.soldeInitial;
                
                // Mettre à jour l'ID de la caisse
                document.getElementById('adminCaisseId').value = caisseId;
                
                // Afficher le modal
                $('#fermetureAdminModal').modal('show');
            } else {
                alert('Impossible de charger les informations de la caisse');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Erreur lors du chargement des informations');
        });
}

function toggleAdminShotFields() {
    var shotChecked = document.getElementById('adminShot').checked;
    var shotFields = document.getElementById('adminShotFields');
    var montantShotInput = document.getElementById('adminMontantShot');
    
    if (shotChecked) {
        shotFields.style.display = 'block';
        montantShotInput.required = true;
    } else {
        shotFields.style.display = 'none';
        montantShotInput.required = false;
        montantShotInput.value = '';
    }
}

function validateFermetureAdmin() {
    var shotChecked = document.getElementById('adminShot').checked;
    var montantShot = document.getElementById('adminMontantShot').value;
    
    if (shotChecked && (!montantShot || parseFloat(montantShot) <= 0)) {
        alert('Si la caisse est SHOT, vous devez spécifier un montant de déficit positif.');
        return false;
    }
    
    return confirm('Êtes-vous sûr de vouloir fermer cette caisse en tant qu\'administrateur ? Cette action est irréversible.');
}

// Initialisation DataTable pour la table
$(document).ready(function() {
    $('#tableCaisses').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json"
        },
        "pageLength": 25,
        "order": [[0, 'desc']] // Trier par ID décroissant
    });
});
</script>

<jsp:include page="footer.jsp" />