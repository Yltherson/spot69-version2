<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal,java.text.SimpleDateFormat,com.spot69.dao.*,java.sql.Timestamp"%>
<meta charset="UTF-8">
<%
System.out.println("=== JSP etat-caisse.jsp DEBUT ===");

// Récupérer la caisse depuis les attributs du servlet (PAS depuis la base de données)
CaisseCaissiere caisse = (CaisseCaissiere) request.getAttribute("caisse");

// Récupérer les autres données du servlet
List<TransactionCaisse> transactions = (List<TransactionCaisse>) request.getAttribute("transactions");
BigDecimal totalVentes = (BigDecimal) request.getAttribute("totalVentes");
BigDecimal totalDepots = (BigDecimal) request.getAttribute("totalDepots");
BigDecimal totalRetraits = (BigDecimal) request.getAttribute("totalRetraits");
BigDecimal soldeTheorique = (BigDecimal) request.getAttribute("soldeTheorique");
BigDecimal difference = (BigDecimal) request.getAttribute("difference");

// Initialiser si null
if (transactions == null) transactions = new ArrayList<>();
if (totalVentes == null) totalVentes = BigDecimal.ZERO;
if (totalDepots == null) totalDepots = BigDecimal.ZERO;
if (totalRetraits == null) totalRetraits = BigDecimal.ZERO;
if (soldeTheorique == null) soldeTheorique = BigDecimal.ZERO;
if (difference == null) difference = BigDecimal.ZERO;

// Récupérer les données du servlet
List<Map<String, Object>> depotsClients = (List<Map<String, Object>>) request.getAttribute("depotsClients");
List<Map<String, Object>> commandesClients = (List<Map<String, Object>>) request.getAttribute("commandesClients");
List<Map<String, Object>> commandesServeuses = (List<Map<String, Object>>) request.getAttribute("commandesServeuses");
List<Map<String, Object>> commandesComptoir = (List<Map<String, Object>>) request.getAttribute("commandesComptoir");
BigDecimal totalShots = (BigDecimal) request.getAttribute("totalShots");

// Initialiser si null
if (depotsClients == null) depotsClients = new ArrayList<>();
if (commandesClients == null) commandesClients = new ArrayList<>();
if (commandesServeuses == null) commandesServeuses = new ArrayList<>();
if (commandesComptoir == null) commandesComptoir = new ArrayList<>();
if (totalShots == null) totalShots = BigDecimal.ZERO;

// Log pour debug
System.out.println("JSP DEBUG - caisse: " + (caisse != null ? "exists, ID=" + caisse.getId() : "null"));
System.out.println("JSP DEBUG - commandesComptoir size: " + commandesComptoir.size());
System.out.println("JSP DEBUG - commandesServeuses size: " + commandesServeuses.size());
System.out.println("JSP DEBUG - depotsClients size: " + depotsClients.size());
System.out.println("JSP DEBUG - commandesClients size: " + commandesClients.size());
if (caisse != null) {
    System.out.println("JSP DEBUG - caisse caissiereId: " + caisse.getCaissiereId());
    System.out.println("JSP DEBUG - caisse statut: " + caisse.getStatut());
}

// Récupérer les paramètres
Integer selectedCaissiereId = null;
String caissiereIdStr = request.getParameter("caissiereId");
if (caissiereIdStr != null && !caissiereIdStr.isEmpty()) {
    selectedCaissiereId = Integer.parseInt(caissiereIdStr);
    System.out.println("JSP DEBUG - caissiereId from param: " + selectedCaissiereId);
}

// Si caisse existe mais pas de caissiereId sélectionné, prendre celui de la caisse
if (caisse != null && selectedCaissiereId == null) {
    selectedCaissiereId = caisse.getCaissiereId();
    System.out.println("JSP DEBUG - selectedCaissiereId from caisse: " + selectedCaissiereId);
}

// DAO pour récupérer les caissiers
UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE)");

// Si aucune caissière sélectionnée et pas de caisse, prendre la première de la liste
if (selectedCaissiereId == null && caissieres != null && !caissieres.isEmpty()) {
    selectedCaissiereId = caissieres.get(0).getId();
    System.out.println("JSP DEBUG - selectedCaissiereId from first in list: " + selectedCaissiereId);
}

// NE PAS recharger la caisse ici - elle vient du servlet
Integer displayCaissiereId = selectedCaissiereId;

// Récupérer les paramètres de filtrage de dates
String dateDebutStr = request.getParameter("dateDebut");
String dateFinStr = request.getParameter("dateFin");

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");

System.out.println("=== JSP etat-caisse.jsp FIN ===");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <!-- Sélecteur de caissier -->
    <div class="row mb-4" style="display:none">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <form method="GET" action="CaisseCaissiereServlet" class="row align-items-center" id="caissiereForm">
              <input type="hidden" name="action" value="etatCaisse">
              
              <% if (caisse != null) { %>
              <input type="hidden" name="caisseId" value="<%= caisse.getId() %>">
              <% } %>
              
              <div class="col-md-8">
                <div class="form-group mb-0">
                  <label for="caissiereSelect" class="form-label">Sélectionner une caissière :</label>
                  <select class="form-control" id="caissiereSelect" name="caissiereId" onchange="this.form.submit()">
                    <%
                    if (caissieres != null && !caissieres.isEmpty()) {
                        for (Utilisateur caissiere : caissieres) {
                            // Utiliser la caissière de la caisse si elle existe
                            boolean isSelected = false;
                            if (caisse != null) {
                                isSelected = (caisse.getCaissiereId() == caissiere.getId());
                            } else if (selectedCaissiereId != null) {
                                isSelected = (selectedCaissiereId == caissiere.getId());
                            }
                            
                            String nomComplet = caissiere.getNom() + " " + caissiere.getPrenom();
                            
                            // Vérifier si le caissier a une caisse ouverte
                            CaisseCaissiereDAO caisseCheckDAO = new CaisseCaissiereDAO();
                            CaisseCaissiere caisseCheck = caisseCheckDAO.getCaisseOuverte(caissiere.getId());
                            boolean hasCaisseOuverte = caisseCheck != null;
                            String statusText = hasCaisseOuverte ? " (Caisse ouverte)" : " (Aucune caisse ouverte)";
                            String statusClass = hasCaisseOuverte ? "text-success" : "text-muted";
                    %>
                    <option value="<%= caissiere.getId() %>" <%= isSelected ? "selected" : "" %>>
                      <%= nomComplet %> 
                      <span class="<%= statusClass %>"><%= statusText %></span>
                    </option>
                    <%
                        }
                    } else {
                    %>
                    <option value="">Aucune caissière disponible</option>
                    <%
                    }
                    %>
                  </select>
                </div>
              </div>
              
              <div class="col-md-4">
                <div class="form-group mb-0">
                  <label>&nbsp;</label>
                  <div class="btn-group w-100">
                    <button type="submit" class="btn btn-primary w-50">
                      <i class="fe fe-refresh-cw"></i> Actualiser
                    </button>
                    <% if (caisse == null && displayCaissiereId != null) { %>
                    <a href="CaisseCaissiereServlet?action=formOuvrirCaisse" class="btn btn-success w-50">
                      <i class="fe fe-plus"></i> Ouvrir caisse
                    </a>
                    <% } %>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <% if (caissieres == null || caissieres.isEmpty()) { %>
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
    
    <% } else if (caisse == null) { 
        // Récupérer les informations de la caissière sélectionnée
        Utilisateur selectedCaissiere = null;
        for (Utilisateur caissiere : caissieres) {
            if (caissiere.getId() == displayCaissiereId) {
                selectedCaissiere = caissiere;
                break;
            }
        }
    %>
    <!-- Caissière sélectionnée mais pas de caisse ouverte -->
    <div class="row" >
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-auto">
                <span class="avatar avatar-xl bg-secondary">
                  <i class="fe fe-briefcase text-white"></i>
                </span>
              </div>
              <div class="col">
                <%
                String nomCaissiere = selectedCaissiere != null ? 
                    selectedCaissiere.getNom() + " " + selectedCaissiere.getPrenom() : "Caissier inconnu";
                %>
                <h2 class="h3 mb-1"><%= nomCaissiere %></h2>
                <p class="text-muted mb-0">
                  <i class="fe fe-user"></i> Caissière | 
                  <span class="badge badge-warning">AUCUNE CAISSE OUVERTE</span>
                </p>
              </div>
              <div class="col-auto">
                <a href="CaisseCaissiereServlet?action=formOuvrirCaisse" class="btn btn-success btn-lg">
                  <i class="fe fe-plus"></i> Ouvrir une caisse
                </a>
              </div>
            </div>
            
            <!-- Dernière caisse fermée -->
            <%
            if (selectedCaissiere != null) {
                CaisseCaissiereDAO caisseDAO = new CaisseCaissiereDAO();
                CaisseCaissiere derniereCaisse = caisseDAO.getDerniereCaisseFermee(selectedCaissiere.getId());
                
                if (derniereCaisse != null) {
            %>
            <div class="row mt-4">
              <div class="col-12">
                <div class="card card-body bg-light">
                  <h6 class="mb-3">Dernière caisse fermée</h6>
                  <div class="row">
                    <div class="col-md-3">
                      <small class="text-muted">Date fermeture</small>
                      <div><%= sdf.format(derniereCaisse.getFermeture()) %></div>
                    </div>
                    <div class="col-md-3">
                      <small class="text-muted">Solde final</small>
                      <div class="font-weight-bold"><%= String.format("%.0f HTG", derniereCaisse.getSoldeFinal()) %></div>
                    </div>
                    <div class="col-md-3">
                      <small class="text-muted">Statut</small>
                      <div>
                        <span class="badge badge-<%= derniereCaisse.isShot() ? "danger" : "success" %>">
                          <%= derniereCaisse.isShot() ? "SHOT" : "OK" %>
                        </span>
                      </div>
                    </div>
                    <div class="col-md-3">
                      <small class="text-muted">Actions</small>
                      <div>
                        <a href="CaisseCaissiereServlet?action=etatCaisse&caisseId=<%= derniereCaisse.getId() %>" 
                           class="btn btn-sm btn-outline-primary">
                          Voir détails
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <%
                }
            }
            %>
          </div>
        </div>
      </div>
    </div>
    
    <% } else { 
        // Caisse trouvée (les données viennent du servlet)
        boolean isOuverte = "OUVERTE".equals(caisse.getStatut());
        
        // Convertir les paramètres de date pour les filtres
        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        
        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                Date parsedDate = sdfInput.parse(dateDebutStr);
                dateDebut = new Timestamp(parsedDate.getTime());
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                Date parsedDate = sdfInput.parse(dateFinStr);
                // Ajouter 23h59m59s pour inclure toute la journée
                Calendar cal = Calendar.getInstance();
                cal.setTime(parsedDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                dateFin = new Timestamp(cal.getTime().getTime());
            }
        } catch (Exception e) {
            System.out.println("Erreur parsing dates: " + e.getMessage());
        }
        
        // Mettre à jour le montant des shots dans l'objet caisse
        if (totalShots != null) {
            caisse.setMontantShot(totalShots);
        }
        
        // Log des données reçues
        System.out.println("JSP - Affichage caisse ID: " + caisse.getId());
        System.out.println("JSP - Nombre commandes comptoir: " + commandesComptoir.size());
        if (!commandesComptoir.isEmpty()) {
            for (int i = 0; i < Math.min(commandesComptoir.size(), 3); i++) {
                System.out.println("JSP - Commande " + i + ": " + commandesComptoir.get(i));
            }
        }
    %>
    
    <!-- En-tête de la caisse -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-auto">
                <span class="avatar avatar-xl bg-<%= isOuverte ? "success" : "danger" %>">
                  <i class="fe fe-briefcase text-white"></i>
                </span>
              </div>
              <div class="col">
                <h2 class="h3 mb-1">Caisse | <%= caisse.getCaissiere().getNom() %> <%= caisse.getCaissiere().getPrenom() %> </h2>
                <p class="text-muted mb-0">
                  <i class="fe fe-user"></i> <%= caisse.getCaissiere().getNom() %> <%= caisse.getCaissiere().getPrenom() %> | 
                  <i class="fe fe-calendar"></i> 
                  <% if (isOuverte) { %>
                  Ouverte le: <%= sdf.format(caisse.getOuverture()) %>
                  <% } else { %>
                  Du <%= sdf.format(caisse.getOuverture()) %> au <%= sdf.format(caisse.getFermeture()) %>
                  <% } %>
                </p>
                <p class="text-muted mb-0">
                  Statut: 
                  <span class="badge badge-<%= isOuverte ? "success" : "danger" %>">
                    <%= caisse.getStatut() %>
                  </span>
                  <% if (caisse.isShot()) { %>
                  <span class="badge badge-danger ml-2">
                    SHOT: <%= String.format("%.0f HTG", caisse.getMontantShot()) %>
                  </span>
                  <% } %>
                </p>
              </div>
              <div class="col-auto">
                <div class="text-center">
                  <div class="h2 text-primary">
                    <%= isOuverte ? 
                        String.format("%.0f HTG", soldeTheorique != null ? soldeTheorique : caisse.getSoldeInitial()) : 
                        String.format("%.0f HTG", caisse.getSoldeFinal()) 
                    %>
                  </div>
                  <small class="text-muted">
                    <%= isOuverte ? "Solde théorique actuel" : "Solde final" %>
                  </small>
                </div>
              </div>
            </div>
            
            <!-- Statistiques -->
     <!-- Statistiques -->
<div class="row mt-4">
  <div class="col-md-3 text-center">
    <div class="card card-body bg-light">
      <div class="h4 text-success"><%= String.format("%.0f HTG", totalVentes) %></div>
      <small>Total ventes</small>
    </div>
  </div>
  <div class="col-md-3 text-center">
    <div class="card card-body bg-light">
      <div class="h4 text-info"><%= String.format("%.0f HTG", totalDepots) %></div>
      <small>Total dépôts</small>
    </div>
  </div>
  <!-- Ici Cash correspond a total vente + total depot -total commandeclient -->
  <div class="col-md-3 text-center">
    <div class="card card-body bg-light">
      <% 
        // Calculer le Cash réel : totalVentes + totalDepots - totalCommandesClients
        BigDecimal totalCommandesClients = BigDecimal.ZERO;
        if (commandesClients != null) {
          for (Map<String, Object> commande : commandesClients) {
            BigDecimal montant = (BigDecimal) commande.get("total");
            if (montant != null) {
              totalCommandesClients = totalCommandesClients.add(montant);
            }
          }
        }
        
        // Calcul du cash réel
        BigDecimal cashReel = totalVentes.add(totalDepots).subtract(totalCommandesClients);
      %>
      <div class="h4 text-primary"><%= String.format("%.0f HTG", cashReel) %></div>
      <small>Cash réel</small>
    </div>
  </div>
  <div class="col-md-3 text-center">
    <div class="card card-body bg-light">
      <div class="h4 text-<%= totalShots.compareTo(BigDecimal.ZERO) > 0 ? "danger" : "success" %>">
        <%= String.format("%.0f HTG", totalShots) %>
      </div>
      <small>Montant dernier Rapport</small>
    </div>
  </div>
</div>
            <!-- Actions -->
            <div class="row mt-4">
              <div class="col-md-12">
                <div class="btn-group">
                 <%--  <% if (isOuverte) { %> --%>
                 
                  <a href="#" class="btn btn-danger" data-toggle="modal" data-target="#fermetureModal">
                    <i class="fe fe-lock"></i> Bloquer la caisse
                  </a>
               <%--    <% } %> --%>
                 
                  <a href="CaisseCaissiereServlet?action=printRapport&caisseId=<%= caisse.getId() %>&rapportType=complet" 
                     target="_blank" class="btn btn-secondary">
                    <i class="fe fe-printer"></i> Rapport fermeture de caisse
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Formulaire de filtrage par dates -->
    <div class="row mt-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Filtrage par période</h5>
          </div>
          <div class="card-body">
            <form method="GET" action="CaisseCaissiereServlet" class="row">
              <input type="hidden" name="action" value="etatCaisse">
              <input type="hidden" name="caisseId" value="<%= caisse.getId() %>">
              
              <div class="col-md-3">
                <div class="form-group">
                  <label for="dateDebut">Date de début</label>
                  <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                         value="<%= dateDebutStr != null ? dateDebutStr : "" %>">
                </div>
              </div>
              
              <div class="col-md-3">
                <div class="form-group">
                  <label for="dateFin">Date de fin</label>
                  <input type="date" class="form-control" id="dateFin" name="dateFin" 
                         value="<%= dateFinStr != null ? dateFinStr : "" %>">
                </div>
              </div>
              
              <div class="col-md-3">
                <div class="form-group">
                  <label>&nbsp;</label>
                  <button type="submit" class="btn btn-primary btn-block">
                    <i class="fe fe-filter"></i> Appliquer filtre
                  </button>
                </div>
              </div>
              
              <div class="col-md-3">
                <div class="form-group">
                  <label>&nbsp;</label>
                  <a href="CaisseCaissiereServlet?action=etatCaisse&caisseId=<%= caisse.getId() %>" 
                     class="btn btn-secondary btn-block">
                    <i class="fe fe-refresh-cw"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Section des transactions détaillées -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Rapport caisse</h5>
          </div>
          <div class="card-body">
            
            <!-- SECTION COMPTE CLIENTS -->
            <div class="mb-5">
              <h6 class=" mb-3">
                <i class="fe fe-users mr-2"></i>Compte Clients
              </h6>
              
              <!-- ENTRÉE - Dépôts -->
              <div class="transaction-section mb-4">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('depotsDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-down text-success mr-2"></i>
                    <span>Dépôts clients</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-success font-weight-bold">
                      <% 
                        BigDecimal totalDepotsClients = BigDecimal.ZERO;
                        if (depotsClients != null) {
                          for (Map<String, Object> depot : depotsClients) {
                            totalDepotsClients = totalDepotsClients.add((BigDecimal) depot.get("total"));
                          }
                        }
                      %>
                      <%= String.format("%.0f HTG", totalDepotsClients) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des dépôts -->
                <div id="depotsDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Date</th>
                          <th>Montant</th>
                          <th>Mode Paiement</th>
                          <th>Client</th>
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (depotsClients != null && !depotsClients.isEmpty()) {
                          for (Map<String, Object> depot : depotsClients) {
                            Timestamp dateOp = (Timestamp) depot.get("date_operation");
                            String dateStr = dateOp != null ? sdf.format(dateOp) : "N/A";
                        %>
                        <tr>
                          <td><%= dateStr %></td>
                          <td class="text-success font-weight-bold">
                            +<%= String.format("%.0f HTG", depot.get("total")) %>
                          </td>
                          <td><%= depot.get("mode_paiement") != null ? depot.get("mode_paiement") : "N/A" %></td>
                          <td>
                            <% 
                            String nomClient = "N/A";
                            if (depot.get("nom_complet") != null) {
                              nomClient = (String) depot.get("nom_complet");
                            } else if (depot.get("client_nom") != null && depot.get("client_prenom") != null) {
                              nomClient = depot.get("client_nom") + " " + depot.get("client_prenom");
                            }
                            %>
                            <%= nomClient %>
                          </td>
                        </tr>
                        <% 
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="4" class="text-center text-muted py-3">
                            <i class="fe fe-inbox fe-24"></i>
                            <p class="mt-2 mb-0">Aucun dépôt enregistré</p>
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
              
              <!-- SORTIE - Commandes clients -->
              <div class="transaction-section">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('commandesClientsDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-up text-danger mr-2"></i>
                    <span>Commandes clients</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-danger font-weight-bold">
                      <% 
                        BigDecimal totCommandesClients = BigDecimal.ZERO;
                        if (commandesClients != null) {
                          for (Map<String, Object> commande : commandesClients) {
                            BigDecimal montant = (BigDecimal) commande.get("total");
                            if (montant != null) {
                            	totCommandesClients = totCommandesClients.add(montant);
                            }
                          }
                        }
                      %>
                      <%= String.format("%.0f HTG", totCommandesClients) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des commandes clients -->
                <div id="commandesClientsDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Date</th>
                          <th>Montant</th>
                          <th>Mode Paiement</th>
                          <th>Client</th>
                          <th>Numéro Commande</th>
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (commandesClients != null && !commandesClients.isEmpty()) {
                          for (Map<String, Object> commande : commandesClients) {
                            Timestamp dateCmd = (Timestamp) commande.get("date_commande");
                            String dateStr = dateCmd != null ? sdf.format(dateCmd) : "N/A";
                        %>
                        <tr>
                          <td><%= dateStr %></td>
                          <td class="text-danger font-weight-bold">
                            -<%= String.format("%.0f HTG", commande.get("total")) %>
                          </td>
                          <td><%= commande.get("mode_paiement") != null ? commande.get("mode_paiement") : "N/A" %></td>
                          <td>
                            <% 
                            String nomClient = "N/A";
                            if (commande.get("nom_complet") != null) {
                              nomClient = (String) commande.get("nom_complet");
                            } else if (commande.get("client_nom") != null && commande.get("client_prenom") != null) {
                              nomClient = commande.get("client_nom") + " " + commande.get("client_prenom");
                            }
                            %>
                            <%= nomClient %>
                          </td>
                          <td>
                            <% if (commande.get("numero_commande") != null) { %>
                            <a href="CommandeServlet?action=detail&id=<%= commande.get("commande_id") %>" 
                               class="text-primary" title="Voir détails">
                              #<%= commande.get("numero_commande") %>
                            </a>
                            <% } else { %>
                            N/A
                            <% } %>
                          </td>
                        </tr>
                        <% 
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="5" class="text-center text-muted py-3">
                            <i class="fe fe-shopping-cart fe-24"></i>
                            <p class="mt-2 mb-0">Aucune commande client enregistrée</p>
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
            
            <!-- SECTION SERVEUSE -->
            <div class="mb-5">
              <h6 class=" mb-3">
                <i class="fe fe-user-check mr-2"></i>Serveuses
              </h6>
              
              <!-- ENTRÉE - Commandes payées par serveuse -->
              <div class="transaction-section mb-4">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('entreeServeusesDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-down text-success mr-2"></i>
                    <span>ENTRÉE - Commandes payées par serveuse</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-success font-weight-bold">
                      <% 
                        BigDecimal totalEntreeServeuses = BigDecimal.ZERO;
                        if (commandesServeuses != null) {
                          for (Map<String, Object> serveuse : commandesServeuses) {
                            BigDecimal montantPaye = (BigDecimal) serveuse.get("total_paye");
                            if (montantPaye != null) {
                              totalEntreeServeuses = totalEntreeServeuses.add(montantPaye);
                            }
                          }
                        }
                      %>
                      <%= String.format("%.2f HTG", totalEntreeServeuses) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des commandes payées par serveuse -->
                <div id="entreeServeusesDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Serveuse/Vendeur</th>
                          <th>Nb Commandes</th>
                          <th>Total Payé</th>
                          <th>Dernière Commande</th>
                         <!--  <th>Actions</th> -->
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (commandesServeuses != null && !commandesServeuses.isEmpty()) {
                          for (Map<String, Object> serveuse : commandesServeuses) {
                            String nomServeuse = (String) serveuse.get("nom_complet");
                            if (nomServeuse == null) {
                              nomServeuse = serveuse.get("nom") + " " + serveuse.get("prenom");
                            }
                            Integer nbCommandes = (Integer) serveuse.get("nb_commandes_payees");
                            BigDecimal totalPaye = (BigDecimal) serveuse.get("total_paye");
                            
                            // S'assurer que le total payé n'est pas nul
                            if (totalPaye == null) totalPaye = BigDecimal.ZERO;
                            
                            if (totalPaye.compareTo(BigDecimal.ZERO) > 0) {
                        %>
                        <tr>
                          <td class="serveuse-nom cursor-pointer" 
                              onclick="toggleServeuseDetails(<%= serveuse.get("serveuse_id") %>, 'paye', '<%= nomServeuse %>')">
                            <u><%= nomServeuse %></u>
                          </td>
                          <td><%= nbCommandes != null ? nbCommandes : 0 %></td>
                          <td class="text-success font-weight-bold">
                            +<%= String.format("%.2f HTG", totalPaye) %>
                          </td>
                          <td>
                            <% 
                            Timestamp derniereDate = (Timestamp) serveuse.get("derniere_commande_payee");
                            String dateStr = derniereDate != null ? sdf.format(derniereDate) : "N/A";
                            %>
                            <%= dateStr %>
                          </td>
                         <%--  <td>
                            <button class="btn btn-sm btn-outline-primary" 
                                    onclick="toggleServeuseDetails(<%= serveuse.get("serveuse_id") %>, 'paye', '<%= nomServeuse %>')">
                              <i class="fe fe-eye"></i> Détails
                            </button>
                          </td> --%>
                        </tr>
                        <% 
                            }
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="5" class="text-center text-muted py-3">
                            <i class="fe fe-user-x fe-24"></i>
                            <p class="mt-2 mb-0">Aucune commande payée par serveuse/vendeur</p>
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
              
              <!-- SORTIE - Commandes non payées par serveuse -->
              <div class="transaction-section">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('sortieServeusesDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-up text-danger mr-2"></i>
                    <span>SORTIE - Commandes non payées par serveuse</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-danger font-weight-bold">
                      <% 
                        BigDecimal totalSortieServeuses = BigDecimal.ZERO;
                        if (commandesServeuses != null) {
                          for (Map<String, Object> serveuse : commandesServeuses) {
                            BigDecimal montantNonPaye = (BigDecimal) serveuse.get("total_non_paye");
                            if (montantNonPaye != null) {
                              totalSortieServeuses = totalSortieServeuses.add(montantNonPaye);
                            }
                          }
                        }
                      %>
                      <%= String.format("%.2f HTG", totalSortieServeuses) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des commandes non payées par serveuse -->
                <div id="sortieServeusesDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Serveuse/Vendeur</th>
                          <th>Nb Commandes</th>
                          <th>Total Non Payé</th>
                          <th>Dernière Commande</th>
                         <!--  <th>Actions</th> -->
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (commandesServeuses != null && !commandesServeuses.isEmpty()) {
                          for (Map<String, Object> serveuse : commandesServeuses) {
                            String nomServeuse = (String) serveuse.get("nom_complet");
                            if (nomServeuse == null) {
                              nomServeuse = serveuse.get("nom") + " " + serveuse.get("prenom");
                            }
                            Integer nbCommandes = (Integer) serveuse.get("nb_commandes_non_payees");
                            BigDecimal totalNonPaye = (BigDecimal) serveuse.get("total_non_paye");
                            
                            // S'assurer que le total non payé n'est pas nul
                            if (totalNonPaye == null) totalNonPaye = BigDecimal.ZERO;
                            
                            if (totalNonPaye.compareTo(BigDecimal.ZERO) > 0) {
                        %>
                        <tr>
                          <td class="serveuse-nom cursor-pointer" 
                              onclick="toggleServeuseDetails(<%= serveuse.get("serveuse_id") %>, 'non_paye', '<%= nomServeuse %>')">
                            <u><%= nomServeuse %></u>
                          </td>
                          <td><%= nbCommandes != null ? nbCommandes : 0 %></td>
                          <td class="text-danger font-weight-bold">
                            -<%= String.format("%.2f HTG", totalNonPaye) %>
                          </td>
                          <td>
                            <% 
                            Timestamp derniereDate = (Timestamp) serveuse.get("derniere_commande_non_payee");
                            String dateStr = derniereDate != null ? sdf.format(derniereDate) : "N/A";
                            %>
                            <%= dateStr %>
                          </td>
                          <td>
                            <button class="btn btn-sm btn-outline-danger" 
                                    onclick="toggleServeuseDetails(<%= serveuse.get("serveuse_id") %>, 'non_paye', '<%= nomServeuse %>')">
                              <i class="fe fe-eye"></i> Détails
                            </button>
                          </td>
                        </tr>
                        <% 
                            }
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="5" class="text-center text-muted py-3">
                            <i class="fe fe-user-x fe-24"></i>
                            <p class="mt-2 mb-0">Aucune commande non payée par serveuse/vendeur</p>
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
            
            <!-- SECTION COMPTOIR -->
            <div class="mb-5">
              <h6 class=" mb-3">
                <i class="fe fe-shopping-cart mr-2"></i>Comptoir (Commandes directes caissière)
              </h6>
              
              <!-- ENTRÉE - Commandes payées directes -->
              <div class="transaction-section mb-4">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('entreeComptoirDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-down text-success mr-2"></i>
                    <span>ENTRÉE - Commandes directes payées</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-success font-weight-bold">
                      <% 
                        BigDecimal totalEntreeComptoir = BigDecimal.ZERO;
                        if (commandesComptoir != null) {
                          for (Map<String, Object> commande : commandesComptoir) {
                            if ("PAYE".equals(commande.get("statut_paiement"))) {
                              BigDecimal montant = (BigDecimal) commande.get("montant_total");
                              if (montant != null) {
                                totalEntreeComptoir = totalEntreeComptoir.add(montant);
                              }
                            }
                          }
                        }
                      %>
                      <%= String.format("%.2f HTG", totalEntreeComptoir) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des commandes payées directes -->
                <div id="entreeComptoirDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Numéro Commande</th>
                          <th>Date</th>
                          <th>Montant</th>
                          <th>Client</th>
                          <th>Mode Paiement</th>
                          <!-- <th>Actions</th> -->
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (commandesComptoir != null && !commandesComptoir.isEmpty()) {
                          for (Map<String, Object> commande : commandesComptoir) {
                            if ("PAYE".equals(commande.get("statut_paiement"))) {
                              Timestamp dateCmd = (Timestamp) commande.get("date_commande");
                              String dateStr = dateCmd != null ? sdf.format(dateCmd) : "N/A";
                              String nomClient = commande.get("client_nom") + " " + commande.get("client_prenom");
                        %>
                        <tr>
                          <td>
                            <a href="CommandeServlet?action=detail&id=<%= commande.get("commande_id") %>" 
                               class="text-primary">
                              #<%= commande.get("numero_commande") %>
                            </a>
                          </td>
                          <td><%= dateStr %></td>
                          <td class="text-success font-weight-bold">
                            +<%= String.format("%.2f HTG", commande.get("montant_total")) %>
                          </td>
                          <td><%= nomClient %></td>
                          <td><%= commande.get("mode_paiement") %></td>
                          <%-- <td>
                            <a href="CommandeServlet?action=detail&id=<%= commande.get("commande_id") %>" 
                               class="btn btn-sm btn-outline-primary">
                              <i class="fe fe-eye"></i>
                            </a>
                          </td> --%>
                        </tr>
                        <% 
                            }
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="6" class="text-center text-muted py-3">
                            <i class="fe fe-shopping-cart fe-24"></i>
                            <p class="mt-2 mb-0">Aucune commande directe payée</p>
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
              
              <!-- SORTIE - Commandes non payées directes -->
              <div class="transaction-section">
                <div class="d-flex justify-content-between align-items-center bg-light p-3 rounded cursor-pointer" 
                     onclick="toggleDetails('sortieComptoirDetails')">
                  <div class="d-flex align-items-center">
                    <i class="fe fe-arrow-up text-danger mr-2"></i>
                    <span>SORTIE - Commandes directes non payées</span>
                  </div>
                  <div class="d-flex align-items-center">
                    <span class="font-weight-bold mr-2">Total :</span>
                    <span class="text-danger font-weight-bold">
                      <% 
                        BigDecimal totalSortieComptoir = BigDecimal.ZERO;
                        if (commandesComptoir != null) {
                          for (Map<String, Object> commande : commandesComptoir) {
                            if (!"PAYE".equals(commande.get("statut_paiement"))) {
                              BigDecimal montant = (BigDecimal) commande.get("montant_total");
                              if (montant != null) {
                                totalSortieComptoir = totalSortieComptoir.add(montant);
                              }
                            }
                          }
                        }
                      %>
                      <%= String.format("%.2f HTG", totalSortieComptoir) %>
                    </span>
                    <i class="fe fe-chevron-down ml-2"></i>
                  </div>
                </div>
                
                <!-- Détails des commandes non payées directes -->
                <div id="sortieComptoirDetails" class="details-container" style="display: none;">
                  <div class="table-responsive mt-2">
                    <table class="table table-sm table-bordered">
                      <thead class="">
                        <tr>
                          <th>Numéro Commande</th>
                          <th>Date</th>
                          <th>Montant</th>
                          <th>Client</th>
                          <th>Statut Paiement</th>
                        <!--   <th>Actions</th> -->
                        </tr>
                      </thead>
                      <tbody>
                        <% 
                        if (commandesComptoir != null && !commandesComptoir.isEmpty()) {
                          for (Map<String, Object> commande : commandesComptoir) {
                            if (!"PAYE".equals(commande.get("statut_paiement"))) {
                              Timestamp dateCmd = (Timestamp) commande.get("date_commande");
                              String dateStr = dateCmd != null ? sdf.format(dateCmd) : "N/A";
                              String nomClient = commande.get("client_nom") + " " + commande.get("client_prenom");
                              String statutClass = "PARTIEL".equals(commande.get("statut_paiement")) ? "warning" : "danger";
                        %>
                        <tr>
                          <td>
                            <a href="CommandeServlet?action=detail&id=<%= commande.get("commande_id") %>" 
                               class="text-primary">
                              #<%= commande.get("numero_commande") %>
                            </a>
                          </td>
                          <td><%= dateStr %></td>
                          <td class="text-danger font-weight-bold">
                            -<%= String.format("%.2f HTG", commande.get("montant_total")) %>
                          </td>
                          <td><%= nomClient %></td>
                          <td>
                            <span class="badge badge-<%= statutClass %>">
                              <%= commande.get("statut_paiement") %>
                            </span>
                          </td>
                          <%-- <td>
                            <a href="CommandeServlet?action=detail&id=<%= commande.get("commande_id") %>" 
                               class="btn btn-sm btn-outline-primary">
                              <i class="fe fe-eye"></i>
                            </a>
                          </td> --%>
                        </tr>
                        <% 
                            }
                          }
                        } else {
                        %>
                        <tr>
                          <td colspan="6" class="text-center text-muted py-3">
                            <i class="fe fe-shopping-cart fe-24"></i>
                            <p class="mt-2 mb-0">Aucune commande directe non payée</p>
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
            
            <!-- TOTAL SHOTS -->
            <div class="row mt-4">
              <div class="col-12">
                <div class="card shadow">
                  <div class="card-header">
                    <h5 class="card-title text-danger">
                      <i class="fe fe-alert-triangle mr-2"></i>Total Shots (Déficits)
                    </h5>
                  </div>
                  <div class="card-body">
                    <div class="text-center">
                      <h2 class="text-danger">
                        <%= String.format("%.0f HTG", totalShots) %>
                      </h2>
                      <p class="text-muted mb-0">
                        Montant total des déficits accumulés par cette caissière
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <% } // Fin du if caisse != null %>
    
  </div>
</main>

<!-- Modal nouvelle transaction -->
<% if (caisse != null && "OUVERTE".equals(caisse.getStatut())) { %>
<div class="modal fade" id="transactionModal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Nouvelle transaction</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="CaisseCaissiereServlet">
        <input type="hidden" name="action" value="enregistrerTransaction">
        <input type="hidden" name="caisseId" value="<%= caisse.getId() %>">
        
        <div class="modal-body">
          <div class="form-group">
            <label>Type d'opération *</label>
            <select class="form-control" name="typeOperation" required>
              <option value="">-- Sélectionner --</option>
              <option value="DEPOT">Dépôt</option>
              <option value="RETRAIT">Retrait</option>
              <option value="VENTE">Vente</option>
              <option value="AUTRE">Autre</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Montant *</label>
            <div class="input-group">
              <input type="number" class="form-control" name="montant" 
                     required min="0.01" step="0.01" placeholder="0.00">
              <div class="input-group-append">
                <span class="input-group-text">HTG</span>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>Mode de paiement</label>
            <select class="form-control" name="modePaiement">
              <option value="CASH">Espèces</option>
              <option value="VIREMENT">Virement</option>
              <option value="MONCASH">MonCash</option>
              <option value="NATCASH">NatCash</option>
              <option value="CHEQUE">Chèque</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Notes (optionnel)</label>
            <textarea class="form-control" name="notes" rows="2"></textarea>
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

<!-- Modal fermeture caisse -->
<div class="modal fade" id="fermetureModal" tabindex="-1" role="dialog">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Fermer la caisse</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form method="POST" action="CaisseCaissiereServlet" onsubmit="return validateFermeture()">
        <input type="hidden" name="action" value="fermerCaisse">
        <input type="hidden" name="caisseId" value="<%= caisse.getId() %>">
        
        <div class="modal-body">
          <div class="alert alert-info">
            <i class="fe fe-info"></i>
            <strong>Solde théorique:</strong> 
            <%= String.format("%.0f HTG", soldeTheorique) %>
          </div>
          
          <div class="form-group">
            <label>Solde réel compté *</label>
            <div class="input-group">
              <input type="number" class="form-control" id="soldeFinal" name="soldeFinal" 
                     required min="0" step="0.01" 
                     value="<%= soldeTheorique != null ? soldeTheorique.toString() : "0.00" %>"
                     onchange="calculerEcart()">
              <div class="input-group-append">
                <span class="input-group-text">HTG</span>
              </div>
            </div>
            <small class="form-text text-muted">
              Comptez physiquement l'argent dans la caisse et entrez le montant exact.
            </small>
          </div>
          
          <div id="ecartContainer" style="display: none;">
            <div class="alert" id="ecartAlert">
              <strong>Écart calculé: <span id="ecartValue">0.00</span> HTG</strong>
            </div>
          </div>
          
          <div class="form-group">
            <div class="form-check">
              <input type="checkbox" class="form-check-input" id="shot" name="shot" 
                     onclick="toggleShotFields()">
              <label class="form-check-label" for="shot">
                <strong class="text-danger">Déclarer la caisse comme SHOT (déficit)</strong>
              </label>
            </div>
          </div>
          
          <div id="shotFields" style="display: none;">
            <div class="alert alert-warning">
              <i class="fe fe-alert-triangle"></i>
              Attention ! La caissière aura un déficit à rembourser.
            </div>
            
            <div class="form-group">
              <label for="montantShot">Montant du déficit (HTG) *</label>
              <div class="input-group">
                <input type="number" class="form-control" id="montantShot" name="montantShot" 
                       step="0.01" min="0.01" placeholder="Montant que la caissière doit">
                <div class="input-group-append">
                  <span class="input-group-text">HTG</span>
                </div>
              </div>
              <small class="form-text text-muted">
                Ce montant sera enregistré comme dette de la caissière et devra être remboursé.
              </small>
            </div>
          </div>
          
          <div class="form-group">
            <label for="notes">Notes (optionnel)</label>
            <textarea class="form-control" id="notes" name="notes" rows="3" 
                      placeholder="Raison de l'écart, observations, problèmes rencontrés..."></textarea>
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
function calculerEcart() {
    var soldeTheorique = <%= soldeTheorique != null ? soldeTheorique.toString() : "0" %>;
    var soldeFinal = document.getElementById('soldeFinal').value;
    var ecart = soldeFinal - soldeTheorique;
    
    var ecartContainer = document.getElementById('ecartContainer');
    var ecartAlert = document.getElementById('ecartAlert');
    var ecartValue = document.getElementById('ecartValue');
    
    if (soldeFinal) {
        ecartContainer.style.display = 'block';
        ecartValue.textContent = Math.abs(ecart).toFixed(2);
        
        if (ecart > 0) {
            ecartAlert.className = 'alert alert-success';
            ecartValue.textContent = '+' + Math.abs(ecart).toFixed(2);
        } else if (ecart < 0) {
            ecartAlert.className = 'alert alert-danger';
            ecartValue.textContent = '-' + Math.abs(ecart).toFixed(2);
            
            // Auto-check shot si écart négatif
            var shotCheckbox = document.getElementById('shot');
            var montantShotInput = document.getElementById('montantShot');
            if (Math.abs(ecart) > 0.01) {
                shotCheckbox.checked = true;
                toggleShotFields();
                montantShotInput.value = Math.abs(ecart).toFixed(2);
            }
        } else {
            ecartAlert.className = 'alert alert-info';
            ecartValue.textContent = '0.00';
        }
    }
}

function toggleShotFields() {
    var shotChecked = document.getElementById('shot').checked;
    var shotFields = document.getElementById('shotFields');
    var montantShotInput = document.getElementById('montantShot');
    
    if (shotChecked) {
        shotFields.style.display = 'block';
        montantShotInput.required = true;
    } else {
        shotFields.style.display = 'none';
        montantShotInput.required = false;
        montantShotInput.value = '';
    }
}

function validateFermeture() {
    var shotChecked = document.getElementById('shot').checked;
    var montantShot = document.getElementById('montantShot').value;
    
    if (shotChecked && (!montantShot || parseFloat(montantShot) <= 0)) {
        alert('Si la caisse est SHOT, vous devez spécifier un montant de déficit positif.');
        return false;
    }
    
    return confirm('Êtes-vous sûr de vouloir fermer la caisse ? Cette action est irréversible.');
}
</script>
<% } %>

<!-- Scripts et styles pour les sections détaillées -->
<script>
// Fonction pour afficher/masquer les détails
function toggleDetails(elementId) {
  const element = document.getElementById(elementId);
  if (element.style.display === 'none' || element.style.display === '') {
    element.style.display = 'block';
  } else {
    element.style.display = 'none';
  }
}

// Fonction pour afficher/masquer les détails d'une serveuse avec filtre
function toggleServeuseDetails(serveuseId, filterType, serveuseNom) {
  const detailsRowId = 'serveuseDetails_' + serveuseId + '_' + filterType;
  let detailsRow = document.getElementById(detailsRowId);
  
  // Si l'élément n'existe pas, le créer
  if (!detailsRow) {
    // Déterminer le texte du filtre
    var filterText = '';
    if (filterType === 'paye') {
        filterText = 'Payées';
    } else {
        filterText = 'Non Payées';
    }
    
    // Créer une nouvelle ligne pour les détails
    const row = document.createElement('tr');
    row.id = detailsRowId;
    row.className = 'serveuse-details-row';
    
    // Construire le HTML avec concaténation
    var rowHTML = '<td colspan="5" class="p-0">' +
                  '<div class="p-3 bg-light">' +
                  '<h6 class="mb-3">Détails des commandes - ' + serveuseNom + ' (' + filterText + ')</h6>' +
                  '<div class="table-responsive">' +
                  '<table class="table table-sm table-bordered">' +
                  '<thead>' +
                  '<tr>' +
                  '<th>Numéro Commande</th>' +
                  '<th>Date</th>' +
                  '<th>Montant Total</th>' +
                  '<th>Statut Paiement</th>' +
                  '<th>Mode Paiement</th>' +
                  '</tr>' +
                  '</thead>' +
                  '<tbody id="serveuseCommandes_' + serveuseId + '_' + filterType + '">' +
                  '<tr>' +
                  '<td colspan="5" class="text-center text-muted">' +
                  '<i class="fe fe-loader fe-spin mr-2"></i>' +
                  'Chargement des détails...' +
                  '</td>' +
                  '</tr>' +
                  '</tbody>' +
                  '</table>' +
                  '</div>' +
                  '</div>' +
                  '</td>';
    
    row.innerHTML = rowHTML;
    
    // Trouver la ligne actuelle et insérer après
    const currentRow = document.querySelector('[onclick*="' + serveuseId + '"]').closest('tr');
    currentRow.parentNode.insertBefore(row, currentRow.nextSibling);
    detailsRow = row;
  }
  
  if (detailsRow.style.display === 'none' || detailsRow.style.display === '') {
    // Afficher la ligne des détails
    detailsRow.style.display = 'table-row';
    
    // Charger les détails des commandes via AJAX avec filtre
    loadServeuseCommandesDetails(serveuseId, filterType);
  } else {
    // Masquer la ligne des détails
    detailsRow.style.display = 'none';
  }
}

// Fonction pour charger les détails des commandes d'une serveuse avec filtre
function loadServeuseCommandesDetails(serveuseId, filterType) {
  const tbodyId = 'serveuseCommandes_' + serveuseId + '_' + filterType;
  const tbody = document.getElementById(tbodyId);
  
  if (!tbody) return;
  
  // Montrer l'indicateur de chargement
  tbody.innerHTML = '<tr>' +
                    '<td colspan="5" class="text-center text-muted">' +
                    '<i class="fe fe-loader fe-spin mr-2"></i>' +
                    'Chargement des détails...' +
                    '</td>' +
                    '</tr>';
  
  // Récupérer l'ID de la caisse
  const caisseId = <%= caisse != null ? caisse.getId() : "null" %>;
  
  if (!caisseId) {
    tbody.innerHTML = '<tr>' +
                      '<td colspan="5" class="text-center text-danger">' +
                      '<i class="fe fe-alert-triangle mr-2"></i>' +
                      'Caisse non disponible' +
                      '</td>' +
                      '</tr>';
    return;
  }
  
  // Faire l'appel AJAX avec le filtre
  let url = 'CommandeServlet?action=get-commandes-by-user-json&userId=' + serveuseId + 
            '&caisseId=' + caisseId + '&filterType=' + (filterType || 'all');
  
  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error('Erreur réseau: ' + response.status);
      }
      return response.json();
    })
    .then(data => {
      if (data.success && data.commandes && data.commandes.length > 0) {
        let html = '';
        
        // Filtrer les commandes selon le type
        const filteredCommandes = data.commandes.filter(function(commande) {
          if (filterType === 'paye') {
            return commande.statutPaiement == 'PAYE';
          } else if (filterType === 'non_paye') {
            return commande.statutPaiement != 'PAYE';
          }
          return true;
        });
        
        if (filteredCommandes.length > 0) {
          filteredCommandes.forEach(function(commande) {
            const dateCommande = commande.dateCommande ? 
              new Date(commande.dateCommande).toLocaleDateString('fr-FR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
              }) : 'N/A';
            
            const montant = parseFloat(commande.montantTotal) || 0;
            const isPaye = commande.statutPaiement == 'PAYE';
            
            // Construire chaque ligne avec concaténation
            var ligneHTML = '<tr>' +
                            '<td>' +
                            '<a href="CommandeServlet?action=detail&id=' + commande.id + '" class="text-primary">' +
                            '#' + (commande.numeroCommande || commande.id) +
                            '</a>' +
                            '</td>' +
                            '<td>' + dateCommande + '</td>' +
                            '<td class="' + (isPaye ? 'text-success' : 'text-danger') + ' font-weight-bold">' +
                            (isPaye ? '+' : '-') + montant.toFixed(2) + ' HTG' +
                            '</td>' +
                            '<td>' +
                            '<span class="badge badge-' + (isPaye ? 'success' : 'warning') + '">' +
                            (commande.statutPaiement || 'N/A') +
                            '</span>' +
                            '</td>' +
                            '<td>' + (commande.modePaiement || 'N/A') + '</td>' +
                            '</tr>';
            
            html += ligneHTML;
          });
          
          tbody.innerHTML = html;
        } else {
          // Déterminer le texte du filtre
          var filterText = '';
          if (filterType === 'paye') {
              filterText = 'payée';
          } else {
              filterText = 'non payée';
          }
          
          tbody.innerHTML = '<tr>' +
                            '<td colspan="5" class="text-center text-muted">' +
                            '<i class="fe fe-info mr-2"></i>' +
                            'Aucune commande ' + filterText + ' disponible' +
                            '</td>' +
                            '</tr>';
        }
      } else {
        // Construire le message avec ou sans parenthèse
        var message = '<i class="fe fe-info mr-2"></i>Aucune commande détaillée disponible';
        
        if (filterType) {
            // Déterminer le texte du filtre
            var filterTextParenthese = '';
            if (filterType === 'paye') {
                filterTextParenthese = 'payées';
            } else {
                filterTextParenthese = 'non payées';
            }
            message += ' (' + filterTextParenthese + ')';
        }
        
        tbody.innerHTML = '<tr>' +
                          '<td colspan="5" class="text-center text-muted">' +
                          message +
                          '</td>' +
                          '</tr>';
      }
    })
    .catch(error => {
      console.error('Erreur lors du chargement des détails:', error);
      tbody.innerHTML = '<tr>' +
                        '<td colspan="5" class="text-center text-danger">' +
                        '<i class="fe fe-alert-triangle mr-2"></i>' +
                        'Erreur lors du chargement des données' +
                        '</td>' +
                        '</tr>';
    });
}

// Initialisation: fermer tous les détails au chargement
document.addEventListener('DOMContentLoaded', function() {
  // Fermer tous les détails par défaut
  document.querySelectorAll('.details-container').forEach(function(el) {
    el.style.display = 'none';
  });
  
  // Fermer tous les détails de serveuse par défaut
  document.querySelectorAll('.serveuse-details-row').forEach(function(el) {
    el.style.display = 'none';
  });
});
</script>

<style>
.transaction-section {
/*   border: 1px solid #e9ecef; */
  border-radius: 5px;
  margin-bottom: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.transaction-section .cursor-pointer {
  cursor: pointer;
  transition: background-color 0.2s;
}

/* .transaction-section .cursor-pointer:hover {
  background-color: #f1f3f4;
} */

.details-container {
  /* background-color: #f8f9fa;
  border-top: 1px solid #e9ecef; */
  padding: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.serveuse-nom {
  color: #007bff;
  text-decoration: underline;
}

.serveuse-nom:hover {
  color: #0056b3;
  cursor: pointer;
}

.table-sm th, .table-sm td {
  padding: 0.5rem;
  font-size: 0.875rem;
}

.table-bordered {
  border: 1px solid #dee2e6;
}

/* Styles pour les badges */
.badge-success { background-color: #28a745; }
.badge-warning { background-color: #ffc107; color: #212529; }
.badge-danger { background-color: #dc3545; }
.badge-info { background-color: #17a2b8; }

/* Styles pour les indicateurs de chargement */
.fe-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>

<jsp:include page="footer.jsp" />