<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.spot69.model.*,java.math.BigDecimal"%>
<meta charset="UTF-8">
<%
BigDecimal soldeInitial = (BigDecimal) request.getAttribute("soldeInitial");
CaisseCaissiere derniereCaisse = (CaisseCaissiere) request.getAttribute("derniereCaisse");
Utilisateur user = (Utilisateur) request.getAttribute("user");
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow">
          <div class="card-header">
            <h2 class="card-title">
              <i class="fe fe-briefcase text-primary"></i>
              Ouverture de caisse
            </h2>
          </div>
          <div class="card-body">
            <!-- Informations caissier -->
            <div class="alert alert-info mb-4">
              <div class="row">
                <div class="col-md-8">
                  <strong><i class="fe fe-user"></i> Caissier(ère): <%= user.getNom() %> <%= user.getPrenom() %></strong><br>
                  <small><i class="fe fe-clock"></i> Date: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) %></small>
                </div>
                <div class="col-md-4 text-right">
                  <span class="badge badge-warning">À OUVRIR</span>
                </div>
              </div>
            </div>
            
            <!-- Dernière caisse fermée -->
            <% if (derniereCaisse != null) { %>
            <div class="card mb-4">
              <div class="card-header">
                <h5 class="card-title mb-0">Information dernière caisse</h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6">
                    <small class="text-muted">Caisse #<%= derniereCaisse.getId() %></small><br>
                    <small>Fermée le: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(derniereCaisse.getFermeture()) %></small>
                  </div>
                  <div class="col-md-6 text-right">
                    <div class="h5 <%= derniereCaisse.getSoldeFinal().compareTo(derniereCaisse.getSoldeInitial()) >= 0 ? "text-success" : "text-danger" %>">
                      <%= String.format("%.2f HTG", derniereCaisse.getSoldeFinal()) %>
                    </div>
                    <small class="text-muted">Solde final</small>
                  </div>
                </div>
              </div>
            </div>
            <% } %>
            
            <!-- Formulaire d'ouverture -->
            <form method="POST" action="CompteClientServlet" onsubmit="return validerOuverture()">
              <input type="hidden" name="action" value="ouvrirCaisse">
              
              <div class="form-group">
                <label for="soldeInitial">Solde initial de la caisse *</label>
                <div class="input-group">
                  <input type="number" class="form-control" id="soldeInitial" name="soldeInitial" 
                         required min="0" step="0.01" 
                         value="<%= soldeInitial != null ? soldeInitial.toString() : "0.00" %>">
                  <div class="input-group-append">
                    <span class="input-group-text">HTG</span>
                  </div>
                </div>
                <small class="form-text text-muted">
                  <i class="fe fe-info"></i> 
                  <% if (derniereCaisse != null) { %>
                  Solde final de la dernière caisse: <%= String.format("%.2f HTG", derniereCaisse.getSoldeFinal()) %>
                  <% } else { %>
                  Vous ouvrez votre première caisse. Le solde initial est généralement 0.
                  <% } %>
                </small>
              </div>
              
              <div class="form-group">
                <label for="notes">Notes (optionnel)</label>
                <textarea class="form-control" id="notes" name="notes" rows="2" 
                          placeholder="Ex: Caisse du matin, reçu de la banque..."></textarea>
              </div>
              
              <div class="alert alert-warning">
                <i class="fe fe-alert-triangle"></i>
                <strong>Important:</strong> Vérifiez bien le solde initial avant de confirmer. 
                Vous ne pourrez pas modifier ce solde après l'ouverture.
              </div>
              
              <div class="text-right">
                <a href="index.jsp" class="btn btn-secondary mr-2">Annuler</a>
                <button type="submit" class="btn btn-success">
                  <i class="fe fe-check"></i> Ouvrir la caisse
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />

<script>
function validerOuverture() {
  const soldeInitial = parseFloat(document.getElementById('soldeInitial').value);
  
  if (soldeInitial < 0) {
    alert('Le solde initial ne peut pas être négatif');
    return false;
  }
  
  return confirm('Confirmez-vous l\'ouverture de la caisse avec un solde initial de ' + 
                 soldeInitial.toFixed(2) + ' HTG?');
}
</script>