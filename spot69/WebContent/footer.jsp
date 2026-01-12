
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">
<!-- Ajout du CDN Font Awesome pour les icônes -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<style>
.floating-cart {
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 999;
}

.cart-button-floating {
    color: var(--gold);
    font-size: 22px;
    border: none;
    border-radius: 50%;
    padding: 14px 16px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.2);
    position: relative;
    cursor: pointer;
    text-decoration: none;
    transition: background-color 0.3s ease;
}

.cart-button-floating:hover {
   color: var(--gold-dark);
}

.cart-count-floating {
    position: absolute;
    top: -5px;
    right: 5px;
    background-color: var(--gold);
    color: white;
    font-size: 12px;
    font-weight: bold;
    padding: 2px 6px;
    border-radius: 50%;
    transition: background-color 0.3s ease;
}

.cart-count-floating:hover{
   background-color: var(--gold-dark);
   color: white;
}

/* Styles pour le modal */
.cart-modal {
    display: none;
    position: fixed;
    top: 0;
    right: 0;
    width: 350px;
    height: 100%;
    background-color: var(--dark);
    box-shadow: -5px 0 15px rgba(0,0,0,0.3);
    z-index: 1000;
    transform: translateX(100%);
    transition: transform 0.3s ease-out;
    color: var(--light);
}

.cart-modal.open {
    transform: translateX(0);
    display: block;
}

.cart-modal-header {
    padding: 20px;
    border-bottom: 1px solid var(--gray);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.cart-modal-title {
    font-size: 1.5rem;
    color: var(--gold);
    margin: 0;
}

.cart-close-btn {
    background: none;
    border: none;
    color: var(--light);
    font-size: 1.5rem;
    cursor: pointer;
    transition: color 0.3s ease;
}

.cart-close-btn:hover {
    color: var(--gold);
}

.cart-modal-body {
    padding: 20px;
    height: calc(73% - 150px);
    overflow-y: auto;
}

.cart-item {
    display: flex;
    margin-bottom: 15px;
    padding-bottom: 15px;
    border-bottom: 1px solid var(--gray);
    position: relative;
}

.cart-item-img {
    width: 70px;
    height: 70px;
    object-fit: cover;
    margin-right: 15px;
}

.cart-item-details {
    flex: 1;
}

.cart-item-name {
    margin: 0 0 5px 0;
    color: var(--light);
}

.cart-item-price {
    color: var(--gold);
    margin: 0 0 5px 0;
}

.cart-item-quantity {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.quantity-btn {
    background-color: var(--gray);
    border: none;
    color: var(--light);
    width: 25px;
    height: 25px;
    cursor: pointer;
    border-radius: 3px;
}

.quantity-input {
    width: 45px;
    height: 30px;
    text-align: center;
    margin: 0 5px;
    background-color: var(--gray);
    border: none;
    color: var(--gold);
    border-radius: 3px;
}

/* Nouveau style pour l'icône de suppression */
.delete-item {
    position: absolute;
    bottom: 15px;
    right: 0;
    background: none;
    border: none;
    color: var(--light-gray);
    cursor: pointer;
    font-size: 1rem;
    transition: color 0.3s ease;
}

.delete-item:hover {
    color: var(--gold);
}

.cart-modal-footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 20px;
    border-top: 1px solid var(--gray);
    background-color: var(--dark);
}

.cart-total {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    margin-right: 47px;
    font-size: 1.2rem;
}

.cart-total-label {
    color: var(--light);
}

.cart-total-amount {
    color: var(--gold);
    font-weight: bold;
}

.checkout-btn {
    width: 88%;
    padding: 12px;
    background-color: var(--gold);
    color: var(--dark);
    border: none;
    border-radius: 4px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.checkout-btn:hover {
    background-color: var(--gold-dark);
    color: var(--light);
}

.overlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.5);
    z-index: 999;
}

.overlay.active {
    display: block;
}
.cart-notes {
    margin-bottom: 15px;
    width: 100%;
}

.cart-notes textarea {
   width: 88%;
    padding: 10px;
    background-color: var(--gray) !important;
    border: 1px solid var(--gray-dark);
    border-radius: 4px;
    color: var(--gold);
    font-family: inherit;
    resize: vertical;
    height:30px;
    min-height: 50px;
}

.cart-notes textarea:focus {
    outline: none;
    border-color: var(--gold);
}
/* Ajout du style pour les sous-totaux */
.subtotal-section {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    border-radius: 4px;
}

.subtotal-label {
    font-weight: bold;
    color: var(--gold);
}

.subtotal-amount {
    font-weight: bold;
    color: var(--gold);
}
</style>

<div class="floating-cart">
    <a href="#" class="cart-button-floating" id="cartButton">
        <i class="fa fa-shopping-cart"></i>
        <span class="cart-count-floating" hidden>3</span>
    </a>
</div>

<!-- Modal du panier -->
<div class="overlay" id="overlay"></div>
<div class="cart-modal" id="cartModal">
    <div class="cart-modal-header">
        <h3 class="cart-modal-title">Votre Panier</h3>
        <button class="cart-close-btn" id="closeCart">&times;</button>
    </div>
    <div class="cart-modal-body">
        <!-- Exemple d'articles dans le panier -->
        
    </div>
    <div class="cart-modal-footer">
        <!-- Ajout du textarea pour les notes -->
    <div class="cart-notes">
        <textarea id="cartNotes" name="notes" placeholder="Ajoutez vos notes ou préférences ici..." rows="3"></textarea>
    </div>
    
    <div class="cart-total">
        <span class="cart-total-label">Total:</span>
        <span class="cart-total-amount">0.00 HTG</span>
    </div>
        <form id="formPasserCommande" action="CommandeServlet?action=ajouter" method="POST" style="display:none;">
		  <!-- Champs cachés dynamiques pour CommandeDetail -->
		  <div style="margin-bottom: 10px;">
		    <label for="noteCommande" style="color: var(--light); font-weight: bold;">Notes / Préférences :</label><br>
		    <textarea id="noteCommande" name="notes" rows="3" style="width: 100%; border-radius: 4px; border: 1px solid var(--gray); padding: 6px; resize: vertical;"></textarea>
		</div>
		  
		  
		  <!-- Exemple des champs de commande (tu peux adapter) -->
		  <input type="hidden" name="clientId" value="123" />
		  <input type="hidden" name="numeroCommande" id="numeroCommande" value="" />
		  <input type="hidden" name="dateCommande" value="<%= new java.sql.Timestamp(System.currentTimeMillis()) %>" />
		  <input type="hidden" name="montantTotal" id="montantTotal" value="" />
		  <input type="hidden" name="modePaiement" value="NON_PAYE" />
		  <input type="hidden" name="statutCommande" value="EN_ATTENTE" />
		  <input type="hidden" name="statutPaiement" value="NON_PAYE" />
		  <input type="hidden" name="montantPaye" value="0" />
		
		  <div id="detailsCache"></div>
		</form>
		        
        <button class="checkout-btn" id="btnPasserCommande">Passer la commande</button>
    </div>
</div>

<footer>
    <a href="#" class="footer-logo"
    ><img src="./image/logo.png" width="90" alt="" srcset=""
        /></a>
    <div class="social-links">
        <a href="#"><i class="fab fa-facebook-f"></i></a>
        <a href="https://www.instagram.com/spot69ht/" target="_blank"
        ><i class="fab fa-instagram"></i
        ></a>
        <a href="https://www.tiktok.com/@thespot69rooftoop" target="_blank"
        ><i class="fab fa-tiktok"></i
        ></a>
        <a href="#"><i class="fab fa-tripadvisor"></i></a>
    </div>
    <p class="copyright">
         © 2025 SPOT 69. Tout droits réservés. Hôtel & Restaurant Premium
        Port-au-Prince.
    </p>
</footer>

<script>
// Gestion du modal du panier
document.addEventListener('DOMContentLoaded', function() {
    const cartButton = document.getElementById('cartButton');
    const cartModal = document.getElementById('cartModal');
    const closeCart = document.getElementById('closeCart');
    const overlay = document.getElementById('overlay');
    
    // Ouvrir le modal
    /* cartButton.addEventListener('click', function(e) {
        e.preventDefault();
        cartModal.classList.add('open');
        overlay.classList.add('active');
    }); */
    cartButton.addEventListener('click', function(e) {
        e.preventDefault();
        chargerPanierDansModal(); // 🔄 Charger les données
        cartModal.classList.add('open');
        overlay.classList.add('active');
    });

    
    // Fermer le modal
    closeCart.addEventListener('click', function() {
        cartModal.classList.remove('open');
        overlay.classList.remove('active');
    });
    
    // Fermer en cliquant sur l'overlay
    overlay.addEventListener('click', function() {
        cartModal.classList.remove('open');
        overlay.classList.remove('active');
    });
    
    // Gestion de la quantité des produits
    const quantityBtns = document.querySelectorAll('.quantity-btn');
    quantityBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.parentElement.querySelector('.quantity-input');
            let value = parseInt(input.value);
            
            if (this.textContent === '+' && value < 99) {
                input.value = value + 1;
            } else if (this.textContent === '-' && value > 1) {
                input.value = value - 1;
            }
            
            // Ici vous pourriez ajouter une fonction pour mettre à jour le total
        });
    });
    
    // Gestion de la suppression d'un article (maintenant avec l'icône poubelle)
    const deleteItems = document.querySelectorAll('.delete-item');
    deleteItems.forEach(item => {
        item.addEventListener('click', function() {
            // Ici vous pourriez ajouter la logique pour supprimer l'article du panier
            this.closest('.cart-item').remove();
        });
    });
});
async function chargerPanierDansModal() {
    const body = document.querySelector('.cart-modal-body');
    const totalElem = document.querySelector('.cart-total-amount');
    body.innerHTML = ''; // Réinitialiser le contenu

    // Fonction pour formater les montants
    function formatMontant(montant) {
        if (isNaN(montant)) return '0';
        const arrondi = Math.round(montant * 100) / 100;
        const str = arrondi.toString();
        return str.endsWith('.00') ? str.slice(0, -3) : str;
    }

    try {
        const response = await fetch('PanierServlet?action=lister-json');
        const data = await response.json();

        if (response.status === 401 || data.error === "non connecte") {
            body.innerHTML = '<p>Veuillez vous connecter pour voir votre panier.</p>';
            totalElem.textContent = '0';
            return;
        }
        

        // Objet pour stocker les items par catégorie
        const itemsParCategorie = {};
        let grandTotal = 0;

        // Organiser les items par catégorie
        data.forEach(function(item) {
            let categorieId, categorieNom, element, prix;
            
            if (item.plat) {
                element = item.plat;
                categorieId = element.categorie ? element.categorie.id : '0';
                categorieNom = element.categorie ? element.categorie.nom : 'Non classé';
                prix = element.prix || 0;
            } else if (item.produit) {
                element = item.produit;
                categorieId = element.categorie ? element.categorie.id : '0';
                categorieNom = element.categorie ? element.categorie.nom : 'Non classé';
                prix = element.prixVente || 0;
            } else {
                return; // Si ni plat ni produit, on ignore
            }

            if (!itemsParCategorie[categorieId]) {
                itemsParCategorie[categorieId] = {
                    nom: categorieNom,
                    items: [],
                    total: 0
                };
            }

            const quantite = item.quantite || 0;
            const totalItem = prix * quantite;
            
            itemsParCategorie[categorieId].items.push({
                element: element,
                quantite: quantite,
                panier_id: item.panier_id,
                type: item.plat ? 'plat' : 'produit',
                totalItem: totalItem
            });
            
            itemsParCategorie[categorieId].total += totalItem;
            grandTotal += totalItem;
        });

        // Créer les sections pour chaque catégorie
        for (const categorieId in itemsParCategorie) {
            const categorie = itemsParCategorie[categorieId];
            
            const categorieSection = document.createElement('div');
            categorieSection.innerHTML = '<h4 style="color: var(--gold); margin-top:0; margin-bottom: 15px; border-bottom: 1px solid var(--gray); padding-bottom: 5px;">' + categorie.nom + '</h4>';
            body.appendChild(categorieSection);

            // Ajouter les items de cette catégorie
            categorie.items.forEach(function(item) {
                const cartItem = createCartItemElement(
                    item.element.nom, 
                    item.element.image || item.element.imageUrl, 
                    item.element.prix || item.element.prixVente, 
                    item.quantite, 
                    item.panier_id, 
                    item.element.id, 
                    item.type
                );
                categorieSection.appendChild(cartItem);
            });

            // Ajouter le sous-total pour cette catégorie
            const categorieSubtotal = document.createElement('div');
            categorieSubtotal.className = 'subtotal-section';
            categorieSubtotal.innerHTML = 
                '<span class="subtotal-label">Sous-total :</span>' +
                '<span class="subtotal-amount">' + formatMontant(categorie.total) + ' HTG</span>';
            categorieSection.appendChild(categorieSubtotal);
        }

        // Mettre à jour le grand total
        totalElem.textContent = formatMontant(grandTotal) + ' HTG';
        
        activerListenersPanier();

    } catch (err) {
        console.error('Erreur:', err);
        body.innerHTML = '<p>Erreur lors du chargement du panier.</p>';
        totalElem.textContent = '0';
    }
}

// Fonction helper pour créer un élément de panier
function createCartItemElement(nom, imageUrl, prix, quantite, panierId, elementId, type) {
    let imagePath = "";
    
    if (type === "produit" && typeof imageUrl === "string" && imageUrl.startsWith("uploads/produits/")) {
        imagePath = "<%=request.getContextPath()%>/images/produits/" + imageUrl.substring("uploads/produits/".length);
    } else if (type === "plat" && typeof imageUrl === "string" && imageUrl.startsWith("uploads/plats/")) {
        imagePath = "<%=request.getContextPath()%>/images/plats/" + imageUrl.substring("uploads/plats/".length);
    } else {
        imagePath = "<%=request.getContextPath()%>/images/plats/default-image.jpg";
    }

    const cartItem = document.createElement('div');
    cartItem.classList.add('cart-item');

    cartItem.innerHTML =
        '<img src="' + imagePath + '" alt="' + nom + '" class="cart-item-img">' +
        '<div class="cart-item-details">' +
            '<h4 class="cart-item-name">' + nom + '</h4>' +
            '<p class="cart-item-price">' + prix.toFixed(2) + ' HTG</p>' +
            '<div class="cart-item-quantity">' +
                '<button class="quantity-btn" data-id="' + elementId + '" data-type="' + type + '" data-op="diminuer">-</button>' +
                '<input type="text" class="quantity-input" value="' + quantite + '" readonly>' +
                '<button class="quantity-btn" data-id="' + panierId + '" data-type="' + type + '" data-element-id="' + elementId + '" data-op="augmenter">+</button>' +
            '</div>' +
            '<button class="delete-item" data-id="' + panierId + '" data-type="' + type + '" data-op="supprimer">' +
                '<i class="fas fa-trash-alt"></i>' +
            '</button>' +
        '</div>';

    return cartItem;
}
function activerListenersPanier() {
    document.querySelectorAll('.quantity-btn').forEach(function(btn) {
        btn.addEventListener('click', async function() {
            var id = this.dataset.id;
            var type = this.dataset.type;
            var op = this.dataset.op;

            var url = '<%=request.getContextPath()%>/PanierServlet?action=' + op +
                      '&type=' + encodeURIComponent(type) +
                      '&id=' + encodeURIComponent(id);
            await fetch(url, { method: 'POST' });
            chargerPanierDansModal(); // Recharger la liste après modification
        });
    });

    document.querySelectorAll('.delete-item').forEach(function(btn) {
        btn.addEventListener('click', async function() {
            var id = this.dataset.id;
            var type = this.dataset.type;

            var url = '<%=request.getContextPath()%>/PanierServlet?action=supprimer' +
                      '&id=' + encodeURIComponent(id) +
                      '&type=' + encodeURIComponent(type);

            await fetch(url, { method: 'POST' });
            chargerPanierDansModal();
        });
    });
}
function logDetailsAvantEnvoi() {
    const platIds = Array.from(document.getElementsByName("platId[]")).map(input => input.value);
    const produitIds = Array.from(document.getElementsByName("produitId[]")).map(input => input.value);
    const quantites = Array.from(document.getElementsByName("quantite[]")).map(input => input.value);
    const prixUnitaires = Array.from(document.getElementsByName("prixUnitaire[]")).map(input => input.value);
    const sousTotals = Array.from(document.getElementsByName("sousTotal[]")).map(input => input.value);
    const notes = Array.from(document.getElementsByName("notesDetail[]")).map(input => input.value);
    const panierIds = Array.from(document.getElementsByName("pannierId[]")).map(input => input.value);
    const notePref = document.getElementsByName("note")[0].value;



}

document.getElementById('btnPasserCommande').addEventListener('click', function(e) {
	  e.preventDefault();

	  const form = document.getElementById('formPasserCommande');
	  const detailsContainer = document.getElementById('detailsCache');
	  detailsContainer.innerHTML = ''; // reset

	  // Récupérer tous les items du panier affichés
	  const cartItems = document.querySelectorAll('.cart-item');
	  
	  let montantTotal = 0;

	  cartItems.forEach((item, index) => {
	    // Récupérer infos nécessaires dans chaque item
	    const quantite = parseInt(item.querySelector('.quantity-input').value);
	    const prixStr = item.querySelector('.cart-item-price').textContent.trim();
	    // Exemple: "1000.00 HTG" -> on extrait juste le nombre
	    const prixUnitaire = parseFloat(prixStr.replace(/[^\d.]/g, '')); 
	    
	    // Récupérer les notes du textarea
	    const notesPref = document.getElementById('cartNotes').value;

	    const sousTotal = quantite * prixUnitaire;
	    montantTotal += sousTotal;

	    // Récupérer les data-id et type sur les boutons pour l'id du plat/produit
	    // On peut aussi stocker data dans un attribut custom pour plus sûr
	    const btnPlus = item.querySelector('.quantity-btn[data-op="augmenter"]');
	    const type = btnPlus ? btnPlus.dataset.type : null;
	    const idStr = btnPlus ? btnPlus.dataset.elementId : null;
	    const pannierIdStr = btnPlus ? btnPlus.dataset.id : null;

	    const id = idStr ? parseInt(idStr) : null;
	    const pannierId = pannierIdStr ? parseInt(pannierIdStr) : null;
	    

	    // Selon type, on remplit platId ou produitId
	    let platId = '';
	    let produitId = '';

	    if(type === 'plat') platId = id;
	    else if(type === 'produit') produitId = id;

	    // Création des champs cachés pour chaque détail
	    // Utilisation du suffixe [] pour tableaux côté servlet
	    detailsContainer.insertAdjacentHTML('beforeend',
	    	    "<input type='hidden' name='platId[]' value='" + platId + "' />" +
	    	    "<input type='hidden' name='produitId[]' value='" + produitId + "' />" +
	    	    "<input type='hidden' name='quantite[]' value='" + quantite + "' />" +
	    	    "<input type='hidden' name='prixUnitaire[]' value='" + prixUnitaire.toFixed(2) + "' />" +
	    	    "<input type='hidden' name='sousTotal[]' value='" + sousTotal.toFixed(2) + "' />" +
	    	    "<input type='hidden' name='notesDetail[]' value='' />" +
	    	    "<input type='hidden' name='pannierId[]' value='" + pannierId + "' />" +
	    	    "<input type='hidden' name='note' value='" + notesPref + "' />"
	    	);

	  });
	  

	  // Mettre à jour le total dans le champ caché
	  document.getElementById('montantTotal').value = montantTotal.toFixed(2);

	  // Générer un numéro de commande (simple, ou faire appel AJAX)
	  // Ici simple exemple local:
	  const numeroCommande = "CMD-" + Math.floor(100 + Math.random()*900) + "-" + Math.floor(1000 + Math.random()*9000);
	  document.getElementById('numeroCommande').value = numeroCommande;

	  // Envoyer le formulaire
	  logDetailsAvantEnvoi();
	 form.submit();
	});


</script>
<%
String toastMessage = (String) session.getAttribute("toastMessage");
String toastType = (String) session.getAttribute("toastType");

if (toastMessage != null && toastType != null) {
%>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        showToast("<%=toastMessage%>", "<%=toastType%>");
	});
</script>
<%
session.removeAttribute("toastMessage");
session.removeAttribute("toastType");
}
%>

<script src="./js/index.js"></script>
<script src="./js/login.js"></script>
<script src="javascript2.js"></script>


<!-- Swiper JS -->
<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>