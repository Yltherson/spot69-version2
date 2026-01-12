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
                Historiques Commande Places
              </h2>
              <div class="d-flex flex-column align-items-end">
                <!-- <a href="CommandeServlet?action=placer-commande" class="btn btn-outline-primary">
                  <i class="fe fe-plus fe-16"></i> Creer une commande
                </a> -->
                
                <form method="GET" action="CommandeServlet" id="filterForm" class="w-100">
                  <input type="hidden" name="action" value="getallcommandesbyfiltres">
                  
                 
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
                    <a href="CommandeServlet?action=liste-commande-par-staff" class="btn btn-secondary">
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
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <%
                    List<Commande> commandes = (List<Commande>) request.getAttribute("commandes");
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    String commandesJson = gson.toJson(commandes);
                    if (commandes != null) {
                        for (Commande commande : commandes) {
                    %>
                    <script>
					    console.log("Liste des commandes :", <%= commandesJson %>);
					</script>
                    <tr>
                      <td class="numero-commande" data-id="<%= commande.getId() %>">
                        <%= commande.getNumeroCommande() %>
                      </td>
                      <td>
					    <%= (commande.getClient() != null) ? commande.getClient().getLogin() : "Indefini" %>
					</td>
                      
                      <td><%= commande.getDateCommande() %></td>
                      <td><%= commande.getMontantTotal() %> HTG</td>
                      <td>
                        <form method="POST" class="form-modifier-statut" action="CommandeServlet">
                          <input type="hidden" name="action" value="modifierStatut">
                          <input type="hidden" name="redirectTo" value="liste-com-passer-par-staff.jsp">
                          <input type="hidden" name="idCommande" value="<%= commande.getId() %>">
                         <select id="statutSelect-<%= commande.getId() %>" name="nouveauStatut"
						        class="form-control form-control-sm statut-select"
						        data-id="<%= commande.getId() %>"
						        onchange="updateColor(this)">
						  <option value="EN_PREPARATION" <%= "EN_PREPARATION".equals(commande.getStatutCommande()) ? "selected" : "" %>>EN_PREPARATION</option>
						  <option value="PRETE" <%= "PRETE".equals(commande.getStatutCommande()) ? "selected" : "" %>>PRETE</option>
						  <option value="ANNULE" <%= "ANNULE".equals(commande.getStatutCommande()) ? "selected" : "" %>>ANNULE</option>
						  <option value="EN_ATTENTE" <%= "EN_ATTENTE".equals(commande.getStatutCommande()) ? "selected" : "" %>>EN_ATTENTE</option>
						  <option value="LIVRE" <%= "LIVRE".equals(commande.getStatutCommande()) ? "selected" : "" %>>LIVRE</option>
						</select>
                        </form>
                      </td>
                      <td><%= commande.getNotes() %></td>
                      <td>
                        <div class="dropdown">
                          <button class="btn btn-sm btn-secondary dropdown-toggle" type="button" data-toggle="dropdown">
                            Action
                          </button>
                          <div class="dropdown-menu dropdown-menu-right">
                            <a class="dropdown-item btn-details-commande" href="#" data-id="<%= commande.getId() %>">Détails</a>
                            <% if (!"LIVRE".equals(commande.getStatutCommande())) { %>
                              <a href="#" class="dropdown-item btn-valider-commande" 
                                 data-toggle="modal" 
                                 data-target=".modal-valider-commande"
                                 data-numero="<%= commande.getNumeroCommande() %>"
                                 data-idcommande="<%= commande.getId() %>"
                                 data-montant="<%= commande.getMontantTotal() %>">
                                 Valider
                              </a>
                            <% } %>
                           <%
								  String commandeJson = gson.toJson(commande);
								  String encodedJson = java.net.URLEncoder.encode(commandeJson, "UTF-8");
								  String commandeId = commande.getNumeroCommande();
								%>
								<a class="dropdown-item"
								   href="spot69://commande/imprimer?commandeId=<%= commandeId %>&data=<%= encodedJson %>">
								   Imprimer
								</a>
				

                            
                            <a class="dropdown-item text-danger" href="CommandeServlet?action=supprimer&id=<%= commande.getId() %>&redirectTo=liste-com-passer-par-staff.jsp" onclick="return confirm('Supprimer ?');">Supprimer</a>
                          </div>
                        </div>
                      </td>
                    </tr>
                    <%
                        }
                    }
                    %>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<!-- Modal modification fournisseur -->
<div class="modal fade modal-valider-commande modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalUpdateFournisseurLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <form method="POST" action="CommandeServlet" id="formEditCommande">
        <input type="hidden" name="action" id="hiddenActionField" />
        <input type="hidden" name="redirectTo" value="liste-com-passer-par-staff.jsp">
        <input type="hidden" name="idCommande" id="idCommande" />

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

          <div class="form-group">
            <label for="numeroCommandeModal">Numéro Commande *</label>
            <input type="text" class="form-control" id="numeroCommandeModal" name="numeroCommande" readonly>
          </div>
          
          <div class="form-group">
            <label for="montantTotalModal">Montant total *</label>
            <input type="text" class="form-control" id="montantTotalModal" name="montantTotal" readonly>
          </div>

          <div class="form-group">
            <label for="actionSelect">Quelle action voulez-vous faire ?</label>
            <select class="form-control" id="actionSelect" name="action" required>
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
              <input type="text" class="form-control" id="versement" name="montantPaye" placeholder="" >
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
	// Initialisation de DataTable
	  const table = $('#dataTableCommandes').DataTable({
	    "order": [[2, "desc"]], // Tri par date décroissante par défaut
	    "columnDefs": [
	      { "type": "date", "targets": 2 } // Définir le type 'date' pour la colonne des dates
	    ]
	  });

	  // Fonction pour extraire la partie date seulement (sans l'heure)
	  function extractDateOnly(datetimeStr) {
	    if (!datetimeStr) return null;
	    return datetimeStr.split(' ')[0]; // Prendre seulement la partie avant l'espace
	  }

	  // Fonction pour filtrer par date
	  function filterByDate(selectedDate) {
	    if (!selectedDate) return;
	    
	    // Convertir la date sélectionnée en format YYYY-MM-DD
	    const filterDate = new Date(selectedDate);
	    const filterDateStr = filterDate.toISOString().split('T')[0];
	    
	    // Filtrer les lignes où la date correspond
	    $.fn.dataTable.ext.search.push(
	      function(settings, data, dataIndex) {
	        const rowDateTime = data[2]; // La date est dans la 3ème colonne (index 2)
	        const rowDateStr = extractDateOnly(rowDateTime);
	        
	        return rowDateStr === filterDateStr;
	      }
	    );
	    
	    table.draw();
	    $.fn.dataTable.ext.search.pop(); // Retirer le filtre après application
	  }
  
  // Appliquer le filtre par défaut (date du jour)
  /* filterByDate($('#filterDate').val()); */
  
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
  
  // Lorsqu'on change le statut dans le select
  $('#dataTableCommandes').on('change', '.statut-select', function () {
    const form = $(this).closest('form');

    if (confirm('Modifier le statut de cette commande ?')) {
      form.submit();
    } else {
      // Recharger la page pour remettre le select à l'ancien état
      window.location.reload();
    }
  });

  // Au clic sur bouton Valider dans dropdown
  $('#dataTableCommandes tbody').on('click', 'a.btn-valider-commande', function () {
    const numero = $(this).data('numero');
    const montant = $(this).data('montant');
    const idCom = $(this).data('idcommande');

    // Remplir les champs du modal
    $('#idCommande').val(idCom);
    $('#numeroCommandeModal').val(numero);
    $('#montantTotalModal').val(montant + ' HTG');
  });
  
  // Au clic sur le numéro de commande
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

  $('#dataTableCommandes tbody').on('click', 'a.btn-details-commande', function (e) {
    e.preventDefault();

    const tr = $(this).closest('tr');
    const row = table.row(tr);
    const commandeId = $(this).data('id');

    if (row.child.isShown()) {
      row.child.hide();
      tr.removeClass('shown');
    } else {
      $.ajax({
        url: 'CommandeServlet?action=getdetailsjson&id=' + commandeId,
        method: 'GET',
        dataType: 'json',
        success: function (details) {
          if (details.length > 0) {
            let html = '<table class="table table-sm mb-0">';
            html += '<thead><tr><th>Produit / Plat</th><th>Qte</th><th>Prix Unitaire</th><th>Total</th><th>Note</th></tr></thead><tbody>';

            details.forEach(function (d) {
              const nomProduit = d.produit && d.produit.nom ? d.produit.nom 
                                : (d.plat && d.plat.nom ? d.plat.nom : '-');
              const total = d.quantite * d.prixUnitaire;
              html += '<tr>' +
                          '<td>' + nomProduit + '</td>' +
                          '<td>' + d.quantite + '</td>' +
                          '<td>' + d.prixUnitaire + ' HTG</td>' +
                          '<td>' + total + ' HTG</td>' +
                          '<td>' + (d.notes ?? '') + '</td>' +
                      '</tr>';
            });

            html += '</tbody></table>';
            row.child(html).show();
            tr.addClass('shown');
          } else {
            row.child('<div class="p-2 text-muted">Aucun détail pour cette commande.</div>').show();
            tr.addClass('shown');
          }
        },
        error: function () {
          row.child('<div class="p-2 text-danger">Erreur lors du chargement des détails.</div>').show();
          tr.addClass('shown');
        }
      });
    }
  });
  
  // Au changement du select action, afficher les champs correspondants
  $('#actionSelect').on('change', function() {
    const val = $(this).val();
    const form = $('#formEditCommande');
    const baseUrl = '<%=request.getContextPath()%>/CommandeServlet';

    if(val === 'valider'){
      $('#validerFields').show();
      $('#modifierStatutFields').hide();
      $('#modePaiement').attr('required', true);
      $('#versement').attr('required', true);
      $('#statutCommandeSelect').removeAttr('required');
      $('#hiddenActionField').val('valider');
    } else if(val === 'modifierStatut'){
      $('#validerFields').hide();
      $('#modifierStatutFields').show();
      $('#statutCommandeSelect').attr('required', true);
      $('#modePaiement').removeAttr('required');
      $('#versement').removeAttr('required');
      $('#hiddenActionField').val('modifierStatut');
    } else {
      $('#validerFields').hide();
      $('#modifierStatutFields').hide();
      $('#modePaiement').removeAttr('required');
      $('#versement').removeAttr('required');
      $('#statutCommandeSelect').removeAttr('required');
      form.attr('action', baseUrl);
    }
  });
});
</script>