
            <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Commande,java.text.SimpleDateFormat,java.util.Date,com.google.gson.Gson,com.spot69.model.Utilisateur,com.spot69.model.Role"%>
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
</style>

<%
// Formatteur de date pour l'input date
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
String today = dateFormat.format(new Date());

// Récupérer les listes d'utilisateurs et de rôles depuis la requête
List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
List<Role> roles = (List<Role>) request.getAttribute("roles");

// Récupérer les valeurs des filtres précédents pour les réafficher
String staffId = request.getParameter("staffId");
String roleId = request.getParameter("roleId");
String dateDebut = request.getParameter("dateDebut");
String dateFin = request.getParameter("dateFin");
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
                Mes Commandes
              </h2>
              <div class="d-flex flex-column align-items-end">
              
                <form method="GET" action="CommandeServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="lister">
                  
                 
                  <div class="filter-container mt-2">
                 
                    <!-- Range Picker pour les dates -->
                    <div class="form-group">
                      <label for="dateRange" class="sr-only">Période</label>
                      <div class="date-range-container">
                        <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                               placeholder="Date début" value="<%= dateDebut != null ? dateDebut : "" %>">
                        <span>à</span>
                        <input type="date" class="form-control" id="dateFin" name="dateFin" 
                               placeholder="Date fin" value="<%= dateFin != null ? dateFin : "" %>">
                      </div>
                    </div>
                  </div>
              
              	 <div class="btn-group-filter mt-2">
                    <button type="submit" class="btn btn-primary">
                      <i class="fe fe-filter fe-16"></i> Appliquer les filtres
                    </button>
                    <a href="CommandeServlet?action=lister" class="btn btn-secondary">
                      <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                    </a>
                  </div>
                </form>
                 
              </div>
            </div>

            <div class="card shadow">
              <div class="card-body">
                <table class="table datatables" id="dataTableCommandes">
                  <thead>
                    <tr>
                      <th>Numéro commande</th>
                      <th>Client</th>
                      <th>Date</th>
                      <th>Montant total</th>
                      <th>Statut</th>
                      <th>Notes</th>
                    </tr>
                  </thead>
                  <tbody>
                    <%
                    List<Commande> commandes = (List<Commande>) request.getAttribute("commandesCredit");
                    java.math.BigDecimal totalMontant = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal totalPaye = java.math.BigDecimal.ZERO;

                    if (commandes != null) {
                        for (Commande commande : commandes) {
                            if (commande.getMontantTotal() != null) {
                                totalMontant = totalMontant.add(commande.getMontantTotal());
                            }
                            if (commande.getMontantPaye() != null) {
                                totalPaye = totalPaye.add(commande.getMontantPaye());
                            }
                        }
                    }
                    java.math.BigDecimal resteAPayer = totalMontant.subtract(totalPaye);
                    if (commandes != null) {
                        for (Commande commande : commandes) {
                    %>
         
                    
                    <tr>
                      <td class="numero-commande" data-id="<%= commande.getId() %>">
                        <%= commande.getNumeroCommande() %>
                      </td>
                      <td><%= commande.getClient().getLogin() %></td>
                      <td><%= commande.getDateCommande() %></td>
                      <td><%= commande.getMontantTotal() %> HTG</td>
                      <%
                      String statut = commande.getStatutCommande();
                      String badgeClass = "badge-secondary"; // par défaut

                      switch (statut) {
                          case "EN_PREPARATION": badgeClass = "badge-warning"; break;
                          case "PRETE":          badgeClass = "badge-primary"; break;
                          case "ANNULE":         badgeClass = "badge-danger"; break;
                          case "EN_ATTENTE":     badgeClass = "badge-light"; break;
                          case "LIVRE":         badgeClass = "badge-success"; break;
                      }
                      %>
                      <td><span class="badge <%= badgeClass %>"><%= statut %></span></td>
                      <td><%= commande.getNotes() %></td>
                    </tr>
                    <%
                        }
                    }
                    %>
                  </tbody>
                  <tfoot>
  <tr>
    <th colspan="3" class="text-right">Totaux :</th>
    <th><%= totalMontant %> HTG</th>
    <th colspan="2"></th>
  </tr>
  <tr>
    <th colspan="3" class="text-right text-success">Déjà payé :</th>
    <th class="text-success"><%= totalPaye %> HTG</th>
    <th colspan="2"></th>
  </tr>
  <tr>
    <th colspan="3" class="text-right text-danger">Reste à payer :</th>
    <th class="text-danger"><%= resteAPayer %> HTG</th>
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

<jsp:include page="footer.jsp" />
           <script type="text/javascript">
    // Récupérer les commandes crédit depuis l'attribut JSP
    var commandesCredit = <%= new com.google.gson.Gson().toJson(request.getAttribute("commandesCredit")) %>;

    console.log("commandesCredit :", commandesCredit);

    if (commandesCredit && commandesCredit.length > 0) {
        console.log("Nombre de commandes crédit : " + commandesCredit.length);
        commandesCredit.forEach(function(c) {
            console.log("Commande ID:", c.id,
                        "Numéro:", c.numeroCommande,
                        "Montant total:", c.montantTotal,
                        "Montant payé:", c.montantPaye,
                        "Statut crédit:", c.credit ? c.credit.statut : "N/A");
        });
    } else {
        console.log("Aucune commande crédit trouvée");
    }
</script>
<script>
$(document).ready(function () {
	  const table = $('#dataTableCommandes').DataTable({
		    "order": [[2, "desc"]], // Tri par date décroissante par défaut
		    "columnDefs": [
		      { 
		        "type": "date", 
		        "targets": 2,
		        "render": function(data, type, row) {
		          // Pour le tri et l'affichage, utiliser la date complète
		          if (type === 'display' || type === 'filter') {
		            return data.split(' ')[0]; // Afficher seulement la date sans l'heure
		          }
		          return data; // Pour le tri, utiliser la valeur originale
		        }
		      }
		    ]
		  });

		  // Fonction pour normaliser la date (extraire YYYY-MM-DD)
		  function normalizeDate(datetimeStr) {
		    if (!datetimeStr) return null;
		    return datetimeStr.split(' ')[0]; // Prendre seulement la partie avant l'espace
		  }

		  // Fonction pour filtrer par date
		  function filterByDate(selectedDate) {
		    if (!selectedDate) return;
		    
		    // Supprimer tous les filtres existants
		    $.fn.dataTable.ext.search = [];
		    
		    // Ajouter notre nouveau filtre
		    $.fn.dataTable.ext.search.push(
		      function(settings, data, dataIndex) {
		        const rowDate = normalizeDate(data[2]); // Normaliser la date de la ligne
		        return rowDate === selectedDate; // Comparer avec la date sélectionnée
		      }
		    );
		    
		    table.draw();
		  }

		  // Appliquer le filtre par défaut (date du jour)
		  filterByDate($('#filterDate').val());
		  
		  // Gestion du clic sur le bouton de filtre
		  $('#filterButton').click(function() {
		    filterByDate($('#filterDate').val());
		  });
		  
		  // Gestion de la touche Entrée dans le champ date
		  $('#filterDate').keypress(function(e) {
		    if (e.which === 13) { // 13 = touche Entrée
		      filterByDate($('#filterDate').val());
		    }
		  });
  // ... (le reste de votre code existant) ...
  
  //Au clic sur le numéro de commande
  $('#dataTableCommandes tbody').on('click', 'td.numero-commande', function () {
    var tr = $(this).closest('tr');
    var row = $('#dataTableCommandes').DataTable().row(tr);
    var commandeId = $(this).data('id');

    if (row.child.isShown()) {
      row.child.hide();
      tr.removeClass('shown');
    } else {
      $.ajax({
        url: "CommandeServlet?action=getdetailsjson&id=" + commandeId,
        method: "GET",
        dataType: "json",
        success: function (details) {
          if (details.length > 0) {
            var html = '<table class="table table-sm mb-0">';
            html += '<thead><tr><th>Produit / Plat</th><th>Qte</th><th>Prix Unitaire</th><th>Total</th><th>Note</th></tr></thead><tbody>';

            details.forEach(function (d) {
              var nomProduit = d.produit && d.produit.nom ? d.produit.nom
                              : (d.plat && d.plat.nom ? d.plat.nom : '-');
              var total = d.quantite * d.prixUnitaire;

              html += "<tr>" +
                        "<td>" + nomProduit + "</td>" +
                        "<td>" + d.quantite + "</td>" +
                        "<td>" + d.prixUnitaire + " HTG</td>" +
                        "<td>" + total + " HTG</td>" +
                        "<td>" + (d.notes ? d.notes : '') + "</td>" +
                      "</tr>";
            });

            html += "</tbody></table>";
            row.child(html).show();
            tr.addClass("shown");
          } else {
            row.child('<div class="p-2 text-muted">Aucun détail pour cette commande.</div>').show();
            tr.addClass("shown");
          }
        },
        error: function () {
          row.child('<div class="p-2 text-danger">Erreur lors du chargement des détails.</div>').show();
          tr.addClass("shown");
        }
      });
    }
  });
});
</script>