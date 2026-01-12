<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List,com.spot69.model.TableRooftop,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">
<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditTables = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_TABLES_ROOFTOP);
boolean canDeleteTables = canEditTables && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_TABLES_ROOFTOP);
%>

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
                                <i class="fe fe-table fe-32 align-self-center text-black"></i>
                                Tables Rooftop
                            </h2>
                            <div class="custom-btn-group">
                                <% if(canEditTables) { %>
                                <button class="btn btn-outline-primary" id="btnAjouterTable" data-toggle="modal" data-target=".modal-table">
                                    <i class="fe fe-plus fe-16"></i> Ajouter une table
                                </button>
                                <% } %>
                            </div>
                        </div>

                        <div class="card shadow">
                            <div class="card-body">
                                <table class="table datatables" id="table-datatable">
                                    <thead>
                                        <tr>
                                            <th>Numéro</th>
                                            <th>État actuel</th>
                                            <th>Plafond</th>
                                            <th>Date de création</th>
                                            <th>Date de modification</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                        List<TableRooftop> tables = (List<TableRooftop>) request.getAttribute("tables");
                                        if(tables != null){
                                            for(TableRooftop t : tables){
                                        %>
                                        <tr>
                                            <td><%= t.getNumeroTable() %></td>
                                            <td><%= t.getEtatActuel() %></td>
                                            <td><%= t.getPlafond() %></td>
                                            <td><%= t.getCreatedAt() != null ? t.getCreatedAt().toString().replace('T',' ') : "-" %></td>
                                            <td><%= t.getUpdatedAt() != null ? t.getUpdatedAt().toString().replace('T',' ') : "-" %></td>
                                            <td>
                                                <button class="btn btn-sm dropdown-toggle more-horizontal" type="button" data-toggle="dropdown">
                                                    <span class="text-muted sr-only">Action</span>
                                                </button>
                                                <div class="dropdown-menu dropdown-menu-right">
                                                    <% if(canEditTables){ %>
                                                        <a class="dropdown-item" href="TableRooftopServlet?action=modifier&id=<%= t.getId() %>">
                                                            Modifier
                                                        </a>
                                                    <% } %>
                                                    <% if(canDeleteTables){ %>
                                                        <form method="POST" action="TableRooftopServlet" style="display:inline;" onsubmit="return confirm('Confirmer la suppression ?');">
                                                            <input type="hidden" name="action" value="supprimer">
                                                            <input type="hidden" name="id" value="<%= t.getId() %>">
                                                            <input type="hidden" name="deletedBy" value="<%= currentUser.getId() %>">
                                                            <button type="submit" class="dropdown-item btn-delete-table">Supprimer</button>
                                                        </form>
                                                    <% } %>
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

        <%
            TableRooftop table = (TableRooftop) request.getAttribute("table");
            boolean isEdit = (table != null);
        %>

        <div class="modal fade modal-table modal-slide" tabindex="-1" role="dialog" aria-labelledby="modalTableLabel" aria-hidden="true">
            <div class="modal-dialog modal-md" role="document">
                <div class="modal-content">
                    <form id="formTable" action="TableRooftopServlet" method="post" accept-charset="UTF-8">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalTableLabel">
                                <%= isEdit ? "Modifier la table" : "Ajouter une table" %>
                            </h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>

                        <div class="modal-body">
                            <input type="hidden" name="action" value="<%= isEdit ? "modifier" : "ajouter" %>">
                            <% if(isEdit){ %>
                                <input type="hidden" name="id" value="<%= table.getId() %>">
                            <% } %>

                            <div class="form-group">
                                <label for="numeroTable">Numéro de table</label>
                                <input type="number" class="form-control" id="numeroTable" name="numeroTable" required value="<%= isEdit ? table.getNumeroTable() : "" %>">
                            </div>

                            <div class="form-group">
                                <label for="etatActuel">État actuel</label>
                                <select class="form-control" id="etatActuel" name="etatActuel" required>
                                    <option value="DISPONIBLE" <%= isEdit && "DISPONIBLE".equals(table.getEtatActuel()) ? "selected" : "" %>>DISPONIBLE</option>
                                    <option value="RESERVE" <%= isEdit && "RESERVE".equals(table.getEtatActuel()) ? "selected" : "" %>>RESERVE</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="plafond">Plafond</label>
                                <input type="number" step="0.01" class="form-control" id="plafond" name="plafond" value="<%= isEdit ? table.getPlafond() : "" %>">
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary btn-block">
                                <%= isEdit ? "Modifier la table" : "Ajouter la table" %>
                                <i class="fe fe-send"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
</main>

<jsp:include page="footer.jsp" />

<% if(isEdit){ %>
<script>
    $(document).ready(function(){
        $('.modal-table').modal('show');
    });
</script>
<% } %>

<script>
$(document).ready(function () {
    $('#table-datatable').DataTable({
        autoWidth: true,
        lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "All"]],
    });
});
</script>
