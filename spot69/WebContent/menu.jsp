<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">


<%@ include file="header.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="com.spot69.model.MenuCategorie"%>
<%@ page import="com.spot69.model.Plat"%>
<link rel="stylesheet" href="./css/menu.css">

<style>
#main-title a{
color: var(--gold);
text-decoration : none;
}

#main-title a:hover{
color: var(--gold-dark);
}

#breadcrumb-category, #breadcrumb-subcategory{
color:var(--gold);
 margin-bottom: 20px;
 font-size:20px;
  cursor: pointer;
}

#breadcrumb-category:hover, #breadcrumb-subcategory:hover{
color:var(--gold-dark);
}

.plat-header {
    position: relative;
    height: 0;
}

.add-to-cart-icon {
    position: absolute;
    top: 0;
    right: 0;
    color: var(--gold);
    padding: 8px;
    border-bottom-left-radius: 8px;
    cursor: pointer;
    font-size: 21px;
    z-index: 10;
    transition: background-color 0.2s ease;
}
.add-to-cart-icon:hover {
    color: var(--gold-dark);
}
.sous-categorie-swiper {
    width: 100%;
    padding: 10px 0;
}

/* Réutilisation du style catégorie parent pour Swiper */
.slide-wrapper {
	position: relative;
	overflow: hidden;
	width: 300px;
	height: 300px;
	margin:auto;
	cursor: pointer;
	border-radius: 12px;
	box-shadow: 0 0 10px rgba(255, 255, 255, 0.05);
}
.slide-wrapper {
  width: 100%;
  max-width: 300px;
  height: 300px;
}

.slide-wrapper img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.4s ease;
	border-radius: 12px;
}

.slide-wrapper:hover img {
	transform: scale(1.05);
}

.slide-wrapper .caption {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	opacity: 0;
	transition: opacity 0.45s ease-in-out;
}

.slide-wrapper:hover .caption {
	opacity: 1;
}

.slide-wrapper .blur {
	background-color: rgba(0, 0, 0, 0.65);
	width: 100%;
	height: 100%;
	position: absolute;
	z-index: 5;
}

.slide-wrapper .caption-text {
	z-index: 10;
	color: #fff;
	position: absolute;
	width: 100%;
	text-align: center;
	top: 50%;
	transform: translateY(-50%);
}

.slide-wrapper .caption-text h1 {
	font-size: 28px;
	margin: 0;
	color: var(--gold);
}

.menu-cat-prev, .menu-cat-next {
color: var(--gold);
}



@media (max-width: 767px) {
  .slide-wrapper .caption, .swiper-slide .caption {
    opacity: 1;
  }
}



</style>


<section class="menu section">
    <h2 class="section-title" id="main-title"><a href="<%=request.getContextPath()%>/MenuServlet?action=categorie-parente">Notre Menu</a></h2>

  <!-- Section de sélection des catégories parentes -->
<div id="menu-selection" class="container container-custom swiper menu-swiper">
  <ul class="caption-style-1 swiper-wrapper">
    <%
    List<MenuCategorie> categoriesParentes = (List<MenuCategorie>) request.getAttribute("categorie-parente");
        String ctx = request.getContextPath();

        if (categoriesParentes != null && !categoriesParentes.isEmpty()) {
            for (MenuCategorie categorie : categoriesParentes) {
                String imageUrl = categorie.getImageUrl();
                String imageSrc = "";

                if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("uploads/categories/")) {
                    imageSrc = ctx + "/images/categories/" + imageUrl.substring("uploads/categories/".length());
                } else {
                    imageSrc = "https://themewagon.github.io/yummy-red/assets/img/menu/menu-item-4.png";
                }
    %>
        <li class="categorie-btn swiper-slide" data-id="<%=categorie.getId()%>">
            <img src="<%= imageSrc %>" alt="<%= categorie.getNom() %>" />
            <div class="caption">
                <div class="blur"></div>
                <div class="caption-text">
                    <h1><%= categorie.getNom() %></h1>
                </div>
            </div>
        </li>
    <%
        }
    } else {
    %>
        <li>Aucune catégorie parente disponible.</li>
    <% } %>
  </ul>

  <!-- Flèches spécifiques Swiper -->
  <div class="swiper-button-prev menu-cat-prev"></div>
  <div class="swiper-button-next menu-cat-next"></div>
</div>





    <!-- Section dynamique des sous-catégories -->
<div id="food-section" class="hide">
    <!-- Bouton de retour -->
   <div class="back-button" style="margin-left: 20px; margin-bottom: 20px; cursor: pointer;">
    <span id="breadcrumb-category" style=""></span> 
    <span id="breadcrumb-subcategory" style=""></span>
</div>


    <h2 class=" food"></h2>

    <div id="sous-categories-container" class="container container-custom">
        <!-- <ul class="caption-style-1" id="sous-categorie-list">
            Les sous-catégories seront insérées ici dynamiquement
        </ul> -->
        <!-- Swiper Container -->
<div class="swiper sous-categorie-swiper">
  <div class="swiper-wrapper" id="sous-categorie-list">
    <!-- Slides insérés dynamiquement en JS -->
  </div>
  <!-- Arrows -->
  <div class="swiper-button-prev" style="color:var(--gold);"></div>
  <div class="swiper-button-next" style="color:var(--gold);"></div>
</div>
        
    </div>

    <div id="plats-container" class="container menu-grid" style="margin-top: 30px;">
        <!-- Les plats de la sous-catégorie sélectionnée s'afficheront ici -->
    </div>
</div>
    
</section>

<%@ include file="footer.jsp"%>

<script>
//Fonction pour charger les plats d'une sous-catégorie
function loadPlatsBySousCategorie(categorieId, contentElement) {

    // Si le contenu est déjà chargé (plutôt que de le recharger à chaque fois)
    if (contentElement.innerHTML !== "<p>Chargement des plats...</p>" && contentElement.innerHTML !== "") {
        return; // Ne pas recharger si le contenu existe déjà
    }

    // Afficher le message de chargement des plats
    contentElement.innerHTML = "<p>Chargement des plats...</p>";

    fetch("<%=request.getContextPath()%>/MenuServlet?action=plats-par-sous-categories&categorieId=" + categorieId)
        .then(response => {
           /*  console.log("Réponse de l'API pour les plats:", response); */
            if (!response.ok) {
                throw new Error("Erreur HTTP: " + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.plats && Array.isArray(data.plats) && data.plats.length > 0) {
                contentElement.innerHTML = ""; // Vider le contenu précédent
		
                // Afficher les plats
              data.plats.forEach(function(plat) {
				    const platDiv = document.createElement("div");
				    platDiv.classList.add("menu-item");
				
				    const isProduit = plat.produit != null;
				
				    const nom = isProduit ? plat.produit.nom : plat.nom;
				    const description = isProduit ? plat.produit.description : plat.description;
				    const elementId = isProduit ? plat.produit.id : plat.id;
				    const prix = isProduit ? plat.produit.prixVente : plat.prix;
				    const dataType = isProduit ? "produit" : "plat";
				    const dataId = isProduit ? plat.produit.id : plat.id;
				
				    let platImage = "";
				    if (isProduit && typeof plat.produit.imageUrl === "string" && plat.produit.imageUrl.startsWith("uploads/produits/")) {
				        platImage = "<%= ctx %>/images/produits/" + plat.produit.imageUrl.substring("uploads/produits/".length);
				    } else if (typeof plat.image === "string" && plat.image.startsWith("uploads/plats/")) {
				        platImage = "<%= ctx %>/images/plats/" + plat.image.substring("uploads/plats/".length);
				    } else {
				        platImage = "<%= ctx %>/images/plats/default-image.jpg";
				    }
				
				    platDiv.innerHTML = 
				        '<div class="plat-header">' +
				            '<i class="fa fa-shopping-cart add-to-cart-icon" title="Ajouter au panier" ' +
				            'data-type="' + dataType + '" data-id="' + elementId + '"></i>' +
				        '</div>' +
				        '<img src="' + platImage + '" class="menu-img" alt="' + nom + '">' +
				        '<h4>' + nom + '</h4>' +
				        '<p class="ingredients">' + description + '</p>' +
				        '<p class="price">' + prix + ' HTG</p>';
				
				    contentElement.appendChild(platDiv);
				});

             // **ICI** : ajout de l’event listener pour les icônes dans contentElement
              contentElement.querySelectorAll(".add-to-cart-icon").forEach(icon => {
            	  icon.addEventListener("click", function() {
            	    const id = this.getAttribute("data-id");
            	    const type = this.getAttribute("data-type");

            	    fetch('<%=request.getContextPath()%>/PanierServlet?action=ajouter&type=' + encodeURIComponent(type) + '&id=' + encodeURIComponent(id), {
            	      method: 'POST',
            	      credentials: 'same-origin'
            	    })
            	    .then(async response => {
            	      const text = await response.text();

            	      if (!response.ok) {
            	        // Message d’erreur (ex: paramètres manquants, ID invalide, etc.)
            	        showToast(text, 'error');
            	        return;
            	      }

            	      if (text === "Connectez-vous pour commander.") {
            	        showToast(text, 'error');
            	      } else {
            	        showToast(text, 'success');
            	        chargerPanierDansModal(); // 🔄 Charger les données
            	        cartModal.classList.add('open');
            	        overlay.classList.add('active');
            	      }
            	    })
            	    .catch(error => {
            	      showToast("Erreur lors de l'ajout au panier : " + error.message, 'error');
            	    });
            	  });
            	});

            } else {
                contentElement.innerHTML = "<p>Rien n'est disponible pour cette sous-catégorie.</p>";
            }
        })
        .catch(error => {
/*             console.error("Erreur lors de la récupération des plats :", error); */
            contentElement.innerHTML = "<p>Une erreur est survenue lors du chargement des plats.</p>";
        });
}


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

// Fonction pour afficher les sous-catégories et les plats associés

function showSousCategories(parentId) {
    document.getElementById("menu-selection").classList.add("hide");
    document.getElementById("food-section").classList.remove("hide");

    // Réinitialiser les spans
    document.getElementById("breadcrumb-category").innerText = "";
    document.getElementById("breadcrumb-subcategory").innerText = "";

    // Stocker le nom de la catégorie sélectionnée dans un attribut pour l'utiliser plus tard
    var clickedCategory = document.querySelector('.categorie-btn[data-id="' + parentId + '"]');
    if (clickedCategory) {
        var categoryName = clickedCategory.querySelector("h1").innerText;
        document.getElementById("breadcrumb-category").innerHTML = categoryName;
        document.getElementById("breadcrumb-category").setAttribute("data-id", parentId);
    }

    const sousCategorieList = document.getElementById("sous-categorie-list");
    const platsContainer = document.getElementById("plats-container");
    sousCategorieList.innerHTML = "";
    platsContainer.innerHTML = "";

    fetch("<%=request.getContextPath()%>/MenuServlet?action=sous-categories&parentId=" + parentId)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.sousCategories && Array.isArray(data.sousCategories)) {
                data.sousCategories.forEach(function(sousCategorie) {
                   /*  var li = document.createElement("li"); */
                   var li = document.createElement("div");
li.classList.add("swiper-slide", "categorie-btn");

                    li.classList.add("categorie-btn");
                    li.setAttribute("data-id", sousCategorie.id);
                    li.setAttribute("data-nom", sousCategorie.nom);

                    var imageSrc = (sousCategorie.imageUrl && sousCategorie.imageUrl.startsWith("uploads/categories/"))
                        ? "<%=ctx%>/images/categories/" + sousCategorie.imageUrl.substring("uploads/categories/".length)
                        : "https://themewagon.github.io/yummy-red/assets/img/menu/menu-item-4.png";

                        li.innerHTML =
                            '<div class="slide-wrapper">' +
                                '<img src="' + imageSrc + '" alt="' + sousCategorie.nom + '" />' +
                                '<div class="caption">' +
                                    '<div class="blur"></div>' +
                                    '<div class="caption-text">' +
                                        '<h1>' + sousCategorie.nom + '</h1>' +
                                    '</div>' +
                                '</div>' +
                            '</div>';

                        li.addEventListener("click", function () {
                            document.getElementById("sous-categories-container").classList.add("hide");

                            document.getElementById("breadcrumb-subcategory").innerHTML = 
                                 "<span style='color:#fff'>/</span> " + sousCategorie.nom ;

                            document.getElementById("breadcrumb-subcategory").setAttribute("data-id", sousCategorie.id);

                            platsContainer.innerHTML = "<p>Chargement des plats...</p>";
                            loadPlatsBySousCategorie(sousCategorie.id, platsContainer);
                        });


                    sousCategorieList.appendChild(li);
                });
            } else {
                sousCategorieList.innerHTML = "<li>Aucune sous-catégorie disponible.</li>";
            }
        })
        .catch(function(error) {
            sousCategorieList.innerHTML = "<li>Erreur lors du chargement des sous-catégories.</li>";
        });
}



//Attacher dynamiquement les événements après chargement de la page
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".categorie-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            const id = this.dataset.id;
            showSousCategories(id);
        });
    });

    // Clic sur une catégorie dans le fil d'ariane
    document.getElementById("breadcrumb-category").addEventListener("click", function () {
        const catId = this.getAttribute("data-id");
        if (catId) {
            document.getElementById("sous-categories-container").classList.remove("hide");
            document.getElementById("breadcrumb-subcategory").innerText = "";
            document.getElementById("plats-container").innerHTML = "";
        }
    });

    // Clic sur la sous-catégorie dans le fil (reload plats)
    document.getElementById("breadcrumb-subcategory").addEventListener("click", function () {
        const sousCatId = this.getAttribute("data-id");
        if (sousCatId) {
            const platsContainer = document.getElementById("plats-container");
            platsContainer.innerHTML = "<p>Chargement des plats...</p>";
            loadPlatsBySousCategorie(sousCatId, platsContainer);
        }
    });

    
});

setTimeout(() => {
	// à la fin du .then(data => { ... })
	if (window.sousCatSwiper) {
	  window.sousCatSwiper.update();
	} else {
		window.sousCatSwiper = new Swiper('.sous-categorie-swiper', {
			  spaceBetween: 20,
			  navigation: {
			    nextEl: '.swiper-button-next',
			    prevEl: '.swiper-button-prev'
			  },
			  freeMode: false,
			  breakpoints: {
			    0: {          // Smartphones
			      slidesPerView: 1
			    },
			    576: {        // Petites tablettes
			      slidesPerView: 2
			    },
			    768: {        // Tablettes
			      slidesPerView: 3
			    },
			    992: {        // Ordinateurs portables
			      slidesPerView: 4
			    },
			    1200: {       // Grands écrans
			      slidesPerView: 4
			    }
			  }
			});


	}

}, 300);

setTimeout(() => {
    if (window.menuCatSwiper) {
        window.menuCatSwiper.update();
    } else {
        window.menuCatSwiper = new Swiper('.menu-swiper', {
            spaceBetween: 20,
            navigation: {
                nextEl: '.menu-cat-next',
                prevEl: '.menu-cat-prev'
            },
            freeMode: false,
            breakpoints: {
                0: { slidesPerView: 1 },
                576: { slidesPerView: 2 },
                768: { slidesPerView: 3 },
                992: { slidesPerView: 4 },
                1200: { slidesPerView: 4 }
            }
        });
    }
}, 300);


// Gestion des onglets plats avec event delegation
document.getElementById("food-section").addEventListener("click", function(event) {
    if (event.target && event.target.classList.contains("tab-btn")) {

        const tabButtons = document.querySelectorAll(".tab-btn");
        const contents = document.querySelectorAll(".tab-content");

        tabButtons.forEach(b => b.classList.remove("active"));
        contents.forEach(c => c.classList.remove("active"));

        // Ajouter la classe active au bouton et au contenu
        event.target.classList.add("active");
        const content = document.getElementById(event.target.dataset.tab);
        content.classList.add("active");
 
            loadPlatsBySousCategorie(event.target.dataset.id, content);
    }
});


</script>
