
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.MenuCategorie,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions, java.util.List,com.spot69.model.Depense,com.spot69.model.DepenseType,com.spot69.model.Utilisateur"%>
<meta charset="UTF-8">

<%

// Récupérer les listes d'utilisateurs, de rôles et de produits depuis la requête
List<Utilisateur> caissieres = (List<Utilisateur>) request.getAttribute("caissieres");

%>


<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

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


<main role="main" class="main-content">
<div class="container-fluid">
    <div class="row justify-content-center">
        <div class="col-12">
            <div class="row">
                <div class="col-md-12 my-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="h4 mb-1">
                            <i class="fe fe-clipboard fe-32 align-self-center text-black"></i>
                            Les Dépenses
                        </h2>
                        <div class="custom-btn-group">
                            <button class="btn btn-outline-primary" type="button"
                                data-toggle="modal" data-target=".modal-depense">
                                <i class="fe fe-plus fe-16"></i> Ajouter une dépense
                            </button>
                            <a href="DepenseServlet?action=lister-type" class="btn btn-outline-secondary" type="button">
								    <i class="fe fe-plus fe-16"></i> Les types de depenses
								</a>

                        </div>
                    </div>

                    <!-- Filtres -->
                    <div class="card shadow mb-4">
                        <div class="card-header">
                            <h5 class="card-title mb-0">Filtres</h5>
                        </div>
                        <div class="card-body">
                            <form method="GET" action="DepenseServlet" id="filterForm" class="w-100">
                                <input type="hidden" name="action" value="lister">

                                <div class="filter-container">
                                  

                                    <!-- Type de dépense -->
                                    <div class="form-group">
                                        <label for="typeId">Type de dépense</label>
                                        <select class="form-control" id="typeId" name="typeId">
    <option value="">Tous les types</option>
    <% 
    List<DepenseType> types = (List<DepenseType>) request.getAttribute("types");
    String selectedTypeId = (String) request.getAttribute("selectedTypeId");
    if(types != null) {
        for(DepenseType t : types) {
    %>
        <option value="<%=t.getId()%>" <%= (selectedTypeId != null && selectedTypeId.equals(String.valueOf(t.getId()))) ? "selected" : "" %>>
            <%=t.getDescription()%>
        </option>
    <% }} %>
</select>

                                    </div>

                                    <!-- Dates -->
                                    <div class="form-group">
                                        <label>Période</label>
                                        <div class="date-range-container d-flex">
                                        <input type="date" class="form-control" id="dateDebut" name="dateDebut"
       value="<%= request.getAttribute("selectedDateDebut") != null ? request.getAttribute("selectedDateDebut") : "" %>">
<span class="mx-2">à</span>
<input type="date" class="form-control" id="dateFin" name="dateFin"
       value="<%= request.getAttribute("selectedDateFin") != null ? request.getAttribute("selectedDateFin") : "" %>">
 </div>
                                    </div>

                                    <!-- Actions -->
                                    <div class="form-group align-self-end">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fe fe-filter fe-16"></i> Appliquer
                                        </button>
                                        <a href="DepenseServlet?action=lister" class="btn btn-secondary">
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
                            <table class="table datatables" id="depense-datatable">
                                <thead>
                                    <tr>
                                        <th>Type</th>
                                        <th>Montant</th>
                                        <th>Notes</th>
                                        <th>Utilisateur</th>
                                        <th>Date</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                List<Depense> depenses = (List<Depense>) request.getAttribute("depenses");
                                if(depenses != null) {
                                    for(Depense d : depenses) {
                                %>
                                    <tr>
                                        <td><%=d.getType()!=null ? d.getType().getDescription() : ""%></td>
                                        <td><%=d.getMontant()%></td>
                                        <td><%=d.getNotes()!=null ? d.getNotes() : "—"%></td>
                                        <td><%=d.getUtilisateur().getLogin()%></td>
                                        <td><%=d.getDate()%></td>
                                        <td>
                                          <form action="DepenseServlet?action=supprimer" method="post" style="display:inline;">
										    <input type="hidden" name="id" value="<%=d.getId()%>">
										    <button type="submit" class="btn btn-sm btn-danger" 
										            onclick="return confirm('Supprimer cette dépense ?');">
										        <i class="fe fe-trash"></i>
										    </button>
										</form>

                                        </td>
                                    </tr>
                                <% }} %>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- MODAL TYPE DEPENSE -->
<div class="modal fade modal-type-depense modal-slide" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form action="DepenseServlet" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">Ajouter un type de dépense</h5>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label>Description</label>
                        <input type="text" name="description" class="form-control" required>
                    </div>
                    <input type="hidden" name="action" value="addType">
                   
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary btn-block">Ajouter</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- MODAL DEPENSE -->
<div class="modal fade modal-depense modal-slide" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form action="DepenseServlet" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">Ajouter une dépense</h5>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label>Type</label>
                        <select name="idType" class="form-control" required>
                            <option value="">-- Choisir --</option>
                            <% if(types != null) {
                                for(DepenseType t : types) { %>
                                <option value="<%=t.getId()%>"><%=t.getDescription()%></option>
                            <% }} %>
                        </select>
                    </div>
                     <div class="form-group">
                      <label for="caissiereId">Choisissez la caissiere </label>
                      <select class="form-control" id="caissiereId" name="caissiereId">
                        <option value="">Tous les utilisateurs</option>
                        <% if(caissieres != null) {
                          for(Utilisateur user : caissieres) { %>
                            <option value="<%= user.getId() %>" >
                              <%= user.getLogin() %>
                            </option>
                          <% }
                        } %>
                      </select>
                    </div>
                    <div class="form-group">
                        <label>Montant</label>
                        <input type="number" name="montant" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Date</label>
                        <input type="date" name="dateDepense" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Notes</label>
                        <textarea name="notes" class="form-control"></textarea>
                    </div>
                    <input type="hidden" name="action" value="ajouter">
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary btn-block">Ajouter</button>
                </div>
            </form>
        </div>
    </div>
</div>


</main>


<jsp:include page="footer.jsp" />
<script>
$(document).ready(function () {
    $('#depense-datatable').DataTable({
        pageLength: 10,
        lengthChange: true,
        searching: true,
        ordering: true,
        language: {
            url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json"
        }
    });
});
</script>

