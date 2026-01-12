
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List,com.spot69.model.Fournisseur,com.spot69.model.InventaireCategorie,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<% 
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canEditFournisseurs = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_FOURNISSEURS);
boolean canDeleteFournisseurs = canEditFournisseurs && PermissionChecker.hasPermission(currentUser, Permissions.SUPPRESSION_FOURNISSEURS);
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
                                <i class="fe fe-layers fe-32 align-self-center text-black"></i>
                                Liste des fournisseurs
                            </h2>
                            <div class="custom-btn-group">
                            <a href="FournisseurServlet?action=add" class="btn btn-outline-primary">
                                    <i class="fe fe-plus fe-16"></i> Ajouter 
                                </a>
                            </div>
                        </div>
                       
                        <div class="card shadow">
                            <div class="card-body">
                                <table class="table datatables" id="dataTableFournisseur">
                                    <thead>
                                        <tr>
                                            <th>Nom</th>
                                            <th>Addresse</th>
                                            <th>Téléphone</th>
                                            <th>Email</th>
                                            <!-- <th>Devise</th>
                                            <th>Mode Paiement</th>
                                            <th>Crédit autorisé</th>
                                            <th>Limite crédit</th>
                                            <th>Solde actuel</th> -->
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
                                            if (fournisseurs != null) {
                                                for (Fournisseur f : fournisseurs) {
                                        %>
                                        <tr>
                                            <td><%= f.getNom() %></td>
                                            <td><%= f.getContact() != null ? f.getContact() : "" %></td>
                                            <td><%= f.getTelephone() != null ? f.getTelephone() : "" %></td>
                                            <td><%= f.getEmail() != null ? f.getEmail() : "" %></td>
                                            <%-- <td><%= f.getDevisePreference() %></td>
                                            <td><%= f.getModePaiement() %></td>
                                            <td><%= f.isCreditAutorise() ? "Oui" : "Non" %></td>
                                            <td><%= f.getLimiteCredit() != null ? f.getLimiteCredit() : "0" %></td>
                                            <td><%= f.getSoldeActuel() != null ? f.getSoldeActuel() : "0" %></td> --%>
                                           <td>
											  <div class="dropdown">
											    <button class="btn btn-sm btn-secondary dropdown-toggle"
											     type="button" id="dropdownMenuButton-<%= f.getId() %>"
											      data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
											      <span class="text-muted sr-only">Action</span>
											    </button>
											    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton-<%= f.getId() %>">
											    <% if (canEditFournisseurs) { %>
											      <a class="dropdown-item btn-edit-fournisseur" href="#"
											         data-toggle="modal" data-target=".modal-update-fournisseur"
											         data-id="<%= f.getId() %>"
											         data-nom="<%= f.getNom() %>"
											         data-contact="<%= f.getContact() != null ? f.getContact() : "" %>"
											         data-telephone="<%= f.getTelephone() != null ? f.getTelephone() : "" %>"
											         data-email="<%= f.getEmail() != null ? f.getEmail() : "" %>"
											         data-devise="<%= f.getDevisePreference() %>"
											         data-modepaiement="<%= f.getModePaiement() %>"
											         data-creditautorise="<%= f.isCreditAutorise() %>"
											         data-limitcredit="<%= f.getLimiteCredit() != null ? f.getLimiteCredit() : "0" %>"
											         data-soldeactuel="<%= f.getSoldeActuel() != null ? f.getSoldeActuel() : "0" %>">
											         Modifier
											      </a>
											      <% } %>
											      
											        <% if (canDeleteFournisseurs) { %>
											      <form method="POST" action="FournisseurServlet?action=supprimer" style="display: inline;" onsubmit="return confirm('Confirmer la suppression ?');">
											        <input type="hidden" name="id" value="<%= f.getId() %>" />
											        <button type="submit" class="dropdown-item ">Supprimer</button>
											      </form>
											      <% } %>
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

       <!-- Modal modification fournisseur -->
<div class="modal fade modal-update-fournisseur modal-slide" tabindex="-1"
     role="dialog" aria-labelledby="modalUpdateFournisseurLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <form method="POST" action="FournisseurServlet?action=modifier" id="formEditFournisseur">
        <div class="modal-header">
          <h5 class="modal-title" id="modalUpdateFournisseurLabel">
            <span class="fe fe-truck fe-24"></span> Modifier le fournisseur
          </h5>
          <button type="button" class="close" data-dismiss="modal" aria-label="Fermer">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>

        <div class="modal-body">
          <input type="hidden" name="id" id="editFournisseurId">

          <div class="form-group">
            <label for="editNom">Nom *</label>
            <input type="text" class="form-control" id="editNom" name="nom" required>
          </div>

          <div class="form-group">
            <label for="editContact">Addresse</label>
            <input type="text" class="form-control" id="editContact" name="contact">
          </div>

          <div class="form-group">
            <label for="editTelephone">Téléphone</label>
            <input type="text" class="form-control" id="editTelephone" name="telephone">
          </div>

          <div class="form-group">
            <label for="editEmail">Email</label>
            <input type="email" class="form-control" id="editEmail" name="email">
          </div>

          <div class="form-group">
            <label for="editDevise">Devise préférée</label>
            <select class="form-control" id="editDevise" name="devisePreference">
              <option value="HTG">HTG</option>
              <option value="USD">USD</option>
            </select>
          </div>

          <div class="form-group">
            <label for="editModePaiement">Mode de paiement</label>
            <select class="form-control" id="editModePaiement" name="modePaiement">
              <option value="CASH">CASH</option>
              <option value="VIREMENT">VIREMENT</option>
              <option value="CREDIT">CREDIT</option>
            </select>
          </div>

          <div class="form-group form-check" hidden>
            <input type="checkbox" class="form-check-input" id="editCreditAutorise" name="creditAutorise">
            <label class="form-check-label" for="editCreditAutorise">Crédit autorisé</label>
          </div>

          <div class="form-group" hidden>
            <label for="editLimiteCredit">Limite crédit</label>
            <input type="number" step="0.01" class="form-control" id="editLimiteCredit" name="limiteCredit" min="0" value="0">
          </div>

          <div class="form-group" hidden>
            <label for="editSoldeActuel">Solde actuel</label>
            <input type="number" step="0.01" class="form-control" id="editSoldeActuel" name="soldeActuel" min="0" value="0">
          </div>
        </div>

        <div class="modal-footer">
          <button type="submit" class="btn btn-primary">
            Enregistrer les modifications <i class="fe fe-send icon"></i>
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


    </div>
</main>

<jsp:include page="footer.jsp" />

<script>
    $(document).ready(function () {
        $('#dataTableFournisseur').DataTable({
            autoWidth: true,
            lengthMenu: [[16, 32, 64, -1], [16, 32, 64, "Tout"]],
        });

            // Remplir le modal modification fournisseur au clic sur le bouton Modifier
            $('.btn-edit-fournisseur').click(function () {
              const btn = $(this);
              $('#editFournisseurId').val(btn.data('id'));
              $('#editNom').val(btn.data('nom'));
              $('#editContact').val(btn.data('contact'));
              $('#editTelephone').val(btn.data('telephone'));
              $('#editEmail').val(btn.data('email'));
              $('#editDevise').val(btn.data('devise'));
              $('#editModePaiement').val(btn.data('modepaiement'));
              $('#editCreditAutorise').prop('checked', btn.data('creditautorise') === true || btn.data('creditautorise') === 'true');
              $('#editLimiteCredit').val(btn.data('limitcredit'));
              $('#editSoldeActuel').val(btn.data('soldeactuel'));
            });
    });
</script>
<script>
  
</script>


