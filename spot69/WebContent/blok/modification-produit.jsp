<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">

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
            <h4>Configuration Produit</h4>
            <h6>Définir les caractéristiques de base du produit</h6>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <div class="row">
              <!-- Section principale -->
              <div class="col-12">
                <h5 class="section-title">Fiche de modification de produit</h5>
              </div>
              
              <!-- Informations du produit -->
              <div class="col-12 mt-3">
                <h6 class="sub-section-title">Informations du produit</h6>
              </div>
              
              <div class="col-lg-6 col-sm-12">
                <div class="form-group">
                  <label>Nom du produit*</label>
                  <input type="text" class="form-control" placeholder="Ex: Caisse bière Prestige" required />
                </div>
              </div>
              
              <div class="col-lg-6 col-sm-12">
                <div class="form-group">
                  <label>Description</label>
                  <textarea class="form-control" rows="2" placeholder="Détails importants..."></textarea>
                </div>
              </div>

              <!-- Catégorisation -->
              <div class="col-12 mt-3">
                <h6 class="sub-section-title">Catégorisation</h6>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Catégorie*</label>
                  <select class="form-control" required>
                    <option value="">Selectionnez</option>
                    <option>Boissons</option>
                    <option>Alimentaire</option>
                    <option>Entretien</option>
                    <option>Autre</option>
                  </select>
                </div>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Sous-catégorie*</label>
                  <select class="form-control" required>
                    <option value="">Selectionnez</option>
                    <option>Boissons</option>
                    <option>Alimentaire</option>
                    <option>Entretien</option>
                    <option>Autre</option>
                  </select>
                </div>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Emplacement par défaut</label>
                  <input type="text" class="form-control" value="Entrepôt" placeholder="Zone/Rayon" />
                </div>
              </div>

              <!-- Unité et stock -->
              <div class="col-12 mt-3">
                <h6 class="sub-section-title">Unités et stock</h6>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Unité de vente*</label>
                  <input type="text" class="form-control" placeholder="Ex: Caisse, Demi-caisse" required />
                </div>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Contenu par unité</label>
                  <input type="number" class="form-control" placeholder="Ex: 24 (bouteilles/caisse)" />
                </div>
              </div>
              
              <div class="col-lg-4 col-sm-6 col-12">
                <div class="form-group">
                  <label>Seuil d'alerte*</label>
                  <input type="number" class="form-control" value="10" required />
                  <small class="text-muted">Quantité minimum restant par unité pour générer une alerte</small>
                </div>
              </div>

              <!-- Boutons de soumission -->
              <div class="col-12 mt-4">
                <div class="form-group d-flex justify-content-end">
                  <a href="productlist.html" class="btn btn-cancel me-2">Annuler</a>
                  <button type="submit" class="btn btn-primary">Enregistrer</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />