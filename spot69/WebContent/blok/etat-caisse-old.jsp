<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal,java.text.SimpleDateFormat,com.spot69.dao.*"%>
<meta charset="UTF-8">
<%
// Récupérer les paramètres
Integer selectedCaissiereId = null;
String caissiereIdStr = request.getParameter("caissiereId");
if (caissiereIdStr != null && !caissiereIdStr.isEmpty()) {
    selectedCaissiereId = Integer.parseInt(caissiereIdStr);
}

// DAO pour récupérer les caissiers
UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE)");

// Si aucune caissière sélectionnée, prendre la première de la liste
if (selectedCaissiereId == null && caissieres != null && !caissieres.isEmpty()) {
    selectedCaissiereId = caissieres.get(0).getId();
}

// Récupérer la caisse ouverte du caissier sélectionné
CaisseCaissiere caisse = null;
Integer displayCaissiereId = selectedCaissiereId;

if (displayCaissiereId != null) {
    CaisseCaissiereDAO caisseDAO = new CaisseCaissiereDAO();
    caisse = caisseDAO.getCaisseOuverte(displayCaissiereId);
}

// Variables pour l'affichage
List<TransactionCaisse> transactions = new ArrayList<>();
BigDecimal totalVentes = BigDecimal.ZERO;
BigDecimal totalDepots = BigDecimal.ZERO;
BigDecimal totalRetraits = BigDecimal.ZERO;
BigDecimal soldeTheorique = BigDecimal.ZERO;
BigDecimal difference = BigDecimal.ZERO;

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <!-- Sélecteur de caissier -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <form method="GET" action="CaisseCaissiereServlet" class="row align-items-center" id="caissiereForm">
              <input type="hidden" name="action" value="etatCaisse">
              
              <div class="col-md-8">
                <div class="form-group mb-0">
                  <label for="caissiereSelect" class="form-label">Sélectionner une caissière :</label>
                  <select class="form-control" id="caissiereSelect" name="caissiereId" onchange="this.form.submit()">
                    <%
                    if (caissieres != null && !caissieres.isEmpty()) {
                        for (Utilisateur caissiere : caissieres) {
                            boolean isSelected = (displayCaissiereId != null && displayCaissiereId == caissiere.getId());
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
    <div class="row">
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
                      <div class="font-weight-bold"><%= String.format("%.2f HTG", derniereCaisse.getSoldeFinal()) %></div>
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
      // Caisse trouvée - charger les données
      CaisseCaissiereDAO caisseDAO = new CaisseCaissiereDAO();
      TransactionCaisseDAO transactionDAO = new TransactionCaisseDAO();
      
      soldeTheorique = caisseDAO.calculerSoldeTheorique(caisse.getId());
      transactions = transactionDAO.getTransactionsByCaisse(caisse.getId());
      
      // Calculer les totaux
      for (TransactionCaisse transaction : transactions) {
          if ("VENTE".equals(transaction.getTypeOperation())) {
              totalVentes = totalVentes.add(transaction.getMontant());
          } else if ("DEPOT".equals(transaction.getTypeOperation())) {
              totalDepots = totalDepots.add(transaction.getMontant());
          } else if ("RETRAIT".equals(transaction.getTypeOperation())) {
              totalRetraits = totalRetraits.add(transaction.getMontant().abs());
          }
      }
      
      boolean isOuverte = "OUVERTE".equals(caisse.getStatut());
      if (!isOuverte && caisse.getSoldeFinal() != null) {
          difference = caisse.getSoldeFinal().subtract(soldeTheorique);
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
                <h2 class="h3 mb-1">Caisse #<%= caisse.getId() %></h2>
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
                    SHOT: <%= String.format("%.2f HTG", caisse.getMontantShot()) %>
                  </span>
                  <% } %>
                </p>
              </div>
              <div class="col-auto">
                <div class="text-center">
                  <div class="h2 text-primary">
                    <%= isOuverte ? 
                        String.format("%.2f HTG", soldeTheorique != null ? soldeTheorique : caisse.getSoldeInitial()) : 
                        String.format("%.2f HTG", caisse.getSoldeFinal()) 
                    %>
                  </div>
                  <small class="text-muted">
                    <%= isOuverte ? "Solde théorique actuel" : "Solde final" %>
                  </small>
                </div>
              </div>
            </div>
            
            <!-- Statistiques -->
            <div class="row mt-4">
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-success"><%= String.format("%.2f HTG", totalVentes) %></div>
                  <small>Total ventes</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-info"><%= String.format("%.2f HTG", totalDepots) %></div>
                  <small>Total dépôts</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-danger"><%= String.format("%.2f HTG", totalRetraits) %></div>
                  <small>Total retraits</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-<%= difference != null && difference.compareTo(BigDecimal.ZERO) == 0 ? "success" : "danger" %>">
                    <%= difference != null ? String.format("%.2f HTG", difference.abs()) : "0.00 HTG" %>
                  </div>
                  <small>Écart <%= difference != null && difference.compareTo(BigDecimal.ZERO) >= 0 ? "(+" : "(-" %>)</small>
                </div>
              </div>
            </div>
            
            <!-- Actions -->
            <div class="row mt-4">
              <div class="col-md-12">
                <div class="btn-group">
                  <% if (isOuverte) { %>
                  <a href="#" class="btn btn-success" data-toggle="modal" data-target="#transactionModal">
                    <i class="fe fe-plus"></i> Nouvelle transaction
                  </a>
                  <a href="#" class="btn btn-danger" data-toggle="modal" data-target="#fermetureModal">
                    <i class="fe fe-lock"></i> Fermer la caisse
                  </a>
                  <% } %>
                  <a href="CaisseCaissiereServlet?action=compteClient&caissiereId=<%= caisse.getCaissiereId() %>&caisseId=<%= caisse.getId() %>" 
                     class="btn btn-info">
                    <i class="fe fe-users"></i> Comptes clients
                  </a>
                  <a href="CaisseCaissiereServlet?action=controleRemettre&caisseId=<%= caisse.getId() %>" 
                     class="btn btn-warning">
                    <i class="fe fe-dollar-sign"></i> Contrôle à remettre
                  </a>
                  <a href="CaisseCaissiereServlet?action=printRapport&caisseId=<%= caisse.getId() %>&rapportType=complet" 
                     target="_blank" class="btn btn-secondary">
                    <i class="fe fe-printer"></i> Imprimer rapport
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Détails du solde -->
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Calcul du solde</h5>
          </div>
          <div class="card-body">
            <table class="table table-sm">
              <tr>
                <td>Solde initial:</td>
                <td class="text-right"><%= String.format("%.2f HTG", caisse.getSoldeInitial()) %></td>
              </tr>
              <tr>
                <td class="text-success">+ Total ventes:</td>
                <td class="text-right text-success">+ <%= String.format("%.2f HTG", totalVentes) %></td>
              </tr>
              <tr>
                <td class="text-info">+ Total dépôts:</td>
                <td class="text-right text-info">+ <%= String.format("%.2f HTG", totalDepots) %></td>
              </tr>
              <tr>
                <td class="text-danger">- Total retraits:</td>
                <td class="text-right text-danger">- <%= String.format("%.2f HTG", totalRetraits) %></td>
              </tr>
              <tr class="table-primary">
                <td><strong>Solde théorique:</strong></td>
                <td class="text-right">
                  <strong><%= String.format("%.2f HTG", soldeTheorique != null ? soldeTheorique : caisse.getSoldeInitial()) %></strong>
                </td>
              </tr>
              <% if (!isOuverte) { %>
              <tr>
                <td>Solde réel compté:</td>
                <td class="text-right"><%= String.format("%.2f HTG", caisse.getSoldeFinal()) %></td>
              </tr>
              <tr class="<%= difference != null && difference.compareTo(BigDecimal.ZERO) == 0 ? "table-success" : "table-danger" %>">
                <td><strong>Écart:</strong></td>
                <td class="text-right">
                  <strong>
                    <%= difference != null && difference.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "-" %>
                    <%= String.format("%.2f HTG", difference != null ? difference.abs() : BigDecimal.ZERO) %>
                  </strong>
                </td>
              </tr>
              <% if (caisse.isShot()) { %>
              <tr class="table-danger">
                <td><strong>Shot déclaré:</strong></td>
                <td class="text-right">
                  <strong>- <%= String.format("%.2f HTG", caisse.getMontantShot()) %></strong>
                </td>
              </tr>
              <% } %>
              <% } %>
            </table>
          </div>
        </div>
      </div>
      
      <!-- Dernières transactions -->
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Dernières transactions</h5>
          </div>
          <div class="card-body">
            <% if (transactions != null && !transactions.isEmpty()) { 
              int count = Math.min(transactions.size(), 5);
              for (int i = 0; i < count; i++) {
                TransactionCaisse trans = transactions.get(i);
                String typeClass = trans.getTypeOperation().equals("VENTE") ? "badge-success" : 
                                 trans.getTypeOperation().equals("DEPOT") ? "badge-info" : "badge-danger";
                String montantClass = trans.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? "text-success" : "text-danger";
                String montantPrefix = trans.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            %>
            <div class="d-flex align-items-center mb-3">
              <div class="mr-3">
                <span class="badge <%= typeClass %>">
                  <%= trans.getTypeOperation() %>
                </span>
              </div>
              <div class="flex-fill">
                <small class="text-muted"><%= sdf.format(trans.getDateOperation()) %></small><br>
                <small><%= trans.getNotes() != null ? trans.getNotes() : "" %></small>
              </div>
              <div class="ml-auto">
                <span class="<%= montantClass %>">
                  <strong><%= montantPrefix %><%= String.format("%.2f HTG", trans.getMontant().abs()) %></strong>
                </span>
              </div>
            </div>
            <% if (i < count - 1) { %><hr class="my-2"><% } %>
            <% } 
            } else { %>
            <div class="text-center py-3">
              <i class="fe fe-list fe-24 text-muted"></i>
              <p class="mt-2 mb-0">Aucune transaction</p>
            </div>
            <% } %>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Table des transactions -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header">
            <h5 class="card-title">Toutes les transactions</h5>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Montant</th>
                    <th>Mode paiement</th>
                    <th>Client</th>
                    <th>Commande</th>
                    <th>Notes</th>
                  </tr>
                </thead>
                <tbody>
                  <% if (transactions != null && !transactions.isEmpty()) { 
                    for (TransactionCaisse trans : transactions) { 
                      String typeClass = trans.getTypeOperation().equals("VENTE") ? "badge-success" : 
                                       trans.getTypeOperation().equals("DEPOT") ? "badge-info" : "badge-danger";
                      String montantClass = trans.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? "text-success" : "text-danger";
                      String montantPrefix = trans.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                  %>
                  <tr>
                    <td><%= sdf.format(trans.getDateOperation()) %></td>
                    <td>
                      <span class="badge <%= typeClass %>">
                        <%= trans.getTypeOperation() %>
                      </span>
                    </td>
                    <td class="<%= montantClass %>">
                      <strong><%= montantPrefix %><%= String.format("%.2f HTG", trans.getMontant().abs()) %></strong>
                    </td>
                    <td><%= trans.getModePaiement() != null ? trans.getModePaiement() : "N/A" %></td>
                    <td>
                      <% if (trans.getClient().getNomComplet() != null) { %>
                      <%= trans.getClient().getNomComplet() %>
                      <% } else { %>
                      N/A
                      <% } %>
                    </td>
                    <td>
                      <% if (trans.getCommandeId() != null) { %>
                      <a href="CommandeServlet?action=detail&id=<%= trans.getCommandeId() %>">
                        #<%= trans.getCommandeId() %>
                      </a>
                      <% } else { %>
                      N/A
                      <% } %>
                    </td>
                    <td><small><%= trans.getNotes() != null ? trans.getNotes() : "" %></small></td>
                  </tr>
                  <% } 
                  } else { %>
                  <tr>
                    <td colspan="7" class="text-center py-4">
                      <i class="fe fe-list fe-32 text-muted"></i>
                      <p class="mt-2">Aucune transaction enregistrée</p>
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
            <%= String.format("%.2f HTG", soldeTheorique) %>
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

<jsp:include page="footer.jsp" />