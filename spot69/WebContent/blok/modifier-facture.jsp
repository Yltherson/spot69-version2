
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<%@ page
	import="com.google.gson.Gson,com.spot69.model.Fournisseur,com.spot69.model.Facture,com.spot69.model.FactureDetail,java.util.List"%>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<style>
.form-control {
	border: none;
	border-bottom: 1px solid #ccc;
	color: #fff;
	background: transparent;
	font-size: 14px;
	padding-left: 0;
}

.form-control:focus {
	box-shadow: none;
	border-bottom: 2px solid #007bff;
}

label {
	font-weight: bold;
	margin-right: 10px;
	white-space: nowrap;
}

.form-group-inline {
	display: flex;
	align-items: center;
	margin-right: 30px;
}

.form-row-flex {
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 30px;
}

.form-row-flex.header-table {
	justify-content: space-between;
}

.form-row-flex.footer-table {
	justify-content: right;
}

.table th, .table td {
	text-align: center;
	vertical-align: middle;
}
</style>
<style>
.fournisseur.autocomplete-suggestions {
	position: absolute;
	background: #222;
	color: #fff;
	border: 1px solid #333;
	max-height: 150px;
	overflow-y: auto;
	z-index: 1000;
	cursor: pointer;
}

.produit-suggestions.autocomplete-suggestions {
	background: #495057;
	color: #fff;
	border: 1px solid #333;
	max-height: 150px;
	overflow-y: auto;
	z-index: 1000;
	cursor: pointer;
}

.autocomplete-suggestion:hover {
	background: #007bff;
	color: white;
}
</style>


<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="page-wrapper">
      <div class="content">
         <form method="post" action="FactureServlet?action=modifier"> <!-- action modifiée -->
         
        <div class="page-header">
          <div class="page-title">
            <h4>Modification de la facture</h4>
            <h6></h6>
          </div>
        </div>

        <%
          Facture facture = (Facture) request.getAttribute("facture");
          List<FactureDetail> details = (List<FactureDetail>) request.getAttribute("details");
          List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
          List<com.spot69.model.Produit> produitsList = (List<com.spot69.model.Produit>) request.getAttribute("produits");
        %>

        <!-- Id caché de la facture pour l'update -->
        <input type="hidden" name="factureId" value="<%= facture != null ? facture.getId() : "" %>" />

        <!-- Ligne Fournisseur / Date / No Facture -->
        <div class="form-row-flex header-table">
          <div class="form-group-inline">
            <label for="fournisseur">Fournisseur :</label>
            <select id="fournisseur" name="fournisseur" class="form-control select-border-bottom" required>
              <option value="">-- Sélectionnez --</option>
              <%
              if (fournisseurs != null) {
                for (Fournisseur f : fournisseurs) {
              %>
              <option value="<%=f.getId()%>" <%= facture != null && facture.getFournisseur() != null && facture.getFournisseur().getId() == f.getId() ? "selected" : "" %>><%=f.getNom()%></option>
              <%
                }
              }
              %>
            </select>
          </div>

          <div class="form-group-inline">
            <label for="date">Date :</label>
            <input type="date" id="date" name="date" class="form-control" required
                   value="<%= facture != null && facture.getCreatedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(facture.getCreatedAt()) : "" %>">
          </div>

          <div class="form-group-inline">
            <label for="facture">No Facture :</label>
            <input type="text" id="facture" name="noFacture" class="form-control"
                   placeholder="Ex : NOF-123-4567"
                   value="<%= facture != null ? facture.getNoFacture() : (request.getAttribute("noFactureParDefaut") != null ? request.getAttribute("noFactureParDefaut") : "") %>"  readonly>
          </div>
        </div>

        <!-- Tableau Produits -->
        <table id="table-produits" class="table table-bordered table-input">
          <thead class="thead-light">
            <tr>
              <th>Nom Produit</th>
              <th>Unité de mesure</th>
              <th>Px Achat</th>
              <th>Qté</th>
              <th>Px Achat Total</th>
              <th>Qté Unité</th>
              <th>Prix Revient Unité</th>
              <th>Supprimer</th>
            </tr>
          </thead>
          <tbody id="product-table-body">
            <%
              if (details != null && !details.isEmpty()) {
                for (FactureDetail detail : details) {
                  String produitNom = "";
                  String uniteMesure = "";
                  if (detail.getProduit() != null) {
                    produitNom = detail.getProduit().getNom() != null ? detail.getProduit().getNom() : "";
                    uniteMesure = detail.getProduit().getUniteVente() != null ? detail.getProduit().getUniteVente() : "";
                  }
            %>
            <tr>
              <td style="position: relative;">
                <input type="hidden" name="produitId[]" value="<%= detail.getProduit() != null ? detail.getProduit().getId() : "" %>" />
                <input type="text" class="form-control produit-nom" name="produitNom[]" placeholder="Ex: Bière, Lassi..." autocomplete="off" required value="<%= produitNom %>">
                <div class="produit-suggestions autocomplete-suggestions"></div>
              </td>
              <td>
                <input type="text" class="form-control" name="uniteMesure[]" placeholder="Ex: caisse, kg, L" required value="<%= uniteMesure %>">
              </td>
              <td>
                <input type="number"  class="form-control" name="prixAchatParUniteMesure[]" placeholder="Prix achat" required value="<%= detail.getPrixAchatParUniteMesure() != null ? detail.getPrixAchatParUniteMesure() : "" %>">
              </td>
              <td>
                <input type="number" step="any" class="form-control" name="quantite[]" placeholder="Qté" required value="<%= detail.getQuantite() %>">
              </td>
              <td>
                <input type="number"  class="form-control" name="prixAchatTotal[]" placeholder="Prix total" value="<%= detail.getPrixAchatTotal() != null ? detail.getPrixAchatTotal() : "" %>">
              </td>
              <td>
			<input type="number" step="0.01" class="form-control" name="qteUnite[]"
			  placeholder="Prix unité"
			  value="<%= detail.getProduit() != null ? detail.getQteUnite() : "" %>"
			  data-contenu-par-unite="<%= (detail.getProduit() != null && detail.getProduit().getContenuParUnite() != null) ? Integer.parseInt(detail.getProduit().getContenuParUnite()) : 0 %>">


              </td>
              <td>
                <input type="number" step="0.01" class="form-control" name="prixRevientUnite[]" placeholder="Prix revient unité" value="<%= detail.getPrixRevientUnite() != null ? detail.getPrixRevientUnite() : "" %>">
              </td>
                     <td style="text-align: center; vertical-align: middle;">
      <button type="button" class="btn btn-link btn-sm btn-supprimer" title="Supprimer cette ligne" onclick="supprimerLigne(this)">
        <i class="fe fe-trash fe-16" style="color: red;"></i>
      </button>
    </td>
            </tr>
            <%
                }
              } else {
            %>
            <!-- Si pas de détails, afficher une ligne vide par défaut -->
            <tr>
              <td style="position: relative;">
                <input type="hidden" name="produitId[]" value="" />
                <input type="text" class="form-control produit-nom" name="produitNom[]" placeholder="Ex: Bière, Lassi..." autocomplete="off" required>
                <div class="produit-suggestions autocomplete-suggestions"></div>
              </td>
              <td>
                <input type="text" class="form-control" name="uniteMesure[]" placeholder="Ex: caisse, kg, L" required>
              </td>
              <td>
                <input type="number" class="form-control" name="prixAchatParUniteMesure[]" placeholder="Prix achat" required>
              </td>
              <td>
                <input type="number" step="any" class="form-control" name="quantite[]" placeholder="Qté" required>
              </td>
              <td>
                <input type="number" class="form-control" name="prixAchatTotal[]" placeholder="Prix total" >
              </td>
              <td>
                <input type="number" class="form-control" name="qteUnite[]" placeholder="Prix unité" >
              </td>
              <td>
                <input type="number" class="form-control" name="prixRevientUnite[]" placeholder="Prix revient unité" >
              </td>
              <td style="text-align: center; vertical-align: middle;">
      <button type="button" class="btn btn-link btn-sm btn-supprimer" title="Supprimer cette ligne" onclick="supprimerLigne(this)">
        <i class="fe fe-trash fe-16" style="color: red;"></i>
      </button>
    </td>
            </tr>
            <%
              }
            %>
          </tbody>
        </table>

        <!-- Ajouter une ligne -->
        <button type="button" class="btn btn-primary mb-3" onclick="ajouterLigne()">Ajouter une ligne</button>

        <!-- Totaux -->
        <div class="form-row-flex" style="justify-content: flex-end; margin-top: 20px;">
          <div class="form-group-inline">
            <label for="total-achat">Total Achat :</label>
            <input type="text" id="total-achat" name="totalAchat" class="form-control" readonly style="width: 120px;"
                   value="<%= facture != null && facture.getMontantTotal() != 0 ? facture.getMontantTotal() : "" %>">
          </div>
        </div>

        <!-- Paiement -->
        <div style="margin-top: 30px;">
          <div class="form-row-flex footer-table">
            <div class="form-group-inline">
              <label for="moyen-paiement">Moyen de paiement :</label>
              <select id="moyen-paiement" name="moyenPaiement" class="form-control">
                <option value="">-- Sélectionnez --</option>
                <option value="Cash" <%= facture != null && "Cash".equalsIgnoreCase(facture.getMoyenPaiement()) ? "selected" : "" %>>Cash</option>
                <option value="Virement" <%= facture != null && "Virement".equalsIgnoreCase(facture.getMoyenPaiement()) ? "selected" : "" %>>Virement</option>
                <option value="Moncash" <%= facture != null && "Moncash".equalsIgnoreCase(facture.getMoyenPaiement()) ? "selected" : "" %>>Natcash</option>
                <option value="Natcash" <%= facture != null && "Natcash".equalsIgnoreCase(facture.getMoyenPaiement()) ? "selected" : "" %>>Natcash</option>
                 <option value="Cheque" <%= facture != null && "Cheque".equalsIgnoreCase(facture.getMoyenPaiement()) ? "selected" : "" %>>Chèque</option>
              </select>
            </div>

            <div class="form-group-inline">
              <label for="paiement-type">Crédit :</label>
              <select id="paiement-type" name="isCredit" class="form-control" onchange="gererCredit()">
                <option value="non" <%= facture != null && !facture.isCredit() ? "selected" : "" %>>Non</option>
                <option value="oui" <%= facture != null && facture.isCredit() ? "selected" : "" %>>Oui</option>
              </select>
            </div>
          </div>

          <div class="form-row-flex footer-table" id="zone-credit" style="display: <%= facture != null && facture.isCredit() ? "flex" : "none" %>;">
            <div class="form-group-inline">
              <label for="montant-verse">Montant versé :</label>
              <input type="number" id="montant-verse" name="montantVerse" class="form-control" oninput="calculerSolde()"
                     value="<%= facture != null && facture.getMontantVerse() != null ? facture.getMontantVerse() : "" %>">
            </div>

            <div class="form-group-inline">
              <label for="solde">Solde :</label>
              <input type="text" id="solde" name="solde" class="form-control" readonly
                     value="<%= facture != null && facture.getSolde() != null ? facture.getSolde() : "" %>">
            </div>
          </div>
        </div>

        <!-- Enregistrer -->
        <div class="text-right">
          <button class="btn btn-primary">Enregistrer</button>
        </div>
          </form> <!-- <-- Fermeture du formulaire -->
      </div>
    </div>
  </div>
</main>
<!-- Script JS -->
<script>
//Fonction pour arrondir à l'entier le plus proche
function arrondir(valeur) {
  return Math.round(valeur);
}

function gererCredit() {
  const type = document.getElementById('paiement-type').value;
  document.getElementById('zone-credit').style.display = type === 'oui' ? 'flex' : 'none';
  if (type === 'non') {
    document.getElementById('montant-verse').value = '';
    document.getElementById('solde').value = '';
  }
}

function calculerTotaux() {
  let totalAchat = 0;

  document.querySelectorAll('#product-table-body tr').forEach(row => {
    const prixAchatInput = row.querySelector('input[name="prixAchatParUniteMesure[]"]');
    const quantiteInput = row.querySelector('input[name="quantite[]"]');
    const totalInput = row.querySelector('input[name="prixAchatTotal[]"]');
    const qteUniteInput = row.querySelector('input[name="qteUnite[]"]');
    const prixRevientInput = row.querySelector('input[name="prixRevientUnite[]"]');

    const prixAchat = parseFloat(prixAchatInput?.value) || 0;
    const quantite = parseFloat(quantiteInput?.value) || 0;

    // Récupérer contenuParUnite fixe (stocké dans data-contenu-par-unite)
    const contenuParUnite = parseFloat(qteUniteInput?.getAttribute('data-contenu-par-unite')) || 0;

    // Calcul du prix total (prix achat * quantité)
    const prixTotal = arrondir(prixAchat * quantite);
    console.log(prixTotal)
    if (totalInput) totalInput.value = prixTotal.toFixed(0);

    // Calcul Qté Unité = quantite * contenuParUnite (sans modifier data-contenu-par-unite)
    let qteUnite = 0;
    if (contenuParUnite > 0) {
      if (quantite === 0) {
        qteUniteInput.value = arrondir(contenuParUnite.toFixed(0));
      } else {
        qteUnite = arrondir(quantite * contenuParUnite);
        qteUniteInput.value = arrondir(qteUnite.toFixed(0));
      }
      qteUniteInput.readOnly = true;
    } else {
      qteUniteInput.readOnly = false;
      // qteUniteInput.value = ''; // Optionnel
    }

    totalAchat += prixTotal;

    // Calcul prix revient unité = prixTotal / Qté Unité calculée
    if (qteUnite > 0) {
      const prixRevient = arrondir(prixTotal / qteUnite);
      prixRevientInput.value = arrondir(prixRevient.toFixed(0));
      prixRevientInput.readOnly = true;
    } else {
      prixRevientInput.value = '';
      prixRevientInput.readOnly = false;
    }
  });

  document.getElementById('total-achat').value = arrondir(totalAchat.toFixed(0));
  calculerSolde();
}

function calculerSolde() {
  const type = document.getElementById('paiement-type').value;
  if (type !== 'oui') {
    // Pas de solde à calculer si pas de crédit
    return;
  }

  const total = parseFloat(document.getElementById('total-achat').value) || 0;
  const verse = parseFloat(document.getElementById('montant-verse').value);

  if (isNaN(verse)) {
    document.getElementById('solde').value = '';
    return;
  }

  const solde = arrondir(total - verse);
  document.getElementById('solde').value = arrondir(solde.toFixed(0));
}

// Recalcul automatique quand l’utilisateur modifie un input dans la table
document.addEventListener('input', function(e) {
  if (e.target.closest('#product-table-body')) {
    calculerTotaux();
  }
});


// === AUTOCOMPLETE ===

// Les listes JSON générées côté serveur (JSP)
const fournisseurs = <%=new Gson().toJson(request.getAttribute("fournisseurs"))%>;
const produits = <%=new Gson().toJson(request.getAttribute("produits"))%>;

function autocomplete(inputElem, suggestionsElem, list) {
  inputElem.addEventListener('input', () => {
    const val = inputElem.value.toLowerCase();
    if (!val) {
      suggestionsElem.style.display = 'none';
      suggestionsElem.innerHTML = '';
      return;
    }

    const filtered = list.filter(item => item.nom.toLowerCase().includes(val)).slice(0, 10);
    suggestionsElem.innerHTML = '';

    filtered.forEach(item => {
      const div = document.createElement('div');
      div.className = 'autocomplete-suggestion';
      div.textContent = item.nom;
      div.onclick = () => {
        inputElem.value = item.nom;
        suggestionsElem.style.display = 'none';

        const produitTrouve = list.find(p => p.nom === item.nom);
        if (produitTrouve) {
          const tr = inputElem.closest('tr');

          // Champ caché produitId[]
          const hiddenInput = tr.querySelector('input[type="hidden"][name="produitId[]"]');
          if (hiddenInput) hiddenInput.value = produitTrouve.id;

          // Unité de mesure
          const inputUnite = tr.querySelector('input[name="uniteMesure[]"]');
          if (inputUnite) inputUnite.value = produitTrouve.uniteVente || '';

          // Qté Unité (avant-dernière colonne)
          const inputQteUnite = tr.querySelector('input[name="qteUnite[]"]');
          if (inputQteUnite) {
            const prix = parseFloat(produitTrouve.contenuParUnite);
            if (!isNaN(prix)) {
              inputQteUnite.value = arrondir(prix.toFixed(0));
              inputQteUnite.setAttribute('data-contenu-par-unite', arrondir(prix.toFixed(0)));
              inputQteUnite.readOnly = true;
            } else {
              inputQteUnite.value = '';
              inputQteUnite.removeAttribute('data-contenu-par-unite');
              inputQteUnite.readOnly = false;
            }
          }
          
          
       // Qté prix d'achat
       
    const inputPrixAchatParUnite = tr.querySelector('input[name="prixAchatParUniteMesure[]"]'); 
          if (inputPrixAchatParUnite) {
            const prixAchat = parseFloat(produitTrouve.prixAchatParUniteVente);
            if (!isNaN(prixAchat)) {
            	inputPrixAchatParUnite.value = prixAchat.toFixed(0);
              inputPrixAchatParUnite.setAttribute('data-contenu-par-unite', prixAchat.toFixed(0));
            } else {
            	inputPrixAchatParUnite.value = '';
              inputPrixAchatParUnite.removeAttribute('data-contenu-par-unite');
            }
          }

          // On peut aussi mettre à jour d'autres champs si besoin...
          
          // Recalcule des totaux suite à la sélection
          calculerTotaux();
        }
      };
      suggestionsElem.appendChild(div);
    });

    suggestionsElem.style.display = filtered.length > 0 ? 'block' : 'none';

    if (filtered.length > 0) {
      const rect = inputElem.getBoundingClientRect();
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft;

      suggestionsElem.style.width = rect.width + 'px';
      suggestionsElem.style.top = (rect.bottom + scrollTop) + 'px';
      suggestionsElem.style.left = (rect.left + scrollLeft) + 'px';
    }
  });

  document.addEventListener('click', (e) => {
    if (e.target !== inputElem && e.target.parentNode !== suggestionsElem) {
      suggestionsElem.style.display = 'none';
    }
  });
}

// Initialise l'autocomplete pour tous les champs produits déjà présents
function initAutocompleteProduitsExistants() {
  document.querySelectorAll('#product-table-body .produit-nom').forEach(input => {
    const suggestionsElem = input.nextElementSibling;
    autocomplete(input, suggestionsElem, produits);
  });
}

// Initialisation au chargement de la page
document.addEventListener('DOMContentLoaded', () => {
  initAutocompleteProduitsExistants();
  calculerTotaux(); // recalcul au chargement
});

function supprimerLigne(button) {
	  const tr = button.closest('tr');
	  if (tr) {
	    tr.remove();
	    calculerTotaux(); // recalculer total après suppression
	  }
	}

// Fonction pour ajouter une nouvelle ligne avec autocomplete activé
function ajouterLigne() {
  const tbody = document.getElementById("product-table-body");
  const row = document.createElement("tr");
  row.innerHTML = `
    <td style="position: relative;">
      <input type="hidden" name="produitId[]" value="" required/>
      <input type="text" class="form-control produit-nom" name="produitNom[]" placeholder="Nom produit" autocomplete="off" required>
      <div class="produit-suggestions autocomplete-suggestions" style="display:none;"></div>
    </td>
    <td><input type="text" class="form-control" name="uniteMesure[]" placeholder="Unité" required></td>
    <td><input type="number" class="form-control" name="prixAchatParUniteMesure[]" placeholder="Prix achat" required></td>
    <td><input type="number" step="any" class="form-control" name="quantite[]" placeholder="Qté" required></td>
    <td><input type="number" class="form-control" name="prixAchatTotal[]" placeholder="Prix total" readonly></td>
    <td><input type="number" class="form-control" name="qteUnite[]" placeholder="Qté unité" readonly></td>
    <td><input type="number" class="form-control" name="prixRevientUnite[]" placeholder="Prix revient unité" readonly></td>
    <td style="text-align: center; vertical-align: middle;">
      <button type="button" class="btn btn-link btn-sm btn-supprimer" title="Supprimer cette ligne" onclick="supprimerLigne(this)">
        <i class="fe fe-trash fe-16" style="color: red;"></i>
      </button>
    </td>
  `;
  tbody.appendChild(row);

  // Active autocomplete pour la nouvelle ligne
  const newInput = row.querySelector('.produit-nom');
  const newSuggestions = row.querySelector('.produit-suggestions');
  autocomplete(newInput, newSuggestions, produits);
}

// Supprimer ligne (bouton poubelle)
function supprimerLigne(button) {
  const tr = button.closest('tr');
  if (tr) {
    tr.remove();
    calculerTotaux();
  }
}
</script>

<jsp:include page="footer.jsp" />
