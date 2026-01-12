        // Variables globales
        let menuData = null;
        let currentRayon = null;
        let currentCategory = null;
        let currentSousCategory = null;
        let filteredCategories = [];
        let filteredSubcategories = [];
        let filteredProducts = [];
        let orderItems = [];
        let totalAmount = 0;
        
        // Couleurs disponibles
        const productColors = [
            'bg-red', 'bg-green', 'bg-blue', 'bg-orange', 'bg-purple', 
            'bg-dark-blue', 'bg-yellow', 'bg-teal', 'bg-pink', 'bg-cyan',
            'bg-brown', 'bg-indigo', 'bg-lime', 'bg-deep-orange', 
            'bg-deep-purple', 'bg-light-blue', 'bg-light-green'
        ];
        
        // Base URL pour les images
        const baseUrl = 'http://localhost:8080';
        
        // Fonction pour échapper les guillemets simples
        function escapeSingleQuotes(str) {
            if (!str) return '';
            return str.replace(/'/g, "\\'");
        }
        
        // Fonction pour formater l'heure
        function formatTime(date) {
            let hours = date.getHours();
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12;
            hours = hours ? hours : 12;
            return hours + ':' + minutes + ' ' + ampm;
        }
        
        // Charger les données du menu
        function loadMenuData() {
            $('#state-message').text('Chargement du menu...');
            
            $.ajax({
                url: baseUrl + '/spot69/api?action=getmenu',
                method: 'GET',
                dataType: 'json',
                success: function(data) {
                    if (data.status === 'ok') {
                        menuData = data.data.rayons;
                        displayRayonsInSidebar();
                        displayRayonContent();
                        $('#state-message').hide();
                    } else {
                        $('#state-message').text('Erreur de chargement du menu');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Erreur lors du chargement du menu:', error);
                    // Données de secours
/*                     menuData = [
                        {
                            "id": 3,
                            "nom": "Tabac",
                            "description": "",
                            "imageUrl": "/spot69/images/default.png",
                            "categories": [
                                {
                                    "id": 64,
                                    "nom": "Tabac et Dérivés",
                                    "description": "Tabac et produits dérivés",
                                    "imageUrl": "/spot69/images/categories/default.png",
                                    "sousCategories": []
                                },
                                {
                                    "id": 63,
                                    "nom": "Produits du Tabac",
                                    "description": "Catégorie des produits du tabac",
                                    "imageUrl": "/spot69/images/categories/6ba482d7-99e9-4b22-8462-501e9d7e8423.jpg",
                                    "sousCategories": [
                                        {
                                            "id": 64,
                                            "nom": "Tabac et Dérivés",
                                            "description": "Tabac et produits dérivés",
                                            "imageUrl": "/spot69/images/categories/default.png",
                                            "plats": [
                                                {
                                                    "id": 221,
                                                    "nom": "test",
                                                    "prix": 123,
                                                    "qtePoints": 0,
                                                    "image": "/spot69/images/plats/default.png"
                                                },
                                                {
                                                    "id": 204,
                                                    "prix": 0,
                                                    "qtePoints": 5,
                                                    "image": "/spot69/images/plats/default.png"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ];
 */                    
                    displayRayonsInSidebar();
                    displayRayonContent();
                    $('#state-message').hide();
                }
            });
        }
        
        // Afficher les rayons dans la barre latérale
        function displayRayonsInSidebar() {
            const container = $('#rayons-sidebar');
            container.empty();
            
            if (!menuData || menuData.length === 0) {
                container.append('<div class="tool-btn"><i class="fas fa-exclamation-circle"></i><span>Aucun rayon</span></div>');
                return;
            }
            
            // Ajouter une séparation
            container.append('<div style="height: 1px; background-color: #444; margin: 5px 0;"></div>');
            
            // Sélectionner le premier rayon par défaut
            currentRayon = menuData[0];
            
            // Ajouter les rayons
            menuData.forEach((rayon, index) => {
                const toolBtn = $('<div class="tool-btn rayon-btn" onclick="selectRayon(' + rayon.id + ')">' +
                    '<i class="fas fa-' + getRayonIcon(rayon.nom) + '"></i>' +
                    '<span>' + (rayon.nom.length > 8 ? rayon.nom.substring(0, 8) + '...' : rayon.nom) + '</span>' +
                    '</div>');
                
                // Calculer le nombre de catégories
                const catCount = rayon.categories ? rayon.categories.length : 0;
                if (catCount > 0) {
                    toolBtn.append('<span class="rayon-badge">' + catCount + '</span>');
                }
                
                // Marquer le premier rayon comme sélectionné
                if (index === 0) {
                    toolBtn.addClass('active');
                }
                
                container.append(toolBtn);
            });
        }
        
        // Sélectionner un rayon
        function selectRayon(rayonId) {
            const rayon = menuData.find(r => r.id === rayonId);
            if (!rayon) return;
            
            currentRayon = rayon;
            
            // Mettre à jour la classe active
            $('.tool-btn.rayon-btn').removeClass('active');
            $('[onclick="selectRayon(' + rayonId + ')"]').addClass('active');
            
            // Réinitialiser les filtres
            $('#category-search').val('');
            $('#subcategory-search').val('');
            $('#product-search').val('');
            
            // Afficher le contenu du rayon
            displayRayonContent();
        }
        
        // Afficher le contenu du rayon sélectionné
        function displayRayonContent() {
            if (!currentRayon) return;
            
            // Mettre à jour les sous-titres
            $('#categories-subheader').html('Rayon: <strong>' + currentRayon.nom + '</strong>');
            $('#subcategories-subheader').text('Sélectionnez une catégorie d\'abord');
            $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            
            // Afficher les catégories
            displayCategories(currentRayon.categories);
            
            // Vider les sous-catégories et produits
            clearSubcategories();
            clearProducts();
        }
        
        // Afficher les catégories (MODIFIÉ - sans description, icône centrée)
        function displayCategories(categories) {
            const container = $('#categories-container');
            container.empty();
            
            if (!categories || categories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune catégorie disponible</div>');
                filteredCategories = [];
                return;
            }
            
            // Stocker les catégories pour la recherche
            filteredCategories = categories;
            
            // Réinitialiser la sélection
            currentCategory = null;
            
            categories.forEach((category, index) => {
                const colorClass = productColors[index % productColors.length];
                const imageUrl = category.imageUrl && category.imageUrl !== '/spot69/images/categories/default.png' 
                    ? baseUrl + category.imageUrl 
                    : null;
                
                // Obtenir l'icône pour la catégorie
                const categoryIcon = getCategoryIcon(category.nom);
                
                let cardHtml = '<div class="category-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Icône centrée (plus grande)
                cardHtml += '<div class="category-icon">';
                cardHtml += '<i class="fas fa-' + categoryIcon + '"></i>';
                cardHtml += '</div>';
                
                // Nom de la catégorie seulement (pas de description)
                cardHtml += '<div class="card-title">' + category.nom + '</div>';
                
                cardHtml += '</div>';
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- C' + category.id + ' -->';
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-category-id', category.id);
                card.attr('data-category-name', category.nom.toLowerCase());
                card.attr('data-category-description', (category.description || '').toLowerCase());
                
                card.click(function() {
                    selectCategory(category.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une catégorie
        function selectCategory(categoryId) {
            if (!currentRayon || !currentRayon.categories) return;
            
            const category = currentRayon.categories.find(c => c.id === categoryId);
            if (!category) return;
            
            currentCategory = category;
            
            // Mettre à jour la sélection visuelle
            $('.category-card').removeClass('selected-card');
            $('.category-card[data-category-id="' + categoryId + '"]').addClass('selected-card');
            
            // Mettre à jour le sous-titre
            $('#subcategories-subheader').html('Catégorie: <strong>' + category.nom + '</strong>');
            
            // Afficher les sous-catégories
            displaySubcategories(category.sousCategories);
            
            // Vider les produits
            clearProducts();
        }
        
        // Afficher les sous-catégories (MODIFIÉ - sans description, icône centrée)
        function displaySubcategories(subcategories) {
            const container = $('#subcategories-container');
            container.empty();
            
            // Réinitialiser la sélection
            currentSousCategory = null;
            
            if (!subcategories || subcategories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune sous-catégorie disponible</div>');
                filteredSubcategories = [];
                
                // Si pas de sous-catégories, afficher les produits de la catégorie directement
                if (currentCategory && currentCategory.plats && currentCategory.plats.length > 0) {
                    displayProducts(currentCategory.plats);
                    $('#products-subheader').html('Catégorie: <strong>' + currentCategory.nom + '</strong>');
                } else {
                    clearProducts();
                }
                return;
            }
            
            // Stocker les sous-catégories pour la recherche
            filteredSubcategories = subcategories;
            
            subcategories.forEach((subcat, index) => {
                const colorClass = productColors[(index + 3) % productColors.length];
                const imageUrl = subcat.imageUrl && subcat.imageUrl !== '/spot69/images/categories/default.png' 
                    ? baseUrl + subcat.imageUrl 
                    : null;
                
                // Obtenir l'icône pour la sous-catégorie
                const subcategoryIcon = getCategoryIcon(subcat.nom);
                
                let cardHtml = '<div class="subcategory-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Icône centrée (plus grande)
                cardHtml += '<div class="subcategory-icon">';
                cardHtml += '<i class="fas fa-' + subcategoryIcon + '"></i>';
                cardHtml += '</div>';
                
                // Nom de la sous-catégorie seulement (pas de description)
                cardHtml += '<div class="card-title">' + subcat.nom + '</div>';
                
                cardHtml += '</div>';
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- SC' + subcat.id + ' -->';
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-subcategory-id', subcat.id);
                card.attr('data-subcategory-name', subcat.nom.toLowerCase());
                card.attr('data-subcategory-description', (subcat.description || '').toLowerCase());
                
                card.click(function() {
                    selectSubcategory(subcat.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une sous-catégorie
        function selectSubcategory(subcategoryId) {
            if (!currentCategory || !currentCategory.sousCategories) return;
            
            const subcategory = currentCategory.sousCategories.find(sc => sc.id === subcategoryId);
            if (!subcategory) return;
            
            currentSousCategory = subcategory;
            
            // Mettre à jour la sélection visuelle
            $('.subcategory-card').removeClass('selected-card');
            $('.subcategory-card[data-subcategory-id="' + subcategoryId + '"]').addClass('selected-card');
            
            // Mettre à jour le sous-titre
            $('#products-subheader').html('Sous-catégorie: <strong>' + subcategory.nom + '</strong>');
            
            // Afficher les produits
            if (subcategory.plats && subcategory.plats.length > 0) {
                displayProducts(subcategory.plats);
            } else {
                clearProducts();
            }
        }
        
        // Afficher les produits (inchangé - garde la description)
        function displayProducts(plats) {
            const container = $('#products-container');
            container.empty();
            
            if (!plats || plats.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Aucun produit disponible</div>');
                filteredProducts = [];
                return;
            }
            
            // Stocker les produits pour la recherche
            filteredProducts = plats;
            
            plats.forEach((plat, index) => {
                const colorClass = productColors[(index + 5) % productColors.length];
                const nom = plat.nom || (plat.produit ? plat.produit.nom : 'Produit ' + plat.id);
                const description = plat.description || '';
                let prix = plat.prix;
                
                if (prix === 0 && plat.produit) {
                    prix = plat.produit.prix;
                }
                
                let points = plat.qtePoints || 0;
                if (points === 0 && plat.produit) {
                    points = plat.produit.qtePoints || 0;
                }
                
                // Image du produit
                let imageUrl = null;
                if (plat.image && plat.image !== '/spot69/images/plats/default.png') {
                    imageUrl = baseUrl + plat.image;
                } else if (plat.produit && plat.produit.imageUrl && plat.produit.imageUrl !== '/spot69/images/produits/default.png') {
                    imageUrl = baseUrl + plat.produit.imageUrl;
                }
                
                // Obtenir l'icône pour le produit
                const productIcon = getProductIcon(nom);
                
                const escapedNom = escapeSingleQuotes(nom);
                
                // Créer la carte produit
                const productItem = $('<div class="product-item"></div>');
                let cardHtml = '<div class="product-card ';
                
                if (imageUrl) {
                    cardHtml += 'card-with-image" style="background-image: url(\'' + imageUrl + '\')">';
                    cardHtml += '<div class="card-overlay"></div>';
                } else {
                    cardHtml += colorClass + '">';
                }
                
                cardHtml += '<div class="card-content">';
                
                // Ajouter l'icône du produit (plus grande si pas d'image)
                if (!imageUrl) {
                    cardHtml += '<div style="text-align: center; margin-top: 10px; margin-bottom: 5px; font-size: 1.8rem; opacity: 0.9;">';
                    cardHtml += '<i class="fas fa-' + productIcon + '"></i>';
                    cardHtml += '</div>';
                }
                
                cardHtml += '<div class="card-title">' + nom + '</div>';
                
                // Description pour les produits uniquement
                if (description && description.trim() !== '') {
                    cardHtml += '<div class="card-description">' + description + '</div>';
                }
                
                cardHtml += '</div>';
                
                // Prix
                if (prix > 0) {
                    cardHtml += '<span class="card-price">' + prix.toFixed(2) + '</span>';
                }
                
                // Points
                if (points > 0) {
                    cardHtml += '<span class="card-points">' + points + ' pts</span>';
                }
                
                // Code en commentaire dans le HTML
                cardHtml += '<!-- P' + plat.id + ' -->';
                
                // Icône d'image si présente
                if (imageUrl) {
                    cardHtml += '<span class="card-image-icon"><i class="fas fa-image"></i></span>';
                }
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-product-id', plat.id);
                card.attr('data-product-name', nom.toLowerCase());
                card.attr('data-product-description', description.toLowerCase());
                card.attr('data-product-price', prix);
                
                card.click(function() {
                    addToOrder(plat.id, nom, prix);
                });
                
                productItem.append(card);
                container.append(productItem);
            });
        }
        
        // Vider les sous-catégories
        function clearSubcategories() {
            $('#subcategories-container').empty();
            $('#subcategories-container').append('<div class="text-center py-5" style="color: #aaa;">Sélectionnez une catégorie d\'abord</div>');
            filteredSubcategories = [];
        }
        
        // Vider les produits
        function clearProducts() {
            $('#products-container').empty();
            $('#products-container').append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Sélectionnez une sous-catégorie d\'abord</div>');
            filteredProducts = [];
        }
        
        // Fonctions de recherche
        function searchCategories() {
            const searchTerm = $('#category-search').val().toLowerCase();
            const container = $('#categories-container');
            
            if (!searchTerm) {
                // Afficher toutes les catégories
                $('.category-card').show();
                return;
            }
            
            $('.category-card').each(function() {
                const name = $(this).attr('data-category-name') || '';
                const description = $(this).attr('data-category-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        function searchSubcategories() {
            const searchTerm = $('#subcategory-search').val().toLowerCase();
            const container = $('#subcategories-container');
            
            if (!searchTerm) {
                // Afficher toutes les sous-catégories
                $('.subcategory-card').show();
                return;
            }
            
            $('.subcategory-card').each(function() {
                const name = $(this).attr('data-subcategory-name') || '';
                const description = $(this).attr('data-subcategory-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        function searchProducts() {
            const searchTerm = $('#product-search').val().toLowerCase();
            const container = $('#products-container');
            
            if (!searchTerm) {
                // Afficher tous les produits
                $('.product-item').show();
                return;
            }
            
            $('.product-item').each(function() {
                const card = $(this).find('.product-card');
                const name = card.attr('data-product-name') || '';
                const description = card.attr('data-product-description') || '';
                
                if (name.includes(searchTerm) || description.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        // Obtenir l'icône pour un rayon
        function getRayonIcon(rayonNom) {
            const icons = {
                'Boisson': 'wine-glass',
                'Nourritures': 'utensils',
                'Tabac': 'smoking',
                'Vins': 'wine-bottle',
                'Champagnes': 'wine-bottle',
                'Jus': 'glass-whiskey',
                'Alcoolisées': 'wine-bottle',
                'Salades': 'leaf',
                'Burgers': 'hamburger',
                'Sandwiches': 'bread-slice',
                'Pizzas': 'pizza-slice',
                'Pâtes': 'utensil-spoon',
                'Poulets': 'drumstick-bite',
                'Fruits de Mer': 'fish',
                'Snacks': 'cookie-bite',
                'Soupes': 'bowl-hot',
                'Combos': 'concierge-bell',
                'Spécialités': 'star',
                'Entrées': 'app-store-ios',
                'Accompagnements': 'pepper-hot',
                'Plats du Jour': 'calendar-day',
                'Cafés': 'coffee',
                'Thés': 'mug-hot',
                'Bières': 'beer',
                'Énergétiques': 'bolt',
                'Gazeuses': 'glass-cheers',
                'Eau': 'tint',
                'Sans Alcool': 'glass-martini-alt',
                'Vin': 'wine-glass-alt',
                'Champagne': 'wine-bottle',
                'Vodka': 'cocktail',
                'Wisky': 'whiskey-glass',
                'Cocktail': 'cocktail',
                'Rhum': 'glass-whiskey'
            };
            
            for (const key in icons) {
                if (rayonNom.includes(key)) {
                    return icons[key];
                }
            }
            
            return 'tag';
        }
        
        // Obtenir l'icône pour un produit
        function getProductIcon(productName) {
            const icons = {
                'cola': 'glass',
                'coca': 'glass',
                'pepsi': 'glass',
                'fanta': 'glass',
                'sprite': 'glass',
                'jus': 'glass-whiskey',
                'eau': 'tint',
                'café': 'coffee',
                'thé': 'mug-hot',
                'bière': 'beer',
                'vin': 'wine-glass-alt',
                'champagne': 'wine-bottle',
                'vodka': 'cocktail',
                'whisky': 'whiskey-glass',
                'rhum': 'glass-whiskey',
                'cocktail': 'cocktail',
                'pizza': 'pizza-slice',
                'burger': 'hamburger',
                'sandwich': 'bread-slice',
                'salade': 'leaf',
                'poulet': 'drumstick-bite',
                'steak': 'drumstick-bite',
                'poisson': 'fish',
                'frites': 'utensils',
                'pâtes': 'utensil-spoon',
                'soupe': 'bowl-hot',
                'dessert': 'birthday-cake',
                'glace': 'ice-cream',
                'gâteau': 'birthday-cake',
                'cigarette': 'smoking',
                'tabac': 'smoking',
                'chicha': 'smoking'
            };
            
            const lowerName = productName.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key)) {
                    return icons[key];
                }
            }
            
            return 'utensils';
        }
        
        // Obtenir l'icône pour une catégorie
        function getCategoryIcon(categoryName) {
            const icons = {
                'tabac': 'smoking',
                'cigarette': 'smoking',
                'cigar': 'smoking',
                'produit': 'box',
                'boisson': 'wine-bottle',
                'vin': 'wine-glass-alt',
                'bière': 'beer',
                'alcool': 'wine-bottle',
                'nourriture': 'utensils',
                'repas': 'utensils',
                'plat': 'utensil-spoon',
                'entrée': 'apple-alt',
                'dessert': 'birthday-cake',
                'salade': 'leaf',
                'burger': 'hamburger',
                'pizza': 'pizza-slice',
                'sandwich': 'bread-slice',
                'poulet': 'drumstick-bite',
                'viande': 'drumstick-bite',
                'poisson': 'fish',
                'fruits': 'apple-alt',
                'légumes': 'carrot',
                'café': 'coffee',
                'thé': 'mug-hot',
                'jus': 'glass-whiskey',
                'eau': 'tint',
                'soda': 'glass',
                'cocktail': 'cocktail',
                'spiritueux': 'whiskey-glass',
                'rhum': 'glass-whiskey',
                'vodka': 'cocktail',
                'whisky': 'whiskey-glass',
                'champagne': 'wine-bottle',
                'snack': 'cookie-bite',
                'apéritif': 'wine-glass',
                'fromage': 'cheese',
                'pain': 'bread-slice',
                'pâtes': 'utensil-spoon',
                'riz': 'utensil-spoon',
                'soupe': 'bowl-hot',
                'frites': 'french-fries',
                'accompagnement': 'pepper-hot',
                'sauce': 'pepper-hot',
                'épice': 'pepper-hot',
                'fruit de mer': 'fish',
                'crustacé': 'fish',
                'coquillage': 'fish',
                'glace': 'ice-cream',
                'gâteau': 'birthday-cake',
                'patisserie': 'birthday-cake',
                'viennoiserie': 'croissant',
                'chocolat': 'candy-cane',
                'bonbon': 'candy-cane',
                'confiserie': 'candy-cane',
                'lait': 'glass-milk',
                'produit laitier': 'glass-milk',
                'yaourt': 'glass-milk',
                'œuf': 'egg',
                'céréale': 'seedling',
                'légumineuse': 'seedling',
                'noix': 'seedling',
                'graine': 'seedling',
                'huile': 'flask',
                'vinaigre': 'flask',
                'condiment': 'pepper-hot',
                'conserves': 'jar',
                'surgelé': 'snowflake',
                'frais': 'leaf',
                'bio': 'leaf',
                'traiteur': 'concierge-bell',
                'emporter': 'shopping-bag',
                'livraison': 'motorcycle',
                'menu': 'concierge-bell',
                'formule': 'concierge-bell',
                'promotion': 'percent',
                'nouveauté': 'star',
                'spécialité': 'star',
                'maison': 'home',
                'tradition': 'history',
                'régional': 'map-marker-alt',
                'international': 'globe',
                'asiatique': 'globe-asia',
                'italien': 'pizza-slice',
                'français': 'flag',
                'mexicain': 'pepper-hot',
                'indien': 'pepper-hot',
                'japonais': 'fish',
                'chinois': 'bowl-rice',
                'thaï': 'lemon',
                'marocain': 'mortar-pestle',
                'libanais': 'leaf',
                'grec': 'lemon',
                'espagnol': 'pepper-hot',
                'portugais': 'fish'
            };
            
            const lowerName = categoryName.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key)) {
                    return icons[key];
                }
            }
            
            // Icônes par défaut selon le type
            if (lowerName.includes('sous')) return 'folder';
            if (lowerName.includes('catégorie')) return 'folder-open';
            if (lowerName.includes('rayon')) return 'tags';
            
            return 'tag'; // Icône par défaut
        }
        
        // Ajouter un produit à la commande
        function addToOrder(productId, productName, price) {
            // Effet visuel
            $('.product-card[data-product-id="' + productId + '"]')
                .css('transform', 'scale(0.95)')
                .animate({transform: 'scale(1)'}, 300);
            
            // Vérifier si le produit est déjà dans la commande
            const existingItem = orderItems.find(item => item.id === productId);
            
            if (existingItem) {
                existingItem.quantity += 1;
                existingItem.total = existingItem.quantity * price;
            } else {
                orderItems.push({
                    id: productId,
                    name: productName,
                    price: price,
                    quantity: 1,
                    total: price
                });
            }
            
            // Mettre à jour l'affichage
            updateOrderDisplay();
        }
        
        // Mettre à jour l'affichage de la commande
        function updateOrderDisplay() {
            const container = $('#order-items');
            container.empty();
            
            totalAmount = 0;
            
            if (orderItems.length === 0) {
                container.append('<tr><td colspan="4" class="text-center text-muted py-3">Aucun article ajouté</td></tr>');
            } else {
                orderItems.forEach(item => {
                    totalAmount += item.total;
                    
                    const row = $('<tr>' +
                        '<td class="text-muted">' + formatTime(new Date()) + '</td>' +
                        '<td>' + item.quantity + '</td>' +
                        '<td>' + item.name + '</td>' +
                        '<td class="text-right">' + item.total.toFixed(2) + '</td>' +
                        '</tr>');
                    
                    container.append(row);
                });
            }
            
            // Mettre à jour le total
            $('#total-amount').text(totalAmount.toFixed(2));
        }
        
        // Fonction pour mettre à jour l'horloge
        function updateClock() {
            const now = new Date();
            let hours = now.getHours();
            const minutes = String(now.getMinutes()).padStart(2, '0');
            const ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12;
            hours = hours ? hours : 12;
            $('#clock').text(hours + ':' + minutes + ' ' + ampm);
            
            // Mettre à jour la date
            const options = { day: 'numeric', month: 'short', year: 'numeric' };
            const dateStr = now.toLocaleDateString('fr-FR', options);
            $('#current-date').text(dateStr);
        }
        
        // Fonctions pour les boutons
        function goBack() {
            // Retour à l'écran précédent
            if (currentSousCategory) {
                currentSousCategory = null;
                $('.subcategory-card').removeClass('selected-card');
                clearProducts();
                $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            } else if (currentCategory) {
                currentCategory = null;
                $('.category-card').removeClass('selected-card');
                clearSubcategories();
                clearProducts();
                $('#subcategories-subheader').text('Sélectionnez une catégorie d\'abord');
                $('#products-subheader').text('Sélectionnez une sous-catégorie d\'abord');
            }
        }
        
        function showSearch() {
            // Focus sur la recherche de produits
            $('#product-search').focus();
        }
        
        function showPLU() {
            alert('Fonctionnalité PLU à implémenter');
        }
        
        function addQuantity() {
            alert('Modification de quantité à implémenter');
        }
        
        function editPrice() {
            alert('Modification de prix à implémenter');
        }
        
        function showDetails() {
            alert('Détails à implémenter');
        }
        
        function showModifiers() {
            alert('Modificateurs à implémenter');
        }
        
        function showActions() {
            alert('Actions à implémenter');
        }
        
        function changeTable() {
            alert('Changement de table à implémenter');
        }
        
        function showFavorites() {
            alert('Favoris à implémenter');
        }
        
        // Initialisation
        $(document).ready(function() {
            // Mettre à jour l'horloge
            updateClock();
            setInterval(updateClock, 1000);
            
            // Charger les données du menu
            loadMenuData();
        });