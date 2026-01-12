<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.spot69.model.Evenement,com.spot69.model.TypeTableEvenement,java.util.List"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<meta charset="UTF-8">

<style>
    .image-preview-container {
        margin-top: 20px;
        text-align: center;
    }
    .image-preview {
        max-width: 300px;
        max-height: 200px;
        border: 2px dashed #ddd;
        padding: 10px;
        border-radius: 5px;
    }
    .required-field::after {
        content: " *";
        color: red;
    }
    .table-item {
        border: 1px solid #dee2e6;
        border-radius: 5px;
        padding: 15px;
        margin-bottom: 15px;
    }
    .table-item .btn-danger {
        opacity: 0.8;
    }
    .table-item .btn-danger:hover {
        opacity: 1;
    }
    .no-tables-message {
        color: #6c757d;
        font-style: italic;
        padding: 20px;
        text-align: center;
        border: 2px dashed #dee2e6;
        border-radius: 5px;
    }
    .form-label {
        font-weight: 500;
        margin-bottom: 5px;
    }
    .system-info {
        font-size: 0.9rem;
    }
</style>

<%
Evenement evenement = (Evenement) request.getAttribute("evenement");
if (evenement == null) {
    response.sendRedirect("EvenementServlet?action=lister");
    return;
}

// Variables pour les valeurs du formulaire
String titre = evenement.getTitre();
String artisteGroupe = evenement.getArtisteGroupe() != null ? evenement.getArtisteGroupe() : "";
String description = evenement.getDescription() != null ? evenement.getDescription() : "";
String statut = evenement.getStatut() != null ? evenement.getStatut() : "VISIBLE";

// Format date for datetime-local input
java.time.format.DateTimeFormatter dateFormatter = 
    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
String formattedDate = evenement.getDateEvent().format(dateFormatter);

// Pour l'image
String ctx = request.getContextPath();
                   
String mediaPath = evenement.getMediaPath();
String fullMediaPath = evenement.getFullMediaPath();
String imagePath;
if (fullMediaPath != null && fullMediaPath.startsWith("/uploads/evenements/")) {
    imagePath = ctx + "/images/evenements/" + fullMediaPath.substring("uploads/evenements/".length());
} else {
    imagePath = ctx + "/images/default/default.jpg";
}
boolean hasMedia = evenement.hasMedia();

// Pour le label du fichier
String fileLabel = hasMedia ? "Changer l'image..." : "Choisir une image...";

// Pour les dates système
java.util.Date createdAt = evenement.getCreatedAtAsDate();
java.util.Date updatedAt = evenement.getUpdatedAtAsDate();

// Récupérer les tables
List<TypeTableEvenement> tables = evenement.getTypesTables();
int tableCount = (tables != null) ? tables.size() : 0;
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
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h2 class="h4 mb-0">
                                <i class="fe fe-edit fe-24 align-self-center text-primary mr-2"></i>
                                Modifier l'Événement
                            </h2>
                            <a href="EvenementServlet?action=lister" class="btn btn-outline-secondary">
                                <i class="fe fe-arrow-left fe-16"></i> Retour à la liste
                            </a>
                        </div>

                        <div class="card shadow">
                            <div class="card-body">
                                <form method="POST" action="EvenementServlet" enctype="multipart/form-data" id="eventForm">
                                    <input type="hidden" name="action" value="modifier">
                                    <input type="hidden" name="id" value="<%= evenement.getId() %>">
                                    
                                    <div class="row">
                                        <!-- Colonne gauche -->
                                        <div class="col-md-6">
                                            <!-- Titre -->
                                            <div class="form-group mb-3">
                                                <label for="titre" class="required-field">Titre de l'événement</label>
                                                <input type="text" class="form-control" id="titre" name="titre" 
                                                       value="<%= titre %>" required maxlength="200">
                                                <small class="form-text text-muted">Maximum 200 caractères</small>
                                            </div>
                                            
                                            <!-- Artiste/Groupe -->
                                            <div class="form-group mb-3">
                                                <label for="artisteGroupe">Artiste/Groupe</label>
                                                <input type="text" class="form-control" id="artisteGroupe" name="artisteGroupe"
                                                       value="<%= artisteGroupe %>" maxlength="100">
                                            </div>
                                            
                                            <!-- Date et Heure -->
                                            <div class="form-group mb-3">
                                                <label for="dateEvent" class="required-field">Date et heure</label>
                                                <input type="datetime-local" class="form-control" id="dateEvent" name="dateEvent"
                                                       value="<%= formattedDate %>" required>
                                            </div>
                                            
                                            <!-- Statut -->
                                            <div class="form-group mb-3">
                                                <label for="statut">Statut</label>
                                                <select class="form-control" id="statut" name="statut">
                                                    <option value="VISIBLE" <%= "VISIBLE".equals(statut) ? "selected" : "" %>>Visible</option>
                                                    <option value="HIDDEN" <%= "HIDDEN".equals(statut) ? "selected" : "" %>>Masqué</option>
                                                </select>
                                            </div>
                                            
                                            <!-- Informations système -->
                                            <div class="card bg-light mt-3">
                                                <div class="card-body system-info">
                                                    <h6 class="card-title">Informations système</h6>
                                                    <% if (createdAt != null) { %>
                                                        <p class="mb-1">
                                                            <small class="text-muted">Créé le:</small><br>
                                                            <fmt:formatDate value="<%= createdAt %>" 
                                                                pattern="dd/MM/yyyy à HH:mm" />
                                                        </p>
                                                    <% } %>
                                                    <% if (updatedAt != null) { %>
                                                        <p class="mb-1">
                                                            <small class="text-muted">Dernière modification:</small><br>
                                                            <fmt:formatDate value="<%= updatedAt %>" 
                                                                pattern="dd/MM/yyyy à HH:mm" />
                                                        </p>
                                                    <% } %>
                                                    <% if (evenement.getCapaciteTotale() > 0) { %>
                                                        <p class="mb-0">
                                                            <small class="text-muted">Capacité totale:</small><br>
                                                            <span class="badge bg-info"><%= evenement.getCapaciteTotale() %> places</span>
                                                        </p>
                                                    <% } %>
                                                    <% if (tableCount > 0) { %>
                                                        <p class="mb-0 mt-2">
                                                            <small class="text-muted">Nombre de tables:</small><br>
                                                            <span class="badge bg-primary"><%= tableCount %> type(s)</span>
                                                        </p>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <!-- Colonne droite -->
                                        <div class="col-md-6">
                                            <!-- Description -->
                                            <div class="form-group mb-3">
                                                <label for="description">Description</label>
                                                <textarea class="form-control" id="description" name="description" 
                                                          rows="5" maxlength="2000"><%= description %></textarea>
                                                <small class="form-text text-muted">Maximum 2000 caractères</small>
                                            </div>
                                            
                                            <!-- Image -->
                                            <div class="form-group mb-3">
                                                <label for="imageEvenement">Image de l'événement</label>
                                                <div class="custom-file">
                                                    <input type="file" class="custom-file-input" id="imageEvenement" 
                                                           name="imageEvenement" accept=".jpg,.jpeg,.png,.gif,.webp">
                                                    <label class="custom-file-label" for="imageEvenement">
                                                        <%= fileLabel %>
                                                    </label>
                                                </div>
                                                <small class="form-text text-muted">
                                                    Formats acceptés: JPG, JPEG, PNG, GIF, WEBP. Taille max: 10MB
                                                </small>
                                                
                                                <!-- Prévisualisation image -->
                                                <div class="image-preview-container">
                                                    <% if (hasMedia) { %>
                                                        <div class="mb-2">
                                                            <img src="<%= imagePath %>" alt="Image actuelle" 
                                                                 class="image-preview" id="currentImage">
                                                            <div class="mt-2">
                                                                <div class="custom-control custom-checkbox">
                                                                    <input type="checkbox" class="custom-control-input" id="deleteImage" name="deleteImage">
                                                                    <label class="custom-control-label text-danger" for="deleteImage">
                                                                        Supprimer cette image
                                                                    </label>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    <% } %>
                                                    <img id="imagePreview" class="image-preview d-none" alt="Aperçu">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Section Tables -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-header d-flex justify-content-between align-items-center">
                                                    <h5 class="mb-0">Configuration des Tables</h5>
                                                    <button type="button" id="add-table-btn" class="btn btn-sm btn-primary">
                                                        <i class="fe fe-plus fe-12"></i> Ajouter une table
                                                    </button>
                                                </div>
                                                <div class="card-body">
                                                    <!-- Conteneur pour les tables -->
                                                    <div id="tables-container">
                                                        <% if (tables != null && !tables.isEmpty()) { 
                                                            for (int i = 0; i < tables.size(); i++) {
                                                                TypeTableEvenement table = tables.get(i);
                                                        %>
                                                        <div class="table-item">
                                                            <input type="hidden" name="tableId[]" value="<%= table.getId() %>">
                                                            <div class="row">
                                                                <div class="col-md-3">
                                                                    <label class="form-label required-field">Nom de la table</label>
                                                                    <input type="text" name="tableNom[]" class="form-control" 
                                                                           value="<%= table.getNom() %>" required>
                                                                </div>
                                                                <div class="col-md-3">
                                                                    <label class="form-label">Description</label>
                                                                    <input type="text" name="tableDescription[]" class="form-control" 
                                                                           value="<%= table.getDescription() != null ? table.getDescription() : "" %>">
                                                                </div>
                                                                <div class="col-md-2">
                                                                    <label class="form-label required-field">Capacité</label>
                                                                    <input type="number" name="tableCapacite[]" class="form-control" 
                                                                           min="1" value="<%= table.getCapacite() %>" required>
                                                                </div>
                                                                <div class="col-md-2">
                                                                    <label class="form-label required-field">Prix (HTG)</label>
                                                                    <input type="number" name="tablePrix[]" class="form-control" 
                                                                           min="0" step="0.01" value="<%= table.getPrix() %>" required>
                                                                </div>
                                                                <div class="col-md-2 d-flex align-items-end">
                                                                    <% if (i == 0 && tables.size() == 1) { %>
                                                                    <button type="button" class="btn btn-danger remove-table-btn" disabled>
                                                                        <i class="fe fe-trash fe-12"></i>
                                                                    </button>
                                                                    <% } else { %>
                                                                    <button type="button" class="btn btn-danger remove-table-btn">
                                                                        <i class="fe fe-trash fe-12"></i>
                                                                    </button>
                                                                    <% } %>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <% } } else { %>
                                                        <!-- Table par défaut si aucune table existante -->
                                                        <div class="table-item">
                                                            <input type="hidden" name="tableId[]" value="new">
                                                            <div class="row">
                                                                <div class="col-md-3">
                                                                    <label class="form-label required-field">Nom de la table</label>
                                                                    <input type="text" name="tableNom[]" class="form-control" 
                                                                           placeholder="Ex: Table VIP" required>
                                                                </div>
                                                                <div class="col-md-3">
                                                                    <label class="form-label">Description</label>
                                                                    <input type="text" name="tableDescription[]" class="form-control" 
                                                                           placeholder="Ex: Table près de la scène">
                                                                </div>
                                                                <div class="col-md-2">
                                                                    <label class="form-label required-field">Capacité</label>
                                                                    <input type="number" name="tableCapacite[]" class="form-control" 
                                                                           min="1" value="4" required>
                                                                </div>
                                                                <div class="col-md-2">
                                                                    <label class="form-label required-field">Prix (HTG)</label>
                                                                    <input type="number" name="tablePrix[]" class="form-control" 
                                                                           min="0" step="0.01" value="1000" required>
                                                                </div>
                                                                <div class="col-md-2 d-flex align-items-end">
                                                                    <button type="button" class="btn btn-danger remove-table-btn" disabled>
                                                                        <i class="fe fe-trash fe-12"></i>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <% } %>
                                                    </div>
                                                    
                                                    <% if (tables == null || tables.isEmpty()) { %>
                                                    <div class="no-tables-message mt-3">
                                                        <i class="fe fe-grid fe-32 mb-2"></i><br>
                                                        <p>Ajoutez différents types de tables avec des capacités et prix variés.</p>
                                                        <small class="text-muted">Ex: Table VIP (2 personnes, 5000 HTG), Table Standard (4 personnes, 2000 HTG), etc.</small>
                                                    </div>
                                                    <% } %>
                                                    
                                                    <div class="alert alert-info mt-3">
                                                        <i class="fe fe-info fe-16"></i>
                                                        <strong>Information :</strong> 
                                                        Pour supprimer une table existante, videz son champ "Nom". 
                                                        Les nouvelles tables seront ajoutées automatiquement.
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Boutons de soumission -->
                                    <div class="row mt-3">
                                        <div class="col-md-12">
                                            <div class="d-flex justify-content-start gap-2">
                                                <button type="submit" class="btn btn-primary btn-lg">
                                                    <i class="fe fe-save fe-16"></i> Mettre à jour
                                                </button>
                                                <a href="EvenementServlet?action=lister" class="btn btn-secondary btn-lg">Annuler</a>
                                                
                                            </div>
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

<jsp:include page="footer.jsp" />

<!-- Script JavaScript simplifié -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Gestion de l'affichage du nom de fichier
    const imageInput = document.getElementById('imageEvenement');
    const fileLabel = document.querySelector('.custom-file-label');
    const imagePreview = document.getElementById('imagePreview');
    const currentImage = document.getElementById('currentImage');
    const deleteImageCheckbox = document.getElementById('deleteImage');
    
    if (imageInput && fileLabel) {
        imageInput.addEventListener('change', function(e) {
            const fileName = e.target.files[0] ? e.target.files[0].name : 'Choisir une image...';
            fileLabel.textContent = fileName;
            
            // Aperçu de l'image
            if (e.target.files && e.target.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    if (imagePreview) {
                        imagePreview.src = e.target.result;
                        imagePreview.classList.remove('d-none');
                    }
                    if (currentImage) {
                        currentImage.classList.add('d-none');
                    }
                    if (deleteImageCheckbox) {
                        deleteImageCheckbox.checked = false;
                    }
                };
                reader.readAsDataURL(e.target.files[0]);
            }
        });
    }
    
    // Gestion de la suppression d'image
    if (deleteImageCheckbox) {
        deleteImageCheckbox.addEventListener('change', function() {
            if (this.checked) {
                if (currentImage) currentImage.classList.add('d-none');
                if (imagePreview) imagePreview.classList.add('d-none');
                if (imageInput) imageInput.value = '';
                if (fileLabel) fileLabel.textContent = 'Choisir une image...';
            }
        });
    }
    
    // Gestion des tables
    const tablesContainer = document.getElementById('tables-container');
    const addTableBtn = document.getElementById('add-table-btn');
    
    if (addTableBtn) {
        addTableBtn.addEventListener('click', function() {
            const newTable = document.createElement('div');
            newTable.className = 'table-item';
            newTable.innerHTML = `
                <input type="hidden" name="tableId[]" value="new">
                <div class="row">
                    <div class="col-md-3">
                        <label class="form-label required-field">Nom de la table</label>
                        <input type="text" name="tableNom[]" class="form-control" 
                               placeholder="Ex: Table Standard" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Description</label>
                        <input type="text" name="tableDescription[]" class="form-control" 
                               placeholder="Description">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label required-field">Capacité</label>
                        <input type="number" name="tableCapacite[]" class="form-control" 
                               min="1" value="4" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label required-field">Prix (HTG)</label>
                        <input type="number" name="tablePrix[]" class="form-control" 
                               min="0" step="0.01" value="500" required>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="button" class="btn btn-danger remove-table-btn">
                            <i class="fe fe-trash fe-12"></i>
                        </button>
                    </div>
                </div>
            `;
            
            tablesContainer.appendChild(newTable);
            
            // Activer le bouton de suppression de la première table s'il était désactivé
            const firstRemoveBtn = document.querySelector('.remove-table-btn');
            if (firstRemoveBtn && firstRemoveBtn.disabled) {
                firstRemoveBtn.disabled = false;
            }
            
            // Masquer le message "pas de tables" s'il existe
            const noTablesMessage = document.querySelector('.no-tables-message');
            if (noTablesMessage) {
                noTablesMessage.style.display = 'none';
            }
            
            // Ajouter l'événement de suppression
            newTable.querySelector('.remove-table-btn').addEventListener('click', function() {
                removeTableItem(this);
            });
        });
    }
    
    // Fonction pour supprimer une table
    function removeTableItem(btn) {
        const tableItem = btn.closest('.table-item');
        const tableItems = tablesContainer.querySelectorAll('.table-item');
        const tableIdInput = tableItem.querySelector('input[name="tableId[]"]');
        const tableNameInput = tableItem.querySelector('input[name="tableNom[]"]');
        
        if (tableItems.length > 1) {
            // Pour les tables existantes (non "new"), vider le nom pour indiquer la suppression
            if (tableIdInput && tableIdInput.value !== 'new') {
                // Table existante, vider le nom pour suppression
                tableNameInput.value = '';
                tableItem.style.display = 'none';
            } else {
                // Nouvelle table, supprimer complètement
                tableItem.remove();
            }
            
            // Si une seule table visible reste, désactiver son bouton de suppression
            const visibleTables = tablesContainer.querySelectorAll('.table-item[style*="display: none"]');
            const allTables = tablesContainer.querySelectorAll('.table-item');
            
            if (allTables.length - visibleTables.length === 1) {
                const remainingRemoveBtn = tablesContainer.querySelector('.remove-table-btn:not([disabled])');
                if (remainingRemoveBtn) {
                    remainingRemoveBtn.disabled = true;
                }
            }
            
        } else {
            alert('Un événement doit avoir au moins une table.');
        }
    }
    
    // Initialiser les boutons de suppression existants
    document.querySelectorAll('.remove-table-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            removeTableItem(this);
        });
    });
    
    // Validation du formulaire
    const eventForm = document.getElementById('eventForm');
    if (eventForm) {
        eventForm.addEventListener('submit', function(e) {
            // Désactiver le bouton pour éviter les doubles clics
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fe fe-loader fe-spin fe-16"></i> En cours...';
            
            // Validation basique
            const titre = document.getElementById('titre').value.trim();
            const dateEvent = document.getElementById('dateEvent').value;
            
            if (!titre) {
                alert('Le titre est obligatoire.');
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
                e.preventDefault();
                return false;
            }
            
            if (!dateEvent) {
                alert('La date est obligatoire.');
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
                e.preventDefault();
                return false;
            }
            
            // Vérifier qu'il y a au moins une table avec un nom
            const tableNames = document.querySelectorAll('input[name="tableNom[]"]');
            let hasValidTable = false;
            
            tableNames.forEach(input => {
                if (input.value.trim() !== '') {
                    hasValidTable = true;
                }
            });
            
            if (!hasValidTable) {
                alert('Vous devez avoir au moins une table avec un nom.');
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
                e.preventDefault();
                return false;
            }
            
            // Si tout est bon, laisser le formulaire s'envoyer
            return true;
        });
    }
    
    // Gestion des notifications toast
    <%
    String toastMessage = (String) session.getAttribute("ToastAdmErrorNotif");
    String toastType = (String) session.getAttribute("toastType");
    if (toastMessage != null) {
    %>
        showToast('<%= toastType %>', '<%= toastMessage %>');
        <%
        session.removeAttribute("ToastAdmErrorNotif");
        session.removeAttribute("toastType");
        %>
    <%
    }
    
    String successMessage = (String) session.getAttribute("ToastAdmSuccesNotif");
    if (successMessage != null) {
    %>
        showToast('success', '<%= successMessage %>');
        <%
        session.removeAttribute("ToastAdmSuccesNotif");
        %>
    <%
    }
    %>
});

// Fonction pour afficher les toasts (version simplifiée)
function showToast(type, message) {
    // Vérifier si Bootstrap est disponible
    if (typeof bootstrap === 'undefined') {
        console.log('Toast: ' + message);
        // Fallback simple
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-' + (type === 'error' ? 'danger' : type) + ' alert-dismissible fade show position-fixed top-0 end-0 m-3';
        alertDiv.style.zIndex = '9999';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
        `;
        document.body.appendChild(alertDiv);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (alertDiv.parentElement) {
                alertDiv.remove();
            }
        }, 5000);
        return;
    }
    
    // Créer le conteneur toast s'il n'existe pas
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    // Créer le toast
    const toastId = 'toast-' + Date.now();
    const toast = document.createElement('div');
    toast.id = toastId;
    toast.className = 'toast align-items-center text-white bg-' + (type === 'error' ? 'danger' : type) + ' border-0';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    try {
        // Initialiser et afficher le toast
        const bsToast = new bootstrap.Toast(toast, {
            animation: true,
            autohide: true,
            delay: 5000
        });
        bsToast.show();
        
        // Supprimer le toast après fermeture
        toast.addEventListener('hidden.bs.toast', function() {
            this.remove();
        });
    } catch (error) {
        console.error('Erreur lors de l\'affichage du toast:', error);
        // Fallback
        toast.remove();
        alert(message);
    }
}
</script>