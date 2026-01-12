<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="java.util.List,com.spot69.model.Commande,java.text.SimpleDateFormat,java.util.Date,com.google.gson.Gson,com.spot69.model.CommandeDetail,java.util.Map, com.spot69.model.Versement"%>
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
</style>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <h2 class="my-4">Historique des Versements</h2>

    <div class="card shadow">'
      <div class="card shadow ">
          <div class="card-header">
            <h5 class="card-title mb-0">Filtres</h5>
          </div>
          <div class="card-body">
            <form id="filterForm" class="w-100">
              <input type="hidden" name="action" value="historique-versement">
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
				     <div class="form-group align-self-end">
                  <button type="submit" id="btnFiltrer" class="btn btn-primary">
                    <i class="fe fe-filter fe-16"></i> Appliquer
                  </button>
                  <a href="CommandeServlet?action=historique-versement" class="btn btn-secondary">
                    <i class="fe fe-refresh-cw fe-16"></i> Réinitialiser
                  </a>
                </div>
              </div>
            </form>
          </div>
        </div>
      <div class="card-body">
        <table class="table table-striped " id="dataTableVersements">
          <thead>
            <tr>
             <!--  <th>Versement ID</th> -->
              <th>Date</th>
              <th>Montant Verser</th>
            </tr>
          </thead>
          <tbody>
            <%
              Map<Versement, List<Commande>> historique =
                (Map<Versement, List<Commande>>) request.getAttribute("historiqueVersements");
              Gson gson = new Gson();
              // Transformer la Map en JSON indexé par versementId
              Map<Integer, List<Commande>> historiqueForJson = new java.util.HashMap<>();
              if(historique != null){
                  for(Map.Entry<Versement, List<Commande>> entry : historique.entrySet()){
                      historiqueForJson.put(entry.getKey().getId(), entry.getValue());
                  }
              }
              String historiqueJson = gson.toJson(historiqueForJson);

              if (historique != null) {
                  for (Map.Entry<Versement, List<Commande>> entry : historique.entrySet()) {
                      Versement versement = entry.getKey();
            %>
            <tr class="versement-row" data-id="<%= versement.getId() %>">
              <%-- <td><%= versement.getId() %></td> --%>
              <td><%= new SimpleDateFormat("dd/MM/yyyy HH:mm").format(versement.getDateVersement()) %></td>
              <td><%= versement.getMontant() %> HTG</td>
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
</main>

<jsp:include page="footer.jsp" />

<!-- Inclure jQuery et DataTables -->

<script>
$(document).ready(function() {
    // JSON côté JS
    var historiqueVersements = <%= historiqueJson %>;
    console.log("Historique Versements :", historiqueVersements);

    // Initialiser DataTable
    var table = $('#dataTableVersements').DataTable({
        "order": [[1, "desc"]]
    });

    // Toggle child rows
    $('#dataTableVersements tbody').on('click', 'tr.versement-row', function() {
        var tr = $(this);
        var row = table.row(tr);
        var versementId = tr.data('id');

        if(row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
        } else {
            var commandes = historiqueVersements[versementId] || [];
            var html = '<table class="table table-sm mb-0"><thead><tr><th>Numéro commande</th><th>Date</th><th>Montant Total</th><th>Statut Crédit</th><th>Détails</th></tr></thead><tbody>';

            commandes.forEach(function(c) {
                html += '<tr>';
                html += '<td>' + c.numeroCommande + '</td>';
                html += '<td>' + c.dateCommande + '</td>';
                html += '<td>' + c.montantTotal + ' HTG</td>';
                html += '<td>' + (c.credit ? c.credit.statut : '-') + '</td>';

                // Détails
                html += '<td><table class="table table-sm mb-0"><thead><tr><th>Produit / Plat</th><th>Qte</th><th>PU</th><th>Total</th><th>Note</th></tr></thead><tbody>';
                if(c.details) {
                    c.details.forEach(function(d) {
                        var nom = d.produit ? d.produit.nom : (d.plat ? d.plat.nom : '-');
                        html += '<tr>';
                        html += '<td>' + nom + '</td>';
                        html += '<td>' + d.quantite + '</td>';
                        html += '<td>' + d.prixUnitaire + ' HTG</td>';
                        html += '<td>' + d.sousTotal + ' HTG</td>';
                        html += '<td>' + (d.notes ? d.notes : '') + '</td>';
                        html += '</tr>';
                    });
                }
                html += '</tbody></table></td>';

                html += '</tr>';
            });

            html += '</tbody></table>';
            row.child(html).show();
            tr.addClass('shown');
        }
    });
});
</script>
