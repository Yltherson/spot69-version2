<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.spot69.model.PrivilegeNiveau,com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
<meta charset="UTF-8">

<style>
    .color-picker {
        width: 40px;
        height: 40px;
        border-radius: 4px;
        border: 2px solid #dee2e6;
        cursor: pointer;
        margin-right: 10px;
    }
    
    .color-option {
        display: inline-flex;
        align-items: center;
        margin-right: 15px;
        margin-bottom: 10px;
    }
    
    .form-section {
        margin-bottom: 2rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e3e6f0;
    }
    
    .form-section-title {
        font-weight: 600;
        color: #4e73df;
        margin-bottom: 1.5rem;
        font-size: 1.1rem;
    }
    
    .current-niveau {
        border-left: 4px solid transparent;
        transition: all 0.3s;
    }
</style>

<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canManageNiveaux = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);

PrivilegeNiveau niveau = (PrivilegeNiveau) request.getAttribute("niveau");
if (niveau == null) {
    response.sendRedirect("PrivilegeNiveauServlet?action=lister");
    return;
}
%>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
    <div class="container-fluid">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="row">
                    <div class="col-md-12 my-4">
                        <!-- En-tête -->
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 class="h4 mb-1">
                                    <i class="fe fe-edit fe-24 align-self-center text-primary"></i>
                                    Modifier le Niveau de Privilège
                                </h2>
                                <p class="text-muted mb-0">Modifiez la configuration du niveau <%= niveau.getNom() %></p>
                            </div>
                            <div class="d-flex align-items-center">
                                <span class="badge <%= niveau.getBadgeClass() %> p-2 mr-3">
                                    <i class="fe fe-award fe-16 mr-1"></i>
                                    <%= niveau.getNom() %>
                                </span>
                                <a href="PrivilegeNiveauServlet?action=lister" class="btn btn-outline-secondary">
                                    <i class="fe fe-arrow-left fe-16 mr-2"></i> Retour à la liste
                                </a>
                            </div>
                        </div>
                        
                        <!-- Carte d'information actuelle -->
                        <div class="card shadow mb-4 current-niveau" style="border-left-color: <%= niveau.getCouleur() %>;">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 class="card-title mb-1">
                                            <span class="badge <%= niveau.getBadgeClass() %> p-2">
                                                <%= niveau.getNom() %>
                                            </span>
                                        </h5>
                                        <p class="text-muted mb-0">
                                            <i class="fe fe-dollar-sign fe-12 mr-1"></i>
                                            Seuil actuel: <strong><%= niveau.getSeuilPoints() %> points</strong> | 
                                            <i class="fe fe-percent fe-12 mr-1"></i>
                                            Réduction: <strong><%= niveau.getPourcentageReduction() %>%</strong>
                                        </p>
                                    </div>
                                    <div>
                                        <span class="badge <%= "ACTIF".equals(niveau.getStatut()) ? "badge-success" : "badge-secondary" %>">
                                            <%= niveau.getStatut() %>
                                        </span>
                                    </div>
                                </div>
                                <% if (niveau.getDescription() != null && !niveau.getDescription().isEmpty()) { %>
                                <div class="mt-3">
                                    <p class="mb-0"><%= niveau.getDescription() %></p>
                                </div>
                                <% } %>
                            </div>
                        </div>
                        
                        <!-- Formulaire de modification -->
                        <div class="card shadow">
                            <div class="card-body">
                                <form method="POST" action="PrivilegeNiveauServlet" id="editNiveauForm">
                                    <input type="hidden" name="action" value="modifier">
                                    <input type="hidden" name="id" value="<%= niveau.getId() %>">
                                    
                                    <div class="form-section">
                                        <h6 class="form-section-title">Informations de base</h6>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="nom">Nom du niveau *</label>
                                                    <input type="text" class="form-control" id="nom" name="nom" 
                                                           value="<%= niveau.getNom() %>" required
                                                           pattern="[A-Za-z0-9\s]+" title="Lettres, chiffres et espaces seulement">
                                                    <small class="form-text text-muted">
                                                        Le nom sera affiché en majuscules. Ex: "gold" → "GOLD"
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="statut">Statut</label>
                                                    <select class="form-control" id="statut" name="statut">
                                                        <option value="ACTIF" <%= "ACTIF".equals(niveau.getStatut()) ? "selected" : "" %>>Actif</option>
                                                        <option value="INACTIF" <%= "INACTIF".equals(niveau.getStatut()) ? "selected" : "" %>>Inactif</option>
                                                    </select>
                                                    <small class="form-text text-muted">
                                                        Un niveau inactif ne sera pas attribué automatiquement
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-section">
                                        <h6 class="form-section-title">Seuil et avantages</h6>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="seuilPoints">Seuil de points *</label>
                                                    <input type="number" class="form-control" id="seuilPoints" name="seuilPoints" 
                                                           value="<%= niveau.getSeuilPoints() %>"
                                                           min="0" max="100000" step="1" required>
                                                    <small class="form-text text-muted">
                                                        Points minimum pour atteindre ce niveau. 0 = niveau de base.
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="pourcentageReduction">Pourcentage de réduction *</label>
                                                    <div class="input-group">
                                                        <input type="number" class="form-control" id="pourcentageReduction" 
                                                               name="pourcentageReduction" 
                                                               value="<%= niveau.getPourcentageReduction() %>"
                                                               min="0" max="100" step="0.01" required>
                                                        <div class="input-group-append">
                                                            <span class="input-group-text">%</span>
                                                        </div>
                                                    </div>
                                                    <small class="form-text text-muted">
                                                        Réduction appliquée aux achats des clients de ce niveau.
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-section">
                                        <h6 class="form-section-title">Apparence</h6>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="couleur">Couleur *</label>
                                                    <div class="d-flex flex-wrap mb-3">
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #CD7F32;" 
                                                                 onclick="selectColor('#CD7F32')"></div>
                                                            <span>Bronze</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #C0C0C0;" 
                                                                 onclick="selectColor('#C0C0C0')"></div>
                                                            <span>Silver</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #FFD700;" 
                                                                 onclick="selectColor('#FFD700')"></div>
                                                            <span>Gold</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #8A2BE2;" 
                                                                 onclick="selectColor('#8A2BE2')"></div>
                                                            <span>Violet</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #FF4500;" 
                                                                 onclick="selectColor('#FF4500')"></div>
                                                            <span>Orange</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #28a745;" 
                                                                 onclick="selectColor('#28a745')"></div>
                                                            <span>Vert</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #4e73df;" 
                                                                 onclick="selectColor('#4e73df')"></div>
                                                            <span>Bleu</span>
                                                        </div>
                                                        <div class="color-option">
                                                            <div class="color-picker" style="background-color: #e74a3b;" 
                                                                 onclick="selectColor('#e74a3b')"></div>
                                                            <span>Rouge</span>
                                                        </div>
                                                    </div>
                                                    <input type="text" class="form-control" id="couleur" name="couleur" 
                                                           value="<%= niveau.getCouleur() %>" required>
                                                    <small class="form-text text-muted">
                                                        Cliquez sur une couleur ou saisissez un code hexadécimal.
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="description">Description</label>
                                                    <textarea class="form-control" id="description" name="description" 
                                                              rows="5" placeholder="Décrivez ce niveau de privilège..."><%= niveau.getDescription() != null ? niveau.getDescription() : "" %></textarea>
                                                    <small class="form-text text-muted">
                                                        Cette description sera visible dans les détails du niveau.
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-section">
                                        <h6 class="form-section-title">Aperçu des modifications</h6>
                                        
                                        <div class="alert alert-info">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <h6 class="mb-0">Niveau: <span id="previewNom" class="badge <%= niveau.getBadgeClass() %>"><%= niveau.getNom() %></span></h6>
                                                <span id="previewPourcentage" class="badge bg-success"><%= niveau.getPourcentageReduction() %>%</span>
                                            </div>
                                            <div class="mb-2">
                                                <strong>Seuil:</strong> <span id="previewSeuil"><%= niveau.getSeuilPoints() %></span> points
                                                <% if (niveau.getSeuilPoints() > 0) { %>
                                                <span class="text-muted small">(Points minimum requis)</span>
                                                <% } %>
                                            </div>
                                            <div class="progress mb-2">
                                                <div class="progress-bar" id="previewProgress" 
                                                     role="progressbar" 
                                                     style="width: <%= Math.min(100, (niveau.getSeuilPoints() * 100) / 3000) %>%"></div>
                                            </div>
                                            <p id="previewDescription" class="mb-0">
                                                <%= niveau.getDescription() != null && !niveau.getDescription().isEmpty() ? 
                                                    niveau.getDescription() : "Aucune description" %>
                                            </p>
                                        </div>
                                        
                                        <div class="alert alert-warning">
                                            <i class="fe fe-alert-triangle fe-16 mr-2"></i>
                                            <strong>Attention:</strong> La modification du seuil de points peut affecter le niveau de privilège 
                                            des utilisateurs existants. Un recalcul sera effectué automatiquement lors de leur prochaine connexion.
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox">
                                            <input type="checkbox" class="custom-control-input" id="confirmation" required>
                                            <label class="custom-control-label" for="confirmation">
                                                Je confirme vouloir modifier ce niveau de privilège
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <a href="PrivilegeNiveauServlet?action=lister" class="btn btn-secondary mr-2">
                                                <i class="fe fe-x fe-16 mr-2"></i> Annuler
                                            </a>
                                            <a href="PrivilegeNiveauServlet?action=details&id=<%= niveau.getId() %>" 
                                               class="btn btn-outline-info" 
                                               onclick="showNiveauDetails('<%= niveau.getId() %>'); return false;">
                                                <i class="fe fe-eye fe-16 mr-2"></i> Voir les détails
                                            </a>
                                        </div>
                                        <div>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fe fe-save fe-16 mr-2"></i> Enregistrer les modifications
                                            </button>
                                        </div>
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

<!-- Modal Détails Niveau -->
<div class="modal fade" id="detailNiveauModal" tabindex="-1" role="dialog" 
     aria-labelledby="detailNiveauModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="detailNiveauModalLabel">
                    <i class="fe fe-eye fe-16 mr-2"></i>Détails du Niveau
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="detailNiveauContent">
                <!-- Le contenu sera chargé dynamiquement -->
                <div class="text-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="sr-only">Chargement...</span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Fermer</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
$(document).ready(function() {
    // Sélection de couleur
    function selectColor(color) {
        $('#couleur').val(color);
        updatePreview();
    }
    
    // Mise à jour de l'aperçu en temps réel
    function updatePreview() {
        // Nom
        const nom = $('#nom').val().toUpperCase() || 'NOM';
        $('#previewNom').text(nom);
        
        // Mise à jour de la classe du badge selon le nom
        let badgeClass = 'badge-secondary';
        if (nom.includes('VIP')) badgeClass = 'badge-vip';
        else if (nom.includes('GOLD')) badgeClass = 'badge-gold';
        else if (nom.includes('SILVER')) badgeClass = 'badge-silver';
        else if (nom.includes('BRONZE')) badgeClass = 'badge-bronze';
        
        $('#previewNom').removeClass().addClass('badge ' + badgeClass);
        
        // Seuil et pourcentage
        const seuil = $('#seuilPoints').val() || 0;
        const pourcentage = $('#pourcentageReduction').val() || 0;
        
        $('#previewSeuil').text(seuil);
        $('#previewPourcentage').text(pourcentage + '%');
        
        // Barre de progression
        const progressPercent = Math.min(100, (seuil / 3000) * 100);
        $('#previewProgress').css('width', progressPercent + '%');
        
        // Description
        const description = $('#description').val() || 'Aucune description';
        $('#previewDescription').text(description);
        
        // Couleur du badge si personnalisée
        const couleur = $('#couleur').val();
        if (couleur && couleur.startsWith('#')) {
            $('#previewNom').css('background-color', couleur);
            // Mettre à jour la bordure de la carte d'aperçu
            $('.current-niveau').css('border-left-color', couleur);
        }
    }
    
    // Écouteurs d'événements pour la mise à jour en temps réel
    $('#nom, #seuilPoints, #pourcentageReduction, #description, #couleur').on('input', updatePreview);
    
    // Initialisation
    updatePreview();
    
    // Validation du formulaire
    $('#editNiveauForm').submit(function(e) {
        // Validation du nom
        const nom = $('#nom').val();
        if (!/^[A-Za-z0-9\s]+$/.test(nom)) {
            e.preventDefault();
            alert('Le nom ne peut contenir que des lettres, chiffres et espaces');
            return false;
        }
        
        // Validation du seuil
        const seuil = parseInt($('#seuilPoints').val());
        if (seuil < 0 || seuil > 100000) {
            e.preventDefault();
            alert('Le seuil doit être entre 0 et 100000 points');
            return false;
        }
        
        // Validation du pourcentage
        const pourcentage = parseFloat($('#pourcentageReduction').val());
        if (pourcentage < 0 || pourcentage > 100) {
            e.preventDefault();
            alert('Le pourcentage doit être entre 0 et 100');
            return false;
        }
        
        // Validation de la couleur
        const couleur = $('#couleur').val();
        if (!/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(couleur)) {
            e.preventDefault();
            alert('Veuillez saisir une couleur hexadécimale valide (ex: #FFD700)');
            return false;
        }
        
        // Avertissement pour les changements importants
        const ancienSeuil = <%= niveau.getSeuilPoints() %>;
        const ancienPourcentage = <%= niveau.getPourcentageReduction() %>;
        const ancienNom = '<%= niveau.getNom() %>';
        
        if (seuil !== ancienSeuil || pourcentage !== ancienPourcentage || nom !== ancienNom) {
            if (!confirm('Les modifications que vous apportez peuvent affecter les privilèges des utilisateurs existants.\n\nÊtes-vous sûr de vouloir continuer ?')) {
                e.preventDefault();
                return false;
            }
        }
        
        return true;
    });
    
    // Fonction pour afficher les détails dans une modal
    window.showNiveauDetails = function(niveauId) {
        $('#detailNiveauContent').html('<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">Chargement...</span></div></div>');
        $('#detailNiveauModal').modal('show');
        
        // Charger les détails via AJAX
        $.ajax({
            url: 'PrivilegeNiveauServlet',
            type: 'GET',
            data: { action: 'details', id: niveauId },
            success: function(response) {
                $('#detailNiveauContent').html(response);
            },
            error: function() {
                $('#detailNiveauContent').html('<div class="alert alert-danger">Erreur lors du chargement des détails.</div>');
            }
        });
    };
    
    // Gestion des messages de notification
    <% 
    String successMsg = (String) session.getAttribute("ToastAdmSuccesNotif");
    String errorMsg = (String) session.getAttribute("ToastAdmErrorNotif");
    
    if(successMsg != null) {
        session.removeAttribute("ToastAdmSuccesNotif");
    %>
        showToast('success', '<%= successMsg %>');
    <% } 
    
    if(errorMsg != null) {
        session.removeAttribute("ToastAdmErrorNotif");
    %>
        showToast('error', '<%= errorMsg %>');
    <% } %>
    
    // Fonction pour afficher les toasts
    function showToast(type, message) {
        var toastClass = type === 'success' ? 'alert-success' : 'alert-danger';
        var toastHtml = '<div class="toast-alert alert ' + toastClass + ' alert-dismissible fade show" role="alert">' +
                        '<i class="fe fe-' + (type === 'success' ? 'check' : 'alert-triangle') + ' fe-16 mr-2"></i>' +
                        message +
                        '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                        '<span aria-hidden="true">&times;</span>' +
                        '</button>' +
                        '</div>';
        
        $('main').prepend(toastHtml);
        
        // Supprimer le toast après 5 secondes
        setTimeout(function() {
            $('.toast-alert').alert('close');
        }, 5000);
    }
});
</script>