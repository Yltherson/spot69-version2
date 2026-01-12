<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal,java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<meta charset="UTF-8">
<%
Map<String, Object> etatCompte = (Map<String, Object>) request.getAttribute("etatCompte");
CompteClient compte = (CompteClient) etatCompte.get("compte");
List<TransactionCompte> transactions = (List<TransactionCompte>) etatCompte.get("transactions");
List<Commande> commandesCredit = (List<Commande>) etatCompte.get("commandesCredit");
BigDecimal totalDepots = (BigDecimal) etatCompte.get("totalDepots");
BigDecimal totalDepenses = (BigDecimal) etatCompte.get("totalDepenses");
BigDecimal soldeCreditTotal = (BigDecimal) etatCompte.get("soldeCreditTotal");

Integer clientId = (Integer) request.getAttribute("clientId");
String selectedDateDebut = (String) request.getAttribute("selectedDateDebut");
String selectedDateFin = (String) request.getAttribute("selectedDateFin");

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<style>
.bg-gold{
 background-color: #daaf5a !important;
}
</style>

<main role="main" class="main-content">
  <div class="container-fluid">
    <!-- En-tête avec infos client -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-auto">
                <span class="avatar avatar-xl">
                  <i class="fe fe-user fe-32"></i>
                </span>
              </div>
              <div class="col">
                <h2 class="h3 mb-1"><%= compte.getClient().getNom() %> <%= compte.getClient().getPrenom() %></h2>
                <p class="text-muted mb-0">
                  <i class="fe fe-mail"></i> <%= compte.getClient().getEmail() %> | 
                  <i class="fe fe-phone"></i> <%= compte.getClient().getTelephone() != null ? compte.getClient().getTelephone() : "Non défini" %>
                </p>
                <p class="text-muted mb-0">Client ID: <%= compte.getClient().getId() %> | Compte ID: <%= compte.getId() %></p>
              </div>
              <div class="col-auto">
                <div class="text-center">
                  <div class="h2 <%= compte.getSolde().compareTo(BigDecimal.ZERO) >= 0 ? "text-success" : "text-danger" %>">
                    <%= String.format("%.2f HTG", compte.getSolde()) %>
                  </div>
                  <small class="text-muted">Solde actuel</small>
                </div>
              </div>
            </div>
            
            <div class="row mt-4">
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-primary"><%= String.format("%.2f HTG", totalDepots != null ? totalDepots : BigDecimal.ZERO) %></div>
                  <small>Total dépôts</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-danger"><%= String.format("%.2f HTG", totalDepenses != null ? totalDepenses : BigDecimal.ZERO) %></div>
                  <small>Total dépenses</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-warning"><%= String.format("%.2f HTG", soldeCreditTotal != null ? soldeCreditTotal : BigDecimal.ZERO) %></div>
                  <small>Crédits impayés</small>
                </div>
              </div>
              <div class="col-md-3 text-center">
                <div class="card card-body bg-light">
                  <div class="h4 text-info"><%= transactions != null ? transactions.size() : 0 %></div>
                  <small>Transactions totales</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Section Crédit -->
<div class="row mb-4">
  <div class="col-md-12">
    <div class="card shadow">
      <div class="card-header">
        <h4 class="card-title">
          <i class="fe fe-credit-card text-primary"></i>
          Informations de crédit
        </h4>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-3 text-center">
            <div class="card card-body <%= compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0 ? "bg-gold text-white" : "bg-light" %>">
              <div class="h3"><%= String.format("%.2f HTG", compte.getLimiteCredit()) %></div>
              <small>Limite de crédit</small>
              <small class="d-block mt-1"><i class="fe fe-info"></i> 
                Peut acheter jusqu'à <%= String.format("%.2f HTG", compte.getLimiteCredit()) %> avec solde 0
              </small>
            </div>
          </div>
          
          <% 
          BigDecimal creditUtilise = BigDecimal.ZERO;
          if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
              creditUtilise = compte.getSolde().abs();
          }
          
          BigDecimal soldeDisponible = compte.getSoldeDisponible();
          %>
          
          <div class="col-md-3 text-center">
            <div class="card card-body bg-light">
              <div class="h3 <%= creditUtilise.compareTo(BigDecimal.ZERO) > 0 ? "text-danger" : "text-success" %>">
                <%= String.format("%.2f HTG", creditUtilise) %>
              </div>
              <small>Crédit utilisé</small>
              <% if (creditUtilise.compareTo(BigDecimal.ZERO) > 0) { %>
              <small class="d-block mt-1 text-danger">
                <i class="fe fe-alert-triangle"></i> Le client utilise du crédit
              </small>
              <% } %>
            </div>
          </div>
          
          <div class="col-md-3 text-center">
            <div class="card card-body <%= soldeDisponible.compareTo(BigDecimal.ZERO) >= 0 ? "bg-success text-white" : "bg-danger text-white" %>">
              <div class="h3"><%= String.format("%.2f HTG", soldeDisponible) %></div>
              <small>Solde disponible</small>
              <small class="d-block mt-1">
                <i class="fe fe-check-circle"></i> Peut encore acheter
              </small>
            </div>
          </div>
          
          <div class="col-md-3 text-center">
            <div class="card card-body ">
              <div class="h3">-<%= String.format("%.2f HTG", compte.getLimiteCredit()) %></div>
              <small>Solde minimum</small>
              <small class="d-block mt-1 text-muted">
                <i class="fe fe-alert-circle"></i> Ne peut pas descendre en dessous
              </small>
            </div>
          </div>
        </div>
        
        <!-- Barre de progression -->
        <% if (compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0) { 
            BigDecimal pourcentage = BigDecimal.ZERO;
            if (compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0) {
                pourcentage = creditUtilise.multiply(new BigDecimal(100))
                                         .divide(compte.getLimiteCredit(), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            String progressClass = "bg-success";
            if (pourcentage.compareTo(new BigDecimal(80)) > 0) {
                progressClass = "bg-danger";
            } else if (pourcentage.compareTo(new BigDecimal(50)) > 0) {
                progressClass = "bg-warning";
            }
        %>
        <div class="mt-4">
          <p class="mb-1">Utilisation du crédit: <strong><%= String.format("%.0f", pourcentage) %>%</strong></p>
          <div class="progress" style="height: 10px;">
            <div class="progress-bar <%= progressClass %>" 
                 role="progressbar" 
                 style="width: <%= pourcentage.min(new BigDecimal(100)) %>%;"
                 aria-valuenow="<%= pourcentage %>" 
                 aria-valuemin="0" 
                 aria-valuemax="100"></div>
          </div>
          <div class="d-flex justify-content-between mt-1">
            <small>0%</small>
            <small>50%</small>
            <small>100%</small>
          </div>
        </div>
        <% } %>
         <a href="CompteClientServlet?action=formDepot&clientId=<%= clientId %>" 
                 class="btn btn-primary mt-4" >
                <i class="fe fe-plus-circle"></i> Effectuer un dépôt
              </a>
      </div>
    </div>
  </div>
</div>
    
    <!-- Actions rapides -->
   <%-- <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="btn-group">
              <a href="CompteClientServlet?action=formDepot&clientId=<%= clientId %>" 
                 class="btn btn-primary">
                <i class="fe fe-plus-circle"></i> Effectuer un dépôt
              </a>
               <a href="CompteClientServlet?action=effectuerRetraitForm&clientId=<%= clientId %>" 
                 class="btn btn-danger">
                <i class="fe fe-minus-circle"></i> Effectuer un retrait
              </a> 
              <a href="CompteClientServlet?action=ajusterSoldeForm&compteId=<%= compte.getId() %>" 
                 class="btn btn-warning">
                <i class="fe fe-edit"></i> Ajuster le solde
              </a>
              <a href="CompteClientServlet?action=printReleve&clientId=<%= clientId %>" 
                 target="_blank" class="btn btn-info">
                <i class="fe fe-printer"></i> Imprimer relevé
              </a>
              <a href="CompteClientServlet?action=exportExcel&typeExport=transactions&clientId=<%= clientId %>" 
                 class="btn btn-secondary">
                <i class="fe fe-download"></i> Exporter historique
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>--%>
    
    <!-- Filtres transactions -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <form method="GET" action="CompteClientServlet" class="row">
              <input type="hidden" name="action" value="etatCompte">
              <input type="hidden" name="clientId" value="<%= clientId %>">
              
              <div class="col-md-4">
                <div class="form-group">
                  <label>Date début</label>
                  <input type="date" class="form-control" name="dateDebut" 
                         value="<%= selectedDateDebut != null ? selectedDateDebut : "" %>">
                </div>
              </div>
              
              <div class="col-md-4">
                <div class="form-group">
                  <label>Date fin</label>
                  <input type="date" class="form-control" name="dateFin" 
                         value="<%= selectedDateFin != null ? selectedDateFin : "" %>">
                </div>
              </div>
              
              <div class="col-md-4 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100">
                  <i class="fe fe-filter"></i> Filtrer
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
    <div class="row">
      <!-- Historique des transactions -->
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header">
            <h4 class="card-title">Historique des transactions</h4>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Montant</th>
                    <th>Solde avant</th>
                    <th>Solde après</th>
                    <th>Caissier</th>
                    <th>Notes</th>
                  </tr>
                </thead>
                <tbody>
                  <% if (transactions != null && !transactions.isEmpty()) { 
                    for (TransactionCompte transaction : transactions) { 
                      String typeClass = transaction.getTypeTransaction().getCode().equals("DEPOT") ? 
                                        "badge-success" : "badge-danger";
                      String montantClass = transaction.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? 
                                          "text-success" : "text-danger";
                      String montantPrefix = transaction.getMontant().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                  %>
                  <tr>
                    <td><%= sdf.format(transaction.getDateTransaction()) %></td>
                    <td>
                      <span class="badge <%= typeClass %>">
                        <%= transaction.getTypeTransaction().getLibelle() %>
                      </span>
                    </td>
                    <td class="<%= montantClass %>">
                      <strong><%= montantPrefix %><%= String.format("%.2f HTG", transaction.getMontant()) %></strong>
                    </td>
                    <td><%= String.format("%.2f HTG", transaction.getSoldeAvant()) %></td>
                    <td><%= String.format("%.2f HTG", transaction.getSoldeApres()) %></td>
                    <td>
                      <% if (transaction.getCaissiere() != null) { %>
                      <%= transaction.getCaissiere().getNom() %>
                      <% } else { %>
                      N/A
                      <% } %>
                    </td>
                    <td><small><%= transaction.getNotes() != null ? transaction.getNotes() : "" %></small></td>
                  </tr>
                  <% } 
                  } else { %>
                  <tr>
                    <td colspan="7" class="text-center py-4">
                      <i class="fe fe-list fe-32 text-muted"></i>
                      <p class="mt-2">Aucune transaction trouvée</p>
                    </td>
                  </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Commandes à crédit -->
      <div class="col-md-4">
        <div class="card shadow">
          <div class="card-header">
            <h4 class="card-title">Commandes à crédit impayées</h4>
          </div>
          <div class="card-body">
            <% if (commandesCredit != null && !commandesCredit.isEmpty()) { 
              for (Commande commandeItem : commandesCredit) { 
                if (commandeItem.getCredit() != null) {
                  BigDecimal montantRestant = BigDecimal.valueOf(
                    commandeItem.getCredit().getMontantTotal() - commandeItem.getCredit().getMontantPaye()
                  );
            %>
            <div class="card mb-2">
              <div class="card-body">
                <div class="row align-items-center">
                  <div class="col">
                    <strong>Commande #<%= commandeItem.getNumeroCommande() %></strong><br>
                    <small class="text-muted">
                      <%= dateSdf.format(commandeItem.getDateCommande()) %> | 
                      <%= String.format("%.2f HTG", commandeItem.getMontantTotal()) %>
                    </small>
                  </div>
                  <div class="col-auto">
                    <span class="badge badge-warning">
                      <%= String.format("%.2f HTG", montantRestant) %>
                    </span>
                  </div>
                </div>
                <div class="mt-2">
                  <a href="CommandeServlet?action=detail&id=<%= commandeItem.getId() %>" 
                     class="btn btn-sm btn-outline-primary btn-block">
                    <i class="fe fe-eye"></i> Voir détail
                  </a>
                </div>
              </div>
            </div>
            <% } 
              } 
            } else { %>
            <div class="text-center py-4">
              <i class="fe fe-check-circle fe-32 text-success"></i>
              <p class="mt-2">Aucune commande à crédit impayée</p>
            </div>
            <% } %>
          </div>
        </div>
        
        <!-- Informations du compte -->
        <div class="card shadow mt-4">
          <div class="card-header">
            <h4 class="card-title">Informations du compte</h4>
          </div>
          <div class="card-body">
            <dl class="row">
              <dt class="col-sm-6">Date création:</dt>
              <dd class="col-sm-6">
                <%= compte.getDateCreation() != null ? 
                    sdf.format(compte.getDateCreation()) : "N/A" %>
              </dd>
              
              <dt class="col-sm-6">Dernière mise à jour:</dt>
              <dd class="col-sm-6">
                <%= compte.getDateMaj() != null ? 
                    sdf.format(compte.getDateMaj()) : "N/A" %>
              </dd>
              
              <dt class="col-sm-6">Nombre de transactions:</dt>
              <dd class="col-sm-6"><%= transactions != null ? transactions.size() : 0 %></dd>
              
              <dt class="col-sm-6">Dernière transaction:</dt>
              <dd class="col-sm-6">
                <% if (transactions != null && !transactions.isEmpty()) { 
                  TransactionCompte lastTrans = transactions.get(0);
                %>
                <%= sdf.format(lastTrans.getDateTransaction()) %><br>
                <small><%= lastTrans.getTypeTransaction().getLibelle() %></small>
                <% } else { %>
                N/A
                <% } %>
              </dd>
            </dl>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />