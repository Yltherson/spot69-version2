<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,java.util.Map,com.spot69.model.Commande,com.spot69.model.Credit,com.spot69.model.Depense,com.spot69.model.Utilisateur, java.util.HashMap, java.util.ArrayList,com.spot69.model.MenuCategorie"%>
<meta charset="UTF-8">

<style>
  .main-table { margin-left: 0; }
  .child-table { margin-left: 30px; width: calc(100% - 30px); }
  .grandchild-table { margin-left: 60px; width: calc(100% - 60px); }
  .detail-table { margin-left: 90px; width: calc(100% - 90px); }
  td.caissier-nom { cursor: pointer; text-decoration: underline; padding-left: 10px !important; }
  td.caissier-nom:hover { color: #b08c3e; }
  .staff-nom { cursor: pointer; text-decoration: underline; padding-left: 30px !important; }
  .filter-container { display: flex; align-items: center; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
  .filter-container .form-group { margin-bottom: 0; min-width: 150px; }
  .date-range-container { display: flex; align-items: center; gap: 5px; }
  tr td u { margin-left: 20px; }
/*   .detail-row { background-color: #f8f9fa; }
  .solde-row { background-color: #fff3cd; }
  .cash-row { background-color: #e9ecef; font-weight: bold; } */
  .child-staff { cursor: pointer; }
</style>

<%
    String dateDebutDate = request.getParameter("dateDebut");
    String dateFinDate = request.getParameter("dateFin");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar2.jsp" />
<%-- <jsp:include page="sidebar.jsp" /> --%>

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">

        <!-- Section filtres -->
        <div class="card shadow mb-4">
          <div class="card-header">
            <h5 class="card-title mb-0">Filtres</h5>
          </div>
          <div class="card-body">
            <form id="filterForm" class="w-100">
              <input type="hidden" name="action" value="caissiere-commandes-cashed">
              <input type="hidden" name="caissierId" id="caissierId" value="">
              <div class="filter-container">
				<div class="form-group">
				  <label>Période</label>
				  <div class="date-range-container">
				    <input type="datetime-local" class="form-control" id="dateDebut" name="dateDebut"
				  value="<%= request.getAttribute("dateDebutStr") != null ? 
				          ((String)request.getAttribute("dateDebutStr")).replace(" ", "T").substring(0, 16) : 
				          "" %>">
				
				<input type="datetime-local" class="form-control" id="dateFin" name="dateFin"
				  value="<%= request.getAttribute("dateFinStr") != null ? 
				          ((String)request.getAttribute("dateFinStr")).replace(" ", "T").substring(0, 16) : 
				          "" %>">
				  </div>
				</div>


			<div class="form-group" style="display:none">
			  <label for="filtreCategorie">Catégorie :</label>
			  <select class="form-control" id="filtreCategorie" name="categorieId">
			      <option value="">-- Toutes --</option>
			      <% 
			      List<MenuCategorie> categories = (List<MenuCategorie>) request.getAttribute("categories");
			      Integer selectedCategorieId = (Integer) request.getAttribute("categorieId");
			      if (categories != null) {
			          for (MenuCategorie c : categories) {
			              if (c.getParentId() == null) { 
			                  boolean selected = (selectedCategorieId != null && selectedCategorieId.equals(c.getId()));
			      %>
			                  <option value="<%=c.getId()%>" <%= selected ? "selected" : "" %>><%=c.getNom()%></option>
			      <%      }
			          }
			      } 
			      %>
			  </select>
</div>

<div class="form-group" style="display:none">
  <label for="filtreSousCategorie">Sous-catégorie :</label>
  <select class="form-control" id="filtreSousCategorie" name="sousCategorieId" <%= (selectedCategorieId == null) ? "disabled" : "" %>>
      <option value="">-- Toutes --</option>
      <%
      Integer selectedSousCategorieId = (Integer) request.getAttribute("sousCategorieId");
      List<MenuCategorie> sousCategories = (List<MenuCategorie>) request.getAttribute("sousCategories");
      if (sousCategories != null && selectedCategorieId != null) {
          for (MenuCategorie sc : sousCategories) {
              boolean selected = (selectedSousCategorieId != null && selectedSousCategorieId.equals(sc.getId()));
      %>
              <option value="<%=sc.getId()%>" <%= selected ? "selected" : "" %>><%=sc.getNom()%></option>
      <%
          }
      }
      %>
  </select>
</div>
                <div class="form-group align-self-end">
                  <button type="submit" id="btnFiltrer" class="btn btn-primary">
                    <i class="fe fe-filter fe-16"></i> Appliquer
                  </button>
                  <a href="CommandeServlet?action=caissiere-commandes-cashed" class="btn btn-secondary">
                    <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>

<div class="card shadow mb-4">
  <div class="card-header">
    <h5 class="card-title mb-0">Commandes encaissées par caissiers</h5>
  </div>
  <div class="card-body">
<table class="table table-hover main-table" id="tableCaissiers">
  <thead>
    <tr>
      <th></th>
      <th>Nb Commandes</th>
      <th>TOTALE CAISSE</th>
    </tr>
  </thead>
  <tbody>
<%
    List<Commande> commandes = (List<Commande>) request.getAttribute("commandes");
    List<Depense> depenses = (List<Depense>) request.getAttribute("depenses");
    List<Commande> commandesCredits = (List<Commande>) request.getAttribute("commandesCredits");

    int grandTotal = 0;
    int totalDepenses = 0;
    int totalCredits = 0;
    int totalSolde = 0;

    if (commandes != null) {
        Map<Integer, List<Commande>> grouped = new HashMap<Integer, List<Commande>>();
        
        // 1. Regrouper les commandes par caissier
        for (Commande c : commandes) {
            Integer caissierId = c.getCaissier().getId();
            if (!grouped.containsKey(caissierId)) {
                grouped.put(caissierId, new ArrayList<Commande>());
            }
            grouped.get(caissierId).add(c);
        }

        // 2. Traiter chaque caissier
        for (Map.Entry<Integer, List<Commande>> entry : grouped.entrySet()) {
            Utilisateur caissier = entry.getValue().get(0).getCaissier();
            List<Commande> cmds = entry.getValue();

            // Calculer les totaux pour ce caissier
            int totalToutesCommandes = 0;
            int soldeCaissier = 0;
            int nbCommandesSansSolde = 0;
            int nbCommandesSolde = 0;
            
            for (Commande c : cmds) {
                if (c.getMontantTotal() != null) {
                    int montant = c.getMontantTotal().intValue();
                    totalToutesCommandes += montant;
                    
                    if ("SOLDE".equalsIgnoreCase(c.getModePaiement())) {
                        soldeCaissier += montant;
                        nbCommandesSolde++;
                    } else {
                        nbCommandesSansSolde++;
                    }
                }
            }
            
            // Le TOTAL affiché à côté du nom = toutes les commandes (inclut SOLDE)
            grandTotal += totalToutesCommandes;
            totalSolde += soldeCaissier;
            
            // 3. Calculer les dépenses pour ce caissier
            int depensesCaissier = 0;
            if (depenses != null) {
                for (Depense d : depenses) {
                    if (d.getCaissiereId() != 0 && d.getCaissiereId() == caissier.getId()) {
                        depensesCaissier += (d.getMontant() != 0 ? d.getMontant() : 0);
                    }
                }
            }
            totalDepenses += depensesCaissier;

            // 4. Calculer les crédits pour ce caissier
            int creditsCaissier = 0;
            if (commandesCredits != null) {
                for (Commande cc : commandesCredits) {
                    if (cc.getCashedBy() == caissier.getId()) {
                        if (cc.getCredit() != null) {
                            int restant = cc.getCredit().getMontantTotal() - cc.getCredit().getMontantPaye();
                            if (restant > 0) creditsCaissier += restant;
                        }
                    }
                }
            }
            totalCredits += creditsCaissier;

            // 5. Calculer le CASH (totalToutesCommandes - dépenses - crédits - solde)
            int cash = totalToutesCommandes - depensesCaissier - creditsCaissier - soldeCaissier;
%>
    <!-- Ligne principale du caissier -->
    <tr class="caissier-main-row">
      <td class="caissier-nom" data-id="<%= caissier.getId() %>">
        <u><%= caissier.getNom() + " " + caissier.getPrenom() %></u>
        <% if (nbCommandesSolde > 0) { %>
          <span class="badge badge-warning ml-2">(+<%= nbCommandesSolde %> SOLDE)</span>
        <% } %>
      </td>
      <td><%= nbCommandesSansSolde + nbCommandesSolde %></td>
      <td><%= new java.text.DecimalFormat("#").format(totalToutesCommandes) %> HTG</td>
    </tr>

    <!-- Ligne Dépenses -->
    <tr class="detail-row">
      <td></td>
      <td class="text-right pr-4">Dépenses</td>
      <td>-<%= new java.text.DecimalFormat("#").format(depensesCaissier) %> HTG</td>
    </tr>

    <!-- Ligne Crédit restant -->
    <tr class="detail-row">
      <td></td>
      <td class="text-right pr-4">Crédit restant</td>
      <td>-<%= new java.text.DecimalFormat("#").format(creditsCaissier) %> HTG</td>
    </tr>

    <!-- Ligne Cash Solde (si applicable) -->
    <% if (soldeCaissier > 0) { %>
    <tr class="solde-row">
      <td></td>
      <td class="text-right pr-4">Cash Solde</td>
      <td>-<%= new java.text.DecimalFormat("#").format(soldeCaissier) %> HTG</td>
    </tr>
    <% } %>

    <!-- Ligne CASH finale -->
    <tr class="cash-row">
      <td></td>
      <td class="text-right pr-4"><strong>CASH</strong></td>
      <td><strong><%= new java.text.DecimalFormat("#").format(cash) %> HTG</strong></td>
    </tr>

    <!-- Ligne vide pour séparation -->
    <tr style="height: 10px;"><td colspan="3"></td></tr>
<%
        }
    }
    
    // Calcul du CASH global
    int cashGlobal = grandTotal - totalDepenses - totalCredits - totalSolde;
%>
  </tbody>

  <!-- Pied de tableau -->
<%--   <tfoot>
    <tr >
      <th></th>
      <th class="text-right pr-4">Total commandes</th>
      <th><%= new java.text.DecimalFormat("#").format(grandTotal) %> HTG</th>
    </tr>
    <tr >
      <th></th>
      <th class="text-right pr-4">Dont paiements SOLDE</th>
      <th>-<%= new java.text.DecimalFormat("#").format(totalSolde) %> HTG</th>
    </tr>
    <tr >
      <th></th>
      <th class="text-right pr-4">Total à encaisser</th>
      <th><%= new java.text.DecimalFormat("#").format(grandTotal - totalSolde) %> HTG</th>
    </tr>
    <tr>
      <th></th>
      <th class="text-right pr-4">Dépenses totales</th>
      <th>-<%= new java.text.DecimalFormat("#").format(totalDepenses) %> HTG</th>
    </tr>
    <tr>
      <th></th>
      <th class="text-right pr-4">Crédit total restant</th>
      <th>-<%= new java.text.DecimalFormat("#").format(totalCredits) %> HTG</th>
    </tr>
    <tr>
      <th></th>
      <th class="text-right pr-4">CASH FINAL EN CAISSE</th>
      <th><%= new java.text.DecimalFormat("#").format(cashGlobal) %> HTG</th>
    </tr>
  </tfoot> --%>
</table>
  </div>
</div>

      </div>
    </div>
  </div>
  <jsp:include page="bottombar.jsp"/>
</main>

<jsp:include page="footer.jsp"/>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // Retirer DataTables pour éviter les conflits - utiliser un tableau simple
    // var tableCaissiers = $('#tableCaissiers').DataTable({
    //     "order": [[2, "desc"]],
    //     "paging": false,
    //     "info": false,
    //     "ordering": true,
    //     "columnDefs": [
    //         { "orderable": false, "targets": 0 },
    //         { "orderable": true, "targets": [1, 2] }
    //     ]
    // });
    
    var currentCaissierId = null;
    var expandedCaissiers = {}; // Pour garder trace des caissiers dépliés

    // Récupérer les valeurs des paramètres
    const urlParams = new URLSearchParams(window.location.search);
    const categorieId = urlParams.get('categorieId');
    const sousCategorieId = urlParams.get('sousCategorieId');
    
    // Initialiser les sélecteurs
    if (categorieId) {
        $('#filtreCategorie').val(categorieId);
        loadSousCategories(categorieId).then(() => {
            if (sousCategorieId) {
                $('#filtreSousCategorie').val(sousCategorieId);
            }
        });
    }

    function formatDateForServer(dateTimeString, isEndOfDay) {
        if (!dateTimeString) return '';
        
        if (dateTimeString.includes('T')) {
            const [datePart, timePart] = dateTimeString.split('T');
            if (isEndOfDay) {
                return datePart + ' 23:59:59';
            } else {
                return datePart + ' ' + timePart + ':00';
            }
        }
        
        return isEndOfDay ? dateTimeString + ' 23:59:59' : dateTimeString + ' 00:00:00';
    }
    
    function loadSousCategories(parentId){
        return new Promise((resolve) => {
            const $sous = $('#filtreSousCategorie');
            $sous.empty().append('<option value="">-- Toutes --</option>');
            if(!parentId){ 
                $sous.prop('disabled', true); 
                resolve();
                return; 
            }

            $.getJSON('MenuServlet', { action: 'sous-categories', parentId: parentId }, function(data){
                if(data && data.sousCategories && data.sousCategories.length>0){
                    data.sousCategories.forEach(function(sc){
                        $sous.append($('<option>', { value: sc.id, text: sc.nom }));
                    });
                    $sous.prop('disabled', false);
                } else { 
                    $sous.prop('disabled', true); 
                }
                resolve();
            });
        });
    }

    $('#filtreCategorie').on('change', function(){ 
        loadSousCategories($(this).val()); 
    });

    // Modifier la soumission du formulaire
    $('#filterForm').on('submit', function(e) {
        e.preventDefault();
        
        // Désactiver les champs désactivés pour qu'ils ne soient pas inclus dans la soumission
        $('#filtreSousCategorie').prop('disabled', false);
        
        // Récupérer les valeurs des champs
        var formData = $(this).serialize();
        
        // Réactiver le désactivé si nécessaire
        if ($('#filtreCategorie').val() === '') {
            $('#filtreSousCategorie').prop('disabled', true);
        }
        
        // Rediriger avec les paramètres de filtre
        window.location.href = 'CommandeServlet?' + formData;
    });

    // Child table au clic sur caissier
    $(document).on('click', 'td.caissier-nom', function(e) {
        e.stopPropagation();
        var caissierId = $(this).data('id');
        $('#caissierId').val(caissierId);
        currentCaissierId = caissierId;
        
        var mainRow = $(this).closest('tr.caissier-main-row');
        var caissierIndex = mainRow.index();
        
        // Vérifier si déjà déplié
        var nextRow = mainRow.next();
        if (nextRow.hasClass('child-details')) {
            // Replier
            $('.child-details').each(function() {
                if ($(this).data('parent-id') == caissierId) {
                    $(this).remove();
                }
            });
            expandedCaissiers[caissierId] = false;
            return;
        }
        
        // Déplier
        $.getJSON('CommandeServlet', {
            action: 'user-commandes-cashed-by-caissiere',
            caissierId: caissierId,
            dateDebut: $('#dateDebut').val(),
            dateFin: $('#dateFin').val(), 
            categorieId: $('#filtreCategorie').val(),   
            sousCategorieId: $('#filtreSousCategorie').val()
        }, function(utilisateurs) {
            if (!utilisateurs || utilisateurs.length === 0) {
                return;
            }
            
            // Supprimer d'abord les anciens détails pour ce caissier
            $('.child-details').each(function() {
                if ($(this).data('parent-id') == caissierId) {
                    $(this).remove();
                }
            });
            
            // Créer une nouvelle ligne pour les détails
            var html = '<tr class="child-details" data-parent-id="' + caissierId + '">';
            html += '<td colspan="3" class="p-0">';
            html += '<div class="child-details-content p-3" >';
            html += '<h6 class="mb-3">Détails par serveuse/client</h6>';
            html += '<table class="table table-sm table-bordered child-table">';
            html += '<thead><tr><th>Serveuse/Client</th><th>Nb Commandes</th><th>Total HTG</th></tr></thead><tbody>';
            
            utilisateurs.forEach(function(user) {
                var total = user.commandes.reduce((acc, c) => {
                    if (c.details && c.details.length > 0) {
                        return acc + c.details.reduce((sum, d) => sum + (parseFloat(d.prixUnitaire || 0) * parseInt(d.quantite || 0)), 0);
                    } else {
                        return acc + (parseFloat(c.montantTotal || 0));
                    }
                }, 0);
                
                html += '<tr class="child-staff" data-staff-id="' + user.id + '">';
                html += '<td class="staff-nom pl-4">' + user.nom + ' ' + user.prenom + '</td>';
                html += '<td>' + user.commandes.length + '</td>';
                html += '<td>' + total.toFixed(0) + ' HTG</td>';
                html += '</tr>';
            });
            
            html += '</tbody></table>';
            html += '</div>';
            html += '</td>';
            html += '</tr>';
            
            // Insérer après la ligne principale du caissier
            mainRow.after(html);
            expandedCaissiers[caissierId] = true;
        }).fail(function(jqXHR, textStatus, errorThrown) {
            console.error('Erreur lors du chargement des détails:', textStatus, errorThrown);
        });
    });

    // Affichage détails plat/produit avec filtres
    $(document).on('click', '.staff-nom', function(e) {
        e.stopPropagation();
        e.preventDefault();
        
        var staffRow = $(this).closest('tr.child-staff');
        var staffId = staffRow.data('staff-id');
        
        // Vérifier si déjà déplié
        var nextRow = staffRow.next();
        if (nextRow.hasClass('product-details')) {
            nextRow.remove();
            return;
        }
        
        var dateDebut = formatDateForServer($('#dateDebut').val(), false);
        var dateFin = formatDateForServer($('#dateFin').val(), true);
        var catId = $('#filtreCategorie').val();
        var sousId = $('#filtreSousCategorie').val();

        $.getJSON('CommandeServlet', {
            action: 'rapport-commandes-cashed-by-caissiere-with-filtres-json',
            staffId: staffId,
            caissierId: currentCaissierId,
            dateDebut: dateDebut,
            dateFin: dateFin,
            categorieId: catId,
            sousCategorieId: sousId
        }, function(data){
            if (!data || !data.rapports || data.rapports.length === 0) {
                return;
            }
            
            var html = '<tr class="product-details">';
            html += '<td colspan="3" class="p-0">';
            html += '<div class="product-details-content p-3" >';
            html += '<h6 class="mb-3">Détails des plats/produits</h6>';
            html += '<table class="table table-sm table-bordered grandchild-table">';
            html += '<thead><tr><th>Plat / Produit</th><th>Quantité totale</th><th>Prix unitaire</th><th>Montant total</th></tr></thead><tbody>';
            
            var totalQuantite = 0;
            var totalMontant = 0;
            
            data.rapports.forEach(function(r) {
                var q = parseInt(r.quantiteTotale || 0);
                var p = parseFloat(r.prixUnitaire || 0);
                var m = q * p;
                
                totalQuantite += q;
                totalMontant += m;
                
                html += '<tr>';
                html += '<td>' + (r.nomPlat || 'N/A') + '</td>';
                html += '<td>' + q + '</td>';
                html += '<td>' + p + '</td>';
                html += '<td>' + m + ' HTG</td>';
                html += '</tr>';
            });
            
            html += '</tbody>';
            html += '<tfoot><tr><th>Total</th><th>' + totalQuantite + '</th><th></th><th>' + totalMontant.toFixed(0) + ' HTG</th></tr></tfoot>';
            html += '</table>';
            html += '</div>';
            html += '</td>';
            html += '</tr>';
            
            // Insérer après la ligne du staff
            staffRow.after(html);
            
        }).fail(function(jqXHR, textStatus, errorThrown) {
            console.error('Erreur lors du chargement des détails produits:', textStatus, errorThrown);
        });
    });
});
</script>