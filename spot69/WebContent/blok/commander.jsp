<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="com.google.gson.Gson,java.util.List,com.spot69.model.Plat,com.spot69.model.Produit,com.spot69.model.Utilisateur,java.math.BigDecimal"%>
<meta charset="UTF-8">

<%@ page import="com.spot69.model.Permissions, com.spot69.utils.PermissionChecker" %>
<%
Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
boolean canPlaceClientOrders = currentUser != null && 
    PermissionChecker.hasPermission(currentUser, Permissions.PLACER_COMMANDE_CLIENTS);
%>
<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<link rel="stylesheet" href="css/commander.css">


<main role="main" class="main-content">
  <div class="container-fluid">
   <div class="p-4">
                <div class="row">
                    <!-- Liste des produits -->
                    <div class="col-lg-6">
                        <div class="form-section">
                            <h4 class="section-title"><i class="fas fa-boxes mr-2"></i>Plats/Boissons Disponibles</h4>
                            
                            <div class="form-group search-box">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" class="form-control" placeholder="Rechercher un produit..." id="productSearch">
                            </div>
                            
                            <div id="productList">
                                <%
    List<Plat> plats = (List<Plat>) request.getAttribute("plats"); 
    if (plats != null && !plats.isEmpty()) {
        for (int i = 0; i < plats.size(); i++) {
            Plat plat = plats.get(i);
            Produit produit = plat.getProduit();

            // Construction image dynamique
            String imagePath;
            if (plat.getImage() != null && !plat.getImage().isEmpty()) {
                imagePath = plat.getImage();
            } else if (produit != null && produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
                imagePath = produit.getImageUrl();
            } else {
                imagePath = null;
            }

            String imgFile = null;
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("uploads/plats/")) {
                    imgFile = "plats/" + imagePath.substring("uploads/plats/".length());
                } else if (imagePath.startsWith("uploads/produits/")) {
                    imgFile = "produits/" + imagePath.substring("uploads/produits/".length());
                } else {
                    imgFile = imagePath;
                }
            }

            String nomAffiche = (produit != null && produit.getNom() != null) ? produit.getNom() : plat.getNom();
            String description = (produit != null && produit.getDescription() != null) ? produit.getDescription() : plat.getDescription();
            BigDecimal prix = (produit != null && produit.getPrixVente() != null)
                    ? produit.getPrixVente()
                    : BigDecimal.valueOf(plat.getPrix());

%>
    <div class="card product-card" data-type="<%= produit != null ? "produit" : "plat" %>" 
     data-id="<%= produit != null ? produit.getId() : plat.getId() %>">
        <div class="card-body">
            <div class="row align-items-center">
                <div class="col-auto pr-0">
                    <div class="custom-control custom-checkbox">
                        <input type="checkbox" class="custom-control-input product-check" id="product<%= i %>"
                        data-type="<%= produit != null ? "produit" : "plat"  %>"
                           data-id="<%= produit != null ? produit.getId() : plat.getId() %>">
                        <label class="custom-control-label" for="product<%= i %>"></label>
                    </div>
                </div>
                <div class="col-auto">
                    <% if (imgFile != null && !imgFile.isEmpty()) { %>
                        <img src="<%=request.getContextPath()%>/blok/images/<%= imgFile %>" alt="<%= nomAffiche %>" class="product-img">
                    <% } else { %>
                        <div class="product-img d-flex align-items-center justify-content-center bg-light border">?</div>
                    <% } %>
                </div>
                <div class="col">
                    <h5 class="product-title"><%= nomAffiche %></h5>
                    <p class="product-description"><%= description %></p>
                    <div class="justify-content-between align-items-center">
                        <span class="product-price"><%= prix %></span> HTG
                        <div class="quantity-control">
                            <button class="quantity-btn decrease"><i class="fe fe-minus fe-16"></i></button>
                            <span class="quantity-value">1</span>
                            <button class="quantity-btn increase"><i class="fe fe-plus fe-16"></i></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<%
        }
    } else {
%>
    <p>Aucun plat disponible.</p>
<%
    }
%>
                                
                            </div>
                        </div>
                    </div>
                    
                    <!-- Formulaire client -->
                    <div class="col-lg-6">
                        <div class="form-section">
                            <h4 class="section-title"><i class="fas fa-user-tie mr-2"></i>Donnees du Client</h4>
                            
                        <% if (canPlaceClientOrders) { %>
    <!-- Nouveau select -->
    <div class="form-group">
        <label for="orderForSelf" class="font-weight-600">Cette commande est pour vous ?</label>
        <select class="form-control custom-select" id="orderForSelf">
            <option value="yes">Oui</option>
            <option value="no">Non</option>
        </select>
    </div>

    <div id="existingClientSection" style="display:none;">
        <div class="form-group">
            <label for="existingClient" class="font-weight-600">Sélectionner un client</label>
            <select class="form-control custom-select" id="existingClient">
                <option value="">-- Sélectionnez un client --</option>
                <%
                    List<Utilisateur> cLients = (List<Utilisateur>) request.getAttribute("cLients");
                    if (cLients != null && !cLients.isEmpty()) {
                        for (Utilisateur client : cLients) {
                %>
                    <option value="<%= client.getId() %>"><%= client.getNom() %> <%= client.getPrenom() %> (<%= client.getEmail() %>)</option>
                <%
                        }
                    } else {
                %>
                    <option value="">Aucun client disponible</option>
                <%
                    }
                %>
            </select>
        </div>
    </div>
<% } else { %>
    <input type="hidden" id="existingClient" value="<%= currentUser != null ? currentUser.getId() : "" %>">
    <div class="alert alert-info">
        Vous passez une commande pour vous-même.
    </div>
<% } %>

                            <div id="newClientSection" style="display: none;">
                                <div class="form-row">
                                    <div class="form-group col-md-6">
                                        <label for="clientLastName" class="font-weight-600">Nom Complet*</label>
                                        <input type="text" class="form-control" id="clientLastName" placeholder="Nom">
                                    </div>
                                </div>
                                
                            </div>
                            
                            <div class="form-group">
                                <label for="clientPreferences" class="font-weight-600"><i class="fas fa-clipboard-list mr-1"></i>Notes de commande</label>
                                <textarea class="form-control" id="clientPreferences" rows="3" placeholder="Ajoutez les notes et préférences du client ici..."></textarea>
                            </div>
                            <!-- Placez ceci avant la fermeture du body -->
						<form id="formCommande" action="CommandeServlet?action=ajouter" method="POST" style="display:none;">
						    <input type="hidden" name="clientId" id="clientIdField">
						    <input type="hidden" name="numeroCommande" id="numeroCommande">
						    <input type="hidden" name="estCredit" id="estCredit" value="yes">
						    <input type="hidden" name="dateCommande" value="<%= new java.sql.Timestamp(System.currentTimeMillis()) %>">
						    <input type="hidden" name="montantTotal" id="montantTotal">
						    <input type="hidden" name="modePaiement" value="NON_PAYE">
						    <input type="hidden" name="statutCommande" value="EN_ATTENTE">
						    <input type="hidden" name="statutPaiement" value="NON_PAYE">
						     <input type="hidden" name="whoIsOrdering" id="whoIsOrdering" value="<%= canPlaceClientOrders ? "byStaff" : "notByStaff" %>">
						    <input type="hidden" name="montantPaye" value="0">
						    <input type="hidden" name="notes" id="notesCommande">
						    
						    <div id="detailsCommande"></div>
						</form>
                        </div>
           
                        
                        <div class="total-section">
                        
                            <hr>
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="total-label">Total:</span>
                                <div>
                                <span class="total-amount" id="totalAmount">0.00</span> HTG
                                </div>
                            </div>
                            
                            <button class="btn btn-outline-primary btn-block mt-4 py-2" id="btnConfirmerCommande">
                                <i class="fas fa-check-circle mr-2"></i>Confirmer la commande
                            </button>
                            
                            
                        </div>
                    </div>
                </div>
            </div>
   </div>
</main>



<jsp:include page="footer.jsp" />
 <script>
$(document).ready(function() {
    // Gestion affichage section client existant selon choix "orderForSelf"
    $('#orderForSelf').change(function() {
        if ($(this).val() === 'yes') {
            $('#existingClientSection').slideUp();
            $('#whoIsOrdering').val('notByStaff');
        } else {
            $('#existingClientSection').slideDown();
            $('#whoIsOrdering').val('byStaff');
        }
    });

    // Trigger au chargement pour bien initialiser l'affichage
    $('#orderForSelf').trigger('change');

    // Augmenter quantité
    $('.increase').click(function() {
        var quantityElement = $(this).siblings('.quantity-value');
        var currentValue = parseInt(quantityElement.text());
        quantityElement.text(currentValue + 1);
        updateOrderSummary();
    });

    // Diminuer quantité
    $('.decrease').click(function() {
        var quantityElement = $(this).siblings('.quantity-value');
        var currentValue = parseInt(quantityElement.text());
        if (currentValue > 1) {
            quantityElement.text(currentValue - 1);
            updateOrderSummary();
        }
    });

    // Sélection des produits cochés
    $('.product-check').change(function() {
        $(this).closest('.product-card').toggleClass('product-selected', this.checked);
        updateOrderSummary();
    });

    // Fonction pour mettre à jour le total (sans taxe ni frais de livraison)
    function updateOrderSummary() {
        var total = 0;
        $('.product-check:checked').each(function() {
            var productCard = $(this).closest('.product-card');
            var priceText = productCard.find('.product-price').text().trim();
            var price = parseFloat(priceText.replace(',', '.'));
            var quantity = parseInt(productCard.find('.quantity-value').text());
            if (!isNaN(price) && !isNaN(quantity)) {
                total += price * quantity;
            }
        });
        $('#totalAmount').text(total.toFixed(2));
    }

    // Recherche de produits
    $('#productSearch').keyup(function() {
        var searchText = $(this).val().toLowerCase();
        $('.product-card').each(function() {
            var productName = $(this).find('.product-title').text().toLowerCase();
            var productDesc = $(this).find('.product-description').text().toLowerCase();
            if (productName.indexOf(searchText) === -1 && productDesc.indexOf(searchText) === -1) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    });

    // Initialiser le total au chargement
    updateOrderSummary();


    // Gestion de la confirmation de commande
    $('#btnConfirmerCommande').click(function(e) {
        e.preventDefault();

        let clientId;

        // Détermination clientId selon choix "Cette commande est pour vous ?"
        if ($('#orderForSelf').val() === 'yes') {
            // Commande pour soi-même : on récupère l'ID du user connecté depuis JSP
            clientId = '<%= currentUser != null ? currentUser.getId() : "" %>';
            if (!clientId) {
                alert('Impossible de récupérer votre identifiant utilisateur. Veuillez vous reconnecter.');
                return;
            }
        } else {
            // Commande pour un client existant
            clientId = $('#existingClient').val();
            if (!clientId) {
                alert('Veuillez sélectionner un client existant.');
                return;
            }
        }

        // Récupération des notes
        const notes = $('#clientPreferences').val();

        // Préparation des items de la commande
        const items = [];
        let montantTotal = 0;

        $('.product-check:checked').each(function() {
            const productCard = $(this).closest('.product-card');
            const type = $(this).data('type');
            const id = $(this).data('id');
            const nom = productCard.find('.product-title').text().trim();
            const prixText = productCard.find('.product-price').text().trim();
            const prix = parseFloat(prixText.replace(',', '.'));
            const quantite = parseInt(productCard.find('.quantity-value').text());
            const sousTotal = prix * quantite;

            montantTotal += sousTotal;

            items.push({
                type: type,
                id: id,
                nom: nom,
                prix: prix,
                quantite: quantite,
                sousTotal: sousTotal
            });
        });

        if (items.length === 0) {
            alert('Veuillez sélectionner au moins un produit.');
            return;
        }

        // Génération numéro commande
        const numeroCommande = "CMD-" + Date.now() + "-" + Math.floor(1000 + Math.random()*9000);

        // Remplissage formulaire caché
        $('#clientIdField').val(clientId);
        $('#numeroCommande').val(numeroCommande);
        $('#montantTotal').val(montantTotal.toFixed(2));
        $('#notesCommande').val(notes);

        // Ajout des détails de commande
        const detailsContainer = $('#detailsCommande');
        detailsContainer.empty();

        items.forEach(function(item) {
            detailsContainer.append(
                '<input type="hidden" name="quantite[]" value="' + item.quantite + '">' +
                '<input type="hidden" name="prixUnitaire[]" value="' + item.prix.toFixed(2) + '">' +
                '<input type="hidden" name="sousTotal[]" value="' + item.sousTotal.toFixed(2) + '">' +
                '<input type="hidden" name="notesDetail[]" value="">'
            );

            if (item.type === 'plat') {
                detailsContainer.append(
                    '<input type="hidden" name="platId[]" value="' + item.id + '">' +
                    '<input type="hidden" name="produitId[]" value="">'
                );
            } else {
                detailsContainer.append(
                    '<input type="hidden" name="platId[]" value="">' +
                    '<input type="hidden" name="produitId[]" value="' + item.id + '">'
                );
            }
        });

     // Affichage des valeurs dans la console pour debug
        let formData = $('#formCommande').serializeArray();
        console.log("Tableau brut serializeArray:", formData);

        // Convertir en objet clé/valeur
        let formObj = {};
        formData.forEach(function(item) {
            if (formObj[item.name]) {
                // si plusieurs valeurs pour le même champ (ex: platId[], produitId[])
                if (!Array.isArray(formObj[item.name])) {
                    formObj[item.name] = [formObj[item.name]];
                }
                formObj[item.name].push(item.value);
            } else {
                formObj[item.name] = item.value;
            }
        });
        //console.log("Objet JSON formData:", formObj);

        // Si tu veux carrément voir ça formaté
        //console.log("JSON stringifié:\n", JSON.stringify(formObj, null, 2));


        // Soumission du formulaire
        $('#formCommande').submit();
    });

});
</script>
 