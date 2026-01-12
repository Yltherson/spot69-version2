<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
</style>

<%
// Obtenir la date d'aujourd'hui pour le champ datetime-local
java.time.LocalDateTime now = java.time.LocalDateTime.now();
java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
String today = now.format(formatter);
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
                <i class="fe fe-calendar fe-32 align-self-center text-black"></i>
                Nouvel événement
              </h2>
              <a href="EvenementServlet?action=lister" class="btn btn-secondary">
                <i class="fe fe-arrow-left fe-16"></i> Retour à la liste
              </a>
            </div>
            
            <div class="card shadow">
              <div class="card-body">
                <form method="POST" action="EvenementServlet" enctype="multipart/form-data" id="eventForm">
                  <input type="hidden" name="action" value="ajouter">
                  
                  <div class="row">
                    <!-- Colonne gauche -->
                    <div class="col-md-6">
                      <!-- Titre -->
                      <div class="form-group mb-3">
                        <label for="titre" class="required-field">Titre de l'événement</label>
                        <input type="text" class="form-control" id="titre" name="titre" 
                               required maxlength="200" placeholder="Ex: Concert de Jazz">
                        <small class="form-text text-muted">Maximum 200 caractères</small>
                      </div>
                      
                      <!-- Artiste/Groupe -->
                      <div class="form-group mb-3">
                        <label for="artisteGroupe">Artiste/Groupe</label>
                        <input type="text" class="form-control" id="artisteGroupe" name="artisteGroupe"
                               maxlength="100" placeholder="Ex: The Jazz Band">
                      </div>
                      
                      <!-- Date et Heure -->
                      <div class="form-group mb-3">
                        <label for="dateEvent" class="required-field">Date et heure</label>
                        <input type="datetime-local" class="form-control" id="dateEvent" name="dateEvent"
                               value="<%= today %>" required>
                      </div>
                      
                      <!-- Statut -->
                      <div class="form-group mb-3">
                        <label for="statut">Statut</label>
                        <select class="form-control" id="statut" name="statut">
                          <option value="VISIBLE" selected>Visible</option>
                          <option value="HIDDEN">Masqué</option>
                        </select>
                      </div>
                    </div>
                    
                    <!-- Colonne droite -->
                    <div class="col-md-6">
                      <!-- Description -->
                      <div class="form-group mb-3">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="5" maxlength="2000" placeholder="Description de l'événement..."></textarea>
                        <small class="form-text text-muted">Maximum 2000 caractères</small>
                      </div>
                      
                      <!-- Image -->
                      <div class="form-group mb-3">
                        <label for="imageEvenement">Image de l'événement</label>
                        <div class="custom-file">
                          <input type="file" class="custom-file-input" id="imageEvenement" 
                                 name="imageEvenement" accept=".jpg,.jpeg,.png,.gif,.webp">
                          <label class="custom-file-label" for="imageEvenement">
                            Choisir une image...
                          </label>
                        </div>
                        <small class="form-text text-muted">
                          Formats acceptés: JPG, JPEG, PNG, GIF, WEBP. Taille max: 10MB
                        </small>
                        
                        <!-- Prévisualisation image -->
                        <div class="image-preview-container mt-2">
                          <img id="imagePreview" class="image-preview d-none" alt="Aperçu">
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Section Tables - SIMPLIFIÉE -->
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
                            <!-- Table par défaut - toujours visible -->
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
                          </div>
                          
                          <!-- Message d'information -->
                          <div class="alert alert-info mt-3">
                            <i class="fe fe-info fe-16"></i>
                            <strong>Information :</strong> Ajoutez différents types de tables avec des capacités et prix variés.
                            Exemple : Table VIP (2 personnes, 5000 HTG), Table Standard (4 personnes, 2000 HTG), etc.
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
                          <i class="fe fe-save fe-16"></i> Créer l'événement
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
    
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const fileName = e.target.files[0] ? e.target.files[0].name : 'Choisir une image...';
            fileLabel.textContent = fileName;
            
            // Aperçu de l'image
            if (e.target.files && e.target.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    imagePreview.src = e.target.result;
                    imagePreview.classList.remove('d-none');
                };
                reader.readAsDataURL(e.target.files[0]);
            }
        });
    }
    
    // Gestion des tables - SIMPLIFIÉE
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
            
            // Activer le bouton de suppression de la première table
            document.querySelector('.remove-table-btn').disabled = false;
            
            // Ajouter l'événement de suppression
            newTable.querySelector('.remove-table-btn').addEventListener('click', function() {
                const tableItems = tablesContainer.querySelectorAll('.table-item');
                if (tableItems.length > 1) {
                    newTable.remove();
                    
                    // Si une seule table reste, désactiver son bouton de suppression
                    if (tablesContainer.querySelectorAll('.table-item').length === 1) {
                        document.querySelector('.remove-table-btn').disabled = true;
                    }
                } else {
                    alert('Un événement doit avoir au moins une table.');
                }
            });
        });
    }
    
    // Activer les boutons de suppression existants
    document.querySelectorAll('.remove-table-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const tableItem = this.closest('.table-item');
            const tableItems = tablesContainer.querySelectorAll('.table-item');
            
            if (tableItems.length > 1) {
                tableItem.remove();
                
                // Si une seule table reste, désactiver son bouton de suppression
                if (tablesContainer.querySelectorAll('.table-item').length === 1) {
                    document.querySelector('.remove-table-btn').disabled = true;
                }
            } else {
                alert('Un événement doit avoir au moins une table.');
            }
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
    
    // Gestion des notifications toast (si présentes)
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

// Fonction pour afficher les toasts
function showToast(type, message) {
    // Vérifier si Bootstrap est disponible
    if (typeof bootstrap === 'undefined') {
        console.log('Bootstrap non disponible, affichage simple du message:', message);
        alert(message);
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
    toast.className = 'toast align-items-center text-white bg-' + type + ' border-0';
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
}
</script>