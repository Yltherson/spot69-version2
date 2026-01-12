<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
    import="java.util.List, com.spot69.model.RapportCommande, com.spot69.model.Utilisateur, 
            com.spot69.model.Role, com.spot69.model.Plat, com.spot69.model.MenuCategorie,
            com.spot69.model.RayonHierarchique"%>
<meta charset="UTF-8">

<style>
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
  .hierarchie-filter {
    flex: 1;
    min-width: 180px;
  }
  .total-row {
    font-weight: bold;
  }
</style>

<%
    List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
    List<Role> roles = (List<Role>) request.getAttribute("roles");
    List<RapportCommande> rapports = (List<RapportCommande>) request.getAttribute("rapports");
    List<RayonHierarchique> hierarchieRayons = (List<RayonHierarchique>) request.getAttribute("hierarchieRayons");

    Integer totalMontant = (Integer) request.getAttribute("totalMontant");
    Integer totalQuantite = (Integer) request.getAttribute("totalQuantite");

    // Récupérer les filtres
    String staffId = (String) request.getAttribute("filtreStaffId");
    String roleId = (String) request.getAttribute("filtreRoleId");
    String rayonId = (String) request.getAttribute("filtreRayonId");
    String categorieId = (String) request.getAttribute("filtreCategorieId");
    String sousCategorieId = (String) request.getAttribute("filtreSousCategorieId");
    String platId = (String) request.getAttribute("filtrePlatId");
    String dateDebut = (String) request.getAttribute("filtreDateDebut");
    String dateFin = (String) request.getAttribute("filtreDateFin");
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
            <h2 class="h4 mb-3">
              <i class="fe fe-clipboard fe-32 align-self-center text-black"></i>
              Rapport des commandes
            </h2>

            <!-- Section filtres hiérarchiques -->
            <div class="card shadow mb-4">
              <div class="card-header">
                <h5 class="card-title mb-0">Filtres hiérarchiques</h5>
              </div>
              <div class="card-body">
                <form method="GET" action="CommandeServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="rapport-commandes-by-filtres">
                  
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
                      <a href="CommandeServlet?action=rapport-commandes-by-filtres" class="btn btn-secondary">
                        <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                      </a>
                      <button type="button" class="btn btn-success" id="btnExport">
                        <i class="fe fe-download fe-16"></i> Exporter
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            <!-- Fin de la section des filtres -->

            <!-- Statistiques -->
            <div class="row mb-4">
              <div class="col-md-4">
                <div class="card border-left-primary shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                          Nombre d'articles vendus</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= totalQuantite != null ? totalQuantite : 0 %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-shopping-cart fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card border-left-success shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                          Chiffre d'affaires</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= totalMontant != null ? totalMontant : 0 %> HTG</div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-dollar-sign fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card border-left-info shadow h-100 py-2">
                  <div class="card-body">
                    <div class="row no-gutters align-items-center">
                      <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                          Nombre de références</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800"><%= rapports != null ? rapports.size() : 0 %></div>
                      </div>
                      <div class="col-auto">
                        <i class="fe fe-list fe-2x text-gray-300"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Tableau des rapports -->
            <div class="card shadow">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="m-0 font-weight-bold text-primary">Détail des ventes</h6>
                <div>
                  <span class="badge badge-light border">
                    <i class="fe fe-info mr-1"></i>
                    <%= rapports != null ? rapports.size() : 0 %> articles trouvés
                  </span>
                </div>
              </div>
              <div class="card-body">
                <div class="table-responsive">
                  <table id="rapportsTable" class="table table-hover">
                    <thead class="">
                      <tr>
                        <th>Plat / Produit</th>
                        <th class="text-center">Quantité vendue</th>
                        <th class="text-right">Prix unitaire</th>
                        <th class="text-right">Montant total</th>
                        
                      </tr>
                    </thead>
                    <tbody>
                      <% if(rapports != null && !rapports.isEmpty()) {
                            int index = 1;
                            for(RapportCommande r : rapports) { 
                                String type = r.getPlatId() != null ? "Plat" : "Produit";
                                String badgeClass = r.getPlatId() != null ? "badge-info" : "badge-warning";
                        %>
                        <tr>
                          <td>
                            <strong><%= r.getNomPlat() != null ? r.getNomPlat() : "N/A" %></strong>
                            <% if(r.getPlatId() != null) { %>
                              <br><small class="text-muted">ID Plat: <%= r.getPlatId() %></small>
                            <% } else if(r.getProduitId() != null) { %>
                              <br><small class="text-muted">ID Produit: <%= r.getProduitId() %></small>
                            <% } %>
                          </td>
                          <td class="text-center">
                            <span class="badge badge-pill badge-primary"><%= r.getQuantiteTotale() %></span>
                          </td>
                          <td class="text-right"><%= r.getPrixUnitaire() %> HTG</td>
                          <td class="text-right font-weight-bold text-success"><%= r.getMontantTotal() %> HTG</td>
                        </tr>
                      <%   }
                        } else { %>
                        <tr>
                          <td colspan="6" class="text-center">
                            <div class="alert alert-info">
                              <i class="fe fe-info mr-2"></i>
                              Aucune donnée trouvée avec les filtres sélectionnés.
                            </div>
                          </td>
                        </tr>
                      <% } %>
                    </tbody>
                    <tfoot class="total-row">
                      <tr>
                        <th colspan="1" class="text-right">TOTAL</th>
                        <th class="text-center"><%= totalQuantite != null ? totalQuantite : 0 %></th>
                        <th class="text-right">-</th>
                        <th class="text-right"><%= totalMontant != null ? totalMontant : 0 %> HTG</th>
                        <th></th>
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
  </div>
</main>

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
    
    // Initialiser DataTable
    if ($.fn.DataTable) {
        $('#rapportsTable').DataTable({
            "paging": true,
            "pageLength": 25,
            "lengthMenu": [10, 25, 50, 100],
            "searching": true,
            "ordering": true,
            "order": [[4, "desc"]], // Tri par montant total décroissant
            "info": true,
            "responsive": true,
            "language": {
                "url": "//cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json"
            },
            "dom": '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>' +
                   '<"row"<"col-sm-12"tr>>' +
                   '<"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>',
            "columnDefs": [
                { "orderable": false, "targets": [0, 5] }, // Désactiver le tri sur les colonnes # et Type
                { "className": "dt-center", "targets": [2] },
                { "className": "dt-right", "targets": [3, 4] }
            ]
        });
    }
    
    // Initialiser les dates si vides
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
    
    // Gestion de l'export
    $('#btnExport').on('click', function() {
        // Construire l'URL avec les paramètres actuels
        let exportUrl = 'CommandeServlet?action=export-rapport';
        const params = $('#filterForm').serialize();
        
        if (params) {
            exportUrl += '&' + params;
        }
        
        // Ouvrir dans une nouvelle fenêtre ou télécharger
        window.open(exportUrl, '_blank');
    });
});
</script>