<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
 import="java.util.List,com.spot69.model.Commande,java.text.SimpleDateFormat,java.util.Date,com.spot69.model.Utilisateur, java.math.BigDecimal"%>
<meta charset="UTF-8">

<style>
  td.numero-commande { cursor: pointer; text-decoration: underline; }
  td.numero-commande:hover { color: #b08c3e; }
  .filter-container { display: flex; align-items: center; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
  .filter-container .form-group { margin-bottom: 0; min-width: 150px; }
  .date-range-container { display: flex; align-items: center; gap: 5px; }
</style>

<%
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
String today = dateFormat.format(new Date());

String commandeType = request.getParameter("commandeType") != null ? request.getParameter("commandeType") : "paye";
String dateDebut = request.getParameter("dateDebut");
String dateFin = request.getParameter("dateFin");

List<Commande> commandes = (List<Commande>) request.getAttribute("commandes");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <h2 class="h4 mb-4">
          <i class="fe fe-clipboard fe-32 align-self-center text-black"></i>
          Mes Commandes
        </h2>

        <!-- Formulaire de filtres -->
        <div class="card shadow mb-4">
          <div class="card-header"><h5 class="card-title mb-0">Filtres</h5></div>
          <div class="card-body">
            <form method="GET" action="CommandeServlet" id="filterForm" class="w-100">
              <input type="hidden" name="action" value="getcmd-by-filtres-for-user">
              <div class="filter-container">
                <div class="form-group">
                  <label for="commandeType">Commandes</label>
                  <select class="form-control" id="commandeType" name="commandeType">
                      <option value="paye" <%= "paye".equals(commandeType) ? "selected" : "" %>>Commandes payées</option>
                      <option value="credit" <%= "credit".equals(commandeType) ? "selected" : "" %>>Commandes à crédit</option>
                       <option value="creditnonvalide" <%= "creditnonvalide".equals(commandeType) ? "selected" : "" %>>Commandes à crédit non validé</option>
                 </select>
                </div>
                <div class="form-group">
                  <label>Période</label>
                  <div class="date-range-container">
                    <input type="date" class="form-control" id="dateDebut" name="dateDebut"
                           value="<%= dateDebut != null ? dateDebut : "" %>">
                    <span>à</span>
                    <input type="date" class="form-control" id="dateFin" name="dateFin"
                           value="<%= dateFin != null ? dateFin : "" %>">
                  </div>
                </div>
                <div class="form-group align-self-end">
                  <button type="submit" class="btn btn-primary">
                    <i class="fe fe-filter fe-16"></i> Appliquer
                  </button>
                  <a href="CommandeServlet?action=lister" class="btn btn-secondary">
                    <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>

        <!-- Tableau des commandes -->
        <!-- Tableau des commandes -->
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
        BigDecimal totalMontant = BigDecimal.ZERO; 
        if (commandes != null) {
            for (Commande cmd : commandes) {
            	 BigDecimal montant = cmd.getMontantTotal() != null ? cmd.getMontantTotal() : BigDecimal.ZERO;
                 totalMontant = totalMontant.add(montant); 
        %>
        <tr>
          <td class="numero-commande" data-id="<%= cmd.getId() %>"><%= cmd.getNumeroCommande() %></td>
          <td><%= cmd.getClient() != null ? cmd.getClient().getLogin() : "-" %></td>
          <td><%= cmd.getDateCommande() %></td>
          <td><%= cmd.getMontantTotal() %> HTG</td>
          <%
          String badgeClass = "badge-secondary";
          if (cmd.getStatutCommande() != null) {
              switch (cmd.getStatutCommande()) {
                  case "EN_PREPARATION": badgeClass = "badge-warning"; break;
                  case "PRETE": badgeClass = "badge-primary"; break;
                  case "ANNULE": badgeClass = "badge-danger"; break;
                  case "EN_ATTENTE": badgeClass = "badge-light"; break;
                  case "LIVRE": badgeClass = "badge-success"; break;
              }
          }
          %>
          <td><span class="badge <%= badgeClass %>"><%= cmd.getStatutCommande() %></span></td>
          <td>
			  <%= cmd.getNotes() != null ? cmd.getNotes() : "" %>
			
			  <% if ("creditnonvalide".equals(commandeType)) { %>
			      <form action="CommandeServlet" method="post" style="display:inline;">
			          <input type="hidden" name="action" value="validercredit">
			          <input type="hidden" name="creditId" value="<%= cmd.getCredit().getId()%>">
			          <input type="hidden" name="utilisateurId" value="<%= cmd.getClient() != null ? cmd.getClient().getId() : 0 %>">
			          <button type="submit" class="btn btn-success btn-sm">Valider</button>
			      </form>
			  <% } %>
			</td>

        </tr>
        <%
            }
        }
        %>
      </tbody>
      <tfoot>
<tr>
  <th colspan="3" class="text-right">Total :</th>
  <th><%= totalMontant %> HTG</th>
  <th colspan="2"></th>
</tr>
</tfoot>
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
  const table = $('#dataTableCommandes').DataTable({
    "order": [[2, "desc"]],
    "columnDefs": [{
      "type": "date",
      "targets": 2,
      "render": function(data, type, row) {
        if (type === 'display' || type === 'filter') return data.split(' ')[0];
        return data;
      }
    }]
  });

  // Au clic sur numéro de commande pour voir les détails
  $('#dataTableCommandes tbody').on('click', 'td.numero-commande', function () {
    var tr = $(this).closest('tr');
    var row = table.row(tr);
    var commandeId = $(this).data('id');

    if (row.child.isShown()) {
      row.child.hide(); tr.removeClass('shown');
    } else {
      $.ajax({
        url: "CommandeServlet?action=getdetailsjson&id=" + commandeId,
        method: "GET",
        dataType: "json",
        success: function(details) {
          var html = '<table class="table table-sm mb-0"><thead><tr><th>Produit / Plat</th><th>Qte</th><th>Prix Unitaire</th><th>Total</th><th>Note</th></tr></thead><tbody>';
          details.forEach(function(d) {
            var nomProduit = d.produit?.nom || d.plat?.nom || '-';
            html += "<tr><td>" + nomProduit + "</td><td>" + d.quantite + "</td><td>" + d.prixUnitaire + " HTG</td><td>" + (d.quantite*d.prixUnitaire) + " HTG</td><td>" + (d.notes || '') + "</td></tr>";
          });
          html += "</tbody></table>";
          row.child(html).show(); tr.addClass("shown");
        },
        error: function() {
          row.child('<div class="p-2 text-danger">Erreur lors du chargement des détails.</div>').show(); tr.addClass("shown");
        }
      });
    }
  });
});
</script>
