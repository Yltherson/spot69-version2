<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.spot69.model.*,java.math.BigDecimal"%>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<%
Commande commande = (Commande) request.getAttribute("commande");
CompteClient compte = (CompteClient) request.getAttribute("compte");
Utilisateur client = (Utilisateur) request.getAttribute("client");
Integer userId = (Integer) request.getAttribute("userId");
%>

<main role="main" class="main-content">
    <div class="container-fluid">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-header">
                        <h4 class="card-title">
                            <i class="fe fe-credit-card text-primary"></i>
                            Paiement de commande
                        </h4>
                        <p class="card-subtitle">Paiement via compte client</p>
                    </div>
                    <div class="card-body">
                        <% if (commande != null && compte != null && client != null) { %>
                        <!-- Informations commande -->
                        <div class="alert alert-primary mb-4">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6>Commande #<%= commande.getNumeroCommande() %></h6>
                                    <p class="mb-1">
                                        <strong>Client:</strong> 
                                        <%= client.getNom() %> <%= client.getPrenom() %>
                                    </p>
                                    <p class="mb-1">
                                        <strong>Date:</strong> 
                                        <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(commande.getDateCommande()) %>
                                    </p>
                                    <p class="mb-0">
                                        <strong>Statut:</strong> 
                                        <span class="badge badge-warning"><%= commande.getStatutPaiement() %></span>
                                    </p>
                                </div>
                                <div class="col-md-6 text-right">
                                    <h3 class="mb-0"><%= commande.getMontantTotal() %> DA</h3>
                                    <p class="mb-0 text-muted">Montant total</p>
                                    <% if (commande.getCredit() != null) { 
                                        double restant = commande.getCredit().getMontantTotal() - commande.getCredit().getMontantPaye();
                                    %>
                                    <p class="mb-0">
                                        <small>Déjà payé: <%= commande.getCredit().getMontantPaye() %> DA</small>
                                    </p>
                                    <p class="mb-0">
                                        <small class="text-danger">Restant: <%= restant %> DA</small>
                                    </p>
                                    <% } %>
                                </div>
                            </div>
                        </div>

                        <!-- Informations compte -->
                        <div class="alert alert-info mb-4">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6>Compte client</h6>
                                    <p class="mb-1">
                                        <strong>Solde disponible:</strong>
                                        <span class="<%= compte.getSolde().compareTo(BigDecimal.valueOf(commande.getMontantTotal())) >= 0 ? "text-success" : "text-danger" %>">
                                            <%= compte.getSolde().setScale(2) %> DA
                                        </span>
                                    </p>
                                    <p class="mb-0">
                                        <strong>Client:</strong> 
                                        <%= client.getNom() %> <%= client.getPrenom() %>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <% 
                                        BigDecimal montantCommande = BigDecimal.valueOf(commande.getMontantTotal());
                                        BigDecimal difference = compte.getSolde().subtract(montantCommande);
                                    %>
                                    <% if (difference.compareTo(BigDecimal.ZERO) >= 0) { %>
                                    <div class="text-success">
                                        <i class="fe fe-check-circle"></i>
                                        <strong>Solde suffisant</strong><br>
                                        <small>Nouveau solde après paiement: <%= difference.setScale(2) %> DA</small>
                                    </div>
                                    <% } else { %>
                                    <div class="text-danger">
                                        <i class="fe fe-alert-triangle"></i>
                                        <strong>Solde insuffisant</strong><br>
                                        <small>Il manque: <%= difference.abs().setScale(2) %> DA</small>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>

                        <!-- Options de paiement -->
                        <div class="mb-4">
                            <h5>Options de paiement</h5>
                            <div class="row">
                                <!-- Paiement complet via compte -->
                                <% if (compte.getSolde().compareTo(BigDecimal.valueOf(commande.getMontantTotal())) >= 0) { %>
                                <div class="col-md-6 mb-3">
                                    <div class="card border-success">
                                        <div class="card-body text-center">
                                            <i class="fe fe-credit-card fe-24 text-success mb-3"></i>
                                            <h5>Paiement complet</h5>
                                            <p class="text-muted">Utiliser le solde du compte</p>
                                            <form method="POST" action="CompteClientServlet" style="display: inline;">
                                                <input type="hidden" name="action" value="payerCommandeViaCompte">
                                                <input type="hidden" name="commandeId" value="<%= commande.getId() %>">
                                                <button type="submit" class="btn btn-success">
                                                    Payer <%= commande.getMontantTotal() %> DA
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <% } %>
                                
                                <!-- Paiement mixte -->
                                <div class="col-md-6 mb-3">
                                    <div class="card border-primary">
                                        <div class="card-body text-center">
                                            <i class="fe fe-dollar-sign fe-24 text-primary mb-3"></i>
                                            <h5>Paiement mixte</h5>
                                            <p class="text-muted">Partie compte + partie espèces</p>
                                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#paiementMixteModal">
                                                Choisir les montants
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="text-center">
                            <a href="CommandeServlet?action=detail&id=<%= commande.getId() %>" 
                               class="btn btn-secondary">
                                <i class="fe fe-arrow-left"></i> Retour à la commande
                            </a>
                        </div>
                        <% } else { %>
                        <div class="alert alert-danger">
                            <i class="fe fe-alert-triangle"></i>
                            Données manquantes. Impossible de procéder au paiement.
                        </div>
                        <div class="text-center">
                            <a href="CommandeServlet" class="btn btn-primary">
                                <i class="fe fe-arrow-left"></i> Retour aux commandes
                            </a>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Modal paiement mixte -->
<div class="modal fade" id="paiementMixteModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form method="POST" action="CompteClientServlet">
                <input type="hidden" name="action" value="payerCommandeMixte">
                <input type="hidden" name="commandeId" value="<%= commande != null ? commande.getId() : "" %>">
                
                <div class="modal-header">
                    <h5 class="modal-title">Paiement mixte</h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <small>Montant total: <strong><%= commande != null ? commande.getMontantTotal() : "0" %> DA</strong></small><br>
                        <small>Solde disponible: <strong><%= compte != null ? compte.getSolde().setScale(2) : "0.00" %> DA</strong></small>
                    </div>
                    
                    <div class="form-group">
                        <label>Montant via compte</label>
                        <div class="input-group">
                            <input type="number" class="form-control" id="montantCompte" 
                                   name="montantCompte" step="0.01" min="0" 
                                   max="<%= compte != null ? compte.getSolde().doubleValue() : 0 %>"
                                   value="<%= compte != null && compte.getSolde().compareTo(BigDecimal.valueOf(commande != null ? commande.getMontantTotal() : 0)) >= 0 ? commande.getMontantTotal() : (compte != null ? compte.getSolde().doubleValue() : 0) %>">
                            <div class="input-group-append">
                                <span class="input-group-text">DA</span>
                            </div>
                        </div>
                        <small class="form-text text-muted">Maximum: <%= compte != null ? compte.getSolde().setScale(2) : "0.00" %> DA</small>
                    </div>
                    
                    <div class="form-group">
                        <label>Montant espèces</label>
                        <div class="input-group">
                            <input type="number" class="form-control" id="montantEspeces" 
                                   name="montantEspeces" step="0.01" min="0" value="0">
                            <div class="input-group-append">
                                <span class="input-group-text">DA</span>
                            </div>
                        </div>
                        <small class="form-text text-muted">Montant à payer en espèces</small>
                    </div>
                    
                    <div class="form-group">
                        <label>Mode de paiement espèces</label>
                        <select class="form-control" name="modePaiementEspeces" required>
                            <option value="ESPECES">Espèces</option>
                            <option value="CARTE">Carte bancaire</option>
                            <option value="CHEQUE">Chèque</option>
                            <option value="VIREMENT">Virement</option>
                        </select>
                    </div>
                    
                    <div class="alert alert-light">
                        <p class="mb-1"><strong>Résumé:</strong></p>
                        <p class="mb-1">Total: <span id="totalPreview"><%= commande != null ? commande.getMontantTotal() : "0" %></span> DA</p>
                        <p class="mb-1">Compte: <span id="comptePreview">0.00</span> DA</p>
                        <p class="mb-1">Espèces: <span id="especesPreview">0.00</span> DA</p>
                        <p class="mb-0">Différence: <span id="differencePreview">0.00</span> DA</p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-primary" id="confirmMixteBtn" disabled>
                        Confirmer le paiement
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
document.addEventListener('DOMContentLoaded', function() {
    const montantCompteInput = document.getElementById('montantCompte');
    const montantEspecesInput = document.getElementById('montantEspeces');
    const totalPreview = document.getElementById('totalPreview');
    const comptePreview = document.getElementById('comptePreview');
    const especesPreview = document.getElementById('especesPreview');
    const differencePreview = document.getElementById('differencePreview');
    const confirmBtn = document.getElementById('confirmMixteBtn');
    
    const total = <%= commande != null ? commande.getMontantTotal() : 0 %>;
    const soldeMax = <%= compte != null ? compte.getSolde().doubleValue() : 0 %>;
    
    function updatePreview() {
        const montantCompte = parseFloat(montantCompteInput.value) || 0;
        const montantEspeces = parseFloat(montantEspecesInput.value) || 0;
        const totalPaye = montantCompte + montantEspeces;
        const difference = total - totalPaye;
        
        comptePreview.textContent = montantCompte.toFixed(2);
        especesPreview.textContent = montantEspeces.toFixed(2);
        differencePreview.textContent = difference.toFixed(2);
        
        // Validation
        const isValid = montantCompte <= soldeMax && 
                       montantCompte >= 0 && 
                       montantEspeces >= 0 &&
                       Math.abs(difference) < 0.01; // Tolérance de 0.01 DA
        
        confirmBtn.disabled = !isValid;
        
        if (difference < -0.01) {
            differencePreview.className = 'text-danger';
            differencePreview.textContent = difference.toFixed(2) + ' (trop payé)';
        } else if (difference > 0.01) {
            differencePreview.className = 'text-danger';
            differencePreview.textContent = difference.toFixed(2) + ' (manquant)';
        } else {
            differencePreview.className = 'text-success';
            differencePreview.textContent = '0.00';
        }
        
        // Ajuster automatiquement les montants
        montantCompteInput.max = soldeMax;
        if (montantCompte > soldeMax) {
            montantCompteInput.value = soldeMax;
        }
    }
    
    montantCompteInput.addEventListener('input', function() {
        const montantCompte = parseFloat(this.value) || 0;
        const reste = total - montantCompte;
        montantEspecesInput.value = reste > 0 ? reste.toFixed(2) : '0.00';
        updatePreview();
    });
    
    montantEspecesInput.addEventListener('input', function() {
        const montantEspeces = parseFloat(this.value) || 0;
        const reste = total - montantEspeces;
        montantCompteInput.value = reste > 0 && reste <= soldeMax ? reste.toFixed(2) : soldeMax;
        updatePreview();
    });
    
    // Initialiser
    updatePreview();
});
</script> --%>