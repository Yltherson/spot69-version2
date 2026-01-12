<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.List,com.spot69.model.Facture"%>
<meta charset="UTF-8">

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<style>
td.numero-facture {
    cursor: pointer;
    text-decoration: underline;
  }
  td.numero-facture:hover {
    color: #b08c3e;
  }
  .table-danger, .table-danger > th, .table-danger > td{
  color:#dc3545;
  background-color: unset;}
</style>

<main role="main" class="main-content">
	<div class="container-fluid">
		<div class="row justify-content-center">
			<div class="col-12">
				<div class="row">
					<!-- Small table -->
		
					<div class="col-md-12 my-4">
						<div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="h4 mb-1">
                                <i class="fe fe-layers fe-32 align-self-center text-black"></i>
                                Liste des factures
                            </h2>
                            <div style="display: inline-flex; align-items: center; gap: 12px;">
							    <div class="custom-btn-group">
							        <a class=" py-2 px-3 btn btn-outline-primary d-flex align-items-center" 
							           href="FournisseurServlet?action=lister"
							           style="text-decoration: none; border-radius: 4px;">
							           <!-- <i class="fe fe-package"></i> -->
							           <i class="fe fe-users"></i>
							            <span class="pl-2">Fournisseurs</span>
							        </a>
							    </div>
							    <div class="custom-btn-group">
							        <a href="FactureServlet?action=add" class="btn btn-outline-primary d-flex align-items-center">
							            <i class="fas fa-plus mr-2"></i> 
							            <span>Reapprovisionner</span>
							        </a>
							    </div>
							</div>
                        </div>
                  <% 
    List<Facture> factures = (List<Facture>) request.getAttribute("factures");
    Integer deletedInterval = (Integer) request.getAttribute("deletedInterval");
    if (deletedInterval == null) deletedInterval = 1;
%>

<div class="d-flex align-items-center mb-3 gap-2">
    <label for="deletedInterval" class="me-2 mb-0">
        Préciser le nombre de jours pour la suppression :
    </label>
    <input type="number" id="deletedInterval" style="width:70px"
           name="deletedInterval" class="form-control"
           min="1" value="<%= deletedInterval %>">
    <button id="sendDeletedInterval" class="ml-3 btn btn-sm btn-primary">
        <i class="fe fe-send fe-16"></i> Envoyer
    </button>
</div>




						<div class="card shadow">
							<div class="card-body">
			<table class="table " id="facture-table">
    <thead>
        <tr>
            <th>No Facture</th>
            <th>Fournisseur</th>
            <th>Prix Achat Total</th>
            <th>Date</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    
      <% 
if (factures != null) {
    for (Facture facture : factures) {
        boolean isAjustement = facture.getNoFacture() != null && 
                              facture.getNoFacture().startsWith("AJUST-");
%>
<tr data-id="<%= facture.getId() %>" class="<%= isAjustement ? "table-danger" : "" %>">
    <td class="numero-facture" data-id="<%= facture.getId() %>">
        <%= facture.getNoFacture() %>
    </td>
    <td><%= facture.getFournisseur() != null ? facture.getFournisseur().getNom() : "-" %></td>
    <td><%= facture.getMontantTotal() %></td>
    <td><%= facture.getCreatedAt() %></td>
    <td>
        <% if (!isAjustement) { %>
        <!-- Afficher les actions seulement pour les factures normales -->
        <div class="dropdown">
            <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" 
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="text-muted sr-only">Action</span>
            </button>
            <div class="dropdown-menu">
                <a class="dropdown-item btn-show-details" href="#">Afficher les détails</a>
                <a class="dropdown-item" href="FactureServlet?action=edit&id=<%= facture.getId() %>">Modifier</a>
                <a class="dropdown-item" href="FactureServlet?action=supprimer&id=<%= facture.getId() %>" 
                   onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette facture ?');">
                    Supprimer
                </a>
            </div>
        </div>
        <% } else { %>
        <!-- Pour les ajustements, afficher juste "Ajustement" -->
        <span class="badge badge-danger">Ajustement</span>
        <% } %>
    </td>
</tr>
<% } } %>
    </tbody>
</table>

							</div>
						</div>
					</div>
					<!-- customized table -->
				</div>
			</div>
		</div>
	</div>

</main>


<jsp:include page="footer.jsp" />

<script>
$(document).ready(function () {
    // Vérifier si DataTables est déjà initialisé
    var table;
    if (!$.fn.DataTable.isDataTable('#facture-table')) {
        // Initialiser DataTable UNE SEULE FOIS
        table = $('#facture-table').DataTable({
            order: [[3, 'desc']],  // tri par la 4e colonne (index 3) en ordre décroissant
            language: {
                url: '//cdn.datatables.net/plug-ins/1.10.25/i18n/French.json'
            }
        });
    } else {
        // Si déjà initialisé, récupérer l'instance existante
        table = $('#facture-table').DataTable();
    }
    
    // Fonction pour afficher les détails d'une facture
    function showFactureDetails(factureId, row, tr) {
        $.ajax({
            url: 'FactureServlet?action=getDetails&id=' + factureId,
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                let html = '<table class="table table-sm mb-0">';
                html += '<thead><tr><th>Nom Produit</th><th>Unité</th><th>Quantité</th><th>Prix Achat Total</th><th>Prix Revient Unité</th></tr></thead><tbody>';

                if (data && data.length > 0) {
                    data.forEach(function (detail) {
                        html += '<tr>';
                        html += '<td>' + (detail.produit && detail.produit.nom ? detail.produit.nom : '-') + '</td>';
                        html += '<td>' + (detail.produit && detail.produit.uniteVente ? detail.produit.uniteVente : '-') + '</td>';
                        html += '<td>' + (detail.quantite || '-') + '</td>';
                        html += '<td>' + (detail.prixAchatTotal || '-') + '</td>';
                        html += '<td>' + (detail.prixRevientUnite || '-') + '</td>';
                        html += '</tr>';
                    });
                } else {
                    html += '<tr><td colspan="5" class="text-center">Aucun détail disponible</td></tr>';
                }

                html += '</tbody></table>';
                row.child(html).show();
                tr.addClass('shown');
            },
            error: function () {
                row.child("<div class='p-2 text-danger'>Erreur lors du chargement des détails.</div>").show();
                tr.addClass('shown');
            }
        });
    }

    // Clic sur le numéro de facture
    $(document).on('click', '#facture-table tbody td.numero-facture', function () {
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const factureId = $(this).data('id');

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
        } else {
            showFactureDetails(factureId, row, tr);
        }
    });

    // Clic sur le bouton "Afficher les détails"
    $(document).on('click', '#facture-table tbody .btn-show-details', function (e) {
        e.preventDefault();
        const tr = $(this).closest('tr');
        const row = table.row(tr);
        const factureId = tr.data('id');
        const button = $(this);

        if (row.child.isShown()) {
            row.child.hide();
            tr.removeClass('shown');
            button.text('Afficher les détails');
        } else {
            showFactureDetails(factureId, row, tr);
            button.text('Cacher les détails');
        }
    });

    // Gestion de l'intervalle de suppression
    if (document.getElementById("sendDeletedInterval")) {
        document.getElementById("sendDeletedInterval").addEventListener("click", function () {
            const input = document.getElementById("deletedInterval");
            const days = input.value;

            if (!days || days < 1) {
                alert("Veuillez entrer un nombre de jours valide.");
                return;
            }

            // Redirection vers la servlet avec les bons paramètres (GET)
            window.location.href = "FactureServlet?action=updatedeletedinterval&days=" + encodeURIComponent(days);
        });
    }
    
    // Validation en temps réel de l'intervalle de suppression
    if (document.getElementById("deletedInterval")) {
        document.getElementById("deletedInterval").addEventListener("input", function() {
            const value = this.value;
            if (value < 1) {
                this.classList.add("is-invalid");
            } else {
                this.classList.remove("is-invalid");
            }
        });
    }
    
    // Permettre l'envoi avec la touche Entrée
    if (document.getElementById("deletedInterval")) {
        document.getElementById("deletedInterval").addEventListener("keypress", function(e) {
            if (e.key === "Enter") {
                e.preventDefault();
                if (document.getElementById("sendDeletedInterval")) {
                    document.getElementById("sendDeletedInterval").click();
                }
            }
        });
    }
});
</script>