<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<meta charset="UTF-8">
<%
List<CompteClient> comptes = (List<CompteClient>) request.getAttribute("comptes");
BigDecimal soldeTotal = (BigDecimal) request.getAttribute("soldeTotal");
Integer nombreComptes = (Integer) request.getAttribute("nombreComptes");
Integer nombrePositifs = (Integer) request.getAttribute("nombrePositifs");
Integer nombreNegatifs = (Integer) request.getAttribute("nombreNegatifs");
String searchTerm = (String) request.getAttribute("searchTerm");
String minSolde = (String) request.getAttribute("minSolde");
String maxSolde = (String) request.getAttribute("maxSolde");
String statutFilter = (String) request.getAttribute("statutFilter");

// Calculer les statistiques de crédit
BigDecimal totalCredit = BigDecimal.ZERO;
BigDecimal totalCreditUtilise = BigDecimal.ZERO;
int nombreAvecCredit = 0;

for (CompteClient compte : comptes) {
    totalCredit = totalCredit.add(compte.getLimiteCredit());
    
    if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
        totalCreditUtilise = totalCreditUtilise.add(compte.getSolde().abs());
    }
    
    if (compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0) {
        nombreAvecCredit++;
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
          <i class="fe fe-credit-card fe-32 align-self-center text-primary"></i>
          Gestion des Comptes Clients
        </h2>
      </div>
    </div>
    
    <!-- Cartes de statistiques (avec crédit) -->
    <div class="row mb-4">
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-primary">
                  <i class="fe fe-users text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Nombre de comptes</p>
                <span class="h3"><%= nombreComptes %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-success">
                  <i class="fe fe-plus text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Comptes positifs</p>
                <span class="h3"><%= nombrePositifs %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-danger">
                  <i class="fe fe-minus text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Comptes négatifs</p>
                <span class="h3"><%= nombreNegatifs %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-warning">
                  <i class="fe fe-dollar-sign text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Solde total</p>
                <span class="h3"><%= soldeTotal != null ? String.format("%.2f", soldeTotal) : "0.00" %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-info">
                  <i class="fe fe-credit-card text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Crédit total</p>
                <span class="h3"><%= String.format("%.2f", totalCredit) %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-2">
        <div class="card shadow">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-3 text-center">
                <span class="circle circle-sm bg-secondary">
                  <i class="fe fe-alert-triangle text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Crédit utilisé</p>
                <span class="h3"><%= String.format("%.2f", totalCreditUtilise) %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Filtres et actions -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <form method="GET" action="CompteClientServlet" class="row">
              <input type="hidden" name="action" value="listeComptes">
              
              <div class="col-md-3">
                <div class="form-group">
                  <label for="search">Recherche client</label>
                  <input type="text" class="form-control" id="search" name="search" 
                         value="<%= searchTerm != null ? searchTerm : "" %>" 
                         placeholder="Nom, prénom, email...">
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="minSolde">Solde min</label>
                  <input type="number" class="form-control" id="minSolde" name="minSolde" 
                         value="<%= minSolde != null ? minSolde : "" %>" 
                         placeholder="0" step="0.01">
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="maxSolde">Solde max</label>
                  <input type="number" class="form-control" id="maxSolde" name="maxSolde" 
                         value="<%= maxSolde != null ? maxSolde : "" %>" 
                         placeholder="100000" step="0.01">
                </div>
              </div>
              
              <div class="col-md-2">
                <div class="form-group">
                  <label for="avecCredit">Avec crédit</label>
                  <select class="form-control" id="avecCredit" name="avecCredit">
                    <option value="">Tous</option>
                    <option value="oui">Avec crédit</option>
                    <option value="non">Sans crédit</option>
                  </select>
                </div>
              </div>
              
              <div class="col-md-3 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100">
                  <i class="fe fe-search"></i> Filtrer
                </button>
              </div>
            </form>
            
            <div class="row mt-3">
              <div class="col-md-12">
                <div class="btn-group">
                  <a href="CompteClientServlet?action=creerCompteForm" class="btn btn-primary">
                    <i class="fe fe-plus"></i> Nouveau compte
                  </a>
                  <a href="CompteClientServlet?action=initialiserComptes" class="btn btn-secondary"
                     onclick="return confirm('Initialiser tous les comptes? Cette action créera des comptes pour tous les clients sans compte.')">
                    <i class="fe fe-refresh-cw"></i> Initialiser comptes
                  </a>
                 
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Table des comptes -->
    <div class="row">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover" id="tableComptes">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Contact</th>
                    <th>Solde</th>
                    <th>Limite crédit</th>
                    <th>Crédit utilisé</th>
                    <th>Solde disponible</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <% if (comptes != null && !comptes.isEmpty()) { 
                    for (CompteClient compte : comptes) { 
                      // Calculs pour chaque compte
                      BigDecimal soldeDisponible = compte.getSolde().add(compte.getLimiteCredit());
                      BigDecimal creditUtilise = BigDecimal.ZERO;
                      if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                          creditUtilise = compte.getSolde().abs();
                      }
                      
                      // Classes CSS
                      String soldeClass = "text-success";
                      String soldeIcon = "fe fe-arrow-up";
                      
                      if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                          soldeClass = "text-danger";
                          soldeIcon = "fe fe-arrow-down";
                      } else if (compte.getSolde().compareTo(BigDecimal.ZERO) == 0) {
                          soldeClass = "text-warning";
                          soldeIcon = "fe fe-minus";
                      }
                      
                      String soldeDisponibleClass = soldeDisponible.compareTo(BigDecimal.ZERO) >= 0 ? "text-success" : "text-danger";
                      String creditUtiliseClass = creditUtilise.compareTo(BigDecimal.ZERO) > 0 ? "text-danger" : "text-muted";
                  %>
                  <tr>
                    <td><%= compte.getId() %></td>
                    <td>
                      <strong><%= compte.getClient().getNom() %> <%= compte.getClient().getPrenom() %></strong><br>
                      <small class="text-muted">ID: <%= compte.getClient().getId() %></small>
                    </td>
                    <td>
                      <i class="fe fe-mail"></i> <%= compte.getClient().getEmail() %><br>
                      <i class="fe fe-phone"></i> <%= compte.getClient().getTelephone() != null ? compte.getClient().getTelephone() : "Non défini" %>
                    </td>
                    <td>
                      <span class="<%= soldeClass %>">
                        <i class="<%= soldeIcon %>"></i>
                        <strong><%= String.format("%.2f HTG", compte.getSolde()) %></strong>
                      </span>
                      <% if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) { %>
                      <br><small class="text-danger"><i class="fe fe-alert-triangle"></i> En crédit</small>
                      <% } %>
                    </td>
                    <td>
                      <span class="text-info">
                        <i class="fe fe-credit-card"></i> 
                        <strong><%= String.format("%.2f HTG", compte.getLimiteCredit()) %></strong>
                      </span>
                    </td>
                    <td>
                      <span class="<%= creditUtiliseClass %>">
                        <i class="fe fe-alert-circle"></i>
                        <strong><%= String.format("%.2f HTG", creditUtilise) %></strong>
                      </span>
                    </td>
                    <td>
                      <span class="<%= soldeDisponibleClass %>">
                        <i class="fe fe-check-circle"></i>
                        <strong><%= String.format("%.2f HTG", soldeDisponible) %></strong>
                      </span>
                    </td>
                    <td>
                      <div class="btn-group" role="group">
                        <a href="CompteClientServlet?action=etatCompte&clientId=<%= compte.getClientId() %>" 
                           class="btn btn-sm btn-outline-info" title="Voir état">
                          <i class="fe fe-eye"></i>
                        </a>
                        <a href="CompteClientServlet?action=formDepot&clientId=<%= compte.getClientId() %>" 
                           class="btn btn-sm btn-outline-success" title="Dépôt / Crédit">
                          <i class="fe fe-plus-circle"></i>
                        </a>
                        <a href="CompteClientServlet?action=historiqueTransactions&clientId=<%= compte.getClientId() %>" 
                           class="btn btn-sm btn-outline-primary" title="Historique">
                          <i class="fe fe-list"></i>
                        </a>
                      </div>
                    </td>
                  </tr>
                  <% } 
                  } else { %>
                  <tr>
                    <td colspan="8" class="text-center py-5">
                      <i class="fe fe-credit-card fe-64 text-muted"></i>
                      <h4 class="mt-3">Aucun compte trouvé</h4>
                      <p class="text-muted">Utilisez les filtres ou créez un nouveau compte.</p>
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
</main>

<jsp:include page="footer.jsp" />