<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Commande,com.spot69.model.Utilisateur,java.text.SimpleDateFormat"%>
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
</style>

<%
List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
List<Commande> commandes = (List<Commande>) request.getAttribute("commandes");

String clientId = request.getParameter("clientId");
String dateDebut = (String) request.getAttribute("dateDebut");
String dateFin = (String) request.getAttribute("dateFin");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">

        <div class="d-flex justify-content-between align-items-center mb-3">
          <h2 class="h4 mb-1">
            <i class="fe fe-credit-card fe-32 align-self-center text-black"></i>
            Liste des commandes à crédit
          </h2>
        </div>

        <!-- Filtres -->
        <div class="card shadow mb-4">
          <div class="card-header">
            <h5 class="card-title mb-0">Filtres</h5>
          </div>
          <div class="card-body">
            <form method="GET" action="CommandeServlet" id="filterForm">
              <input type="hidden" name="action" value="get-all-cmd-credit-for-client">

              <div class="filter-container">
                <!-- Sélecteur Client -->
                <div class="form-group">
                  <label for="clientId">Client</label>
                  <select class="form-control" id="clientId" name="clientId">
                    <option value="">Tous les clients</option>
                    <% if(utilisateurs != null) {
                      for(Utilisateur user : utilisateurs) { %>
                        <option value="<%= user.getId() %>" 
                          <%= (clientId != null && clientId.equals(String.valueOf(user.getId()))) ? "selected" : "" %>>
                          <%= user.getLogin() %>
                        </option>
                    <% }} %>
                  </select>
                </div>

                <!-- Période -->
                
					<div class="form-group">
					  <label>Période</label>
					  <div class="date-range-container">
					    <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
					           value="<%= (dateDebut != null) ? dateDebut : "" %>"
					           placeholder="Date début">
					    <span>à</span>
					    <input type="date" class="form-control" id="dateFin" name="dateFin" 
					           value="<%= (dateFin != null) ? dateFin : "" %>"
					           placeholder="Date fin">
					  </div>
					  <small class="form-text text-muted">Laisser vide pour toutes les dates</small>
					</div>
                <!-- Boutons -->
                <div class="form-group align-self-end">
                  <button type="submit" class="btn btn-primary">
                    <i class="fe fe-filter fe-16"></i> Appliquer
                  </button>
                  <a href="CommandeServlet?action=get-all-cmd-credit-for-client" class="btn btn-secondary">
                    <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>

        <!-- Tableau -->
        <div class="card shadow">
          <div class="card-body">
            <table class="table datatables" id="dataTableCommandes">
              <thead>
                <tr>
                  <th>Numéro</th>
                  <th>Client</th>
                  <th>Staff</th>
                  <th>Date</th>
                  <th>Total</th>
                  <th>Payé</th>
                  <th>Restant</th>
                  <th>Statut Crédit</th>
                  <!-- <th>Actions</th> -->
                </tr>
              </thead>
              <tbody>
                <% if (commandes != null) {
                  for (Commande commande : commandes) {
                      int montantTotal = (commande.getCredit() != null) ? commande.getCredit().getMontantTotal() : 0;
                      int montantPaye = (commande.getCredit() != null) ? commande.getCredit().getMontantPaye() : 0;
                      int restant = montantTotal - montantPaye;
                %>
                <tr>
                  <td class="numero-commande" data-id="<%= commande.getId() %>"><%= commande.getNumeroCommande() %></td>
                  <td><%= (commande.getClient() != null) ? commande.getClient().getLogin() : "N/A" %></td>
                  <td><%= (commande.getUtilisateur() != null) ? commande.getUtilisateur().getLogin() : "N/A" %></td>
                  <td><%= commande.getDateCommande() %></td>
                  <td><%= montantTotal %> HTG</td>
                  <td><%= montantPaye %> HTG</td>
                  <td><%= restant %> HTG</td>
                  <td><%= (commande.getCredit() != null) ? commande.getCredit().getStatut() : "-" %></td>
                <%--   <td>
                    <a href="#" class="btn btn-sm btn-info btn-details-commande" data-id="<%= commande.getId() %>">Détails</a>
                  </td> --%>
                </tr>
                <% }} %>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />
<script>
$(document).ready(function () {
    // Vérification que DataTables est chargé
    if (!$.fn.dataTable) {
        console.error('DataTables n\'est pas chargé correctement');
        alert('Erreur: DataTables n\'est pas chargé. Vérifiez l\'inclusion des fichiers.');
        return;
    }

    // Initialisation DataTable
    const table = $('#dataTableCommandes').DataTable({
        "language": { "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/French.json" },
        "pageLength": 10,
        "lengthMenu": [10, 25, 50, 100],
        "order": [[3, "desc"]],
        "responsive": true,
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api();
            var intVal = function (i) {
                if (typeof i === 'string') i = i.replace(/[\HTG,\s]/g, '');
                return parseFloat(i) || 0;
            };

            var colRestant = 6; // colonne Montant restant
            var totalRestant = api.column(colRestant, { page: 'all' }).data().reduce(function (a, b) {
                return a + intVal(b);
            }, 0);

            $(api.column(colRestant).footer()).html(totalRestant.toFixed(2) + ' HTG');
        }
    });

    // Clic sur numéro de commande pour voir les détails
    $('#dataTableCommandes').on('click', 'td.numero-commande, a.btn-details-commande', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const commandeId = $(this).data('id') || $(this).closest('tr').find('td.numero-commande').data('id');

        if (!commandeId) return alert("Erreur: ID de commande non trouvé");

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
        } else {
            row.child('<div class="text-center p-3"><i class="fas fa-spinner fa-spin"></i> Chargement des détails...</div>').show();
            tr.addClass('shown');

            // AJAX pour récupérer détails
            $.ajax({
                url: 'CommandeServlet?action=getdetailsjson&id=' + commandeId,
                method: 'GET',
                dataType: 'json',
                success: function (response) {
                    if (response && response.length > 0) {
                        let html = '<div class="p-3"><h6>Détails de la commande</h6>';
                        html += '<table class="table table-sm table-bordered">';
                        html += '<thead><tr><th>Produit/Plat</th><th>Qté</th><th>Prix U.</th><th>Total</th><th>Note</th></tr></thead><tbody>';
                        
                        response.forEach(function (d) {
                            var nomProduit = d.produit && d.produit.nom ? d.produit.nom
                                            : (d.plat && d.plat.nom ? d.plat.nom : 'N/A');
                            var total = d.quantite * d.prixUnitaire;
                            html += "<tr>" +
                                    "<td>" + nomProduit + "</td>" +
                                    "<td>" + d.quantite + "</td>" +
                                    "<td>" + d.prixUnitaire.toFixed(2) + " HTG</td>" +
                                    "<td>" + total.toFixed(2) + " HTG</td>" +
                                    "<td>" + (d.notes ? d.notes : '-') + "</td>" +
                                    "</tr>";
                        });
                        

                        html += '</tbody></table></div>';
                        row.child(html).show();
                    } else {
                        row.child('<div class="p-3 text-muted">Aucun détail trouvé pour cette commande.</div>').show();
                    }
                },
                error: function (xhr, status, error) {
                    row.child('<div class="p-3 text-danger">Erreur lors du chargement des détails.</div>').show();
                }
            });
        }
    });
});
</script>
