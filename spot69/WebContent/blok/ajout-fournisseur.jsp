<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<main role="main" class="main-content">
  <div class="container-fluid">
    <form method="POST" action="FournisseurServlet?action=ajouter" >
      <div class="row justify-content-center">
        <div class="col-12">
          <h2 class="page-title">Ajout d'un fournisseur</h2>

          <div class="card shadow mb-4">
            <div class="card-header">
              <strong class="card-title">Formulaire d'ajout</strong>
            </div>
            <div class="card-body">
              <div class="row">
                <!-- Partie gauche -->
                <div class="col-md-6">

                  <div class="form-group mb-3">
                    <label for="nom">Nom du fournisseur *</label>
                    <input type="text" id="nom" name="nom" class="form-control" required />
                  </div>

                  <div class="form-group mb-3">
                    <label for="contact">Addresse</label>
                    <input type="text" id="contact" name="contact" class="form-control" />
                  </div>

                  <div class="form-group mb-3">
                    <label for="telephone">Téléphone</label>
                    <input type="text" id="telephone" name="telephone" class="form-control" />
                  </div>

                  <div class="form-group mb-3">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" class="form-control" />
                  </div>

                </div>

                <!-- Partie droite -->
                <div class="col-md-6">

                  <div class="form-group mb-3">
                    <label for="devisePreference">Devise préférée</label>
                    <select id="devisePreference" name="devisePreference" class="form-control">
                      <option value="HTG" selected>HTG</option>
                      <option value="USD">USD</option>
                    </select>
                  </div>

                  <div class="form-group mb-3">
                    <label for="modePaiement">Mode de paiement</label>
                    <select id="modePaiement" name="modePaiement" class="form-control">
                      <option value="CASH" selected>CASH</option>
                      <option value="VIREMENT">VIREMENT</option>
                      <option value="CREDIT">CREDIT</option>
                    </select>
                  </div>

                  <div class="form-check mb-3" hidden>
                    <input type="checkbox" id="creditAutorise" name="creditAutorise" class="form-check-input" />
                    <label for="creditAutorise" class="form-check-label">Crédit autorisé</label>
                  </div>

                  <div class="form-group mb-3" hidden>
                    <label for="limiteCredit">Limite crédit</label>
                    <input type="number" step="0.01" id="limiteCredit" name="limiteCredit" class="form-control" min="0" value="0" />
                  </div>

                  <div class="form-group mb-3" hidden>
                    <label for="soldeActuel">Solde actuel</label>
                    <input type="number" step="0.01" id="soldeActuel" name="soldeActuel" class="form-control" min="0" value="0" />
                  </div>

                </div>
              </div>

              <button type="submit" class="btn btn-primary">
                Ajouter le fournisseur <i class="fe fe-send icon"></i>
                <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
              </button>

            </div>
          </div>
        </div>
      </div>
    </form>
  </div>
</main>

<jsp:include page="footer.jsp" />
