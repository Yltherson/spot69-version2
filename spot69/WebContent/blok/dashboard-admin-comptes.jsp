<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.spot69.model.*,java.math.BigDecimal"%>

<%
Map<String, Object> etatGeneral = (Map<String, Object>) request.getAttribute("etatGeneral");
List<CaisseCaissiere> caissesOuvertes = (List<CaisseCaissiere>) request.getAttribute("caissesOuvertes");
List<CompteClient> comptesNegatifs = (List<CompteClient>) request.getAttribute("comptesNegatifs");
Utilisateur user = (Utilisateur) request.getAttribute("user");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <h2 class="h5 page-title">
          <i class="fe fe-bar-chart fe-32 align-self-center text-primary"></i>
          Dashboard Comptes & Caisses
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
                  <i class="fe fe-dollar-sign text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Solde total</p>
                <span class="h3">
                  <%= etatGeneral.get("soldeTotal") != null ? 
                      String.format("%.2f HTG", (BigDecimal) etatGeneral.get("soldeTotal")) : "0.00 HTG" %>
                </span>
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
                  <i class="fe fe-users text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Nombre de comptes</p>
                <span class="h3"><%= etatGeneral.get("nombreComptes") %></span>
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
                  <i class="fe fe-briefcase text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Caisses ouvertes</p>
                <span class="h3"><%= etatGeneral.get("nombreCaissesOuvertes") %></span>
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
                  <i class="fe fe-shopping-cart text-white"></i>
                </span>
              </div>
              <div class="col-9">
                <p class="small mb-0">Commandes crédit</p>
                <span class="h3"><%= etatGeneral.get("nombreCommandesCredit") %></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="row">
      <!-- Caisses ouvertes -->
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-header">
            <h4 class="card-title">Caisses ouvertes</h4>
          </div>
          <div class="card-body">
            <% if (caissesOuvertes != null && !caissesOuvertes.isEmpty()) { 
              for (CaisseCaissiere caisse : caissesOuvertes) { 
            %>
            <div class="card mb-2">
              <div class="card-body">
                <div class="row align-items-center">
                  <div class="col">
                    <strong>Caisse #<%= caisse.getId() %></strong><br>
                    <small class="text-muted">
                      <i class="fe fe-user"></i> <%= caisse.getCaissiere().getNom() %> |
                      <i class="fe fe-clock"></i> 
                      <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(caisse.getOuverture()) %>
                    </small>
                  </div>
                  <div class="col-auto">
                    <a href="CompteClientServlet?action=etatCaisse&caisseId=<%= caisse.getId() %>" 
                       class="btn btn-sm btn-outline-primary">
                      <i class="fe fe-eye"></i> Voir
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <% } 
            } else { %>
            <div class="text-center py-4">
              <i class="fe fe-briefcase fe-32 text-muted"></i>
              <p class="mt-2">Aucune caisse ouverte</p>
            </div>
            <% } %>
          </div>
        </div>
      </div>
      
      <!-- Comptes à solde négatif -->
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-header">
            <h4 class="card-title">Comptes à solde négatif</h4>
          </div>
          <div class="card-body">
            <% if (comptesNegatifs != null && !comptesNegatifs.isEmpty()) { 
              for (CompteClient compte : comptesNegatifs) { 
            %>
            <div class="card mb-2">
              <div class="card-body">
                <div class="row align-items-center">
                  <div class="col">
                    <strong><%= compte.getClient().getNom() %> <%= compte.getClient().getPrenom() %></strong><br>
                    <small class="text-muted">
                      <i class="fe fe-mail"></i> <%= compte.getClient().getEmail() %>
                    </small>
                  </div>
                  <div class="col-auto">
                    <span class="badge badge-danger">
                      <%= String.format("%.2f HTG", compte.getSolde()) %>
                    </span>
                  </div>
                </div>
                <div class="mt-2">
                  <a href="CompteClientServlet?action=etatCompte&clientId=<%= compte.getClientId() %>" 
                     class="btn btn-sm btn-outline-danger btn-block">
                    <i class="fe fe-alert-triangle"></i> Voir détail
                  </a>
                </div>
              </div>
            </div>
            <% } 
            } else { %>
            <div class="text-center py-4">
              <i class="fe fe-check-circle fe-32 text-success"></i>
              <p class="mt-2">Aucun compte à solde négatif</p>
            </div>
            <% } %>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Actions rapides -->
    <div class="row mt-4">
      <div class="col-12">
        <div class="card shadow">
          <div class="card-body">
            <div class="row">
              <div class="col-md-4">
                <a href="CompteClientServlet?action=listeComptes" class="btn btn-primary btn-block mb-2">
                  <i class="fe fe-credit-card"></i> Gérer les comptes
                </a>
              </div>
              <div class="col-md-4">
                <a href="CompteClientServlet?action=listeCaisses" class="btn btn-info btn-block mb-2">
                  <i class="fe fe-briefcase"></i> Voir toutes les caisses
                </a>
              </div>
              <div class="col-md-4">
                <a href="CompteClientServlet?action=initialiserComptes" 
                   class="btn btn-warning btn-block mb-2"
                   onclick="return confirm('Initialiser tous les comptes?')">
                  <i class="fe fe-refresh-cw"></i> Initialiser comptes
                </a>
              </div>
            </div>
            
            <div class="row mt-3">
              <div class="col-md-4">
                <a href="CompteClientServlet?action=getEtatGeneralJSON" target="_blank" 
                   class="btn btn-secondary btn-block mb-2">
                  <i class="fe fe-download"></i> Exporter JSON
                </a>
              </div>
              <div class="col-md-4">
                <a href="CompteClientServlet?action=exportExcel&typeExport=comptes" 
                   class="btn btn-success btn-block mb-2">
                  <i class="fe fe-file"></i> Rapport Excel
                </a>
              </div>
              <div class="col-md-4">
                <a href="CompteClientServlet?action=dashboard&refresh=true" 
                   class="btn btn-outline-primary btn-block mb-2">
                  <i class="fe fe-refresh-cw"></i> Actualiser
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />