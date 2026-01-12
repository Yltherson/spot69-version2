
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.MenuCategorie,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions, java.util.List,com.spot69.model.Depense,com.spot69.model.DepenseType,com.spot69.model.Utilisateur"%>
<meta charset="UTF-8">



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
                            <i class="fe fe-list fe-32 align-self-center text-black"></i>
                            Les Types de Dépense
                        </h2>
                        <div class="custom-btn-group">
                            <button class="btn btn-outline-primary" type="button"
                                data-toggle="modal" data-target=".modal-type-depense">
                                <i class="fe fe-plus fe-16"></i> Ajouter un type
                            </button>
                            <a href="DepenseServlet?action=lister" class="btn btn-outline-secondary" type="button">
								    <i class="fe fe-back fe-16"></i> Retour au liste de depenses
								</a>
                        </div>
                        
                    </div>

                    <!-- Tableau des types -->
                    <div class="card shadow">
                        <div class="card-body">
                            <table class="table datatables" id="type-datatable">
                                <thead>
                                    <tr>
                                        <th>Description</th>
                                        <th>Créé le</th>
                                        <th>Mis à jour le</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <%
                                List<DepenseType> types = (List<DepenseType>) request.getAttribute("types");
                                if(types != null) {
                                    for(DepenseType t : types) {
                                %>
                                    <tr>
                                        <td><%=t.getDescription()%></td>
                                        <td><%=t.getCreatedAt()%></td>
                                        <td><%=t.getUpdatedAt()%></td>
                                        <td>
                                            <a href="DepenseServlet?action=deleteType&id=<%=t.getId()%>" 
                                               class="btn btn-sm btn-danger" 
                                               onclick="return confirm('Supprimer ce type ?');">
                                                <i class="fe fe-trash"></i>
                                            </a>
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

<!-- MODAL AJOUT TYPE -->
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

</main>


<jsp:include page="footer.jsp" />
