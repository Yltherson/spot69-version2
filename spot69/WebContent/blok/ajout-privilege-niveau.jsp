<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.spot69.model.Utilisateur,com.spot69.model.Permissions,com.spot69.utils.PermissionChecker"%>
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
    
    .text-gold{
    color: #daaf5a;
    }
    
    .form-section-title {
        font-weight: 600;
        margin-bottom: 1.5rem;
        font-size: 1.1rem;
    }
</style>

<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canManageNiveaux = PermissionChecker.hasPermission(currentUser, Permissions.GESTION_COMPTES);
%>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
    <div class="container-fluid">
        <div class="">
            <div class="">
                <div class="row">
                    <div class="col-md-12 my-4">
                        <!-- En-tête -->
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 class="h4 mb-1">
                                    <i class="fe fe-plus fe-24 align-self-center"></i>
                                    Nouveau Niveau de Privilège
                                </h2>
                                <p class="text-muted mb-0">Configurez un nouveau niveau pour le programme Privilège 69</p>
                            </div>
                            <div>
                                <a href="PrivilegeNiveauServlet?action=lister" class="btn btn-outline-secondary">
                                    <i class="fe fe-arrow-left fe-16 mr-2"></i> Retour à la liste
                                </a>
                            </div>
                        </div>
                        
                        <!-- Formulaire -->
                        <div class="card shadow">
                            <div class="card-body">
                                <form method="POST" action="PrivilegeNiveauServlet" id="addNiveauForm">
                                    <input type="hidden" name="action" value="ajouter">
                                    
                                    <div class="form-section">
                                        <h6 class="form-section-title">Informations de base</h6>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="nom">Nom du niveau *</label>
                                                    <input type="text" class="form-control" id="nom" name="nom" 
                                                           placeholder="Ex: GOLD, VIP, PREMIUM" required
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
                                                        <option value="ACTIF" selected>Actif</option>
                                                        <option value="INACTIF">Inactif</option>
                                                    </select>
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
                                                           min="0" max="100000" step="1" required
                                                           placeholder="Ex: 500, 1000, 2000">
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
                                                               name="pourcentageReduction" min="0" max="100" step="0.01" 
                                                               placeholder="Ex: 5, 10, 15" required>
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
                                    
                                    <!-- <div class="form-section">
                                        <h6 class="form-section-title">Apparence</h6>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="couleur">Couleur *</label>
                                                    <div class="d-flex flex-wrap">
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
                                                    </div>
                                                    <input type="text" class="form-control mt-3" id="couleur" name="couleur" 
                                                           placeholder="#FFD700" required>
                                                    <small class="form-text text-muted">
                                                        Cliquez sur une couleur ou saisissez un code hexadécimal.
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="description">Description</label>
                                                    <textarea class="form-control" id="description" name="description" 
                                                              rows="5" placeholder="Décrivez ce niveau de privilège..."></textarea>
                                                    <small class="form-text text-muted">
                                                        Cette description sera visible dans les détails du niveau.
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                     -->
                                   <!--  <div class="form-section">
                                        <h6 class="form-section-title">Aperçu</h6>
                                        
                                        <div class="alert alert-info">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <h6 class="mb-0">Niveau: <span id="previewNom" class="badge">NOM</span></h6>
                                                <span id="previewPourcentage" class="badge bg-success">0%</span>
                                            </div>
                                            <div class="mb-2">
                                                <strong>Seuil:</strong> <span id="previewSeuil">0</span> points
                                            </div>
                                            <div class="progress mb-2">
                                                <div class="progress-bar" id="previewProgress" 
                                                     role="progressbar" style="width: 0%"></div>
                                            </div>
                                            <p id="previewDescription" class="mb-0">Aucune description</p>
                                        </div>
                                    </div> -->
                                    
                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox">
                                            <input type="checkbox" class="custom-control-input" id="confirmation" required>
                                            <label class="custom-control-label" for="confirmation">
                                                Je confirme que les informations saisies sont correctes
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <div class="d-flex justify-content-between">
                                        <a href="PrivilegeNiveauServlet?action=lister" class="btn btn-secondary">
                                            <i class="fe fe-x fe-16 mr-2"></i> Annuler
                                        </a>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fe fe-save fe-16 mr-2"></i> Créer le niveau
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
        }
    }
    
    // Écouteurs d'événements pour la mise à jour en temps réel
    $('#nom, #seuilPoints, #pourcentageReduction, #description, #couleur').on('input', updatePreview);
    
    // Initialisation
    updatePreview();
    
    // Validation du formulaire
    $('#addNiveauForm').submit(function(e) {
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
        
        return true;
    });
    
    // Sélectionner une couleur par défaut
    selectColor('#4e73df');
});
</script>