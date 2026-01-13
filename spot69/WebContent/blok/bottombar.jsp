<!-- <div class="bottom-nav">
    <div class="user-profile">
        Espace réservé pour le profil utilisateur
    </div>

    <div class="category-scroll" id="bottom-categories">
        <div class="cat-btn active" onclick="showFavorites()">
            <i class="fas fa-star"></i>
            <span>Favorites</span>
        </div>
        <div class="cat-btn">
            <a href="MenuServlet?action=placer-commande" style="color: white; text-decoration: none;">
            	<i class="fas fa-glass-whiskey"></i>
                <span>BOARD</span>
            </a>
        </div>
        <div class="cat-btn">
            <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;">
            	<i class="fas fa-glass-whiskey"></i>
                <span>Produits</span>
            </a>
        </div>
        <div class="cat-btn">
            <a href="FactureServlet?action=lister" style="color: white; text-decoration: none;">
            	<i class="fas fa-truck"></i>
                <span>Achats</span>
            </a>
        </div>
        <div class="cat-btn">
            <a class="" href="MouvementStockServlet?action=lister" style="color: white; text-decoration: none;">
            <i class="fas fa-box-open"></i><br>
                <span class="">Inventaire</span>
            </a>
        </div>
        <div class="cat-btn">
                
                <a class="" href="CommandeServlet?action=caissiere-commandes-cashed" style="color: white; text-decoration: none;">
                    <i class="fas fa-birthday-cake"></i><br>
                    <span class="">Rapport</span>
                </a>
            </div>
        <div class="cat-btn bg-teal">
            <i class="fas fa-percent"></i>
            <span>Taux</span>
        </div>
    </div>
</div> -->

<!-- <div class="bottom-nav">
        <div class="user-profile">
            Espace réservé pour le profil utilisateur
        </div>

        <div class="category-scroll" id="bottom-categories">
            <div class="cat-btn" onclick="showFavorites()">
                <i class="fas fa-star"></i>
                <span>Favorites</span>
            </div>
            <div class="cat-btn">
                <i class="fas fa-glass-whiskey"></i>
                <a href="MenuServlet?action=placer-commande" style="color: white; text-decoration: none;">
                    <span>BOARD</span>
                </a>
            </div>
            <a href="MenuServlet?action=placer-commande"style="color: white; text-decoration: none;">
	            <div class="cat-btn">
	                <i class="fas fa-glass-whiskey"></i>
	                    <span>BOARD</span>
	            </div>
            </a>
            <a href="ProduitServlet?action=lister"style="color: white; text-decoration: none;">
	            <div class="cat-btn">
	                <i class="fas fa-glass-whiskey"></i>
	                    <span>Produits</span>
	            </div>
            </a>
            <div class="cat-btn">
                
                <a href="FactureServlet?action=lister" style="color: white; text-decoration: none;">
                    <i class="fas fa-truck"></i>
                    <span>Achats</span>
                </a>
            </div>
            <a href="FactureServlet?action=lister"style="color: white; text-decoration: none;">
	            <div class="cat-btn">
	                <i class="fas fa-truck"></i>
	                    <span>Achats</span>
	            </div>
            </a>
            <a href="MouvementStockServlet?action=lister"style="color: white; text-decoration: none;">
	            <div class="cat-btn">
	                <i class="fas fa-truck"></i>
	                    <span>Inventaire</span>
	            </div>
            </a>
            <div class="cat-btn"> 
                <a class="" href="CommandeServlet?action=caissiere-commandes-cashed" style="color: white; text-decoration: none;">
                    <i class="fas fa-receipt"></i>
                    <span class="">Rapport</span>
                </a>
            </div>
            <a href="CommandeServlet?action=caissiere-commandes-cashed"style="color: white; text-decoration: none;">
	            <div class="cat-btn">
	                <i class="fas fa-truck"></i>
	                    <span>Inventaire</span>
	            </div>
            </a>
            <div class="cat-btn bg-teal">
                <i class="fas fa-percent"></i>
                <span>Taux</span>
            </div>
        </div>
    </div> -->
    
    <div class="bottom-nav">
    <div class="category-scroll" id="bottom-categories">
        <!-- Favorites -->
        <div class="cat-btn" onclick="showFavorites()" title="Favorites">
            <i class="fas fa-star"></i>
            <span>Favorites</span>
        </div>
        
        <!-- BOARD -->
        <a href="MenuServlet?action=placer-commande" style="color: white; text-decoration: none;" title="Tableau de bord">
            <div class="cat-btn">
                <i class="fas fa-columns"></i>
                <span>BOARD</span>
            </div>
        </a>
        
        <!-- Produits -->
        <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;" title="Gestion des produits">
            <div class="cat-btn">
                <i class="fas fa-boxes"></i>
                <span>Produits</span>
            </div>
        </a>
        
        <!-- Achats -->
        <a href="FactureServlet?action=lister" style="color: white; text-decoration: none;" title="Historique des achats">
            <div class="cat-btn">
                <i class="fas fa-shopping-cart"></i>
                <span>Achats</span>
            </div>
        </a>
        
        <!-- Inventaire -->
        <a href="MouvementStockServlet?action=lister" style="color: white; text-decoration: none;" title="Gestion d'inventaire">
            <div class="cat-btn">
                <i class="fas fa-clipboard-list"></i>
                <span>Inventaire</span>
            </div>
        </a>
        
        <!-- Rapport -->
        <a href="CommandeServlet?action=caissiere-commandes-cashed" style="color: white; text-decoration: none;" title="Rapports et statistiques">
            <div class="cat-btn">
                <i class="fas fa-chart-bar"></i>
                <span>Rapport</span>
            </div>
        </a>
        
        <!-- Taux -->
        <div class="cat-btn bg-teal" title="Taux et pourcentages">
            <i class="fas fa-percentage"></i>
            <span>Taux</span>
        </div>
    </div>
</div>