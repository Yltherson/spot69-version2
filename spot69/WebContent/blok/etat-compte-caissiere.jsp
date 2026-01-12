<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal,java.text.SimpleDateFormat,com.spot69.dao.*,java.sql.Timestamp"%>
<meta charset="UTF-8">
<%
System.out.println("=== JSP etat-compte-caissiere.jsp DEBUT ===");

// Récupérer les données du servlet
List<Utilisateur> caissieres = (List<Utilisateur>) request.getAttribute("caissieres");
Integer selectedCaissiereId = (Integer) request.getAttribute("selectedCaissiereId");
Utilisateur selectedCaissiere = (Utilisateur) request.getAttribute("selectedCaissiere");
String dateDebut = (String) request.getAttribute("dateDebut");
String dateFin = (String) request.getAttribute("dateFin");
List<Map<String, Object>> tableauEtats = (List<Map<String, Object>>) request.getAttribute("tableauEtats");

// Initialiser si null
if (caissieres == null) caissieres = new ArrayList<>();
if (tableauEtats == null) tableauEtats = new ArrayList<>();

// Formats de date
SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");

System.out.println("=== JSP etat-compte-caissiere.jsp FIN ===");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
   
    <!-- En-tête avec titre et informations -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col">
                <h2 class="h3 mb-1">
                  <i class="fe fe-bar-chart-2 mr-2"></i>État Compte Caissière
                </h2>
                <p class="text-muted mb-0">
                  Tableau récapitulatif des mouvements de caisse par caissière
                </p>
              </div>
              <div class="col-auto">
                <button onclick="window.print()" class="btn btn-outline-primary">
                  <i class="fe fe-printer mr-2"></i>Imprimer
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <% if (caissieres.isEmpty()) { %>
    <!-- Aucune caissière disponible -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body text-center py-5">
            <i class="fe fe-users fe-48 text-muted mb-3"></i>
            <h4 class="text-muted">Aucune caissière disponible</h4>
            <p class="text-muted mb-0">
              Aucune caissière n'est enregistrée dans le système.
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <% } else { %>
    
    <!-- Filtres -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Filtres</h5>
          </div>
          <div class="card-body">
            <form method="GET" action="CaisseCaissiereServlet" class="row">
              <input type="hidden" name="action" value="etatCompteCaissiere">
              
              <!-- Sélection caissière -->
              <div class="col-md-4">
                <div class="form-group">
                  <label for="caissiereId">Caissière</label>
                  <select class="form-control" id="caissiereId" name="caissiereId" required>
                    <option value="">Sélectionnez une caissière</option>
                    <% for (Utilisateur caissiere : caissieres) { 
                        String fullName = caissiere.getNom() + " " + caissiere.getPrenom();
                        boolean selected = selectedCaissiereId != null && caissiere.getId() == selectedCaissiereId;
                    %>
                    <option value="<%= caissiere.getId() %>" <%= selected ? "selected" : "" %>>
                      <%= fullName %>
                    </option>
                    <% } %>
                  </select>
                </div>
              </div>
              
              <!-- Date début -->
              <div class="col-md-3">
                <div class="form-group">
                  <label for="dateDebut">Date début</label>
                  <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                         value="<%= dateDebut != null ? dateDebut : "" %>">
                </div>
              </div>
              
              <!-- Date fin -->
              <div class="col-md-3">
                <div class="form-group">
                  <label for="dateFin">Date fin</label>
                  <input type="date" class="form-control" id="dateFin" name="dateFin" 
                         value="<%= dateFin != null ? dateFin : "" %>">
                </div>
              </div>
              
              <!-- Boutons -->
              <div class="col-md-2">
                <div class="form-group">
                  <label>&nbsp;</label>
                  <div class="d-flex">
                    <button type="submit" class="btn btn-primary mr-2">
                      <i class="fe fe-filter"></i> Appliquer
                    </button>
                    <a href="CaisseCaissiereServlet?action=etatCompteCaissiere" 
                       class="btn btn-secondary">
                      <i class="fe fe-refresh-cw"></i> Tout
                    </a>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Informations de la caissière sélectionnée -->
    <% if (selectedCaissiere != null) { %>
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-auto">
                <span class="avatar avatar-xl bg-primary">
                  <i class="fe fe-user text-white"></i>
                </span>
              </div>
              <div class="col">
                <h3 class="h4 mb-1"><%= selectedCaissiere.getNom() %> <%= selectedCaissiere.getPrenom() %></h3>
                <p class="text-muted mb-0">
                  <i class="fe fe-user"></i> Caissière
                  <% if (selectedCaissiere.getTelephone() != null) { %>
                  | <i class="fe fe-phone"></i> <%= selectedCaissiere.getTelephone() %>
                  <% } %>
                </p>
                <% if (dateDebut != null || dateFin != null) { %>
                <p class="text-muted mb-0">
                  <i class="fe fe-calendar"></i> Période : 
                  <%= dateDebut != null ? dateDebut : "Début" %> 
                  au 
                  <%= dateFin != null ? dateFin : "Fin" %>
                </p>
                <% } %>
              </div>
              <div class="col-auto">
                <!-- Statistiques rapides -->
                <%
                BigDecimal totalSoldeDebut = BigDecimal.ZERO;
                BigDecimal totalEntrees = BigDecimal.ZERO;
                BigDecimal totalSorties = BigDecimal.ZERO;
                BigDecimal totalShots = BigDecimal.ZERO;
                
                for (Map<String, Object> ligne : tableauEtats) {
                    totalSoldeDebut = totalSoldeDebut.add((BigDecimal) ligne.get("soldeDebut"));
                    totalEntrees = totalEntrees.add((BigDecimal) ligne.get("entrees"));
                    totalSorties = totalSorties.add((BigDecimal) ligne.get("sorties"));
                    
                    Boolean shot = (Boolean) ligne.get("shot");
                    BigDecimal montantShot = (BigDecimal) ligne.get("montantShot");
                    if (shot != null && shot && montantShot != null) {
                        totalShots = totalShots.add(montantShot);
                    }
                }
                %>
                <div class="row text-center">
                  <div class="col-6">
                    <div class="h4 text-success"><%= String.format("%.0f HTG", totalEntrees) %></div>
                    <small>Total Entrées</small>
                  </div>
                  <div class="col-6">
                    <div class="h4 text-danger"><%= String.format("%.0f HTG", totalSorties) %></div>
                    <small>Total Sorties</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <% } %>
    
    <!-- Tableau principal -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">
              <i class="fe fe-grid mr-2"></i>Détail Caisse
              <% if (selectedCaissiere != null) { %>
              - <%= selectedCaissiere.getNom() %> <%= selectedCaissiere.getPrenom() %>
              <% } %>
            </h5>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-bordered table-hover" id="tableauEtatCompte">
                <thead class="thead-dark">
                  <tr>
                    <th>Date Ouverture</th>
                    <th>Date Fermeture</th>
                    <th>Libellé</th>
                    <th>Solde Début</th>
                    <th>Entrées</th>
                    <th>Sorties</th>
                    <th>Solde Fin Théorique</th>
                    <th>Solde Fin Réel</th>
                    <th>Shot</th>
                    <th>Versement</th>
                  </tr>
                </thead>
                <tbody>
                  <% 
                  if (tableauEtats != null && !tableauEtats.isEmpty()) {
                    for (Map<String, Object> ligne : tableauEtats) {
                        Timestamp dateOuverture = (Timestamp) ligne.get("dateOuverture");
                        Timestamp dateFermeture = (Timestamp) ligne.get("dateFermeture");
                        String statut = (String) ligne.get("statut");
                        BigDecimal soldeDebut = (BigDecimal) ligne.get("soldeDebut");
                        BigDecimal entrees = (BigDecimal) ligne.get("entrees");
                        BigDecimal sorties = (BigDecimal) ligne.get("sorties");
                        BigDecimal soldeFinTheorique = (BigDecimal) ligne.get("soldeFinTheorique");
                        BigDecimal soldeFinReel = (BigDecimal) ligne.get("soldeFinReel");
                        Boolean shot = (Boolean) ligne.get("shot");
                        BigDecimal montantShot = (BigDecimal) ligne.get("montantShot");
                        BigDecimal montantDonne = (BigDecimal) ligne.get("montantDonne");
                        
                        // Formater les dates
                        String dateOuvertureStr = dateOuverture != null ? 
                            sdfDateTime.format(dateOuverture) : "N/A";
                        String dateFermetureStr = dateFermeture != null ? 
                            sdfDateTime.format(dateFermeture) : "En cours";
                        
                        // Calculer la couleur pour le shot
                        String shotClass = "";
                        String shotText = "";
                        if (shot != null && shot && montantShot != null) {
                            shotClass = "text-danger font-weight-bold";
                            shotText = "-" + String.format("%.0f HTG", montantShot);
                        }
                  %>
                  <tr>
                    <td><%= dateOuvertureStr %></td>
                    <td><%= dateFermetureStr %></td>
                    <td>
                      <strong>Rapport de contrôle</strong><br>
                      <small class="text-muted">
                        <%= dateOuverture != null ? sdfDate.format(dateOuverture) : "" %>
                        <% if (dateFermeture != null) { %>
                        au <%= sdfDate.format(dateFermeture) %>
                        <% } %>
                      </small>
                    </td>
                    <td class="text-primary font-weight-bold">
                      <%= String.format("%.0f HTG", soldeDebut) %>
                    </td>
                    <td class="text-success font-weight-bold">
                      +<%= String.format("%.0f HTG", entrees) %>
                    </td>
                    <td class="text-danger font-weight-bold">
                      -<%= String.format("%.0f HTG", sorties) %>
                    </td>
                    <td class="font-weight-bold">
                      <%= String.format("%.0f HTG", soldeFinTheorique) %>
                    </td>
                    <td>
                      <% if (soldeFinReel != null) { %>
                        <span class="font-weight-bold 
                            <%= soldeFinReel.compareTo(soldeFinTheorique) == 0 ? "text-success" : "text-warning" %>">
                          <%= String.format("%.0f HTG", soldeFinReel) %>
                        </span>
                        <% if (soldeFinReel.compareTo(soldeFinTheorique) != 0) { %>
                        <br>
                        <small class="text-muted">
                          <i class="fe fe-alert-triangle"></i>
                          Diff: <%= String.format("%.0f HTG", soldeFinReel.subtract(soldeFinTheorique).abs()) %>
                        </small>
                        <% } %>
                      <% } else { %>
                        <span class="text-muted">-</span>
                      <% } %>
                    </td>
                    <td class="<%= shotClass %>">
                      <%= shotText %>
                      <% if (shot != null && shot) { %>
                      <br>
                      <small class="badge badge-danger">SHOT</small>
                      <% } %>
                    </td>
                    <td>
                      <% if (montantDonne != null && montantDonne.compareTo(BigDecimal.ZERO) > 0) { %>
                        <span class="text-info font-weight-bold">
                          <%= String.format("%.0f HTG", montantDonne) %>
                        </span>
                        <% if (soldeFinReel != null) { 
                            BigDecimal differenceVersement = montantDonne.subtract(soldeFinReel);
                            if (differenceVersement.compareTo(BigDecimal.ZERO) != 0) {
                        %>
                        <br>
                        <small class="<%= differenceVersement.compareTo(BigDecimal.ZERO) > 0 ? "text-danger" : "text-warning" %>">
                          <i class="fe fe-arrow-<%= differenceVersement.compareTo(BigDecimal.ZERO) > 0 ? "down" : "up" %>"></i>
                          <%= String.format("%.0f HTG", differenceVersement.abs()) %>
                        </small>
                        <% } } %>
                      <% } else { %>
                        <span class="text-muted">-</span>
                      <% } %>
                    </td>
                  </tr>
                  <% 
                    } // fin for
                  } else { 
                    // Aucune donnée
                    boolean hasCaissiere = selectedCaissiere != null;
                    boolean hasDates = dateDebut != null || dateFin != null;
                  %>
                  <tr>
                    <td colspan="10" class="text-center py-5">
                      <i class="fe fe-inbox fe-48 text-muted mb-3"></i>
                      <h5 class="text-muted">
                        <% if (!hasCaissiere) { %>
                        Sélectionnez une caissière
                        <% } else if (hasDates) { %>
                        Aucun rapport trouvé pour la période sélectionnée
                        <% } else { %>
                        Aucun rapport disponible pour cette caissière
                        <% } %>
                      </h5>
                      <p class="text-muted mb-0">
                        <% if (!hasCaissiere) { %>
                        Veuillez sélectionner une caissière dans la liste ci-dessus.
                        <% } else if (hasDates) { %>
                        Essayez d'élargir la période ou vérifiez les dates.
                        <% } else { %>
                        Cette caissière n'a pas encore de rapports de caisse.
                        <% } %>
                      </p>
                    </td>
                  </tr>
                  <% } %>
                </tbody>
                <!-- Pied de tableau avec totaux -->
                <% if (tableauEtats != null && !tableauEtats.isEmpty()) { 
                    BigDecimal totalSoldeDebut = BigDecimal.ZERO;
                    BigDecimal totalEntrees = BigDecimal.ZERO;
                    BigDecimal totalSorties = BigDecimal.ZERO;
                    BigDecimal totalSoldeFinTheorique = BigDecimal.ZERO;
                    BigDecimal totalSoldeFinReel = BigDecimal.ZERO;
                    BigDecimal totalShots = BigDecimal.ZERO;
                    BigDecimal totalVersements = BigDecimal.ZERO;
                    
                    for (Map<String, Object> ligne : tableauEtats) {
                        totalSoldeDebut = totalSoldeDebut.add((BigDecimal) ligne.get("soldeDebut"));
                        totalEntrees = totalEntrees.add((BigDecimal) ligne.get("entrees"));
                        totalSorties = totalSorties.add((BigDecimal) ligne.get("sorties"));
                        totalSoldeFinTheorique = totalSoldeFinTheorique.add((BigDecimal) ligne.get("soldeFinTheorique"));
                        
                        BigDecimal soldeFinReel = (BigDecimal) ligne.get("soldeFinReel");
                        if (soldeFinReel != null) {
                            totalSoldeFinReel = totalSoldeFinReel.add(soldeFinReel);
                        }
                        
                        Boolean shot = (Boolean) ligne.get("shot");
                        BigDecimal montantShot = (BigDecimal) ligne.get("montantShot");
                        if (shot != null && shot && montantShot != null) {
                            totalShots = totalShots.add(montantShot);
                        }
                        
                        BigDecimal montantDonne = (BigDecimal) ligne.get("montantDonne");
                        if (montantDonne != null) {
                            totalVersements = totalVersements.add(montantDonne);
                        }
                    }
                %>
                <tfoot class="thead-light">
                  <tr>
                    <th colspan="3" class="text-right">TOTAUX :</th>
                    <th class="text-primary font-weight-bold">
                      <%= String.format("%.0f HTG", totalSoldeDebut) %>
                    </th>
                    <th class="text-success font-weight-bold">
                      +<%= String.format("%.0f HTG", totalEntrees) %>
                    </th>
                    <th class="text-danger font-weight-bold">
                      -<%= String.format("%.0f HTG", totalSorties) %>
                    </th>
                    <th class="font-weight-bold">
                      <%= String.format("%.0f HTG", totalSoldeFinTheorique) %>
                    </th>
                    <th class="font-weight-bold">
                      <%= String.format("%.0f HTG", totalSoldeFinReel) %>
                    </th>
                    <th class="text-danger font-weight-bold">
                      <% if (totalShots.compareTo(BigDecimal.ZERO) > 0) { %>
                      -<%= String.format("%.0f HTG", totalShots) %>
                      <% } else { %>
                      <span class="text-success">Aucun</span>
                      <% } %>
                    </th>
                    <th class="text-info font-weight-bold">
                      <%= String.format("%.0f HTG", totalVersements) %>
                    </th>
                  </tr>
                </tfoot>
                <% } %>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Légende et explications -->
    <div class="row mt-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title"><i class="fe fe-info mr-2"></i>Légende et Explications</h5>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-md-6">
                <h6>Colonnes :</h6>
                <ul class="list-unstyled">
                  <li><strong>Solde Début :</strong> Montant initial dans la caisse à l'ouverture</li>
                  <li><strong>Entrées :</strong> Total des dépôts (clients + caisse) + ventes payées</li>
                  <li><strong>Sorties :</strong> Retraits + commandes clients non payées</li>
                  <li><strong>Solde Fin Théorique :</strong> Solde Début + Entrées - Sorties</li>
                  <li><strong>Solde Fin Réel :</strong> Montant réellement compté en caisse</li>
                  <li><strong>Shot :</strong> Déficit (en rouge) lorsque le réel < théorique</li>
                  <li><strong>Versement :</strong> Montant donné par la caissière à la fermeture</li>
                </ul>
              </div>
              <div class="col-md-6">
                <h6>Couleurs :</h6>
                <div class="d-flex align-items-center mb-2">
                  <span class="badge badge-success mr-2">Vert</span>
                  <span>Entrées d'argent</span>
                </div>
                <div class="d-flex align-items-center mb-2">
                  <span class="badge badge-danger mr-2">Rouge</span>
                  <span>Sorties d'argent et SHOTS</span>
                </div>
                <div class="d-flex align-items-center mb-2">
                  <span class="badge badge-primary mr-2">Bleu</span>
                  <span>Soldes initiaux et versements</span>
                </div>
                <div class="d-flex align-items-center mb-2">
                  <span class="badge badge-warning mr-2">Orange</span>
                  <span>Écarts entre réel et théorique</span>
                </div>
                <div class="alert alert-info mt-3">
                  <i class="fe fe-alert-circle mr-2"></i>
                  <strong>Note :</strong> Un SHOT indique que la caissière a donné plus d'argent 
                  qu'il n'y en avait réellement en caisse. Les versements supérieurs au solde réel 
                  sont des dettes de la caissière.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <% } // fin du else (si caissières disponibles) %>
    
  </div>
</main>

<jsp:include page="footer.jsp" />

<style>
/* Styles spécifiques pour la page */
#tableauEtatCompte th {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  vertical-align: middle;
  font-size: 0.9rem;
}

#tableauEtatCompte td {
  vertical-align: middle;
  font-size: 0.9rem;
}

#tableauEtatCompte tfoot th {
  background-color: #ecf0f1;
  color: #2c3e50;
  font-size: 1rem;
}

.table-hover tbody tr:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

/* Styles d'impression */
@media print {
  .sidebar, .topbar, .btn, .form-group, .card-header .btn {
    display: none !important;
  }
  
  .card {
    border: none !important;
    box-shadow: none !important;
  }
  
  .card-body {
    padding: 0 !important;
  }
  
  #tableauEtatCompte {
    font-size: 12px !important;
  }
  
  .no-print {
    display: none !important;
  }
  
  h2, h3, h4, h5, h6 {
    page-break-after: avoid;
  }
  
  table {
    page-break-inside: avoid;
  }
}

/* Responsive */
@media (max-width: 768px) {
  #tableauEtatCompte {
    font-size: 0.8rem;
  }
  
  #tableauEtatCompte th,
  #tableauEtatCompte td {
    padding: 0.5rem !important;
  }
}
</style>

<script>
// Script pour améliorer l'expérience utilisateur
document.addEventListener('DOMContentLoaded', function() {
    // Mettre en surbrillance les lignes avec SHOT
    const rows = document.querySelectorAll('#tableauEtatCompte tbody tr');
    rows.forEach(row => {
        const shotCell = row.cells[8]; // Colonne Shot
        if (shotCell && shotCell.textContent.includes('SHOT')) {
            row.classList.add('table-danger');
        }
        
        // Surbrillance au survol
        row.addEventListener('mouseenter', function() {
            if (!this.classList.contains('table-danger')) {
                this.style.backgroundColor = '#f8f9fa';
            }
        });
        
        row.addEventListener('mouseleave', function() {
            if (!this.classList.contains('table-danger')) {
                this.style.backgroundColor = '';
            }
        });
    });
    
    // Auto-sélection de la période par défaut (mois en cours)
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    
    function formatDateForInput(date) {
        return date.toISOString().split('T')[0];
    }
    
    // Remplir automatiquement les dates si vides
    const dateDebutInput = document.getElementById('dateDebut');
    const dateFinInput = document.getElementById('dateFin');
    
    if (dateDebutInput && !dateDebutInput.value) {
        dateDebutInput.value = formatDateForInput(firstDay);
    }
    
    if (dateFinInput && !dateFinInput.value) {
        dateFinInput.value = formatDateForInput(lastDay);
    }
    
    // Validation des dates
    if (dateDebutInput && dateFinInput) {
        dateFinInput.addEventListener('change', function() {
            const debut = new Date(dateDebutInput.value);
            const fin = new Date(dateFinInput.value);
            
            if (fin < debut) {
                alert('La date de fin ne peut pas être antérieure à la date de début.');
                dateFinInput.value = formatDateForInput(debut);
            }
        });
    }
    
    // Exporter en Excel (fonctionnalité optionnelle)
    const exportBtn = document.createElement('button');
    exportBtn.className = 'btn btn-success ml-2 no-print';
    exportBtn.innerHTML = '<i class="fe fe-download mr-2"></i>Excel';
    exportBtn.onclick = exportToExcel;
    
    const printBtn = document.querySelector('.btn-outline-primary');
    if (printBtn) {
        printBtn.parentNode.appendChild(exportBtn);
    }
});

function exportToExcel() {
    // Cette fonction nécessiterait une bibliothèque comme SheetJS pour l'export Excel complet
    // Pour l'instant, on peut faire un export CSV simple
    const table = document.getElementById('tableauEtatCompte');
    const rows = table.querySelectorAll('tr');
    let csv = [];
    
    rows.forEach(row => {
        const rowData = [];
        const cells = row.querySelectorAll('th, td');
        
        cells.forEach(cell => {
            // Nettoyer le texte
            let text = cell.textContent.trim();
            text = text.replace(/\n/g, ' ').replace(/\s+/g, ' ');
            // Encadrer les chaînes avec des guillemets
            rowData.push('"' + text + '"');
        });
        
        csv.push(rowData.join(';'));
    });
    
    const csvContent = "data:text/csv;charset=utf-8," + csv.join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "etat_compte_caissiere_" + new Date().toISOString().split('T')[0] + ".csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
</script>