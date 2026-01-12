<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<main role="main" class="main-content">
  <div class="container-fluid">
    <form method="POST" action="UtilisateurServlet?action=register" >
    <input type="hidden" name="whoislogin" value="ADM" required />
      <div class="row justify-content-center">
        <div class="col-12">
          <h2 class="page-title">Ajout d'un utilisateur</h2>

          <div class="card shadow mb-4">
            <div class="card-header">
              <strong class="card-title">Formulaire d'ajout</strong>
            </div>
            <div class="card-body">
              <div class="row">
                <!-- Partie gauche -->
                <div class="col-md-6">
                  <div class="row">
                    <div class="col-md-6">
                      <div class="form-group mb-3">
                        <label for="nom">Nom *</label>
                        <input type="text" id="nom" name="nom" class="form-control" required />
                      </div>
                    </div>
                    <div class="col-md-6">
                      <div class="form-group mb-3">
                        <label for="prenom">Prénom *</label>
                        <input type="text" id="prenom" name="prenom" class="form-control" required />
                      </div>
                    </div>
                  </div>

                  <div class="row">
                    <div class="col-md-6">
                      <div class="form-group mb-3">
                        <label for="telephone">Login *</label>
                        <input type="text" id="username" name="username" class="form-control" required />
                      </div>
                    </div>
                    <div class="col-md-6">
                      <div class="form-group mb-3">
                        <label for="identifiant">Mot de passe *</label>
                        <input type="password" id="password" name="password" class="form-control" required />
                      </div>
                    </div>
                  </div>

                  <div class="form-group mb-3">
					  <label for="role">Rôle *</label>
					  <select name="role" id="roleSelect" class="form-control" required>
					    <option disabled selected>Choisir un rôle</option>
					    <c:forEach var="role" items="${roles}">
					      <option value="${role.id}">${role.roleName}</option>
					    </c:forEach>
					  </select>
					</div>


                  <div class="form-group mb-3" id="passwordField" hidden>
                    <label for="example-password">Mot de passe *</label>
                    <input type="password" name="password" id="example-password" class="form-control" placeholder="Mot de passe" />
                  </div>
                </div>

                <!-- Partie droite -->
                <div class="col-md-6">
                  <div class="form-group mb-3">
                    <label for="email">Email *</label>
                    <input type="email" name="email" id="email" class="form-control"  />
                  </div>
                   <!-- <div class="form-group mb-3">
                    <label for="pourcentage">Pourcentage </label>
                    <input type="number" name="pourcentage" id="pourcentage" class="form-control"  />
                  </div> -->
                </div>
                
                
              </div>

              <button type="submit" class="btn btn-primary">
                Créer l'utilisateur <i class="fe fe-send icon"></i>
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
