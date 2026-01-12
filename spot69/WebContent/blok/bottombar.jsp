<div class="bottom-nav">
    <div class="user-profile">
        <!-- Espace réservé pour le profil utilisateur -->
    </div>

    <div class="category-scroll" id="bottom-categories">
        <div class="cat-btn active" onclick="showFavorites()">
            <i class="fas fa-star"></i>
            <span>Favorites</span>
        </div>
        <div class="cat-btn">
            <i class="fas fa-glass-whiskey"></i>
            <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;">
                <span>BOARD</span>
            </a>
        </div>
        <div class="cat-btn">
            <i class="fas fa-glass-whiskey"></i>
            <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;">
                <span>Produits</span>
            </a>
        </div>
        <div class="cat-btn">
            <i class="fas fa-truck"></i>
            <a href="FactureServlet?action=lister" style="color: white; text-decoration: none;">
                <span>Achats</span>
            </a>
        </div>
        <div class="cat-btn">
            <i class="fas fa-box-open"></i>
            <a class="" href="MouvementStockServlet?action=lister" style="color: white; text-decoration: none;">
                <span class="">Inventaire</span>
            </a>
        </div>
        <div class="cat-btn">
            <i class="fas fa-birthday-cake"></i>
            <span>Rapport</span>
        </div>
        <div class="cat-btn bg-teal">
            <i class="fas fa-percent"></i>
            <span>Taux</span>
        </div>
    </div>
</div>