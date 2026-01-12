<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.spot69.model.*,java.math.BigDecimal"%>
<meta charset="UTF-8">
<%
Utilisateur client = (Utilisateur) request.getAttribute("client");
CompteClient compte = (CompteClient) request.getAttribute("compte");
CaisseCaissiere caisse = (CaisseCaissiere) request.getAttribute("caisse");

// Calculer le crédit utilisé
BigDecimal creditUtilise = BigDecimal.ZERO;
if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
    creditUtilise = compte.getSolde().abs();
}

// Formater les dates
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
String dateOuverture = dateFormat.format(caisse.getOuverture());
%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<style>
.bg-gold{
 background-color: #daaf5a !important;
}

.text-gold{
 color: #daaf5a !important;
}
</style>

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="">
      <div class="">
        <div class="card shadow">
          <div class="card-header">
            <h2 class="card-title">
              <i class="fe fe-plus-circle text-success"></i>
              Effectuer un dépôt / Gérer le crédit
            </h2>
          </div>
          <div class="card-body">
            <!-- Informations client avec crédit -->
            <div class="card mb-4">
              <div class="card-body bg-light">
                <div class="row">
                  <div class="col-md-6">
                    <h5><%= client.getNom() %> <%= client.getPrenom() %></h5>
                    <p class="mb-1"><i class="fe fe-mail"></i> <%= client.getEmail() %></p>
                    <% if (client.getTelephone() != null) { %>
                    <p class="mb-0"><i class="fe fe-phone"></i> <%= client.getTelephone() %></p>
                    <% } %>
                  </div>
                  <div class="col-md-6 text-right">
                    <% 
                    String soldeClass = "text-success";
                    if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                        soldeClass = "text-danger";
                    }
                    %>
                    <div class="h3 <%= soldeClass %>">
                      <%= String.format("%.2f HTG", compte.getSolde()) %>
                    </div>
                    <small class="text-muted">Solde actuel</small>
                  </div>
                </div>
                
                <!-- Informations crédit -->
                <div class="row mt-3 border-top pt-3">
                  <div class="col-md-4 text-center">
                    <p class="mb-1"><strong>Limite crédit:</strong></p>
                    <h5 class="text-info"><%= String.format("%.2f HTG", compte.getLimiteCredit()) %></h5>
                  </div>
                  <div class="col-md-4 text-center">
                    <p class="mb-1"><strong>Crédit utilisé:</strong></p>
                    <h5 class="text-danger"><%= String.format("%.2f HTG", creditUtilise) %></h5>
                  </div>
                  <div class="col-md-4 text-center">
                    <p class="mb-1"><strong>Solde disponible:</strong></p>
                    <% 
                    String soldeDisponibleClass = "text-success";
                    if (compte.getSoldeDisponible().compareTo(BigDecimal.ZERO) < 0) {
                        soldeDisponibleClass = "text-danger";
                    }
                    %>
                    <h5 class="<%= soldeDisponibleClass %>">
                      <%= String.format("%.2f HTG", compte.getSoldeDisponible()) %>
                    </h5>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Informations caisse -->
            <div class="alert ">
              <div class="row">
                <div class="col-md-6">
                  <strong><i class="fe fe-briefcase"></i> Caisse: #<%= caisse.getId() %></strong><br>
                  <small>Caissier: Vous</small>
                </div>
                <div class="col-md-6 text-right">
                  <small>Ouverte le: <%= dateOuverture %></small><br>
                  <small>Solde initial: <%= String.format("%.2f HTG", caisse.getSoldeInitial()) %></small>
                </div>
              </div>
            </div>
            
            <!-- Onglets Dépôt / Gérer crédit -->
            <ul class="nav nav-tabs mb-4" id="myTab" role="tablist">
              <li class="nav-item">
                <a class="nav-link active" id="depot-tab" data-toggle="tab" href="#depot" role="tab">
                  <i class="fe fe-plus-circle"></i> Effectuer un dépôt
                </a>
              </li>
              <li class="nav-item">
                <a class="nav-link" id="credit-tab" data-toggle="tab" href="#credit" role="tab">
                  <i class="fe fe-credit-card"></i> Gérer la limite de crédit
                </a>
              </li>
            </ul>
            
            <div class="tab-content" id="myTabContent">
              <!-- Onglet Dépôt -->
              <div class="tab-pane fade show active" id="depot" role="tabpanel">
                <form method="POST" action="CompteClientServlet" onsubmit="return validerDepot()">
                  <input type="hidden" name="action" value="effectuerDepot">
                  <input type="hidden" name="clientId" value="<%= client.getId() %>">
                  
                  <div class="row">
                    <div class="col-md-6">
                      <div class="form-group">
                        <label for="montant">Montant du dépôt *</label>
                        <div class="input-group">
                          <input type="number" class="form-control" id="montant" name="montant" 
                                 required min="1" step="0.01" placeholder="0.00">
                          <div class="input-group-append">
                            <span class="input-group-text">HTG</span>
                          </div>
                        </div>
                        <small class="form-text text-muted">Montant minimum: 1 HTG</small>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="form-group">
                        <label for="modePaiement">Mode de paiement *</label>
                        <select class="form-control" id="modePaiement" name="modePaiement" required>
                          <option value="">-- Sélectionner --</option>
                          <option value="CASH">Espèces</option>
                          <option value="VIREMENT">Virement bancaire</option>
                          <option value="MONCASH">MonCash</option>
                          <option value="NATCASH">NatCash</option>
                          <option value="CHEQUE">Chèque</option>
                          <option value="CARTE">Carte bancaire</option>
                        </select>
                      </div>
                    </div>
                    
                    <div class="col-md-12">
                      <div class="form-group">
                        <label for="notes">Notes (optionnel)</label>
                        <textarea class="form-control" id="notes" name="notes" rows="2" 
                                  placeholder="Ex: Dépôt mensuel, paiement facture..."></textarea>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Aperçu du nouveau solde -->
                  <div class="card mb-4">
                    <div class="card-body">
                      <h5 class="card-title">Aperçu après dépôt</h5>
                      <div class="row">
                        <div class="col-md-4">
                          <p class="mb-1">Solde actuel:</p>
                          <h4><%= String.format("%.2f HTG", compte.getSolde()) %></h4>
                        </div>
                        <div class="col-md-4">
                          <p class="mb-1">Montant du dépôt:</p>
                          <h4 id="montantApercu" class="text-success">+ 0.00 HTG</h4>
                        </div>
                        <div class="col-md-4">
                          <p class="mb-1">Nouveau solde:</p>
                          <h4 id="nouveauSoldeApercu" class="<%= soldeClass %>"><%= String.format("%.2f HTG", compte.getSolde()) %></h4>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="text-right">
                    <a href="CompteClientServlet?action=etatCompte&clientId=<%= client.getId() %>" 
                       class="btn btn-secondary mr-2">Annuler</a>
                    <button type="submit" class="btn btn-success">
                      <i class="fe fe-check"></i> Confirmer le dépôt
                    </button>
                  </div>
                </form>
              </div>
              
              <!-- Onglet Gérer crédit -->
              <div class="tab-pane fade" id="credit" role="tabpanel">
                <form method="POST" action="CompteClientServlet" onsubmit="return validerLimiteCredit()">
                  <input type="hidden" name="action" value="modifierLimiteCredit">
                  <input type="hidden" name="compteId" value="<%= compte.getId() %>">
                  
                  <%-- <div class="alert alert-warning">
                    <i class="fe fe-info"></i>
                    <strong>Information:</strong> Le client peut acheter jusqu'à 
                    <strong><%= String.format("%.2f HTG", compte.getLimiteCredit()) %></strong> de crédit 
                    même avec solde 0.
                  </div> --%>
                  
                  <div class="form-group">
                    <label for="nouvelleLimite">Nouvelle limite de crédit *</label>
                    <div class="input-group">
                      <input type="number" class="form-control" id="nouvelleLimite" 
                             name="nouvelleLimite" required min="0" step="0.01"
                             value="<%= String.format("%.2f", compte.getLimiteCredit()) %>">
                      <div class="input-group-append">
                        <span class="input-group-text">HTG</span>
                      </div>
                    </div>
                    <small class="form-text text-muted">
                      Exemple: 5000 HTG permet au client d'acheter jusqu'à 5000 HTG même avec solde 0
                    </small>
                  </div>
                  
                  <div class="form-group">
                    <label for="raison">Raison du changement</label>
                    <textarea class="form-control" id="raisonCredit" name="raison" rows="2"
                              placeholder="Ex: Client fidèle, augmentation temporaire..."></textarea>
                  </div>
                  
                  <!-- Aperçu -->
                  <div class="card mb-4">
                    <div class="card-body">
                      <h5 class="card-title">Aperçu des changements</h5>
                      <table class="table table-sm">
                        <tr>
                          <td>Ancienne limite:</td>
                          <td class="text-right"><%= String.format("%.2f HTG", compte.getLimiteCredit()) %></td>
                        </tr>
                        <tr>
                          <td>Nouvelle limite:</td>
                          <td class="text-right">
                            <span id="nouvelleLimiteApercu" class="font-weight-bold text-info">
                              <%= String.format("%.2f HTG", compte.getLimiteCredit()) %>
                            </span>
                          </td>
                        </tr>
                        <tr>
                          <td>Changement:</td>
                          <td class="text-right">
                            <span id="changementApercu" class="font-weight-bold text-success">
                              +0.00 HTG
                            </span>
                          </td>
                        </tr>
                        <tr class="">
                          <td><strong>Nouveau solde disponible:</strong></td>
                          <td class="text-right">
                            <strong><span id="nouveauTotalDisponible" class="<%= soldeDisponibleClass %>">
                              <%= String.format("%.2f HTG", compte.getSoldeDisponible()) %>
                            </span></strong>
                          </td>
                        </tr>
                      </table>
                    </div>
                  </div>
                  
                  <div class="text-gold">
                    <i class="fe fe-alert-circle"></i>
                    Après modification, le client pourra acheter jusqu'à <span id="achatMax" class="font-weight-bold">
                    <%= String.format("%.2f HTG", compte.getLimiteCredit()) %></span> même avec solde 0.
                  </div>
                  
                  <div class="text-right">
                    <a href="CompteClientServlet?action=etatCompte&clientId=<%= client.getId() %>" 
                       class="btn btn-secondary mr-2">Annuler</a>
                    <button type="submit" class="btn btn-gold">
                      <i class="fe fe-save"></i> Mettre à jour la limite
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />

<script>
// Calcul en temps réel pour le dépôt
document.getElementById('montant').addEventListener('input', function() {
  const montant = parseFloat(this.value) || 0;
  const soldeActuel = <%= compte.getSolde().doubleValue() %>;
  const nouveauSolde = soldeActuel + montant;
  
  document.getElementById('montantApercu').textContent = '+ ' + montant.toFixed(2) + ' HTG';
  document.getElementById('nouveauSoldeApercu').textContent = nouveauSolde.toFixed(2) + ' HTG';
  
  // Changer la couleur selon le nouveau solde
  const soldeElement = document.getElementById('nouveauSoldeApercu');
  if (nouveauSolde >= 0) {
    soldeElement.className = 'text-primary';
  } else {
    soldeElement.className = 'text-danger';
  }
});

// Calcul en temps réel pour la limite de crédit
document.getElementById('nouvelleLimite').addEventListener('input', function() {
  const nouvelleLimite = parseFloat(this.value) || 0;
  const ancienneLimite = <%= compte.getLimiteCredit().doubleValue() %>;
  const soldeActuel = <%= compte.getSolde().doubleValue() %>;
  const changement = nouvelleLimite - ancienneLimite;
  const nouveauTotal = soldeActuel + nouvelleLimite;
  
  // Mettre à jour l'affichage de la nouvelle limite
  document.getElementById('nouvelleLimiteApercu').textContent = 
    nouvelleLimite.toFixed(2) + ' HTG';
  
  // Mettre à jour le changement
  const changementEl = document.getElementById('changementApercu');
  if (changement >= 0) {
    changementEl.textContent = '+' + changement.toFixed(2) + ' HTG';
    changementEl.className = 'font-weight-bold text-success';
  } else {
    changementEl.textContent = changement.toFixed(2) + ' HTG';
    changementEl.className = 'font-weight-bold text-danger';
  }
  
  // Mettre à jour le solde disponible
  document.getElementById('nouveauTotalDisponible').textContent = 
    nouveauTotal.toFixed(2) + ' HTG';
  
  // Changer la couleur selon le total disponible
  const totalEl = document.getElementById('nouveauTotalDisponible');
  if (nouveauTotal >= 0) {
    totalEl.className = 'text-primary';
  } else {
    totalEl.className = 'text-danger';
  }
  
  // Mettre à jour le maximum d'achat
  document.getElementById('achatMax').textContent = 
    nouvelleLimite.toFixed(2) + ' HTG';
});

function validerDepot() {
  const montant = parseFloat(document.getElementById('montant').value);
  const modePaiement = document.getElementById('modePaiement').value;
  
  if (montant <= 0) {
    alert('Le montant doit être supérieur à 0');
    return false;
  }
  
  if (!modePaiement) {
    alert('Veuillez sélectionner un mode de paiement');
    return false;
  }
  
  return confirm('Confirmez-vous le dépôt de ' + montant.toFixed(2) + ' HTG pour ' +
                 '<%= client.getNom() %> <%= client.getPrenom() %>?');
}

function validerLimiteCredit() {
  const nouvelleLimite = parseFloat(document.getElementById('nouvelleLimite').value);
  const ancienneLimite = <%= compte.getLimiteCredit().doubleValue() %>;
  
  if (nouvelleLimite === ancienneLimite) {
    alert('Aucun changement détecté. La limite reste la même.');
    return false;
  }
  
  const changement = nouvelleLimite - ancienneLimite;
  let message = '';
  
  if (changement > 0) {
    message = 'AUGMENTATION de la limite de crédit:\n' +
              'Ancienne: ' + ancienneLimite.toFixed(2) + ' HTG\n' +
              'Nouvelle: ' + nouvelleLimite.toFixed(2) + ' HTG\n' +
              '(+' + changement.toFixed(2) + ' HTG)\n\n' +
              'Le client pourra acheter ' + nouvelleLimite.toFixed(2) + ' HTG de plus.';
  } else {
    message = 'RÉDUCTION de la limite de crédit:\n' +
              'Ancienne: ' + ancienneLimite.toFixed(2) + ' HTG\n' +
              'Nouvelle: ' + nouvelleLimite.toFixed(2) + ' HTG\n' +
              '(' + changement.toFixed(2) + ' HTG)\n\n' +
              'Attention: Si le client utilise déjà plus de crédit que la nouvelle limite, il ne pourra plus acheter.';
  }
  
  return confirm(message + '\n\nConfirmer la modification?');
}
</script>