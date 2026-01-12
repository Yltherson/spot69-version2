<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ page import="java.util.*, com.spot69.model.Chambre" %>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="page-wrapper">
      <div class="content">
        <div class="page-header">
          <div class="page-title">
            <h4>Configuration Chambre</h4>
            <h6>Définir les caractéristiques de la chambre</h6>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <form method="post" action="ChambreServlet?action=ajouter" enctype="multipart/form-data">
              <div class="row">

                <!-- Titre section -->
                <div class="col-12">
                  <h5 class="section-title">Fiche Chambre</h5>
                </div>

                <!-- Nom de la chambre -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Nom de la chambre*</label>
                    <input type="text" class="form-control" name="nomChambre" placeholder="Ex: Suite Présidentielle, Chambre Deluxe" required />
                  </div>
                </div>

                <!-- Description -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Description</label>
                    <textarea class="form-control" name="descriptionChambre" rows="2" placeholder="Description détaillée de la chambre..."></textarea>
                  </div>
                </div>

                <!-- Images section -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Images de la chambre</h6>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Images de la chambre</label>
                    <input type="file" class="form-control" name="media" id="mediaChambre" accept="image/*" multiple onchange="previewImages(this, 'previewImagesContainer')">
                    <small class="text-muted">Vous pouvez sélectionner plusieurs images</small>
                  </div>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Aperçu des images</label>
                    <div class="images-preview mt-2" id="previewImagesContainer" style="display:none;">
                      <!-- Les aperçus d'images seront ajoutés ici dynamiquement -->
                    </div>
                    <div id="noImagesPreview" class="text-muted" style="padding: 50px 0; text-align: center; border: 1px dashed #ccc;">
                      Aucune image sélectionnée
                    </div>
                  </div>
                </div>

                <!-- CARACTÉRISTIQUES -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Caractéristiques de la chambre</h6>
                </div>

                <!-- Capacité -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Capacité (personnes)*</label>
                    <input type="number" class="form-control" name="capacite" min="1" max="10" value="2" required />
                    <small class="text-muted">Nombre maximum d'occupants</small>
                  </div>
                </div>

                <!-- Prix par nuit -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Prix par nuit (HTG)*</label>
                    <input type="number" class="form-control" name="prix" min="0" step="0.01" placeholder="Ex: 2500" required />
                  </div>
                </div>

                <!-- Disponibilité -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Statut</label>
                    <div class="custom-control custom-switch mt-2">
                      <input type="checkbox" class="custom-control-input" id="disponibleSwitch" name="disponible" value="true" checked>
                      <label class="custom-control-label" for="disponibleSwitch">Chambre disponible</label>
                    </div>
                  </div>
                </div>

                <!-- INSTALLATIONS -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Installations et équipements</h6>
                </div>

                <div class="col-12">
                  <div class="form-group">
                    <label>Sélectionnez les installations disponibles</label>
                    <div class="row">
                      <div class="col-md-4 col-sm-6">
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="wifi" name="installations" value="Wi-Fi">
                          <label class="custom-control-label" for="wifi">Wi-Fi gratuit</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="tv" name="installations" value="TV">
                          <label class="custom-control-label" for="tv">Télévision</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="climatisation" name="installations" value="Climatisation">
                          <label class="custom-control-label" for="climatisation">Climatisation</label>
                        </div>
                      </div>
                      <div class="col-md-4 col-sm-6">
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="minibar" name="installations" value="Minibar">
                          <label class="custom-control-label" for="minibar">Minibar</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="jacuzzi" name="installations" value="Jacuzzi">
                          <label class="custom-control-label" for="jacuzzi">Jacuzzi</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="service" name="installations" value="Service en chambre">
                          <label class="custom-control-label" for="service">Service en chambre</label>
                        </div>
                      </div>
                      <!-- <div class="col-md-4 col-sm-6">
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="securite" name="installations" value="Coffre-fort">
                          <label class="custom-control-label" for="securite">Coffre-fort</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="terrasse" name="installations" value="Terrasse">
                          <label class="custom-control-label" for="terrasse">Terrasse/Balcon</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="vue" name="installations" value="Vue mer">
                          <label class="custom-control-label" for="vue">Vue sur mer</label>
                        </div>
                      </div> -->
                    </div>
                  </div>
                </div>

                <!-- AUTRES ÉQUIPEMENTS -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Autres équipements</h6>
                </div>

                <div class="col-12">
                  <div class="form-group">
                    <label>Équipements supplémentaires (séparés par des virgules)</label>
                    <input type="text" class="form-control" name="installations" placeholder="Ex: Sèche-cheveux, Peignoir, Pantoufles, Plateau de courtoisie" />
                    <small class="text-muted">Ajoutez des équipements non listés ci-dessus</small>
                  </div>
                </div>

                <!-- INFORMATIONS SUPPLÉMENTAIRES -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Informations supplémentaires</h6>
                </div>
<!--
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Superficie (m²)</label>
                    <input type="number" class="form-control" name="installations" placeholder="Ex: 35" />
                  </div>
                </div>
-->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Type de lit</label>
                    <select class="form-control" name="installations">
                      <option value="">Sélectionnez</option>
                      <option value="Lit double">Lit double</option>
                      <option value="Lits jumeaux">Lits jumeaux</option>
                      <option value="King size">King size</option>
                      <option value="Queen size">Queen size</option>
                    </select>
                  </div>
                </div>

                <!-- BOUTONS -->
                <div class="col-12 mt-4">
                  <div class="form-group d-flex justify-content-end">
                    <a href="ChambreServlet?action=lister" class="btn btn-cancel me-2">Annuler</a>
                    <button type="submit" class="btn btn-primary">Enregistrer la chambre</button>
                  </div>
                </div>

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
  // Attendre que le DOM soit complètement chargé
  document.addEventListener('DOMContentLoaded', function() {
    // Fonction pour prévisualiser les images multiples
    function previewImages(input, previewContainerId) {
      if (!input || !previewContainerId) return;
      
      const previewContainer = document.getElementById(previewContainerId);
      const noImagesPreview = document.getElementById('noImagesPreview');
      
      if (!previewContainer || !noImagesPreview) return;

      const files = input.files;
      
      // Vider le conteneur de prévisualisation
      previewContainer.innerHTML = '';
      
      if (files && files.length > 0) {
        previewContainer.style.display = 'block';
        noImagesPreview.style.display = 'none';
        
        for (let i = 0; i < files.length; i++) {
          const file = files[i];
          const reader = new FileReader();
          
          reader.onload = function(e) {
            const imgWrapper = document.createElement('div');
            imgWrapper.className = 'image-preview-wrapper';
            imgWrapper.style.cssText = 'display: inline-block; margin: 5px; position: relative;';
            
            const img = document.createElement('img');
            img.src = e.target.result;
            img.className = 'img-thumbnail';
            img.style.cssText = 'width: 120px; height: 90px; object-fit: cover;';
            
            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.className = 'btn btn-sm btn-danger';
            removeBtn.style.cssText = 'position: absolute; top: 5px; right: 5px; width: 20px; height: 20px; padding: 0; font-size: 10px;';
            removeBtn.innerHTML = '×';
            removeBtn.onclick = function() {
              imgWrapper.remove();
              // Mettre à jour l'input file (plus complexe, nécessiterait un re-creation)
              updateFileInputAfterRemoval(input, file);
            };
            
            imgWrapper.appendChild(img);
            imgWrapper.appendChild(removeBtn);
            previewContainer.appendChild(imgWrapper);
          }
          
          reader.readAsDataURL(file);
        }
      } else {
        previewContainer.style.display = 'none';
        noImagesPreview.style.display = 'block';
      }
    }

    // Fonction pour mettre à jour l'input file après suppression d'une image (simplifiée)
    function updateFileInputAfterRemoval(input, removedFile) {
      // Cette fonction est complexe à implémenter car on ne peut pas modifier directement FileList
      // Une solution serait de recréer l'input file ou d'utiliser un système de gestion de fichiers plus avancé
      console.log('Image supprimée:', removedFile.name);
      // Pour l'instant, on se contente de supprimer l'aperçu visuel
    }

    // Configurer l'écouteur d'événement pour l'input images multiples
    const mediaInput = document.getElementById('mediaChambre');
    if (mediaInput) {
      mediaInput.addEventListener('change', function() {
        previewImages(this, 'previewImagesContainer');
      });
    }

    // Validation du formulaire
    const form = document.querySelector('form');
    if (form) {
      form.addEventListener('submit', function(e) {
        const prix = parseFloat(document.querySelector('input[name="prix"]').value);
        if (prix < 0) {
          e.preventDefault();
          alert('Le prix ne peut pas être négatif');
          return false;
        }
        
        const capacite = parseInt(document.querySelector('input[name="capacite"]').value);
        if (capacite < 1 || capacite > 10) {
          e.preventDefault();
          alert('La capacité doit être entre 1 et 10 personnes');
          return false;
        }
        
        const nomChambre = document.querySelector('input[name="nomChambre"]').value.trim();
        if (!nomChambre) {
          e.preventDefault();
          alert('Le nom de la chambre est requis');
          return false;
        }
        
        return true;
      });
    }

    // Gestion des installations - cocher/décocher automatiquement
    const installationsInput = document.querySelector('input[name="installations"]');
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name="installations"]');
    
    if (installationsInput && checkboxes.length > 0) {
      // Mettre à jour le champ texte quand les checkboxes changent
      checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateInstallationsText);
      });
      
      function updateInstallationsText() {
        const selected = Array.from(checkboxes)
          .filter(cb => cb.checked)
          .map(cb => cb.value);
        
        // Récupérer les installations manuelles existantes
        const manualInstallations = installationsInput.value.split(',')
          .map(item => item.trim())
          .filter(item => item && !checkboxes.some(cb => cb.value === item));
        
        const allInstallations = [...selected, ...manualInstallations];
        installationsInput.value = allInstallations.join(', ');
      }
      
      // Initialiser
      updateInstallationsText();
    }
  });

  // Fonction utilitaire pour prévisualiser les images (accessible globalement)
  function previewImages(input, previewId) {
    document.addEventListener('DOMContentLoaded', function() {
      const previewContainer = document.getElementById(previewId);
      const noPreview = document.getElementById('noImagesPreview');
      
      if (input.files && input.files.length > 0) {
        previewContainer.style.display = 'block';
        noPreview.style.display = 'none';
        
        // Vider le conteneur existant
        previewContainer.innerHTML = '';
        
        Array.from(input.files).forEach((file, index) => {
          const reader = new FileReader();
          reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.className = 'img-thumbnail mr-2 mb-2';
            img.style.cssText = 'width: 120px; height: 90px; object-fit: cover;';
            img.alt = 'Aperçu image ' + (index + 1);
            
            previewContainer.appendChild(img);
          };
          reader.readAsDataURL(file);
        });
      } else {
        previewContainer.style.display = 'none';
        noPreview.style.display = 'block';
      }
    });
  }
</script>

<style>
.section-title {
  color: #2c3e50;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.sub-section-title {
  color: #34495e;
  font-weight: 600;
  margin-bottom: 15px;
  padding-left: 10px;
  border-left: 4px solid #3498db;
}

.image-preview-wrapper {
  position: relative;
  display: inline-block;
}

.image-preview-wrapper .btn-danger {
  width: 20px;
  height: 20px;
  padding: 0;
  font-size: 10px;
  line-height: 1;
}

.custom-control-label {
  cursor: pointer;
}

.btn-cancel {
  background-color: #95a5a6;
  color: white;
  border: none;
}

.btn-cancel:hover {
  background-color: #7f8c8d;
  color: white;
}
</style> --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ page import="java.util.*, com.spot69.model.Chambre" %>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="page-wrapper">
      <div class="content">
        <div class="page-header">
          <div class="page-title">
            <h4>Configuration Chambre</h4>
            <h6>Définir les caractéristiques de la chambre</h6>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <form method="post" action="ChambreServlet?action=ajouter" enctype="multipart/form-data">
              <div class="row">

                <!-- Titre section -->
                <div class="col-12">
                  <h5 class="section-title">Fiche Chambre</h5>
                </div>

                <!-- Nom de la chambre -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Nom de la chambre*</label>
                    <input type="text" class="form-control" name="nomChambre" placeholder="Ex: Suite Présidentielle, Chambre Deluxe" required />
                  </div>
                </div>

                <!-- Description -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Description</label>
                    <textarea class="form-control" name="descriptionChambre" rows="2" placeholder="Description détaillée de la chambre..."></textarea>
                  </div>
                </div>

                <!-- Images section -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Images de la chambre</h6>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Images de la chambre</label>
                    <input type="file" class="form-control" name="media" id="mediaChambre" accept="image/*" multiple onchange="previewImages(this, 'previewImagesContainer')">
                    <small class="text-muted">Vous pouvez sélectionner plusieurs images</small>
                  </div>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Aperçu des images</label>
                    <div class="images-preview mt-2" id="previewImagesContainer" style="display:none;">
                      <!-- Les aperçus d'images seront ajoutés ici dynamiquement -->
                    </div>
                    <div id="noImagesPreview" class="text-muted" style="padding: 50px 0; text-align: center; border: 1px dashed #ccc;">
                      Aucune image sélectionnée
                    </div>
                  </div>
                </div>

                <!-- CARACTÉRISTIQUES -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Caractéristiques de la chambre</h6>
                </div>

                <!-- Capacité -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Capacité (personnes)*</label>
                    <input type="number" class="form-control" name="capacite" min="1" max="10" value="2" required />
                    <small class="text-muted">Nombre maximum d'occupants</small>
                  </div>
                </div>

                <!-- Prix pour tous les types -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Prix par séjour (HTG)*</label>
                    <input type="number" class="form-control" name="prixSejour" min="0" step="0.01" placeholder="Ex: 5000" required />
                    <small class="text-muted">Prix par nuit pour un séjour</small>
                  </div>
                </div>

                <!-- Disponibilité -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Statut</label>
                    <div class="custom-control custom-switch mt-2">
                      <input type="checkbox" class="custom-control-input" id="disponibleSwitch" name="disponible" value="true" checked>
                      <label class="custom-control-label" for="disponibleSwitch">Chambre disponible</label>
                    </div>
                  </div>
                </div>

                <!-- PRIX SUPPLEMENTAIRES -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Tarification additionnelle</h6>
                </div>

                <div class="row">
                  <div class="col-lg-3 col-sm-6 col-12">
                    <div class="form-group">
                      <label>Prix pour une nuit (HTG)</label>
                      <input type="number" class="form-control" name="prixNuit" min="0" step="0.01" placeholder="Ex: 3000" />
                      <small class="text-muted">20h-7h</small>
                    </div>
                  </div>
                  
                  <div class="col-lg-3 col-sm-6 col-12">
                    <div class="form-group">
                      <label>Prix pour une journée (HTG)</label>
                      <input type="number" class="form-control" name="prixJour" min="0" step="0.01" placeholder="Ex: 4000" />
                      <small class="text-muted">8h-19h</small>
                    </div>
                  </div>
                  
                  <div class="col-lg-3 col-sm-6 col-12">
                    <div class="form-group">
                      <label>Prix par créneau (HTG)</label>
                      <input type="number" class="form-control" name="prixMoment" min="0" step="0.01" placeholder="Ex: 1000" />
                      <small class="text-muted">Créneau de 2h</small>
                    </div>
                  </div>
                  
                  <div class="col-lg-3 col-sm-6 col-12">
                    <!-- Optionnel: Vous pouvez ajouter un autre champ ici ou laisser vide -->
                  </div>
                </div>

                <!-- INSTALLATIONS -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Installations et équipements</h6>
                </div>

                <div class="col-12">
                  <div class="form-group">
                    <label>Sélectionnez les installations disponibles</label>
                    <div class="row">
                      <div class="col-md-4 col-sm-6">
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="wifi" name="installations" value="Wi-Fi">
                          <label class="custom-control-label" for="wifi">Wi-Fi gratuit</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="tv" name="installations" value="TV">
                          <label class="custom-control-label" for="tv">Télévision</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="climatisation" name="installations" value="Climatisation">
                          <label class="custom-control-label" for="climatisation">Climatisation</label>
                        </div>
                      </div>
                      <div class="col-md-4 col-sm-6">
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="minibar" name="installations" value="Minibar">
                          <label class="custom-control-label" for="minibar">Minibar</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="jacuzzi" name="installations" value="Jacuzzi">
                          <label class="custom-control-label" for="jacuzzi">Jacuzzi</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                          <input type="checkbox" class="custom-control-input" id="service" name="installations" value="Service en chambre">
                          <label class="custom-control-label" for="service">Service en chambre</label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- AUTRES ÉQUIPEMENTS -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Autres équipements</h6>
                </div>

                <div class="col-12">
                  <div class="form-group">
                    <label>Équipements supplémentaires (séparés par des virgules)</label>
                    <input type="text" class="form-control" name="autresInstallations" placeholder="Ex: Sèche-cheveux, Peignoir, Pantoufles, Plateau de courtoisie" />
                    <small class="text-muted">Ajoutez des équipements non listés ci-dessus</small>
                  </div>
                </div>

                <!-- INFORMATIONS SUPPLÉMENTAIRES -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Informations supplémentaires</h6>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Type de lit</label>
                    <select class="form-control" name="typeLit">
                      <option value="">Sélectionnez</option>
                      <option value="Lit double">Lit double</option>
                      <option value="Lits jumeaux">Lits jumeaux</option>
                      <option value="King size">King size</option>
                      <option value="Queen size">Queen size</option>
                    </select>
                  </div>
                </div>

                <!-- BOUTONS -->
                <div class="col-12 mt-4">
                  <div class="form-group d-flex justify-content-end">
                    <a href="ChambreServlet?action=lister" class="btn btn-cancel me-2">Annuler</a>
                    <button type="submit" class="btn btn-primary">Enregistrer la chambre</button>
                  </div>
                </div>

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
  // Attendre que le DOM soit complètement chargé
  document.addEventListener('DOMContentLoaded', function() {
    // Fonction pour prévisualiser les images multiples
    function previewImages(input, previewContainerId) {
      if (!input || !previewContainerId) return;
      
      const previewContainer = document.getElementById(previewContainerId);
      const noImagesPreview = document.getElementById('noImagesPreview');
      
      if (!previewContainer || !noImagesPreview) return;

      const files = input.files;
      
      // Vider le conteneur de prévisualisation
      previewContainer.innerHTML = '';
      
      if (files && files.length > 0) {
        previewContainer.style.display = 'block';
        noImagesPreview.style.display = 'none';
        
        for (let i = 0; i < files.length; i++) {
          const file = files[i];
          const reader = new FileReader();
          
          reader.onload = function(e) {
            const imgWrapper = document.createElement('div');
            imgWrapper.className = 'image-preview-wrapper';
            imgWrapper.style.cssText = 'display: inline-block; margin: 5px; position: relative;';
            
            const img = document.createElement('img');
            img.src = e.target.result;
            img.className = 'img-thumbnail';
            img.style.cssText = 'width: 120px; height: 90px; object-fit: cover;';
            
            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.className = 'btn btn-sm btn-danger';
            removeBtn.style.cssText = 'position: absolute; top: 5px; right: 5px; width: 20px; height: 20px; padding: 0; font-size: 10px;';
            removeBtn.innerHTML = '×';
            removeBtn.onclick = function() {
              imgWrapper.remove();
              // Mettre à jour l'input file (plus complexe, nécessiterait un re-creation)
              updateFileInputAfterRemoval(input, file);
            };
            
            imgWrapper.appendChild(img);
            imgWrapper.appendChild(removeBtn);
            previewContainer.appendChild(imgWrapper);
          }
          
          reader.readAsDataURL(file);
        }
      } else {
        previewContainer.style.display = 'none';
        noImagesPreview.style.display = 'block';
      }
    }

    // Fonction pour mettre à jour l'input file après suppression d'une image (simplifiée)
    function updateFileInputAfterRemoval(input, removedFile) {
      // Cette fonction est complexe à implémenter car on ne peut pas modifier directement FileList
      // Une solution serait de recréer l'input file ou d'utiliser un système de gestion de fichiers plus avancé
      console.log('Image supprimée:', removedFile.name);
      // Pour l'instant, on se contente de supprimer l'aperçu visuel
    }

    // Configurer l'écouteur d'événement pour l'input images multiples
    const mediaInput = document.getElementById('mediaChambre');
    if (mediaInput) {
      mediaInput.addEventListener('change', function() {
        previewImages(this, 'previewImagesContainer');
      });
    }

    // Validation du formulaire
    const form = document.querySelector('form');
    if (form) {
      form.addEventListener('submit', function(e) {
        // Validation des prix
        const prixSejour = parseFloat(document.querySelector('input[name="prixSejour"]').value);
        if (prixSejour < 0) {
          e.preventDefault();
          alert('Le prix par séjour ne peut pas être négatif');
          return false;
        }
        
        const prixNuit = parseFloat(document.querySelector('input[name="prixNuit"]').value);
        if (prixNuit < 0) {
          e.preventDefault();
          alert('Le prix pour une nuit ne peut pas être négatif');
          return false;
        }
        
        const prixJour = parseFloat(document.querySelector('input[name="prixJour"]').value);
        if (prixJour < 0) {
          e.preventDefault();
          alert('Le prix pour une journée ne peut pas être négatif');
          return false;
        }
        
        const prixMoment = parseFloat(document.querySelector('input[name="prixMoment"]').value);
        if (prixMoment < 0) {
          e.preventDefault();
          alert('Le prix par créneau ne peut pas être négatif');
          return false;
        }
        
        const capacite = parseInt(document.querySelector('input[name="capacite"]').value);
        if (capacite < 1 || capacite > 10) {
          e.preventDefault();
          alert('La capacité doit être entre 1 et 10 personnes');
          return false;
        }
        
        const nomChambre = document.querySelector('input[name="nomChambre"]').value.trim();
        if (!nomChambre) {
          e.preventDefault();
          alert('Le nom de la chambre est requis');
          return false;
        }
        
        return true;
      });
    }

    // Gestion des installations - cocher/décocher automatiquement
    const installationsInput = document.querySelector('input[name="autresInstallations"]');
    const checkboxes = document.querySelectorAll('input[type="checkbox"][name="installations"]');
    
    if (installationsInput && checkboxes.length > 0) {
      // Mettre à jour le champ texte quand les checkboxes changent
      checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateInstallationsText);
      });
      
      function updateInstallationsText() {
        const selected = Array.from(checkboxes)
          .filter(cb => cb.checked)
          .map(cb => cb.value);
        
        // Récupérer les installations manuelles existantes
        const manualInstallations = installationsInput.value.split(',')
          .map(item => item.trim())
          .filter(item => item && !checkboxes.some(cb => cb.value === item));
        
        const allInstallations = [...selected, ...manualInstallations];
        installationsInput.value = allInstallations.join(', ');
      }
      
      // Initialiser
      updateInstallationsText();
    }
  });

  // Fonction utilitaire pour prévisualiser les images (accessible globalement)
  function previewImages(input, previewId) {
    document.addEventListener('DOMContentLoaded', function() {
      const previewContainer = document.getElementById(previewId);
      const noPreview = document.getElementById('noImagesPreview');
      
      if (input.files && input.files.length > 0) {
        previewContainer.style.display = 'block';
        noPreview.style.display = 'none';
        
        // Vider le conteneur existant
        previewContainer.innerHTML = '';
        
        Array.from(input.files).forEach((file, index) => {
          const reader = new FileReader();
          reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.className = 'img-thumbnail mr-2 mb-2';
            img.style.cssText = 'width: 120px; height: 90px; object-fit: cover;';
            img.alt = 'Aperçu image ' + (index + 1);
            
            previewContainer.appendChild(img);
          };
          reader.readAsDataURL(file);
        });
      } else {
        previewContainer.style.display = 'none';
        noPreview.style.display = 'block';
      }
    });
  }
</script>

<style>
.section-title {
  color: #2c3e50;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.sub-section-title {
  color: #34495e;
  font-weight: 600;
  margin-bottom: 15px;
  padding-left: 10px;
  border-left: 4px solid #3498db;
}

.image-preview-wrapper {
  position: relative;
  display: inline-block;
}

.image-preview-wrapper .btn-danger {
  width: 20px;
  height: 20px;
  padding: 0;
  font-size: 10px;
  line-height: 1;
}

.custom-control-label {
  cursor: pointer;
}

.btn-cancel {
  background-color: #95a5a6;
  color: white;
  border: none;
}

.btn-cancel:hover {
  background-color: #7f8c8d;
  color: white;
}

/* Style pour les champs de prix */
input[name="prixSejour"],
input[name="prixNuit"],
input[name="prixJour"],
input[name="prixMoment"] {
  font-weight: 600;
}
</style>