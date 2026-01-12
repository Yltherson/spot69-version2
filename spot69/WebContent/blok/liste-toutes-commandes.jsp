<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Commande,java.text.SimpleDateFormat,java.util.Date,com.google.gson.Gson,com.spot69.model.Utilisateur,com.spot69.model.Role,com.spot69.model.RayonHierarchique"%>
<meta charset="UTF-8">

<style>
  td.numero-commande {
    cursor: pointer;
    text-decoration: underline;
  }
  td.numero-commande:hover {
    color: #b08c3e;
  }
  .filter-container {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
    flex-wrap: wrap;
  }
  .filter-container .form-group {
    margin-bottom: 0;
    min-width: 150px;
  }
  .date-range-container {
    display: flex;
    align-items: center;
    gap: 5px;
  }
  .filter-title {
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }
  .hierarchie-filter {
    flex: 1;
    min-width: 180px;
  }
  .loading {
    color: #666;
    font-style: italic;
  }
  
  .table .thead-light th {
    color: #e9ecef !important;
     background-color: unset !important;
    border-color: #212529;
}
</style>

<%
// Récupérer les données depuis la requête
List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
List<Role> roles = (List<Role>) request.getAttribute("roles");
List<RayonHierarchique> hierarchieRayons = (List<RayonHierarchique>) request.getAttribute("hierarchieRayons");

// Récupérer les filtres
String staffId = request.getParameter("staffId");
String roleId = request.getParameter("roleId");
String rayonId = request.getParameter("rayonId");
String categorieId = request.getParameter("categorieId");
String sousCategorieId = request.getParameter("sousCategorieId");
String platId = request.getParameter("platId");
String dateDebut = request.getParameter("dateDebut");
String dateFin = request.getParameter("dateFin");

// Formatteur pour datetime-local
SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
                <i class="fe fe-clipboard fe-32 align-self-center text-black"></i>
                Liste des commandes
              </h2>
            </div>
            
            <!-- Section des filtres hiérarchiques -->
            <div class="card shadow mb-4">
              <div class="card-header">
                <h5 class="card-title mb-0">Filtres hiérarchiques</h5>
              </div>
              <div class="card-body">
                <form method="GET" action="CommandeServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="getallcommandesbyfiltres">
                  
                  <div class="filter-container">
                    <!-- Sélecteur Utilisateurs -->
                    <div class="form-group hierarchie-filter">
                      <label for="staffId">Utilisateur</label>
                      <select class="form-control" id="staffId" name="staffId">
                        <option value="">Tous les utilisateurs</option>
                        <% if(utilisateurs != null) {
                          for(Utilisateur user : utilisateurs) { %>
                            <option value="<%= user.getId() %>" <%= (staffId != null && staffId.equals(String.valueOf(user.getId()))) ? "selected" : "" %>>
                              <%= user.getLogin() %>
                            </option>
                          <% }
                        } %>
                      </select>
                    </div>
                    
                    <!-- Sélecteur Rôles -->
                    <div class="form-group hierarchie-filter">
                      <label for="roleId">Rôle</label>
                      <select class="form-control" id="roleId" name="roleId">
                        <option value="">Tous les rôles</option>
                        <% if(roles != null) {
                          for(Role role : roles) { %>
                            <option value="<%= role.getId() %>" <%= (roleId != null && roleId.equals(String.valueOf(role.getId()))) ? "selected" : "" %>>
                              <%= role.getRoleName() %>
                            </option>
                          <% }
                        } %>
                      </select>
                    </div>
                    
                    <!-- Sélecteur Rayons -->
                    <div class="form-group hierarchie-filter">
                      <label for="rayonId">Rayon</label>
                      <select class="form-control" id="rayonId" name="rayonId">
                        <option value="">Tous les rayons</option>
                        <% if(hierarchieRayons != null) {
                          for(RayonHierarchique rh : hierarchieRayons) { 
                            if(rh.getRayon() != null) { 
                                String rayonNom = rh.getRayon().getNom();
                                int rayonIdValue = rh.getRayon().getId();
                                boolean isSelected = (rayonId != null && rayonId.equals(String.valueOf(rayonIdValue)));
                                %>
                              <option value="<%= rayonIdValue %>" <%= isSelected ? "selected" : "" %>>
                                <%= rayonNom %>
                                <% if(rh.getCategories() != null) { %>
                                  <small>(<%= rh.getCategories().size() %> catégories)</small>
                                <% } %>
                              </option>
                            <% }
                          }
                        } %>
                      </select>
                    </div>
                    
                    <!-- Sélecteur Catégories (dépend du rayon) -->
                    <div class="form-group hierarchie-filter">
                      <label for="categorieId">Catégorie</label>
                      <select class="form-control" id="categorieId" name="categorieId">
                        <option value="">Toutes les catégories</option>
                        <% 
                          // Afficher les catégories selon le rayon sélectionné
                          if(hierarchieRayons != null && rayonId != null && !rayonId.isEmpty()) {
                            try {
                              int selectedRayonId = Integer.parseInt(rayonId);
                              for(RayonHierarchique rh : hierarchieRayons) {
                                if(rh.getRayon() != null && rh.getRayon().getId() == selectedRayonId) {
                                  if(rh.getCategories() != null) {
                                    for(com.spot69.model.MenuCategorie categorie : rh.getCategories()) {
                                      boolean isSelected = (categorieId != null && categorieId.equals(String.valueOf(categorie.getId())));
                                      %>
                                      <option value="<%= categorie.getId() %>" <%= isSelected ? "selected" : "" %>>
                                        <%= categorie.getNom() %>
                                      </option>
                                    <% }
                                  }
                                }
                              }
                            } catch(NumberFormatException e) {
                              // Ignorer
                            }
                          }
                        %>
                      </select>
                    </div>
                    
                    <!-- Sélecteur Sous-catégories (dépend de la catégorie) -->
                    <div class="form-group hierarchie-filter">
                      <label for="sousCategorieId">Sous-catégorie</label>
                      <select class="form-control" id="sousCategorieId" name="sousCategorieId">
                        <option value="">Toutes les sous-catégories</option>
                        <% 
                          // Afficher les sous-catégories selon la catégorie sélectionnée
                          if(hierarchieRayons != null && categorieId != null && !categorieId.isEmpty()) {
                            try {
                              int selectedCategorieId = Integer.parseInt(categorieId);
                              for(RayonHierarchique rh : hierarchieRayons) {
                                if(rh.getSousCategoriesParCategorie() != null) {
                                  List<com.spot69.model.MenuCategorie> sousCategories = 
                                      rh.getSousCategoriesParCategorie().get(selectedCategorieId);
                                  if(sousCategories != null) {
                                    for(com.spot69.model.MenuCategorie sousCategorie : sousCategories) {
                                      boolean isSelected = (sousCategorieId != null && sousCategorieId.equals(String.valueOf(sousCategorie.getId())));
                                      %>
                                      <option value="<%= sousCategorie.getId() %>" <%= isSelected ? "selected" : "" %>>
                                        <%= sousCategorie.getNom() %>
                                      </option>
                                    <% }
                                  }
                                }
                              }
                            } catch(NumberFormatException e) {
                              // Ignorer
                            }
                          }
                        %>
                      </select>
                    </div>
                    
                    <!-- Sélecteur Plats (dépend de la sous-catégorie) -->
                    <div class="form-group hierarchie-filter">
                      <label for="platId">Plat</label>
                      <select class="form-control" id="platId" name="platId">
                        <option value="">Tous les plats</option>
                        <% 
                          // Afficher les plats selon la sous-catégorie sélectionnée
                          if(hierarchieRayons != null && sousCategorieId != null && !sousCategorieId.isEmpty()) {
                            try {
                              int selectedSousCategorieId = Integer.parseInt(sousCategorieId);
                              for(RayonHierarchique rh : hierarchieRayons) {
                                if(rh.getPlatsParSousCategorie() != null) {
                                  List<com.spot69.model.Plat> plats = 
                                      rh.getPlatsParSousCategorie().get(selectedSousCategorieId);
                                  if(plats != null) {
                                    for(com.spot69.model.Plat plat : plats) {
                                      boolean isSelected = (platId != null && platId.equals(String.valueOf(plat.getId())));
                                      String platNom = (plat.getProduit() != null && plat.getProduit().getNom() != null) 
                                              ? plat.getProduit().getNom() : plat.getNom();
                                      %>
                                      <option value="<%= plat.getId() %>" <%= isSelected ? "selected" : "" %>>
                                        <%= platNom %>
                                        <small>(<%= plat.getPrix() %> HTG)</small>
                                      </option>
                                    <% }
                                  }
                                }
                              }
                            } catch(NumberFormatException e) {
                              // Ignorer
                            }
                          }
                        %>
                      </select>
                    </div>
                    
                    <!-- Range Picker pour les dates -->
                    <div class="form-group">
                      <label>Période</label>
                      <div class="date-range-container">
                        <input type="datetime-local" class="form-control" id="dateDebut" name="dateDebut" 
                               value="<%= dateDebut != null ? dateDebut : "" %>">
                        <span>à</span>
                        <input type="datetime-local" class="form-control" id="dateFin" name="dateFin" 
                               value="<%= dateFin != null ? dateFin : "" %>">
                      </div>
                    </div>
                    
                    <!-- Boutons d'action -->
                    <div class="form-group align-self-end">
                      <button type="submit" class="btn btn-primary">
                        <i class="fe fe-filter fe-16"></i> Appliquer
                      </button>
                      <a href="CommandeServlet?action=liste-toutes-commande" class="btn btn-secondary">
                        <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                      </a>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            <!-- Fin de la section des filtres -->
            
            <!-- Statistiques rapides -->
            <% 
            List<Commande> commandes = (List<Commande>) request.getAttribute("commandes");
            if(commandes != null && !commandes.isEmpty()) { 
                int totalCommandes = commandes.size();
                double montantTotal = 0;
                int commandesLivrees = 0;
                int commandesEnCours = 0;
                
                for(Commande cmd : commandes) {
                    if(cmd.getMontantTotal() != null) {
                        montantTotal += cmd.getMontantTotal().doubleValue();
                    }
                    if("LIVRE".equals(cmd.getStatutCommande())) {
                        commandesLivrees++;
                    } else if(!"ANNULE".equals(cmd.getStatutCommande())) {
                        commandesEnCours++;
                    }
                }
            %>
            <div class="row mb-4">
              <div class="col-md-3">
                <div class="card border-left-primary shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                          Total commandes</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= totalCommandes %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-shopping-cart fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-left-success shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                          Montant total</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= String.format("%.0f", montantTotal) %> HTG</div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-dollar-sign fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-left-warning shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                          En cours</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= commandesEnCours %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-clock fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-left-info shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                          Livrées</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= commandesLivrees %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-check-circle fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <% } %>
            
            <!-- Table des commandes -->
            <div class="card shadow">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="m-0 font-weight-bold text-primary">Liste des commandes</h6>
                <div>
                  <span class="badge badge-light border">
                    <i class="fe fe-info mr-1"></i>
                    Cliquez sur un numéro pour voir les détails
                  </span>
                </div>
              </div>
              <div class="card-body">
                <table class="table datatables" id="dataTableCommandes">
                  <thead>
                    <tr>
                      <th>N° Commande</th>
                      <th>Client</th>
                      <th>Staff</th>
                      <th>Date Commande</th>
                      <th>Montant</th>
                      <th>Statut</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <%
                    if (commandes != null) {
                        for (Commande commande : commandes) {
                    %>
                    <tr>
                      <td class="numero-commande" data-id="<%= commande.getId() %>">
                        <strong><%= commande.getNumeroCommande() %></strong>
                      </td>
                      <td>
                        <%= (commande.getNotes() != null) ? commande.getNotes() : "Client inconnu" %>
                      </td>
                      <td>
                        <%= (commande.getUtilisateur() != null) ? commande.getUtilisateur().getLogin() : "Staff inconnu" %>
                        <% if(commande.getUtilisateur() != null && commande.getUtilisateur().getRole() != null) { %>
                          <br><small class="text-muted"><%= commande.getUtilisateur().getRole().getRoleName() %></small>
                        <% } %>
                      </td>
                      <%
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                      %>
                      <td>
                        <%= commande.getDateCommande() != null ? sdf.format(commande.getDateCommande()) : "-" %>
                        <% if(commande.getTableRooftop() != null) { %>
                          <br><small class="text-muted">Table: <%= commande.getTableRooftop().getNumeroTable() %></small>
                        <% } %>
                      </td>
                      <td>
                        <strong><%= commande.getMontantTotal() %> HTG</strong>
                        <% if(commande.getStatutPaiement() != null) { %>
                          <br>
                          <% if("PAYE".equals(commande.getStatutPaiement())) { %>
                            <span class="badge badge-success">Payé</span>
                          <% } else if("PARTIEL".equals(commande.getStatutPaiement())) { %>
                            <span class="badge badge-warning">Partiel</span>
                          <% } else { %>
                            <span class="badge badge-danger">Impayé</span>
                          <% } %>
                        <% } %>
                      </td>
                      <td>
                        <%
                          String statutCommande = commande.getStatutCommande();
                          String statutPaiement = commande.getStatutPaiement();
                          String badgeClass = "badge-secondary";
                          
                          if("LIVRE".equals(statutCommande)) {
                            badgeClass = "badge-success";
                          } else if("EN_PREPARATION".equals(statutCommande)) {
                            badgeClass = "badge-warning";
                          } else if("PRETE".equals(statutCommande)) {
                            badgeClass = "badge-info";
                          } else if("ANNULE".equals(statutCommande)) {
                            badgeClass = "badge-danger";
                          } else if("EN_ATTENTE".equals(statutCommande)) {
                            badgeClass = "badge-light";
                          }
                        %>
                        
                        <span class="badge <%= badgeClass %>"><%= statutCommande %></span>
                        
                        <!-- Formulaire pour changer le statut -->
                        <form method="POST" action="CommandeServlet" class="form-modifier-statut mt-2">
                          <input type="hidden" name="redirectTo" value="liste-toutes-commandes.jsp">
                          <input type="hidden" name="action" value="modifierStatut">
                          <input type="hidden" name="idCommande" value="<%= commande.getId() %>">
                          
                          <% if (!"LIVRE".equals(statutCommande) && !"ANNULE".equals(statutCommande)) { %>
                            <select name="nouveauStatut" class="form-control form-control-sm statut-select" data-id="<%= commande.getId() %>">
                              <option value="EN_ATTENTE" <%= "EN_ATTENTE".equals(statutCommande) ? "selected" : "" %>>EN_ATTENTE</option>
                              <option value="EN_PREPARATION" <%= "EN_PREPARATION".equals(statutCommande) ? "selected" : "" %>>EN_PREPARATION</option>
                              <option value="PRETE" <%= "PRETE".equals(statutCommande) ? "selected" : "" %>>PRETE</option>
                              <option value="LIVRE" <%= "LIVRE".equals(statutCommande) ? "selected" : "" %>>LIVRE</option>
                              <option value="ANNULE" <%= "ANNULE".equals(statutCommande) ? "selected" : "" %>>ANNULE</option>
                            </select>
                          <% } %>
                        </form>
                      </td>
                      <td>
                        <div class="dropdown">
                          <button class="btn btn-sm btn-outline-primary dropdown-toggle" type="button" data-toggle="dropdown">
                            <i class="fe fe-more-vertical"></i>
                          </button>
                          <div class="dropdown-menu dropdown-menu-right">
                          <a href="CommandeServlet?action=imprimer&id=<%= commande.getId() %>" 
							   target="_blank" 
							   class="dropdown-item btn btn-info btn-sm">
							   <i class="fe fe-eye mr-2"></i> Preview
							</a>
                            <%-- <a class="dropdown-item btn-details-commande" href="#" data-id="<%= commande.getId() %>">
                              <i class="fe fe-eye mr-2"></i> Détails
                            </a> --%>
                            <% if (!"LIVRE".equals(commande.getStatutCommande()) && !"ANNULE".equals(commande.getStatutCommande())) { %>
                              <a href="#" class="dropdown-item btn-valider-commande" 
                                 data-toggle="modal" 
                                 data-target=".modal-valider-commande"
                                 data-numero="<%= commande.getNumeroCommande() %>"
                                 data-idcommande="<%= commande.getId() %>"
                                 data-montant="<%= commande.getMontantTotal() %>">
                                 <i class="fe fe-check-circle mr-2"></i> Valider
                              </a>
                            <% } %>
                            <%
                              com.google.gson.Gson gson = new com.google.gson.Gson();
                              String commandeJson = gson.toJson(commande);
                              String encodedJson = java.net.URLEncoder.encode(commandeJson, "UTF-8");
                              String commandeNumero = commande.getNumeroCommande();
                            %>
                            <a class="dropdown-item"
                               href="spot69://commande/imprimer?commandeId=<%= commandeNumero %>&data=<%= encodedJson %>">
                               <i class="fe fe-printer mr-2"></i> Traitement
                            </a>
                            <div class="dropdown-divider"></div>
                            <a class="dropdown-item text-danger" href="CommandeServlet?action=supprimer&id=<%= commande.getId() %>&redirectTo=liste-toutes-commandes.jsp" 
                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette commande ?');">
                              <i class="fe fe-trash-2 mr-2"></i> Supprimer
                            </a>
                          </div>
                        </div>
                      </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                      <td colspan="7" class="text-center">
                        <div class="alert alert-info">
                          <i class="fe fe-info mr-2"></i>
                          Aucune commande trouvée avec les filtres sélectionnés.
                        </div>
                      </td>
                    </tr>
                    <% } %>
                  </tbody>
                  <tfoot>
                    <tr>
                      <th colspan="4" style="text-align:right">Total :</th>
                      <th id="totalMontant"></th>
                      <th colspan="2"></th>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<!-- Modal pour validation/modification de commande -->
<div class="modal fade modal-valider-commande modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalUpdateFournisseurLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <form method="POST" action="CommandeServlet" id="formEditCommande">
        <input type="hidden" name="action" id="hiddenActionField" />
        <input type="hidden" name="idCommande" id="idCommande" />
        <input type="hidden" name="redirectTo" value="liste-toutes-commandes.jsp">

        <div class="modal-header">
          <h5 class="modal-title" id="modalUpdateFournisseurLabel">
            Validation de la commande
          </h5>
          <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>

        <div class="modal-body">
          <input type="hidden" name="id" id="commandeIdModal">
          <input type="hidden" name="redirectTo" value="liste-toutes-commandes.jsp">

          <div class="form-group">
            <label for="numeroCommandeModal">Numéro Commande</label>
            <input type="text" class="form-control" id="numeroCommandeModal" name="numeroCommande" readonly>
          </div>
          
          <div class="form-group">
            <label for="montantTotalModal">Montant total</label>
            <input type="text" class="form-control" id="montantTotalModal" name="montantTotal" readonly>
          </div>

          <div class="form-group">
            <label for="actionSelect">Action à effectuer</label>
            <select class="form-control" id="actionSelect" name="modalAction" required>
              <option value="" disabled selected>-- Choisir une action --</option>
              <option value="valider">Valider la commande</option>
              <option value="modifierStatut">Modifier le statut</option>
            </select>
          </div>

          <!-- Bloc pour Valider la commande -->
          <div id="validerFields" style="display:none;">
            <div class="form-group">
              <label for="modePaiement">Mode de paiement *</label>
              <select class="form-control" id="modePaiement" name="modePaiement">
                <option value="CASH">CASH</option>
                <option value="VIREMENT">VIREMENT</option>
                <option value="MONCASH">MONCASH</option>
                <option value="NATCASH">NATCASH</option>
              </select>
            </div>

            <div class="form-group">
              <label for="versement">Versement *</label>
              <input type="number" class="form-control" id="versement" name="montantPaye" placeholder="Montant payé" step="0.01">
            </div>
          </div>

          <!-- Bloc pour Modifier le statut -->
          <div id="modifierStatutFields" style="display:none;">
            <div class="form-group">
              <label for="statutCommandeSelect">Nouveau statut *</label>
              <select class="form-control" id="statutCommandeSelect" name="nouveauStatut">
                <option value="EN_PREPARATION">EN_PREPARATION</option>
                <option value="PRETE">PRETE</option>
                <option value="ANNULE">ANNULE</option>
                <option value="EN_ATTENTE">EN_ATTENTE</option>
              </select>
            </div>
          </div>

        </div>

        <div class="modal-footer">
          <button type="submit" class="btn btn-primary">
            Valider <i class="fe fe-send icon"></i>
            <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
          </button>
          <button type="button" class="btn btn-secondary" data-dismiss="modal">
            Annuler
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />
<script>
$(document).ready(function () {
    // Données hiérarchiques pour JavaScript
    var hierarchieData = {};
    
    <% if(hierarchieRayons != null) { %>
      <% for(RayonHierarchique rh : hierarchieRayons) { %>
        <% if(rh.getRayon() != null) { %>
          hierarchieData[<%= rh.getRayon().getId() %>] = {
            nom: "<%= rh.getRayon().getNom() %>",
            categories: {}
          };
          
          <% if(rh.getCategories() != null) { %>
            <% for(com.spot69.model.MenuCategorie categorie : rh.getCategories()) { %>
              hierarchieData[<%= rh.getRayon().getId() %>].categories[<%= categorie.getId() %>] = {
                nom: "<%= categorie.getNom() %>",
                sousCategories: {}
              };
              
              <% if(rh.getSousCategoriesParCategorie() != null && 
                   rh.getSousCategoriesParCategorie().get(categorie.getId()) != null) { %>
                <% for(com.spot69.model.MenuCategorie sousCategorie : 
                       rh.getSousCategoriesParCategorie().get(categorie.getId())) { %>
                  hierarchieData[<%= rh.getRayon().getId() %>].categories[<%= categorie.getId() %>]
                    .sousCategories[<%= sousCategorie.getId() %>] = {
                      nom: "<%= sousCategorie.getNom() %>",
                      plats: {}
                  };
                  
                  <% if(rh.getPlatsParSousCategorie() != null && 
                       rh.getPlatsParSousCategorie().get(sousCategorie.getId()) != null) { %>
                    <% for(com.spot69.model.Plat plat : 
                           rh.getPlatsParSousCategorie().get(sousCategorie.getId())) { %>
                      var platNom = "<%= (plat.getProduit() != null && plat.getProduit().getNom() != null) 
                                      ? plat.getProduit().getNom().replace("\"", "\\\"") 
                                      : plat.getNom().replace("\"", "\\\"") %>";
                      hierarchieData[<%= rh.getRayon().getId() %>].categories[<%= categorie.getId() %>]
                        .sousCategories[<%= sousCategorie.getId() %>].plats[<%= plat.getId() %>] = {
                          nom: platNom,
                          prix: <%= plat.getPrix() %>
                      };
                    <% } %>
                  <% } %>
                <% } %>
              <% } %>
            <% } %>
          <% } %>
        <% } %>
      <% } %>
    <% } %>
    
    // Gestion des changements hiérarchiques
    $('#rayonId').on('change', function() {
        var rayonId = $(this).val();
        var categorieSelect = $('#categorieId');
        var sousCategorieSelect = $('#sousCategorieId');
        var platSelect = $('#platId');
        
        // Réinitialiser les selects dépendants
        categorieSelect.empty().append('<option value="">Toutes les catégories</option>');
        sousCategorieSelect.empty().append('<option value="">Toutes les sous-catégories</option>');
        platSelect.empty().append('<option value="">Tous les plats</option>');
        
        if (!rayonId) return;
        
        // Remplir les catégories du rayon sélectionné
        if (hierarchieData[rayonId] && hierarchieData[rayonId].categories) {
            $.each(hierarchieData[rayonId].categories, function(categorieId, categorie) {
                categorieSelect.append('<option value="' + categorieId + '">' + categorie.nom + '</option>');
            });
        }
    });
    
    $('#categorieId').on('change', function() {
        var rayonId = $('#rayonId').val();
        var categorieId = $(this).val();
        var sousCategorieSelect = $('#sousCategorieId');
        var platSelect = $('#platId');
        
        // Réinitialiser les selects dépendants
        sousCategorieSelect.empty().append('<option value="">Toutes les sous-catégories</option>');
        platSelect.empty().append('<option value="">Tous les plats</option>');
        
        if (!rayonId || !categorieId) return;
        
        // Remplir les sous-catégories de la catégorie sélectionnée
        if (hierarchieData[rayonId] && hierarchieData[rayonId].categories[categorieId] && 
            hierarchieData[rayonId].categories[categorieId].sousCategories) {
            $.each(hierarchieData[rayonId].categories[categorieId].sousCategories, 
                   function(sousCategorieId, sousCategorie) {
                sousCategorieSelect.append('<option value="' + sousCategorieId + '">' + 
                                          sousCategorie.nom + '</option>');
            });
        }
    });
    
    $('#sousCategorieId').on('change', function() {
        var rayonId = $('#rayonId').val();
        var categorieId = $('#categorieId').val();
        var sousCategorieId = $(this).val();
        var platSelect = $('#platId');
        
        // Réinitialiser le select plat
        platSelect.empty().append('<option value="">Tous les plats</option>');
        
        if (!rayonId || !categorieId || !sousCategorieId) return;
        
        // Remplir les plats de la sous-catégorie sélectionnée
        if (hierarchieData[rayonId] && 
            hierarchieData[rayonId].categories[categorieId] && 
            hierarchieData[rayonId].categories[categorieId].sousCategories[sousCategorieId] && 
            hierarchieData[rayonId].categories[categorieId].sousCategories[sousCategorieId].plats) {
            $.each(hierarchieData[rayonId].categories[categorieId].sousCategories[sousCategorieId].plats, 
                   function(platId, plat) {
                platSelect.append('<option value="' + platId + '">' + plat.nom + 
                                 ' (' + plat.prix + ' HTG)</option>');
            });
        }
    });
    
    // Initialiser les selects si des valeurs sont déjà sélectionnées
    <% if(rayonId != null && !rayonId.isEmpty()) { %>
        $('#rayonId').trigger('change');
        setTimeout(function() {
            <% if(categorieId != null && !categorieId.isEmpty()) { %>
                $('#categorieId').val("<%= categorieId %>").trigger('change');
                setTimeout(function() {
                    <% if(sousCategorieId != null && !sousCategorieId.isEmpty()) { %>
                        $('#sousCategorieId').val("<%= sousCategorieId %>").trigger('change');
                        setTimeout(function() {
                            <% if(platId != null && !platId.isEmpty()) { %>
                                $('#platId').val("<%= platId %>");
                            <% } %>
                        }, 100);
                    <% } %>
                }, 100);
            <% } %>
        }, 100);
    <% } %>
    
    // === Initialisation DataTable ===
    if (!$.fn.dataTable) {
        alert('Erreur: DataTables n\'est pas chargé. Vérifiez l\'inclusion des fichiers.');
        return;
    }
    const table = $('#dataTableCommandes').DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json"
        },
        "pageLength": 25,
        "lengthMenu": [10, 25, 50, 100],
        "order": [[3, "desc"]],
        "responsive": true,
        "dom": '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>' +
               '<"row"<"col-sm-12"tr>>' +
               '<"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>',
        "columnDefs": [
            { 
                "type": "datetime", 
                "targets": 3,
                "render": function(data, type, row) {
                    if (type === 'sort' || type === 'type') {
                        return data;
                    }
                    return data;
                }
            },
            // Formater la colonne montant pour faciliter le calcul
            {
                "targets": 4,
                "render": function(data, type, row) {
                    // Pour l'affichage
                    if (type === 'display') {
                        return data;
                    }
                    // Pour le tri/filtrage, conserver la valeur numérique
                    if (type === 'sort' || type === 'filter') {
                        var num = data.replace(/[^\d.-]/g, '');
                        return parseFloat(num) || 0;
                    }
                    return data;
                }
            }
        ],
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api();
            var intVal = function (i) {
                if (typeof i === 'string') {
                    // Extraire uniquement le nombre (supprimer "HTG" et autres caractères)
                    return parseFloat(i.replace(/[^\d.-]/g, '')) || 0;
                }
                return typeof i === 'number' ? i : 0;
            };

            // === SOLUTION : Calculer le total sur TOUTES les données ===
            var total = api
                .column(4, { search: 'applied' }) // Prendre en compte les filtres
                .data()
                .reduce(function (a, b) {
                    return a + intVal(b);
                }, 0);

            // Afficher le total formaté
            $('#totalMontant').html(total.toLocaleString('fr-FR', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }) + ' HTG');

            // Optionnel : Afficher aussi le total de la page courante
            var pageTotal = api
                .column(4, { page: 'current', search: 'applied' })
                .data()
                .reduce(function (a, b) {
                    return a + intVal(b);
                }, 0);
            
            // Vous pouvez ajouter une deuxième ligne pour afficher les deux totaux
    
        }
    });

    // === Gestion du clic sur les numéros de commande ===
    $('#dataTableCommandes').on('click', 'td.numero-commande, a.btn-details-commande', function (e) {
        e.preventDefault();
        e.stopPropagation();
        
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const commandeId = $(this).data('id') || $(this).closest('tr').find('td.numero-commande').data('id');
        
        if (!commandeId) {
            alert("Erreur: ID de commande non trouvé");
            return;
        }

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
            tr.find('td.numero-commande').removeClass('text-primary');
        } else {
            row.child('<div class="text-center p-3"><i class="fas fa-spinner fa-spin fa-2x text-primary"></i><div class="mt-2">Chargement des détails...</div></div>').show();
            tr.addClass('shown');
            tr.find('td.numero-commande').addClass('text-primary');
            
            $.ajax({
                url: 'CommandeServlet?action=getdetailsjson&id=' + commandeId,
                method: 'GET',
                dataType: 'json',
                timeout: 10000,
                success: function (response) {
                    if (response && response.length > 0) {
                        let html = '<div class="p-3">';
                        html += '<h6 class="text-primary mb-3"><i class="fe fe-list mr-2"></i>Détails de la commande</h6>';
                        html += '<div class="table-responsive">';
                        html += '<table class="table table-sm table-bordered table-hover">';
                        html += '<thead class="thead-light"><tr>';
                        html += '<th>Produit/Plat</th>';
                        html += '<th class="text-center">Qté</th>';
                        html += '<th class="text-right">Prix U.</th>';
                        html += '<th class="text-right">Total</th>';
                        html += '<th>Note</th>';
                        html += '</tr></thead><tbody>';
                        
                        let sousTotal = 0;
                        
                        response.forEach(function (d) {
                            var nomProduit = d.produit && d.produit.nom ? d.produit.nom
                                            : (d.plat && d.plat.nom ? d.plat.nom : 'N/A');
                            var total = d.quantite * d.prixUnitaire;
                            sousTotal += total;
                            
                            html += "<tr>";
                            html += "<td>" + nomProduit + "</td>";
                            html += "<td class='text-center'>" + d.quantite + "</td>";
                            html += "<td class='text-right'>" + d.prixUnitaire.toFixed(2) + " HTG</td>";
                            html += "<td class='text-right font-weight-bold'>" + total.toFixed(2) + " HTG</td>";
                            html += "<td><small class='text-muted'>" + (d.notes ? d.notes : '-') + "</small></td>";
                            html += "</tr>";
                        });
                        
                        html += '</tbody>';
                        html += '<tfoot class="table-primary">';
                        html += '<tr>';
                        html += '<td colspan="3" class="text-right font-weight-bold">Sous-total:</td>';
                        html += '<td class="text-right font-weight-bold">' + sousTotal.toFixed(2) + ' HTG</td>';
                        html += '<td></td>';
                        html += '</tr>';
                        html += '</tfoot>';
                        html += '</table>';
                        html += '</div>';
                        html += '</div>';
                        
                        row.child(html).show();
                    } else {
                        row.child('<div class="p-3 text-center"><i class="fe fe-alert-circle fa-2x text-warning"></i><div class="mt-2 text-muted">Aucun détail trouvé pour cette commande.</div></div>').show();
                    }
                },
                error: function (xhr, status, error) {
                    var errorMsg = "Erreur lors du chargement des détails.";
                    if (xhr.status === 404) {
                        errorMsg = "Service non trouvé. Vérifiez l'URL.";
                    } else if (xhr.status === 500) {
                        errorMsg = "Erreur serveur. Contactez l'administrateur.";
                    }
                    row.child('<div class="p-3 text-center text-danger"><i class="fe fe-alert-triangle fa-2x"></i><div class="mt-2">' + errorMsg + '</div></div>').show();
                }
            });
        }
    });

    // === Changement statut ===
    $('#dataTableCommandes').on('change', '.statut-select', function () {
        const selectedValue = $(this).val();
        const commandeId = $(this).data('id');

        if (!selectedValue) {
            alert('Veuillez sélectionner un statut valide');
            return;
        }

        if (!confirm('Modifier le statut de cette commande ?')) {
            // Annuler le changement en rechargeant la page
            location.reload();
            return;
        }

        $(this).prop('disabled', true).after('<span class="ml-2"><i class="fas fa-spinner fa-spin"></i></span>');
        
        $.post('CommandeServlet', {
            action: 'modifierStatut',
            idCommande: commandeId,
            nouveauStatut: selectedValue,
            redirectTo: 'liste-toutes-commandes.jsp'
        }).done(function(response) {
            location.reload();
        }).fail(function() {
            alert('Erreur lors de la modification du statut.');
            location.reload();
        });
    });

    // === Remplir modal Validation commande ===
    $(document).on('click', '.btn-valider-commande', function () {
        const numero = $(this).data('numero');
        const montant = $(this).data('montant');
        const idCom = $(this).data('idcommande');

        $('#idCommande').val(idCom);
        $('#numeroCommandeModal').val(numero);
        $('#montantTotalModal').val(montant + ' HTG');
        
        $('#actionSelect').val('');
        $('#validerFields, #modifierStatutFields').hide();
    });

    // === Gestion du select action dans modal ===
    $('#actionSelect').on('change', function() {
        const val = $(this).val();
        
        if(val === 'valider'){
            $('#validerFields').show();
            $('#modifierStatutFields').hide();
            $('#modePaiement').prop('required', true);
            $('#versement').prop('required', true);
            $('#statutCommandeSelect').prop('required', false);
            $('#hiddenActionField').val('valider');
        } else if(val === 'modifierStatut'){
            $('#validerFields').hide();
            $('#modifierStatutFields').show();
            $('#statutCommandeSelect').prop('required', true);
            $('#modePaiement').prop('required', false);
            $('#versement').prop('required', false);
            $('#hiddenActionField').val('modifierStatut');
        } else {
            $('#validerFields, #modifierStatutFields').hide();
            $('#modePaiement, #versement, #statutCommandeSelect').prop('required', false);
        }
    });
    
    // === Auto-soumettre le formulaire quand on change un filtre (optionnel) ===
    $('#staffId, #roleId, #rayonId, #platId').on('change', function() {
        // Vous pouvez activer cette ligne pour auto-soumettre le formulaire
        // $('#filterForm').submit();
    });
    
    // === Initialiser les dates si vides ===
    if (!$('#dateDebut').val()) {
        const now = new Date();
        const startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        $('#dateDebut').val(startOfDay.toISOString().slice(0, 16));
    }
    
    if (!$('#dateFin').val()) {
        const now = new Date();
        const endOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59);
        $('#dateFin').val(endOfDay.toISOString().slice(0, 16));
    }
});
</script>