<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ page import="java.util.*, com.spot69.model.MenuCategorie, com.spot69.model.Rayon" %>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar2.jsp" />
<%-- <jsp:include page="sidebar.jsp" /> --%>

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="page-wrapper">
      <div class="content">
        <div class="page-header">
          <div class="page-title">
            <h4>Configuration Produit</h4>
            <h6>Définir les caractéristiques de base du produit</h6>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <form method="post" action="ProduitServlet?action=ajouter" enctype="multipart/form-data">
              <div class="row">

                <!-- Titre section -->
                <div class="col-12">
                  <h5 class="section-title">Fiche Produit</h5>
                </div>

                <!-- Nom -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Nom du produit*</label>
                    <input type="text" class="form-control" name="nom" placeholder="Ex: Caisse bière Prestige" required />
                  </div>
                </div>

                <!-- Description -->
                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Description</label>
                    <textarea class="form-control" name="description" rows="2" placeholder="Détails importants..."></textarea>
                  </div>
                </div>

                <!-- Image section -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Image du produit</h6>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Image du produit</label>
                    <input type="file" class="form-control" name="image" id="imageProduit" accept="image/*" onchange="previewImage(this, 'previewImage')">
                  </div>
                </div>

                <div class="col-lg-6 col-sm-12">
                  <div class="form-group">
                    <label>Aperçu de l'image</label>
                    <div class="image-preview mt-2" id="previewImageContainer" style="display:none;">
                      <img id="previewImage" src="#" alt="Aperçu de l'image" class="img-thumbnail" style="max-width: 200px; max-height: 200px;"/>
                    </div>
                    <div id="noImagePreview" class="text-muted" style="padding: 50px 0; text-align: center; border: 1px dashed #ccc;">
                      Aucune image sélectionnée
                    </div>
                  </div>
                </div>

                <!-- CATEGORISATION -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Catégorisation</h6>
                </div>

                <!-- RAYON -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Rayon*</label>
                    <select class="form-control" name="rayonId" id="rayonSelect" required>
                      <option value="">-- Sélectionner un rayon --</option>
                      <%
                        // Récupérer la liste des rayons
                        com.spot69.dao.RayonDAO rayonDAO = new com.spot69.dao.RayonDAO();
                        List<com.spot69.model.Rayon> rayons = rayonDAO.getAll();
                        
                        if (rayons != null) {
                          for (com.spot69.model.Rayon rayon : rayons) {
                      %>
                        <option value="<%= rayon.getId() %>"><%= rayon.getNom() %></option>
                      <% 
                          }
                        } 
                      %>
                    </select>
                  </div>
                </div>

                <!-- CATEGORIE -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Catégorie*</label>
                    <select class="form-control" name="categorieId" id="categorieSelect" required disabled>
                      <option value="">-- Sélectionnez d'abord un rayon --</option>
                    </select>
                  </div>
                </div>

                <!-- SOUS-CATEGORIE -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Sous-catégorie*</label>
                    <select class="form-control" name="sousCategorieId" id="sousCategorieSelect" required disabled>
                      <option value="">-- Sélectionnez d'abord une catégorie --</option>
                    </select>
                  </div>
                </div>

                <!-- EMPLACEMENT -->
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Emplacement par défaut</label>
                    <input type="text" class="form-control" name="emplacement" placeholder="Zone/Rayon" value="Entrepôt" />
                  </div>
                </div>

                <!-- UNITÉ -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Unités et stock</h6>
                </div>

                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Unité de vente*</label>
                    <input type="text" class="form-control" id="uniteVente" name="uniteVente" placeholder="Ex: Caisse, Demi-caisse" required />
                    <small class="text-muted">Pour plusieurs unites, utilise des virgules</small>
                  </div>
                </div>

                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Contenu par unité</label>
                    <input type="number" class="form-control" id="contenuUnite" name="contenuParUnite" placeholder="Ex: 24 (bouteilles/caisse)" />
                  </div>
                </div>
                
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Prix d'Achat</label>
                    <input type="number" class="form-control" id="prixAchatParUniteVente" name="prixAchatParUniteVente" placeholder="Ex: 3200" />
                    <small class="text-muted">Prix d'achat de l'unite de vente (caisse ou demi-caisse)</small>
                  </div>
                </div>

                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Seuil d'alerte*</label>
                    <input type="number" class="form-control" name="seuilAlerte" value="10" required />
                    <small class="text-muted">Quantité minimum pour alerte</small>
                  </div>
                </div>
                
                <!-- PRIX DE VENTE -->
                <div class="col-12 mt-3">
                  <h6 class="sub-section-title">Prix </h6>
                </div>
                
                <div class="col-lg-4 col-sm-6 col-12">
                  <div class="form-group">
                    <label>Prix de vente</label>
                    <input type="number" class="form-control" id="prixVente" name="prixVente" placeholder="Ex: 250" />
                  </div>
                </div>
                
                <div class="col-lg-4 col-sm-6 col-12" style="display:none">
                  <div class="form-group">
                    <label>Quantite points</label>
                    <input type="number" class="form-control" id="qtePoints" name="qtePoints" placeholder="Ex: 10" />
                    <small class="text-muted">La qte de points qu'un client aura a chaque fois qu'il achète ce produit</small>
                  </div>
                </div>

                <!-- BOUTONS -->
                <div class="col-12 mt-4">
                  <div class="form-group d-flex justify-content-end">
                    <a href="ProduitServlet?action=lister" class="btn btn-cancel me-2">Annuler</a>
                    <button type="submit" class="btn btn-primary">Enregistrer</button>
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

<jsp:include page="bottombar.jsp" />
<jsp:include page="footer.jsp" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
    // Fonction pour prévisualiser l'image
    function previewImage(input, previewId) {
        if (!input || !previewId) return;
        
        const preview = document.getElementById(previewId);
        const previewContainer = document.getElementById(previewId + 'Container');
        const noImagePreview = document.getElementById('noImagePreview');
        
        if (!preview || !previewContainer || !noImagePreview) return;

        const file = input.files[0];
        const reader = new FileReader();
        
        reader.onload = function(e) {
            preview.src = e.target.result;
            previewContainer.style.display = 'block';
            noImagePreview.style.display = 'none';
        }
        
        if (file) {
            reader.readAsDataURL(file);
        } else {
            preview.src = "#";
            previewContainer.style.display = 'none';
            noImagePreview.style.display = 'block';
        }
    }

    // Configurer l'écouteur d'événement pour l'input image
    const imageInput = document.getElementById('imageProduit');
    if (imageInput) {
        imageInput.addEventListener('change', function() {
            previewImage(this, 'previewImage');
        });
    }

    // Gestion des unités
    const uniteInput = document.getElementById("uniteVente");
    const contenuInput = document.getElementById("contenuUnite");

    if (uniteInput && contenuInput) {
        function concatenerUnite() {
            const valeurActuelle = uniteInput.value;
            const contenu = contenuInput.value.trim();

            const uniteOriginale = uniteInput.getAttribute("data-original") || valeurActuelle.split(" (")[0].trim();

            if (uniteOriginale && contenu) {
                uniteInput.value = uniteOriginale + " (" + contenu + ")";
            } else {
                uniteInput.value = uniteOriginale;
            }

            uniteInput.setAttribute("data-original", uniteOriginale);
        }

        contenuInput.addEventListener("input", concatenerUnite);
        uniteInput.addEventListener("input", function() {
            uniteInput.removeAttribute("data-original");
        });
    }

    // Gérer le changement de rayon
    $('#rayonSelect').on('change', function() {
        const rayonId = $(this).val();
        const $categorieSelect = $('#categorieSelect');
        const $sousCategorieSelect = $('#sousCategorieSelect');
        
        // Réinitialiser les selects
        $categorieSelect.empty().prop('disabled', true).prop('required', false);
        $sousCategorieSelect.empty().prop('disabled', true).prop('required', false);
        
        if (!rayonId) {
            $categorieSelect.append('<option value="">-- Sélectionnez d\'abord un rayon --</option>');
            $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
            return;
        }
        
        // Afficher un indicateur de chargement
        $categorieSelect.append('<option value="">Chargement des catégories...</option>');
        $categorieSelect.prop('disabled', true);
        
        // Charger les catégories du rayon sélectionné
        $.ajax({
            url: 'MenuServlet?action=categories-by-rayon&rayonId=' + rayonId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $categorieSelect.empty();
                
                if (data.categories && data.categories.length > 0) {
                    $categorieSelect.append('<option value="">-- Sélectionner une catégorie --</option>');
                    
                    data.categories.forEach(function(cat) {
                        $categorieSelect.append(
                            $('<option>', { 
                                value: cat.id, 
                                text: cat.nom 
                            })
                        );
                    });
                    
                    $categorieSelect.prop('disabled', false);
                    $categorieSelect.prop('required', true);
                } else {
                    $categorieSelect.append('<option value="">Aucune catégorie disponible pour ce rayon</option>');
                    $categorieSelect.prop('disabled', true);
                    $categorieSelect.prop('required', false);
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des catégories:", error);
                $categorieSelect.empty();
                $categorieSelect.append('<option value="">Erreur de chargement</option>');
                $categorieSelect.prop('disabled', true);
                $categorieSelect.prop('required', false);
                
                alert("Erreur lors du chargement des catégories. Veuillez réessayer.");
            }
        });
    });
    
    // Gérer le changement de catégorie
    $('#categorieSelect').on('change', function() {
        const categorieId = $(this).val();
        const $sousCategorieSelect = $('#sousCategorieSelect');
        
        // Réinitialiser le select des sous-catégories
        $sousCategorieSelect.empty().prop('disabled', true).prop('required', false);
        
        if (!categorieId) {
            $sousCategorieSelect.append('<option value="">-- Sélectionnez d\'abord une catégorie --</option>');
            return;
        }
        
        // Afficher un indicateur de chargement
        $sousCategorieSelect.append('<option value="">Chargement des sous-catégories...</option>');
        $sousCategorieSelect.prop('disabled', true);
        
        // Charger les sous-catégories de la catégorie sélectionnée
        $.ajax({
            url: 'MenuServlet?action=sous-categories&parentId=' + categorieId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $sousCategorieSelect.empty();
                
                if (data.sousCategories && data.sousCategories.length > 0) {
                    $sousCategorieSelect.append('<option value="">-- Sélectionner une sous-catégorie --</option>');
                    
                    data.sousCategories.forEach(function(cat) {
                        $sousCategorieSelect.append(
                            $('<option>', { 
                                value: cat.id, 
                                text: cat.nom 
                            })
                        );
                    });
                    
                    $sousCategorieSelect.prop('disabled', false);
                    $sousCategorieSelect.prop('required', true);
                } else {
                    $sousCategorieSelect.append('<option value="">Aucune sous-catégorie disponible</option>');
                    $sousCategorieSelect.prop('disabled', true);
                    $sousCategorieSelect.prop('required', false);
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des sous-catégories:", error);
                $sousCategorieSelect.empty();
                $sousCategorieSelect.append('<option value="">Erreur de chargement</option>');
                $sousCategorieSelect.prop('disabled', true);
                $sousCategorieSelect.prop('required', false);
                
                alert("Erreur lors du chargement des sous-catégories. Veuillez réessayer.");
            }
        });
    });
    
    // Validation du formulaire
    $('form').on('submit', function(e) {
        const rayonId = $('#rayonSelect').val();
        const categorieId = $('#categorieSelect').val();
        const sousCategorieId = $('#sousCategorieSelect').val();
        
        // Validation rayon
        if (!rayonId) {
            e.preventDefault();
            alert('Veuillez sélectionner un rayon.');
            return false;
        }
        
        // Validation catégorie
        if (!categorieId) {
            e.preventDefault();
            alert('Veuillez sélectionner une catégorie.');
            return false;
        }
        
        // Validation sous-catégorie
        if (!sousCategorieId) {
            e.preventDefault();
            alert('Veuillez sélectionner une sous-catégorie.');
            return false;
        }
        
        return true;
    });
});
</script>