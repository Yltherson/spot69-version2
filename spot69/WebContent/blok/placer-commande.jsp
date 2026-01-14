<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List,com.spot69.model.MenuCategorie,com.spot69.model.Utilisateur,java.math.BigDecimal"%>
<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
    Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LA DIVINITE DE DIEU</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="css/feather.css">
    
    <style>
        /* --- GLOBAL STYLES & VARIABLES --- */
        :root {
            --bg-dark: #1a1a1a;
            --bg-panel: #222222;
            --bg-ticket: #2b2e33;
            --text-grey: #a0a0a0;
            --border-color: #3e3e3e;
            
            /* Couleurs des produits */
            --color-red: #ef5350;
            --color-green: #4caf50;
            --color-blue: #42a5f5;
            --color-orange: #ffa726;
            --color-purple: #ab47bc;
            --color-dark-blue: #3f51b5;
            --color-dark-btn: #37474f;
            --color-dark-green: #388e3c;
            --color-yellow: #fdd835;
        }

        body {
            background-color: var(--bg-dark);
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            color: white;
            height: 100vh;
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }

        /* --- UTILITAIRES --- */
        .no-gutters { margin-right: 0; margin-left: 0; }
        .no-gutters > .col, .no-gutters > [class*="col-"] { padding-right: 0; padding-left: 0; }
        .full-height { height: 100%; }
        .text-xs { font-size: 0.7rem; }
        
        /* --- HEADER (TOP BAR) --- */
        .top-bar {
            background-color: #000;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 12px;
            border-bottom: 1px solid var(--border-color);
        }
        .top-icon {
            color: white;
            text-align: center;
            font-size: 0.7rem;
            margin: 0 6px;
            cursor: pointer;
        }
        .top-icon i { display: block; font-size: 0.9rem; margin-bottom: 2px; }
        .brand-logo { color: #ef5350; font-size: 1.3rem; font-weight: bold; }
        .sold { 
            color: #66bb6a; 
            font-size: 1.1rem; 
            font-weight: bold;
            padding: 0 5px;
        }

        /* --- MAIN LAYOUT --- */
        .main-workspace {
            flex: 1;
            display: flex;
            overflow: hidden;
        }

        /* --- LEFT TOOLBAR (Vertical) - Rayons --- */
        .left-tools {
            width: 90px;
            background-color: #111;
            display: flex;
            flex-direction: column;
            border-right: 1px solid var(--border-color);
            overflow-y: auto;
        }
        .rayon-btn {
            height: 70px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: #ccc;
            border-bottom: 1px solid #333;
            cursor: pointer;
            position: relative;
            padding: 4px;
            text-align: center;
            transition: all 0.3s ease;
        }
        .rayon-btn:hover { 
            background-color: #333; 
            color: white;
        }
        .rayon-btn i { 
            font-size: 1.1rem; 
            margin-bottom: 4px;
            color: #fdd835;
        }
        .rayon-btn span { 
            font-size: 0.6rem; 
            text-transform: uppercase;
            line-height: 1.1;
        }
        .rayon-btn.active { 
            background-color: #2a2a2a; 
            color: #fdd835;
            border-left: 3px solid #fdd835;
        }
        .rayon-badge {
            position: absolute;
            top: 4px;
            right: 4px;
            background-color: #ef5350;
            color: white;
            font-size: 0.45rem;
            padding: 1px 3px;
            border-radius: 3px;
            min-width: 14px;
            text-align: center;
        }
        .left-tools-header {
            padding: 8px 4px;
            background-color: #222;
            color: #fdd835;
            text-align: center;
            font-size: 0.75rem;
            font-weight: bold;
            border-bottom: 1px solid #444;
        }

        /* --- PRODUCT AREA --- */
        .product-area {
            flex: 1;
            padding: 4px;
            overflow-y: auto;
            background-color: var(--bg-panel);
            display: flex;
        }
        
        /* Sections pour catégories, sous-catégories et produits */
        .section-container {
            flex: 1;
            display: flex;
            flex-direction: column;
            max-width: 13%;
            min-width: 140px;
            border-right: 1px solid #444;
            overflow-y: auto;
        }
        
        .section-container:last-child {
            border-right: none;
        }
        
        /* Conteneur principal des produits */
        .products-main-container {
            flex: 3;
            display: flex;
            flex-direction: column;
            overflow-y: auto;
        }

        /* Style des cartes */
        .category-card, .subcategory-card, .product-card {
            height: 85px;
            margin: 3px;
            border-radius: 4px;
            padding: 5px;
            position: relative;
            cursor: pointer;
            transition: transform 0.1s, opacity 0.2s;
            color: white;
            font-weight: 500;
            font-size: 0.75rem;
            line-height: 1.1;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            width: 130px;
        }
        
        .category-card:hover, .subcategory-card:hover, .product-card:hover { 
            opacity: 0.9; 
            transform: translateY(-2px);
            box-shadow: 0 2px 5px rgba(0,0,0,0.3);
        }
        
        .category-card:active, .subcategory-card:active, .product-card:active { 
            transform: scale(0.98); 
        }
        
        /* Image dans les cartes */
        .card-image {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            opacity: 0.3;
            border-radius: 4px;
            z-index: 1;
        }
        
        .card-content {
            position: relative;
            z-index: 2;
            height: 100%;
            width: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
        }
        
        /* Style spécifique pour les cartes de catégories et sous-catégories */
        .category-card .card-content,
        .subcategory-card .card-content {
            padding: 6px 3px;
        }
        
        /* Cartes avec images pour les produits */
        .card-with-image {
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
        }
        
        .card-overlay {
            background: rgba(0, 0, 0, 0.5);
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            border-radius: 4px;
            z-index: 1;
        }
        
        /* Couleurs */
        .bg-red { background-color: var(--color-red); }
        .bg-green { background-color: var(--color-green); }
        .bg-blue { background-color: var(--color-blue); }
        .bg-orange { background-color: var(--color-orange); }
        .bg-purple { background-color: var(--color-purple); }
        .bg-dark-blue { background-color: var(--color-dark-btn); }
        .bg-discount { background-color: var(--color-dark-green); }
        .bg-yellow { background-color: var(--color-yellow); color: #333; }
        .bg-teal { background-color: #009688; }
        .bg-pink { background-color: #ec407a; }
        .bg-cyan { background-color: #26c6da; }
        .bg-brown { background-color: #8d6e63; }
        .bg-indigo { background-color: #5c6bc0; }
        .bg-lime { background-color: #cddc39; color: #333; }
        .bg-deep-orange { background-color: #ff5722; }
        .bg-deep-purple { background-color: #673ab7; }
        .bg-light-blue { background-color: #03a9f4; }
        .bg-light-green { background-color: #8bc34a; }

        .card-price {
            position: absolute;
            bottom: 3px;
            right: 5px;
            font-size: 0.7rem;
            background: rgba(0,0,0,0.7);
            padding: 1px 5px;
            border-radius: 8px;
            font-weight: bold;
            z-index: 3;
        }
        
        .card-code {
            position: absolute;
            bottom: 3px;
            left: 5px;
            font-size: 0.55rem;
            opacity: 0.8;
            text-transform: uppercase;
            background: rgba(0,0,0,0.5);
            padding: 1px 3px;
            border-radius: 3px;
            z-index: 3;
        }
        
        .card-points {
            position: absolute;
            top: 3px;
            right: 5px;
            font-size: 0.55rem;
            background: rgba(255,255,255,0.9);
            color: #333;
            padding: 1px 3px;
            border-radius: 6px;
            font-weight: bold;
            z-index: 3;
        }
        
        .card-title {
            font-weight: bold;
            font-size: 0.75rem;
            margin-bottom: 2px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            text-align: center;
            width: 100%;
            padding: 0 2px;
        }
        
        /* Style spécifique pour les titres de catégories et sous-catégories */
        .category-card .card-title,
        .subcategory-card .card-title {
            margin-top: 5px;
            margin-bottom: 0;
            font-size: 0.7rem;
        }
        
        .card-description {
            font-size: 0.65rem;
            opacity: 0.9;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            flex-grow: 1;
            padding: 0 2px;
        }
        
        .card-image-icon {
            position: absolute;
            bottom: 3px;
            right: 3px;
            font-size: 0.75rem;
            opacity: 0.7;
            z-index: 3;
        }
        
        /* Icône centrée pour catégories et sous-catégories */
        .category-icon,
        .subcategory-icon {
            font-size: 1.3rem;
            margin-bottom: 5px;
            opacity: 0.9;
        }
        
        .selected-card {
            border: 2px solid #fdd835 !important;
            box-shadow: 0 0 6px rgba(253, 216, 53, 0.5);
        }

        /* En-têtes de section */
        .section-header {
            padding: 6px;
            background-color: #333;
            color: #fdd835;
            font-size: 0.85rem;
            font-weight: bold;
            border-bottom: 1px solid #444;
            text-align: center;
        }
        
        .section-subheader {
            padding: 5px;
            background-color: #2a2a2a;
            color: #ccc;
            font-size: 0.7rem;
            border-bottom: 1px solid #444;
            text-align: center;
        }

        /* --- TICKET AREA (Right Side) - OPTIMISÉ --- */
        .ticket-area {
            width: 360px;
            background-color: var(--bg-ticket);
            display: flex;
            flex-direction: column;
            border-left: 1px solid var(--border-color);
        }
        
        .ticket-header {
            padding: 6px;
            background-color: #222;
            border-bottom: 1px solid #444;
        }
        
        .ticket-tabs {
            display: flex;
            margin-top: 4px;
        }
        
        .ticket-tabs .nav-link {
            flex: 1;
            background-color: #3e3e3e;
            color: #ccc;
            border: none;
            margin-right: 1px;
            border-radius: 0;
            padding: 5px 8px;
            font-size: 0.7rem;
            text-align: center;
            cursor: pointer;
        }
        .ticket-tabs .nav-link.active {
            background-color: #5d6166;
            color: white;
        }
        .ticket-tabs .nav-link:hover {
            background-color: #4a4e53;
        }

        /* Section client - HAUTEUR RÉDUITE */
        .client-section {
            background-color: #2a2a2a;
            padding: 6px;
            border-bottom: 1px solid #444;
            min-height: auto;
            height: auto;
        }
        
        .client-label {
            font-size: 0.75rem;
            margin-bottom: 3px;
            color: #fdd835;
            display: flex;
            align-items: center;
        }
        .client-label i {
            margin-right: 4px;
            font-size: 0.8rem;
        }
        
        .client-input {
            width: 100%;
            padding: 5px 7px;
            background-color: #222;
            border: 1px solid #444;
            color: white;
            border-radius: 3px;
            font-size: 0.75rem;
            height: 28px;
            box-sizing: border-box;
        }
        .client-input:focus {
            border-color: #fdd835;
            outline: none;
        }

        /* Liste des articles du panier - OPTIMISÉ */
        .order-list {
            flex: 1;
            overflow-y: auto;
            font-size: 0.75rem;
            min-height: 180px;
            max-height: calc(100vh - 420px);
        }
        
        /* Styles pour les items du panier - AMÉLIORÉS */
        .cart-item {
            display: flex;
            align-items: center;
            padding: 5px 3px;
            border-bottom: 1px solid #3a3a3a;
            transition: background-color 0.2s;
        }
        
        .cart-item:hover {
            background-color: rgba(255, 255, 255, 0.05);
        }
        
        .cart-item:last-child {
            border-bottom: none;
        }
        
        .cart-item-qty {
            width: 60px;
            text-align: center;
        }
        
        .cart-item-name {
            flex: 1;
            padding: 0 8px 0 10px; /* PADDING-LEFT AJOUTÉ */
            font-size: 0.7rem;
            line-height: 1.2;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .cart-item-price {
            width: 60px;
            text-align: right;
            font-size: 0.7rem;
            color: #ccc;
            font-weight: bold;
        }
        
        .cart-item-actions {
            width: 25px;
            text-align: center;
        }
        
        .remove-item-btn {
            background: none;
            border: none;
            color: #ef5350;
            cursor: pointer;
            font-size: 0.75rem;
            padding: 2px 4px;
            border-radius: 3px;
            transition: background-color 0.3s;
        }
        
        .remove-item-btn:hover {
            background-color: rgba(239, 83, 80, 0.2);
        }
        
        .empty-cart {
            text-align: center;
            padding: 25px 12px;
            color: #777;
            font-size: 0.8rem;
        }
        
        .empty-cart i {
            font-size: 1.8rem;
            margin-bottom: 8px;
            opacity: 0.5;
        }
        
        /* Styles pour les contrôles de quantité */
        .qty-control {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 2px;
        }
        
        .qty-btn {
            width: 20px;
            height: 20px;
            border: none;
            background-color: #444;
            color: white;
            border-radius: 3px;
            cursor: pointer;
            font-size: 0.65rem;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background-color 0.3s;
        }
        
        .qty-btn:hover {
            background-color: #555;
        }
        
        .qty-value {
            width: 22px;
            text-align: center;
            font-size: 0.7rem;
            font-weight: bold;
            color: #fdd835;
        }
        
        /* Sections de configuration - HAUTEUR RÉDUITE */
        .devise-selector {
            padding: 6px;
            background-color: #333;
            border-bottom: 1px solid #444;
            min-height: auto;
        }
        
        .devise-label {
            font-size: 0.75rem;
            color: #ccc;
            margin-bottom: 3px;
            display: flex;
            align-items: center;
        }
        .devise-label i {
            margin-right: 4px;
            color: #fdd835;
            font-size: 0.8rem;
        }
        
        .devise-buttons {
            display: flex;
            gap: 6px;
            margin-top: 3px;
        }
        
        .devise-btn {
            flex: 1;
            padding: 5px;
            text-align: center;
            background-color: #444;
            border-radius: 3px;
            cursor: pointer;
            font-size: 0.75rem;
            border: 1px solid transparent;
            transition: all 0.3s;
        }
        
        .devise-btn:hover {
            background-color: #555;
        }
        
        .devise-btn.active {
            background-color: #fdd835;
            color: #333;
            font-weight: bold;
            border-color: #fdd835;
        }
        
        .taux-info {
            font-size: 0.65rem;
            color: #aaa;
            margin-top: 3px;
            text-align: center;
        }
        
        .discount-section {
            padding: 6px;
            background-color: #2a2a2a;
            border-bottom: 1px solid #444;
            min-height: auto;
        }
        
        .discount-label {
            font-size: 0.75rem;
            color: #ccc;
            margin-bottom: 3px;
            display: flex;
            align-items: center;
        }
        .discount-label i {
            margin-right: 4px;
            color: #4caf50;
            font-size: 0.8rem;
        }
        
        .discount-input {
            width: 100%;
            padding: 5px 7px;
            background-color: #222;
            border: 1px solid #444;
            color: white;
            border-radius: 3px;
            font-size: 0.75rem;
            height: 28px;
            box-sizing: border-box;
        }
        .discount-input:focus {
            border-color: #4caf50;
            outline: none;
        }
        
        .before-discount {
            font-size: 0.65rem;
            color: #aaa;
            margin-top: 3px;
        }

        /* Footer du ticket - OPTIMISÉ POUR VISIBILITÉ */
        .ticket-footer {
            background-color: #222;
            padding: 0;
            border-top: 1px solid #444;
            margin-top: auto;
        }
        .total-display {
            background-color: #2b2e33;
            padding: 8px 12px;
            text-align: right;
        }
        .total-label { 
            font-size: 0.75rem; 
            color: #ccc; 
        }
        .total-amount { 
            font-size: 1.3rem; 
            font-weight: bold; 
            color: white;
            margin-top: 2px;
        }
        .total-amount-usd {
            font-size: 1.1rem;
            color: #3ad29f;
            margin-top: 2px;
            display: none;
        }
        
        /* Boutons d'action - VISIBLES */
        .action-grid { 
            display: flex; 
            padding: 6px;
            gap: 4px;
        }
        .action-btn {
            flex: 1;
            background-color: #37474f;
            color: white;
            border: 1px solid #222;
            padding: 8px 4px;
            text-align: center;
            font-size: 0.75rem;
            border-radius: 3px;
            cursor: pointer;
            transition: background-color 0.3s;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 32px;
        }
        .action-btn i {
            margin-right: 4px;
            font-size: 0.8rem;
        }
        .action-btn:hover { 
            background-color: #455a64; 
        }
        .action-btn.primary { 
            background-color: #007bff; 
        }
        .action-btn.primary:hover { 
            background-color: #0069d9; 
        }
        .action-btn.warning { 
            background-color: #ffc107; 
            color: #333;
        }
        .action-btn.warning:hover { 
            background-color: #e0a800; 
        }

        /* --- BOTTOM NAV --- */
        .bottom-nav {
            height: 75px;
            background-color: #111;
            display: flex;
            align-items: center;
            padding: 0 6px;
            border-top: 1px solid #333;
        }
        
        .user-profile {
            width: 55px;
            text-align: center;
            border-right: 1px solid #333;
            margin-right: 6px;
            padding-right: 6px;
        }
        .avatar-img { 
            width: 22px; 
            height: 22px; 
            border-radius: 50%; 
            object-fit: cover; 
            margin-bottom: 2px; 
        }

        .category-scroll {
            display: flex;
            flex: 1;
            overflow-x: auto;
            gap: 6px;
            padding: 6px 0;
        }
        
        .cat-btn {
            min-width: 80px;
            height: 60px;
            background-color: #333;
            border-radius: 5px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: white;
            cursor: pointer;
            padding: 4px;
            transition: background-color 0.3s;
        }
        .cat-btn:hover {
            background-color: #444;
        }
        .cat-btn i { font-size: 1rem; margin-bottom: 2px; }
        .cat-btn span { font-size: 0.7rem; text-align: center; }
        .cat-btn.active { background-color: #444; color: #fdd835; }
        .cat-btn.bg-teal { background-color: #009688; }

        /* États de visibilité */
        .visible { display: block !important; }
        .flex-visible { display: flex !important; }
        
        /* Message d'état */
        .state-message {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #aaa;
            font-size: 0.9rem;
            padding: 15px;
        }
        
        /* Conteneur de grille de produits */
        .products-grid {
            display: flex;
            flex-wrap: wrap;
            padding: 3px;
            gap: 3px;
            overflow-y: auto;
        }
        
        .product-item {
            width: calc(25% - 3px);
            min-width: 100px;
        }
        
        /* Image par défaut */
        .default-image {
            background-color: #555;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .default-image i {
            font-size: 1.3rem;
            opacity: 0.5;
        }
        
        /* Barre de recherche */
        .search-bar {
            padding: 5px;
            background-color: #333;
            border-bottom: 1px solid #444;
        }
        
        .search-bar input {
            width: 100%;
            padding: 4px 6px;
            border-radius: 3px;
            border: 1px solid #555;
            background-color: #222;
            color: white;
            font-size: 0.75rem;
        }
        
        /* Conteneurs pour les différentes sections de tickets */
        .tickets-content {
            flex: 1;
            overflow-y: auto;
            display: none;
        }
        
        .tickets-content.active {
            display: block;
        }
        
        .last-tickets-list, .daily-sales-list {
            padding: 8px;
        }
        
        .ticket-history-item {
            padding: 6px;
            border-bottom: 1px solid #444;
            font-size: 0.75rem;
        }
        
        .ticket-history-item:last-child {
            border-bottom: none;
        }
        
        .ticket-history-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 3px;
        }
        
        .ticket-history-client {
            font-weight: bold;
            color: #fdd835;
        }
        
        .ticket-history-amount {
            color: #4caf50;
            font-weight: bold;
        }
        
        .ticket-history-details {
            font-size: 0.7rem;
            color: #aaa;
        }
        
        .daily-sales-summary {
            background-color: #2a2a2a;
            padding: 8px;
            border-radius: 4px;
            margin-bottom: 8px;
        }
        
        .sales-stat {
            display: flex;
            justify-content: space-between;
            margin-bottom: 4px;
            font-size: 0.75rem;
        }
        
        .sales-stat-label {
            color: #ccc;
        }
        
        .sales-stat-value {
            color: #4caf50;
            font-weight: bold;
        }
        
        /* Scrollbar personnalisée */
        ::-webkit-scrollbar {
            width: 5px;
            height: 5px;
        }
        
        ::-webkit-scrollbar-track {
            background: #2a2a2a;
            border-radius: 2px;
        }
        
        ::-webkit-scrollbar-thumb {
            background: #555;
            border-radius: 2px;
        }
        
        ::-webkit-scrollbar-thumb:hover {
            background: #666;
        }
        
        /* Animation pour les ajouts au panier */
        @keyframes itemAdded {
            0% { transform: scale(0.95); opacity: 0.7; }
            100% { transform: scale(1); opacity: 1; }
        }
        
        .item-added {
            animation: itemAdded 0.3s ease-out;
        }
        
        /* Responsive */
        @media (max-width: 1400px) {
            .product-item {
                width: calc(33.333% - 3px);
            }
            
            .ticket-area {
                width: 340px;
            }
        }
        
        @media (max-width: 1200px) {
            .product-item {
                width: calc(50% - 3px);
            }
            
            .ticket-area {
                width: 320px;
            }
            
            .category-card, .subcategory-card, .product-card {
                width: 110px;
                height: 75px;
            }
            
            .left-tools {
                width: 80px;
            }
        }
        
        @media (max-width: 992px) {
            .section-container {
                max-width: 15%;
                min-width: 110px;
            }
            
            .category-card, .subcategory-card, .product-card {
                width: 90px;
                height: 70px;
                margin: 2px;
                font-size: 0.7rem;
            }
            
            .card-title {
                font-size: 0.7rem;
            }
            
            .card-price {
                font-size: 0.6rem;
                padding: 1px 3px;
            }
            
            .ticket-area {
                width: 300px;
            }
        }
    </style>
</head>
<body>

    <header class="top-bar">
        <div class="d-flex align-items-center">
            <div class="top-icon" onclick="goBack()"><i class="fas fa-chevron-left"></i> Back</div>
            <div class="top-icon" onclick="showSearch()"><i class="fas fa-search"></i> SEARCH</div>
            <div class="top-icon" onclick="showPLU()"><i class="fas fa-th"></i> PLU</div>
        </div>
        
        <div class="sold" id="solde-htg">
            <% 
                BigDecimal montantHTG = (BigDecimal) request.getAttribute("montantTotalHTG");
                if (montantHTG != null) {
                    java.text.DecimalFormat dfHTG = new java.text.DecimalFormat("#,##0");
                    out.print(dfHTG.format(montantHTG) + " HTG");
                } else {
                    out.print("0 HTG");
                }
            %>
        </div>
        
        <div class="brand-logo">
            <img alt="" src="<%=request.getContextPath()%>/image/logo.jpg" style="width: 70px; height: auto;">
        </div>
        
        <div class="sold" id="solde-usd">
            <% 
                BigDecimal montantUSD = (BigDecimal) request.getAttribute("montantTotalUSD");
                if (montantUSD != null) {
                    java.text.DecimalFormat dfUSD = new java.text.DecimalFormat("#,##0.00");
                    out.print(dfUSD.format(montantUSD) + " USD");
                } else {
                    out.print("0.00 USD");
                }
            %>
        </div>
        
        <div class="d-flex align-items-center">
            <div class="top-icon" onclick="showSettings()"><i class="fas fa-cog"></i> SETTINGS</div>
            <div class="top-icon"><i class="fas fa-receipt"></i> CONFIG</div>
            <div class="top-icon">
                <a class="nav-link" href="RoleServlet?action=lister" style="color: white; text-decoration: none;">
                    <i class="fas fa-user-plus"></i>
                    <span class="ml-2 item-text">USERS</span>
                </a>
            </div>
            <div class="top-icon">
                <ul class="nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-muted pr-0 d-flex align-items-center gap-2" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true aria-expanded="false">
                            <div class="user-profile">
                                <img src="https://i.pravatar.cc/100?img=11" alt="User" class="avatar-img">
                                <div class="text-xs font-weight-bold text-uppercase">
                                    <%
                                        if (username != null && !username.isEmpty()) {
                                    %>
                                        <%= username %>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </a>
                        
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
                            <a class="dropdown-item d-flex align-items-center" href="#">
                                <i class="fe fe-user fe-16 mr-2"></i> Profile
                            </a>
                            <a class="dropdown-item d-flex align-items-center" href="UtilisateurServlet?action=logout&who=ADM">
                                <i class="fe fe-log-out fe-16 mr-2"></i> Déconnexion
                            </a>
                            <a class="dropdown-item d-flex align-items-center" href="<%=request.getContextPath()%>/index.jsp">
                                <i class="fe fe-home fe-16 mr-2"></i> Acceder au site
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </header>

    <div class="main-workspace">
        
        <!-- Barre latérale gauche avec rayons -->
        <div class="left-tools" id="rayons-sidebar">
            <div class="left-tools-header">RAYONS</div>
            <!-- Les rayons seront ajoutés ici dynamiquement -->
        </div>

        <div class="product-area">
            <!-- Section 1: Catégories -->
            <div class="section-container" id="categories-section">
                <div class="section-header">Catégories</div>
                <div id="categories-container">
                    <!-- Les catégories seront chargées ici -->
                </div>
            </div>
            
            <!-- Section 2: Sous-catégories -->
            <div class="section-container" id="subcategories-section">
                <div class="section-header">Sous-catégories</div>
                <div id="subcategories-container">
                    <!-- Les sous-catégories seront chargées ici -->
                </div>
            </div>
            
            <!-- Section 3: Produits -->
            <div class="products-main-container" id="products-section">
                <div class="section-header">Produits</div>
                <div class="search-bar">
                    <input type="text" id="product-search" placeholder="Rechercher un produit..." onkeyup="searchProducts()">
                </div>
                <div class="products-grid" id="products-container">
                    <!-- Les produits seront chargés ici -->
                </div>
            </div>
            
            <!-- Message d'état -->
            <div class="state-message" id="state-message">
                Chargement du menu...
            </div>
        </div>

        <div class="ticket-area">
            <!-- En-tête du ticket -->
            <div class="ticket-header">
                <div class="d-flex justify-content-between align-items-center text-white">
                    <span style="font-size: 0.9rem; font-weight: bold;">COMMANDE</span>
                    <div class="text-right text-xs text-muted">
                        <span id="current-date"></span><br><span id="clock">4:26 AM</span>
                    </div>
                </div>
                
                <!-- NOUVEAUX ONGLETS -->
                <div class="ticket-tabs">
                    <a class="nav-link active" href="#" onclick="showTab('ticket')">Ticket</a>
                    <a class="nav-link" href="#" onclick="showTab('last-tickets')">Last Tickets</a>
                    <a class="nav-link" href="#" onclick="showTab('daily-sales')">Daily Sales</a>
                </div>
            </div>

            <!-- Section client - HAUTEUR RÉDUITE -->
            <div class="client-section">
                <div class="client-label">
                    <i class="fas fa-user"></i> Client
                </div>
                <input type="text" class="client-input" id="nomClient" 
                       placeholder="Nom du client" required>
                <div id="client-error" style="color: #dc3545; font-size: 0.65rem; margin-top: 2px; display: none;"></div>
            </div>

            <!-- CONTENU DES ONGLETS -->
            
            <!-- Onglet Ticket (Panier actuel) -->
            <div class="tickets-content active" id="ticket-tab">
                <div class="order-list" id="order-list">
                    <div id="cart-items-container">
                        <!-- Les articles seront chargés ici dynamiquement -->
                    </div>
                </div>

                <!-- Sélecteur de devise -->
                <!-- <div class="devise-selector" id="deviseSelector" style="display: none;">
                    <div class="devise-label">
                        <i class="fas fa-dollar-sign"></i> Devise
                    </div>
                    <div class="devise-buttons">
                        <div class="devise-btn active" data-devise="HTG" onclick="selectDevise('HTG')">
                            $ HT
                        </div>
                        <div class="devise-btn" data-devise="USD" onclick="selectDevise('USD')">
                            USD
                        </div>
                    </div>
                    <div class="taux-info">
                        1 USD = <span id="currentExchangeRate">0</span> HTG
                    </div>
                </div>

                Section discount
                <div class="discount-section" id="discountSection" style="display: none;">
                    <div class="discount-label">
                        <i class="fas fa-percent"></i> Rabais
                    </div>
                    <input type="number" class="discount-input" id="discountAmount" 
                           min="0" step="0.01" placeholder="Montant" value="0">
                    <div class="before-discount">
                        Avant: <span id="amountBeforeDiscount">0 HTG</span>
                    </div>
                </div> -->
            </div>
            
            <!-- Onglet Last Tickets -->
            <div class="tickets-content" id="last-tickets-tab">
                <div class="last-tickets-list" id="last-tickets-container">
                    <div class="text-center py-4" style="color: #777;">
                        <i class="fas fa-receipt fa-2x mb-3"></i>
                        <p>Chargement des derniers tickets...</p>
                    </div>
                </div>
            </div>
            
            <!-- Onglet Daily Sales -->
            <div class="tickets-content" id="daily-sales-tab">
                <div class="daily-sales-list" id="daily-sales-container">
                    <div class="text-center py-4" style="color: #777;">
                        <i class="fas fa-chart-bar fa-2x mb-3"></i>
                        <p>Chargement des ventes journalières...</p>
                    </div>
                </div>
            </div>

            <!-- Footer du ticket - TOUJOURS VISIBLE -->
            <div class="ticket-footer">
            	<!-- Sélecteur de devise -->
                <div class="devise-selector" id="deviseSelector" style="display: none;">
                    <div class="devise-label">
                        <i class="fas fa-dollar-sign"></i> Devise
                    </div>
                    <div class="devise-buttons">
                        <div class="devise-btn active" data-devise="HTG" onclick="selectDevise('HTG')">
                            $ HT
                        </div>
                        <div class="devise-btn" data-devise="USD" onclick="selectDevise('USD')">
                            USD
                        </div>
                    </div>
                    <div class="taux-info">
                        1 USD = <span id="currentExchangeRate">0</span> HTG
                    </div>
                </div>

                <!-- Section discount -->
                <div class="discount-section" id="discountSection" style="display: none;">
                    <div class="discount-label">
                        <i class="fas fa-percent"></i> Rabais
                    </div>
                    <input type="number" class="discount-input" id="discountAmount" 
                           min="0" step="0.01" placeholder="Montant" value="0">
                    <div class="before-discount">
                        Avant: <span id="amountBeforeDiscount">0 HTG</span>
                    </div>
                </div>
                <div class="total-display">
                    <span class="total-label">Total:</span>
                    <div class="total-amount" id="total-amount">0.00 HTG</div>
                    <div class="total-amount-usd" id="total-amount-usd">0.00 USD</div>
                </div>
                
                <!-- Boutons d'action - TOUJOURS VISIBLES -->
                <div class="action-grid">
                    <div class="action-btn primary" onclick="passerCommande()" id="commandeBtn" style="display: none;">
                        <i class="fas fa-check-circle"></i> Commander
                    </div>
                    <div class="action-btn warning" onclick="genererProforma()" id="proformaBtn" style="display: none;">
                        <i class="fas fa-print"></i> Proforma
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="bottom-nav">
        <div class="category-scroll" id="bottom-categories">
            <div class="cat-btn" onclick="showFavorites()" title="Favorites">
                <i class="fas fa-star"></i>
                <span>Favorites</span>
            </div>
            
            <a href="MenuServlet?action=placer-commande" style="color: white; text-decoration: none;" title="Tableau de bord">
                <div class="cat-btn active">
                    <i class="fas fa-columns"></i>
                    <span>BOARD</span>
                </div>
            </a>
            
            <a href="ProduitServlet?action=lister" style="color: white; text-decoration: none;" title="Gestion des produits">
                <div class="cat-btn">
                    <i class="fas fa-boxes"></i>
                    <span>Produits</span>
                </div>
            </a>
            
            <a href="FactureServlet?action=lister" style="color: white; text-decoration: none;" title="Historique des achats">
                <div class="cat-btn">
                    <i class="fas fa-shopping-cart"></i>
                    <span>Achats</span>
                </div>
            </a>
            
            <a href="MouvementStockServlet?action=lister" style="color: white; text-decoration: none;" title="Gestion d'inventaire">
                <div class="cat-btn">
                    <i class="fas fa-clipboard-list"></i>
                    <span>Inventaire</span>
                </div>
            </a>
            
            <a href="CommandeServlet?action=caissiere-commandes-cashed" style="color: white; text-decoration: none;" title="Rapports et statistiques">
                <div class="cat-btn">
                    <i class="fas fa-chart-bar"></i>
                    <span>Rapport</span>
                </div>
            </a>
            
            <div class="cat-btn bg-teal" onclick="showTaux()" title="Taux et pourcentages">
                <i class="fas fa-percentage"></i>
                <span>Taux</span>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <script>
        // Variables globales du système de commande
        let currentCategoryId = null;
        let currentSubCategoryId = null;
        let currentCategoryName = null;
        let cart = {
            items: [],
            total: 0,
            count: 0,
            totalUSD: 0
        };
        
        // Variables pour le discount
        let discountAmount = 0;
        let discountAmountHTG = 0;
        let isDiscountEnabled = false;
        let finalAmount = 0;
        
        // Variables globales pour la devise
        let selectedDevise = 'HTG';
        let exchangeRate = 0;
        
        // Variables pour les catégories et produits
        let menuData = null;
        let currentRayon = null;
        let currentCategory = null;
        let currentSousCategory = null;
        let filteredCategories = [];
        let filteredSubcategories = [];
        let filteredProducts = [];
        
        // Variable pour l'onglet actif
        let activeTab = 'ticket';
        
        // Couleurs disponibles
        const productColors = [
            'bg-red', 'bg-green', 'bg-blue', 'bg-orange', 'bg-purple', 
            'bg-dark-blue', 'bg-yellow', 'bg-teal', 'bg-pink', 'bg-cyan',
            'bg-brown', 'bg-indigo', 'bg-lime', 'bg-deep-orange', 
            'bg-deep-purple', 'bg-light-blue', 'bg-light-green'
        ];
        
        // Base URL pour les images
        const contextPath = '<%=request.getContextPath()%>';
        
        // ============================================
        // FONCTIONS POUR LES ONGLETS
        // ============================================
        
        // Fonction pour afficher un onglet
        function showTab(tabName) {
            activeTab = tabName;
            
            // Mettre à jour l'apparence des onglets
            $('.ticket-tabs .nav-link').removeClass('active');
            
            // CORRECTION ICI : Utiliser une méthode différente pour sélectionner l'onglet
            if (tabName === 'ticket') {
                $('.ticket-tabs .nav-link:contains("Ticket")').addClass('active');
            } else if (tabName === 'last-tickets') {
                $('.ticket-tabs .nav-link:contains("Last Tickets")').addClass('active');
            } else if (tabName === 'daily-sales') {
                $('.ticket-tabs .nav-link:contains("Daily Sales")').addClass('active');
            }
            
            // Masquer tous les contenus
            $('.tickets-content').removeClass('active');
            
            // Afficher le contenu correspondant
            $('#' + tabName + '-tab').addClass('active');
            
            // Charger les données si nécessaire
            if (tabName === 'last-tickets') {
                loadLastTickets();
            } else if (tabName === 'daily-sales') {
                loadDailySales();
            }
            
            // Afficher/masquer les boutons d'action selon l'onglet
            if (tabName === 'ticket') {
                if (cart.count > 0) {
                    $('#commandeBtn').show();
                    $('#proformaBtn').show();
                }
            } else {
                $('#commandeBtn').hide();
                $('#proformaBtn').hide();
            }
        }
        
        // Charger les derniers tickets
        function loadLastTickets() {
            $.ajax({
                url: 'CommandeServlet',
                type: 'GET',
                data: { action: 'get-last-tickets', limit: 10 },
                dataType: 'json',
                success: function(response) {
                    displayLastTickets(response);
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement derniers tickets:", error);
                    $('#last-tickets-container').html('<div class="text-center py-4" style="color: #777;">' +
                        '<i class="fas fa-exclamation-circle fa-2x mb-3"></i>' +
                        '<p>Erreur de chargement</p></div>');
                }
            });
        }
        
        // Afficher les derniers tickets
        function displayLastTickets(tickets) {
            const container = $('#last-tickets-container');
            container.empty();
            
            if (!tickets || tickets.length === 0) {
                container.html('<div class="text-center py-4" style="color: #777;">' +
                    '<i class="fas fa-receipt fa-2x mb-3"></i>' +
                    '<p>Aucun ticket récent</p></div>');
                return;
            }
            
            tickets.forEach(ticket => {
                const ticketItem = $('<div class="ticket-history-item"></div>');
                
                const header = $('<div class="ticket-history-header"></div>');
                header.append('<span class="ticket-history-client">#' + ticket.numero + ' - ' + ticket.clientName + '</span>');
                header.append('<span class="ticket-history-amount">' + ticket.montantTotal + ' ' + ticket.devise + '</span>');
                
                const details = $('<div class="ticket-history-details"></div>');
                details.append('<div>Date: ' + ticket.dateCreation + '</div>');
                details.append('<div>Statut: ' + ticket.statut + '</div>');
                
                ticketItem.append(header);
                ticketItem.append(details);
                
                container.append(ticketItem);
            });
        }
        
        // Charger les ventes journalières
        function loadDailySales() {
            $.ajax({
                url: 'CommandeServlet',
                type: 'GET',
                data: { action: 'get-daily-sales', date: new Date().toISOString().split('T')[0] },
                dataType: 'json',
                success: function(response) {
                    displayDailySales(response);
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement ventes journalières:", error);
                    $('#daily-sales-container').html('<div class="text-center py-4" style="color: #777;">' +
                        '<i class="fas fa-exclamation-circle fa-2x mb-3"></i>' +
                        '<p>Erreur de chargement</p></div>');
                }
            });
        }
        
        // Afficher les ventes journalières
        function displayDailySales(salesData) {
            const container = $('#daily-sales-container');
            container.empty();
            
            if (!salesData) {
                container.html('<div class="text-center py-4" style="color: #777;">' +
                    '<i class="fas fa-chart-bar fa-2x mb-3"></i>' +
                    '<p>Aucune donnée de vente aujourd\'hui</p></div>');
                return;
            }
            
            // Résumé des ventes
            const summary = $('<div class="daily-sales-summary"></div>');
            
            summary.append('<div class="sales-stat"><span class="sales-stat-label">Total HTG:</span><span class="sales-stat-value">' + 
                          (salesData.totalHTG || 0) + ' HTG</span></div>');
            
            summary.append('<div class="sales-stat"><span class="sales-stat-label">Total USD:</span><span class="sales-stat-value">' + 
                          (salesData.totalUSD || 0) + ' USD</span></div>');
            
            summary.append('<div class="sales-stat"><span class="sales-stat-label">Nombre de tickets:</span><span class="sales-stat-value">' + 
                          (salesData.nbTickets || 0) + '</span></div>');
            
            summary.append('<div class="sales-stat"><span class="sales-stat-label">Moyenne par ticket:</span><span class="sales-stat-value">' + 
                          (salesData.moyenneTicket || 0) + ' HTG</span></div>');
            
            container.append(summary);
            
            // Liste des tickets si disponible
            if (salesData.tickets && salesData.tickets.length > 0) {
                const ticketsTitle = $('<div style="font-size: 0.8rem; color: #fdd835; margin: 10px 0 5px 0; font-weight: bold;">Tickets du jour:</div>');
                container.append(ticketsTitle);
                
                salesData.tickets.forEach(ticket => {
                    const ticketItem = $('<div class="ticket-history-item"></div>');
                    
                    const header = $('<div class="ticket-history-header"></div>');
                    header.append('<span class="ticket-history-client">#' + ticket.numero + ' - ' + ticket.clientName + '</span>');
                    header.append('<span class="ticket-history-amount">' + ticket.montantTotal + ' ' + ticket.devise + '</span>');
                    
                    const details = $('<div class="ticket-history-details"></div>');
                    details.append('<div>Heure: ' + ticket.heure + '</div>');
                    
                    ticketItem.append(header);
                    ticketItem.append(details);
                    
                    container.append(ticketItem);
                });
            }
        }
        
        // ============================================
        // FONCTIONS POUR LES RAYONS
        // ============================================
        
        // Fonction pour charger les rayons
        function loadRayons() {
            console.log("Chargement des rayons...");
            $.ajax({
                url: 'MenuServlet',
                type: 'GET',
                data: { action: 'rayons-json', format: 'json' },
                dataType: 'json',
                success: function(response) {
                    if (response && response.length > 0) {
                        displayRayons(response);
                        // Charger les catégories du premier rayon
                        if (response.length > 0) {
                            selectRayon(response[0].id);
                        }
                    } else {
                        // Si pas de rayons, charger les catégories directement
                        loadCategories();
                        $('#rayons-sidebar').html('<div class="left-tools-header">RAYONS</div><div class="text-center py-3" style="color: #aaa; font-size: 0.65rem;">Aucun rayon</div>');
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement rayons:", error);
                    // En cas d'erreur, charger les catégories directement
                    loadCategories();
                    $('#rayons-sidebar').html('<div class="left-tools-header">RAYONS</div><div class="text-center py-3" style="color: #aaa; font-size: 0.65rem;">Erreur de chargement</div>');
                }
            });
        }
        
        // Afficher les rayons dans la barre latérale
        function displayRayons(rayons) {
            const container = $('#rayons-sidebar');
            container.find('.rayon-btn').remove();
            
            if (!rayons || rayons.length === 0) {
                container.append('<div class="text-center py-3" style="color: #aaa; font-size: 0.65rem;">Aucun rayon disponible</div>');
                return;
            }
            
            rayons.forEach((rayon, index) => {
                const rayonBtn = $('<div class="rayon-btn" onclick="selectRayon(' + rayon.id + ')">' +
                    '<i class="fas fa-' + getRayonIcon(rayon.nom) + '"></i>' +
                    '<span>' + (rayon.nom.length > 9 ? rayon.nom.substring(0, 9) + '...' : rayon.nom) + '</span>' +
                    '</div>');
                
                if (rayon.nbCategories && rayon.nbCategories > 0) {
                    rayonBtn.append('<span class="rayon-badge">' + rayon.nbCategories + '</span>');
                }
                
                if (index === 0) {
                    rayonBtn.addClass('active');
                }
                
                container.append(rayonBtn);
            });
        }
        
        // Sélectionner un rayon
        function selectRayon(rayonId) {
            $('.rayon-btn').removeClass('active');
            $('[onclick="selectRayon(' + rayonId + ')"]').addClass('active');
            loadCategoriesByRayon(rayonId);
        }
        
        // Charger les catégories par rayon
        function loadCategoriesByRayon(rayonId) {
            $.ajax({
                url: 'MenuServlet',
                type: 'GET',
                data: { action: 'categories-by-rayon', rayonId: rayonId, format: 'json' },
                dataType: 'json',
                success: function(response) {
                    if (response && response.length > 0) {
                        displayCategories(response);
                        $('#state-message').hide();
                    } else {
                        showNoContent("Aucune catégorie dans ce rayon");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement catégories par rayon:", error);
                    showError("Erreur de chargement des catégories");
                }
            });
        }
        
        // ============================================
        // FONCTIONS DU SYSTÈME DE COMMANDE
        // ============================================
        
        // Fonction pour charger le taux de change
        function loadExchangeRate() {
            $.ajax({
                url: 'TauxServlet',
                type: 'GET',
                data: { action: 'get-taux-actif' },
                dataType: 'json',
                success: function(response) {
                    if (response && response.tauxDeChange) {
                        exchangeRate = parseFloat(response.tauxDeChange);
                        $('#currentExchangeRate').text(exchangeRate.toFixed(2));
                        
                        if (cart.count > 0) {
                            updateTotalDisplay();
                        }
                    } else {
                        exchangeRate = 120;
                        $('#currentExchangeRate').text(exchangeRate.toFixed(2));
                        if (cart.count > 0) {
                            updateTotalDisplay();
                        }
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement taux de change:", error);
                    exchangeRate = 120;
                    $('#currentExchangeRate').text(exchangeRate.toFixed(2));
                    if (cart.count > 0) {
                        updateTotalDisplay();
                    }
                }
            });
        }
        
        // Fonction pour sélectionner la devise
        function selectDevise(devise) {
            selectedDevise = devise;
            
            $('.devise-btn').removeClass('active');
            $('.devise-btn[data-devise="' + devise + '"]').addClass('active');
            
            updateTotalDisplay();
            
            if (devise === 'HTG') {
                $('#total-amount').show();
                $('#total-amount-usd').hide();
            } else {
                $('#total-amount').hide();
                $('#total-amount-usd').show();
            }
        }
        
        // Fonction pour mettre à jour l'affichage du total - TAILLE RÉDUITE
        function updateTotalDisplay() {
            if (exchangeRate <= 0) return;
            
            let totalHTG = 0;
            let totalUSD = 0;
            
            cart.items.forEach(item => {
                let prixHTG = 0;
                
                if (item.plat) {
                    if (item.plat.produit && item.plat.produit.prixVente) {
                        prixHTG = parseFloat(item.plat.produit.prixVente);
                    } else {
                        prixHTG = parseFloat(item.plat.prix);
                    }
                } else if (item.produit) {
                    prixHTG = parseFloat(item.produit.prixVente);
                }
                
                const sousTotalHTG = prixHTG * item.quantite;
                totalHTG += sousTotalHTG;
                
                const prixUSD = Math.round((prixHTG / exchangeRate) * 1000) / 1000;
                const sousTotalUSD = Math.round((prixUSD * item.quantite) * 1000) / 1000;
                totalUSD += sousTotalUSD;
            });
            
            totalHTG = Math.round(totalHTG * 1000) / 1000;
            totalUSD = Math.round(totalUSD * 1000) / 1000;
            
            cart.total = totalHTG;
            cart.totalUSD = totalUSD;
            
            let montantFinalHTG = totalHTG;
            let montantFinalUSD = totalUSD;
            let discountHTG = 0;
            let discountUSD = 0;
            
            if (isDiscountEnabled && discountAmount > 0) {
                if (selectedDevise === 'HTG') {
                    discountHTG = Math.round(discountAmount * 1000) / 1000;
                    discountUSD = Math.round((discountHTG / exchangeRate) * 1000) / 1000;
                } else if (selectedDevise === 'USD') {
                    discountUSD = Math.round(discountAmount * 1000) / 1000;
                    discountHTG = Math.round((discountUSD * exchangeRate) * 1000) / 1000;
                }
                
                montantFinalHTG = Math.round((totalHTG - discountHTG) * 1000) / 1000;
                if (montantFinalHTG < 0) montantFinalHTG = 0;
                
                montantFinalUSD = Math.round((totalUSD - discountUSD) * 1000) / 1000;
                if (montantFinalUSD < 0) montantFinalUSD = 0;
            }
            
            finalAmount = montantFinalHTG;
            discountAmountHTG = discountHTG;
            
            // MONTANT AFFICHÉ EN TAILLE RÉDUITE
            $('#total-amount').text(montantFinalHTG.toFixed(0) + ' HTG');
            $('#total-amount-usd').text(montantFinalUSD.toFixed(2) + ' USD');
            
            if (selectedDevise === 'HTG') {
                $('#amountBeforeDiscount').text(totalHTG.toFixed(0) + ' HTG');
            } else {
                $('#amountBeforeDiscount').text(totalUSD.toFixed(2) + ' USD');
            }
            
            if (cart.count > 0 && activeTab === 'ticket') {
                $('#deviseSelector').show();
                $('#discountSection').show();
                $('#commandeBtn').show();
                $('#proformaBtn').show();
            } else {
                $('#deviseSelector').hide();
                $('#discountSection').hide();
                if (activeTab !== 'ticket') {
                    $('#commandeBtn').hide();
                    $('#proformaBtn').hide();
                }
            }
        }
        
        // Fonction pour valider le client
        function validateClient() {
            const clientName = $('#nomClient').val().trim();
            if (!clientName) {
                $('#client-error').text('Nom client requis').show();
                return false;
            }
            $('#client-error').hide();
            return true;
        }
        
        // Fonction pour passer commande
        function passerCommande() {
            if (!validateClient()) return;
            
            if (cart.count === 0) {
                showToast('Panier vide', 'error');
                return;
            }
            
            const clientName = $('#nomClient').val().trim();
            const devise = selectedDevise;
            
            if (exchangeRate <= 0) {
                showToast('Taux de change indisponible', 'error');
                loadExchangeRate();
                return;
            }
            
            let montantFinal = 0;
            let discountPourEnvoi = 0;
            
            if (devise === 'HTG') {
                montantFinal = Math.round(cart.total * 1000) / 1000;
                discountPourEnvoi = Math.round(discountAmountHTG * 1000) / 1000;
            } else if (devise === 'USD') {
                let totalUSDRecalc = 0;
                
                cart.items.forEach(item => {
                    let prixHTG = 0;
                    
                    if (item.plat) {
                        if (item.plat.produit && item.plat.produit.prixVente) {
                            prixHTG = parseFloat(item.plat.produit.prixVente);
                        } else {
                            prixHTG = parseFloat(item.plat.prix);
                        }
                    } else if (item.produit) {
                        prixHTG = parseFloat(item.produit.prixVente);
                    }
                    
                    const prixUSD = Math.round((prixHTG / exchangeRate) * 1000) / 1000;
                    const sousTotalUSD = Math.round((prixUSD * item.quantite) * 1000) / 1000;
                    totalUSDRecalc += sousTotalUSD;
                });
                
                montantFinal = Math.round(totalUSDRecalc * 1000) / 1000;
                
                if (selectedDevise === 'USD') {
                    discountPourEnvoi = Math.round(discountAmount * 1000) / 1000;
                } else {
                    discountPourEnvoi = Math.round((discountAmountHTG / exchangeRate) * 1000) / 1000;
                }
                
                montantFinal = Math.round((montantFinal - discountPourEnvoi) * 1000) / 1000;
                if (montantFinal < 0) montantFinal = 0;
            }
            
            $.ajax({
                url: 'CommandeServlet',
                type: 'POST',
                data: {
                    action: 'handle-commande',
                    clientName: clientName,
                    isCredit: 0,
                    devise: devise,
                    montantTotal: montantFinal.toFixed(3),
                    tauxChange: exchangeRate.toFixed(3),
                    discount: discountPourEnvoi.toFixed(3)
                },
                dataType: 'json',
                success: function(response) {
                    if (response.success && response.commandeId) {
                        clearCart();
                        
                        var printWindow = window.open(
                            "CommandeServlet?action=imprimer&id=" + response.commandeId + 
                            "&clientName=" + encodeURIComponent(response.clientName || clientName) + 
                            "&devise=" + devise +
                            "&tauxChange=" + exchangeRate.toFixed(3) +
                            "&montantTotal=" + montantFinal.toFixed(3) +
                            "&discount=" + discountPourEnvoi.toFixed(3),
                            "_blank"
                        );
                        
                        showToast('Commande #' + response.numero + ' réussie!', 'success');
                        
                        // Recharger les derniers tickets
                        if (activeTab === 'last-tickets') {
                            loadLastTickets();
                        }
                        if (activeTab === 'daily-sales') {
                            loadDailySales();
                        }
                    } else {
                        showToast(response.message || 'Erreur commande', 'error');
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur commande:", error);
                    showToast('Erreur lors de la commande', 'error');
                }
            });
        }
        
        // Fonction pour générer un proforma
        function genererProforma() {
            if (!validateClient()) return;
            
            if (cart.count === 0) {
                showToast('Panier vide', 'error');
                return;
            }
            
            const clientName = $('#nomClient').val().trim();
            const devise = selectedDevise;
            
            if (exchangeRate <= 0) {
                showToast('Taux de change indisponible', 'error');
                return;
            }
            
            let montantFinal = 0;
            let discountPourEnvoi = 0;
            
            if (devise === 'HTG') {
                montantFinal = Math.round(cart.total * 1000) / 1000;
                discountPourEnvoi = Math.round(discountAmountHTG * 1000) / 1000;
            } else if (devise === 'USD') {
                let totalUSDRecalc = 0;
                
                cart.items.forEach(item => {
                    let prixHTG = 0;
                    
                    if (item.plat) {
                        if (item.plat.produit && item.plat.produit.prixVente) {
                            prixHTG = parseFloat(item.plat.produit.prixVente);
                        } else {
                            prixHTG = parseFloat(item.plat.prix);
                        }
                    } else if (item.produit) {
                        prixHTG = parseFloat(item.produit.prixVente);
                    }
                    
                    const prixUSD = Math.round((prixHTG / exchangeRate) * 1000) / 1000;
                    const sousTotalUSD = Math.round((prixUSD * item.quantite) * 1000) / 1000;
                    totalUSDRecalc += sousTotalUSD;
                });
                
                montantFinal = Math.round(totalUSDRecalc * 1000) / 1000;
                
                if (selectedDevise === 'USD') {
                    discountPourEnvoi = Math.round(discountAmount * 1000) / 1000;
                } else {
                    discountPourEnvoi = Math.round((discountAmountHTG / exchangeRate) * 1000) / 1000;
                }
                
                montantFinal = Math.round((montantFinal - discountPourEnvoi) * 1000) / 1000;
                if (montantFinal < 0) montantFinal = 0;
            }
            
            window.open('CommandeServlet?action=handle-proforma&clientName=' + 
                        encodeURIComponent(clientName) + '&devise=' + devise + 
                        '&tauxChange=' + exchangeRate.toFixed(3) + '&montantTotal=' + 
                        montantFinal.toFixed(3) + '&discount=' + discountPourEnvoi.toFixed(3),
                        '_blank');
        }
        
        // ============================================
        // FONCTIONS DU SYSTÈME DE MENU
        // ============================================
        
        // Charger les catégories parentes
        function loadCategories() {
            $.ajax({
                url: 'MenuServlet',
                type: 'GET',
                data: { action: 'categorie-parente-json', format: 'json' },
                dataType: 'json',
                success: function(response) {
                    if (response && response.length > 0) {
                        displayCategories(response);
                        $('#state-message').hide();
                    } else {
                        showNoContent("Aucune catégorie disponible");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement catégories:", status, error);
                    showError("Erreur de chargement des catégories");
                }
            });
        }
        
        // Afficher les catégories
        function displayCategories(categories) {
            const container = $('#categories-container');
            container.empty();
            
            if (!categories || categories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune catégorie disponible</div>');
                filteredCategories = [];
                return;
            }
            
            filteredCategories = categories;
            currentCategory = null;
            
            categories.forEach((category, index) => {
                const colorClass = productColors[index % productColors.length];
                const imageUrl = category.imageUrl && category.imageUrl !== '/spot69/images/categories/default.png' 
                    ? contextPath + category.imageUrl 
                    : null;
                
                const categoryIcon = getCategoryIcon(category.nom);
                
                let cardHtml = '<div class="category-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                cardHtml += '<div class="category-icon">';
                cardHtml += '<i class="fas fa-' + categoryIcon + '"></i>';
                cardHtml += '</div>';
                cardHtml += '<div class="card-title">' + category.nom + '</div>';
                cardHtml += '</div>';
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-category-id', category.id);
                card.attr('data-category-name', category.nom.toLowerCase());
                
                card.click(function() {
                    selectCategory(category.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une catégorie
        function selectCategory(categoryId) {
            const category = filteredCategories.find(c => c.id === categoryId);
            if (!category) return;
            
            currentCategory = category;
            currentCategoryId = categoryId;
            currentCategoryName = category.nom;
            
            $('.category-card').removeClass('selected-card');
            $('.category-card[data-category-id="' + categoryId + '"]').addClass('selected-card');
            
            loadSubCategories(categoryId);
            clearProducts();
        }
        
        // Charger les sous-catégories
        function loadSubCategories(categoryId) {
            $.ajax({
                url: 'MenuServlet',
                type: 'GET',
                data: { action: 'sous-categories', parentId: categoryId },
                dataType: 'json',
                success: function(response) {
                    if (response && response.sousCategories && response.sousCategories.length > 0) {
                        displaySubCategories(response.sousCategories);
                    } else {
                        loadProducts(null, categoryId);
                        $('#subcategories-container').html('<div class="text-center py-5" style="color: #aaa;">Chargement...</div>');
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement sous-catégories:", error);
                    $('#subcategories-container').html('<div class="text-center py-5" style="color: #aaa;">Erreur</div>');
                }
            });
        }
        
        // Afficher les sous-catégories
        function displaySubCategories(subcategories) {
            const container = $('#subcategories-container');
            container.empty();
            
            currentSousCategory = null;
            currentSubCategoryId = null;
            
            if (!subcategories || subcategories.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa;">Aucune sous-catégorie</div>');
                filteredSubcategories = [];
                return;
            }
            
            filteredSubcategories = subcategories;
            
            subcategories.forEach((subcat, index) => {
                const colorClass = productColors[(index + 3) % productColors.length];
                const imageUrl = subcat.imageUrl && subcat.imageUrl !== '/spot69/images/categories/default.png' 
                    ? contextPath + subcat.imageUrl 
                    : null;
                
                const subcategoryIcon = getCategoryIcon(subcat.nom);
                
                let cardHtml = '<div class="subcategory-card ' + colorClass + '">';
                
                if (imageUrl) {
                    cardHtml += '<div class="card-image" style="background-image: url(\'' + imageUrl + '\');"></div>';
                }
                
                cardHtml += '<div class="card-content">';
                cardHtml += '<div class="subcategory-icon">';
                cardHtml += '<i class="fas fa-' + subcategoryIcon + '"></i>';
                cardHtml += '</div>';
                cardHtml += '<div class="card-title">' + subcat.nom + '</div>';
                cardHtml += '</div>';
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-subcategory-id', subcat.id);
                card.attr('data-subcategory-name', subcat.nom.toLowerCase());
                
                card.click(function() {
                    selectSubcategory(subcat.id);
                });
                
                container.append(card);
            });
        }
        
        // Sélectionner une sous-catégorie
        function selectSubcategory(subcategoryId) {
            const subcategory = filteredSubcategories.find(sc => sc.id === subcategoryId);
            if (!subcategory) return;
            
            currentSousCategory = subcategory;
            currentSubCategoryId = subcategoryId;
            
            $('.subcategory-card').removeClass('selected-card');
            $('.subcategory-card[data-subcategory-id="' + subcategoryId + '"]').addClass('selected-card');
            
            loadProducts(subcategoryId, currentCategoryId);
        }
        
        // Charger les produits
        function loadProducts(subCategoryId, categoryId) {
            $.ajax({
                url: 'MenuServlet',
                type: 'GET',
                data: { 
                    action: 'plats-par-sous-categories', 
                    categorieId: subCategoryId || categoryId 
                },
                dataType: 'json',
                success: function(response) {
                    if (response && response.plats && response.plats.length > 0) {
                        displayProducts(response.plats);
                    } else {
                        clearProducts();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement produits:", error);
                    clearProducts();
                }
            });
        }
        
        // Afficher les produits
        function displayProducts(products) {
            const container = $('#products-container');
            container.empty();
            
            if (!products || products.length === 0) {
                container.append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Aucun produit disponible</div>');
                filteredProducts = [];
                return;
            }
            
            filteredProducts = products;
            
            products.forEach((plat, index) => {
                const colorClass = productColors[(index + 5) % productColors.length];
                const nom = plat.nom || (plat.produit ? plat.produit.nom : 'Produit ' + plat.id);
                const description = plat.description || '';
                let prix = plat.prix;
                
                if (prix === 0 && plat.produit) {
                    prix = plat.produit.prix;
                }
                
                let imageUrl = null;
                if (plat.image && plat.image !== '/spot69/images/plats/default.png') {
                    imageUrl = contextPath + plat.image;
                } else if (plat.produit && plat.produit.imageUrl && plat.produit.imageUrl !== '/spot69/images/produits/default.png') {
                    imageUrl = contextPath + plat.produit.imageUrl;
                }
                
                const productIcon = getProductIcon(nom);
                
                const productItem = $('<div class="product-item"></div>');
                let cardHtml = '<div class="product-card ';
                
                if (imageUrl) {
                    cardHtml += 'card-with-image" style="background-image: url(\'' + imageUrl + '\')">';
                    cardHtml += '<div class="card-overlay"></div>';
                } else {
                    cardHtml += colorClass + '">';
                }
                
                cardHtml += '<div class="card-content">';
                
                if (!imageUrl) {
                    cardHtml += '<div style="text-align: center; margin-top: 6px; margin-bottom: 3px; font-size: 1.3rem; opacity: 0.9;">';
                    cardHtml += '<i class="fas fa-' + productIcon + '"></i>';
                    cardHtml += '</div>';
                }
                
                cardHtml += '<div class="card-title">' + nom + '</div>';
                
                if (description && description.trim() !== '') {
                    cardHtml += '<div class="card-description">' + description + '</div>';
                }
                
                cardHtml += '</div>';
                
                if (prix > 0) {
                    cardHtml += '<span class="card-price">' + prix.toFixed(0) + ' HTG</span>';
                }
                
                cardHtml += '</div>';
                
                const card = $(cardHtml);
                card.attr('data-product-id', plat.id);
                card.attr('data-product-name', nom.toLowerCase());
                card.attr('data-product-price', prix);
                card.attr('data-product-type', 'plat');
                
                card.click(function() {
                    addToCart(plat.id, 'plat');
                    $(this).addClass('item-added');
                    setTimeout(() => {
                        $(this).removeClass('item-added');
                    }, 300);
                });
                
                productItem.append(card);
                container.append(productItem);
            });
        }
        
        // Ajouter au panier
        function addToCart(productId, type) {
            $.ajax({
                url: 'PanierServlet',
                type: 'POST',
                data: {
                    action: 'ajouter',
                    type: type,
                    id: productId
                },
                success: function(response) {
                    if (response.includes('Erreur') || response.includes('Échec')) {
                        showToast('Erreur: ' + response, 'error');
                    } else {
                        loadCart();
                        showToast('Produit ajouté', 'success');
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur ajout panier:", error);
                    showToast('Erreur ajout', 'error');
                }
            });
        }
        
        // Charger le panier
        function loadCart() {
            $.ajax({
                url: 'PanierServlet',
                type: 'GET',
                data: { action: 'lister-json' },
                dataType: 'json',
                success: function(response) {
                    if (Array.isArray(response)) {
                        updateCart(response);
                    } else {
                        updateCart([]);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur chargement panier:", error);
                    updateCart([]);
                }
            });
        }
        
        // Mettre à jour le panier
        function updateCart(items) {
            cart.items = items;
            cart.count = items.reduce((total, item) => total + item.quantite, 0);
            cart.total = items.reduce((total, item) => {
                if (item.plat) {
                    return total + (item.plat.prix * item.quantite);
                } else if (item.produit) {
                    return total + (item.produit.prixVente * item.quantite);
                }
                return total;
            }, 0);
            
            updateCartDisplay();
            updateTotalDisplay();
        }
        
        // Mettre à jour l'affichage du panier - AVEC PADDING-LEFT ET ICÔNE POUBELLE
        function updateCartDisplay() {
            const container = $('#cart-items-container');
            container.empty();
            
            if (cart.items.length === 0) {
                container.html('<div class="empty-cart">' +
                              '<i class="fas fa-shopping-cart"></i>' +
                              '<p>Panier vide</p>' +
                              '</div>');
                return;
            }
            
            const itemsList = $('<div></div>');
            
            cart.items.forEach((item, index) => {
                const product = item.plat || item.produit;
                let price = 0;
                let productName = '';
                
                if (item.plat) {
                    if (item.plat.produit) {
                        price = item.plat.produit.prixVente || 0;
                        productName = item.plat.produit.nom || item.plat.nom || 'Produit';
                    } else {
                        price = item.plat.prix || 0;
                        productName = item.plat.nom || 'Plat';
                    }
                } else if (item.produit) {
                    price = item.produit.prixVente || 0;
                    productName = item.produit.nom || 'Produit';
                }
                
                const total = price * item.quantite;
                const panierId = item.panier_id || 0;
                const itemType = item.plat ? 'plat' : 'produit';
                
                const cartItem = $('<div class="cart-item"></div>');
                
                const qtyControl = $('<div class="qty-control"></div>');
                qtyControl.append('<button class="qty-btn" onclick="updateCartQuantity(' + panierId + ', \'' + itemType + '\', -1)">-</button>');
                qtyControl.append('<span class="qty-value">' + item.quantite + '</span>');
                qtyControl.append('<button class="qty-btn" onclick="updateCartQuantity(' + panierId + ', \'' + itemType + '\', 1)">+</button>');
                
                const qtyCell = $('<div class="cart-item-qty"></div>').append(qtyControl);
                
                // NOM AVEC PADDING-LEFT AJOUTÉ
                const nameCell = $('<div class="cart-item-name" title="' + productName + '">' + productName + '</div>');
                
                const priceCell = $('<div class="cart-item-price">' + total.toFixed(0) + '</div>');
                
                // BOUTON SUPPRESSION AVEC ICÔNE POUBELLE
                const actionCell = $('<div class="cart-item-actions"></div>');
                actionCell.append('<button class="remove-item-btn" onclick="removeFromCart(' + panierId + ', \'' + itemType + '\')" title="Supprimer">' +
                                 '<i class="fas fa-trash"></i>' + // ICÔNE POUBELLE
                                 '</button>');
                
                cartItem.append(qtyCell);
                cartItem.append(nameCell);
                cartItem.append(priceCell);
                cartItem.append(actionCell);
                
                itemsList.append(cartItem);
            });
            
            container.append(itemsList);
        }
        
        // Mettre à jour la quantité d'un item
        function updateCartQuantity(panierId, type, change) {
            const action = change > 0 ? 'augmenter' : 'diminuer';
            
            $.ajax({
                url: 'PanierServlet',
                type: 'POST',
                data: {
                    action: action,
                    type: type,
                    id: panierId
                },
                success: function(response) {
                    loadCart();
                },
                error: function(xhr, status, error) {
                    console.error("Erreur mise à jour quantité:", error);
                    showToast('Erreur quantité', 'error');
                }
            });
        }
        
        // Supprimer du panier
        function removeFromCart(panierId, type) {
            if (confirm('Retirer cet article ?')) {
                $.ajax({
                    url: 'PanierServlet',
                    type: 'POST',
                    data: {
                        action: 'supprimer',
                        type: type,
                        id: panierId
                    },
                    success: function(response) {
                        loadCart();
                        showToast('Article retiré', 'success');
                    },
                    error: function(xhr, status, error) {
                        console.error("Erreur suppression:", error);
                        showToast('Erreur suppression', 'error');
                    }
                });
            }
        }
        
        // Vider complètement le panier
        function clearCart() {
            cart.items = [];
            cart.count = 0;
            cart.total = 0;
            cart.totalUSD = 0;
            discountAmount = 0;
            discountAmountHTG = 0;
            isDiscountEnabled = false;
            
            updateCartDisplay();
            updateTotalDisplay();
            $('#discountAmount').val(0);
        }
        
        // Recherche de produits
        function searchProducts() {
            const searchTerm = $('#product-search').val().toLowerCase();
            if (!searchTerm) {
                $('.product-item').show();
                return;
            }
            
            $('.product-item').each(function() {
                const card = $(this).find('.product-card');
                const name = card.attr('data-product-name') || '';
                if (name.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        // Vider les produits
        function clearProducts() {
            $('#products-container').empty();
            $('#products-container').append('<div class="text-center py-5" style="color: #aaa; width: 100%;">Sélectionnez</div>');
            filteredProducts = [];
        }
        
        // ============================================
        // FONCTIONS UTILITAIRES
        // ============================================
        
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
                'Rhum': 'glass-whiskey',
                'Restaurant': 'utensils',
                'Bar': 'cocktail',
                'Cafétéria': 'coffee',
                'Fast-Food': 'hamburger',
                'Boulangerie': 'bread-slice',
                'Pâtisserie': 'birthday-cake',
                'Glacier': 'ice-cream',
                'Boucherie': 'drumstick-bite',
                'Poissonnerie': 'fish',
                'Primeurs': 'carrot',
                'Épicerie': 'shopping-basket',
                'Surgelés': 'snowflake',
                'Conserves': 'jar',
                'Liquides': 'wine-bottle',
                'Secs': 'weight',
                'Frais': 'leaf'
            };
            
            const lowerName = rayonNom.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key.toLowerCase())) {
                    return icons[key];
                }
            }
            
            return 'tag';
        }
        
        // Obtenir l'icône pour une catégorie
        function getCategoryIcon(categoryName) {
            const icons = {
                'tabac': 'smoking',
                'cigarette': 'smoking',
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
                'champagne': 'wine-bottle'
            };
            
            const lowerName = categoryName.toLowerCase();
            for (const key in icons) {
                if (lowerName.includes(key)) {
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
        
        function showNoContent(message) {
            $('#state-message').text(message).show();
        }
        
        function showError(message) {
            $('#state-message').text(message).css('color', '#ef5350').show();
        }
        
        function showToast(message, type) {
            const toastClass = type === 'success' ? 'bg-success' : 'bg-danger';
            const toastIcon = type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-circle';
            
            const toastHtml = '<div class="toast ' + toastClass + ' text-white" style="position: fixed; bottom: 15px; right: 15px; z-index: 1050; min-width: 180px; font-size: 0.8rem;">' +
                            '<div class="toast-body p-2">' +
                            '<div class="d-flex align-items-center">' +
                            '<i class="' + toastIcon + ' mr-2"></i>' +
                            '<span>' + message + '</span>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
            
            $('body').append(toastHtml);
            const toast = $('.toast').last();
            toast.toast({ delay: 3000 }).toast('show');
            toast.on('hidden.bs.toast', function() {
                $(this).remove();
            });
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
            
            const options = { day: 'numeric', month: 'short', year: 'numeric' };
            const dateStr = now.toLocaleDateString('fr-FR', options);
            $('#current-date').text(dateStr);
        }
        
        // Fonctions des boutons de navigation
        function goBack() {
            if (currentSousCategory) {
                currentSousCategory = null;
                currentSubCategoryId = null;
                $('.subcategory-card').removeClass('selected-card');
                clearProducts();
            } else if (currentCategory) {
                currentCategory = null;
                currentCategoryId = null;
                currentCategoryName = null;
                $('.category-card').removeClass('selected-card');
                $('#subcategories-container').empty();
                clearProducts();
            }
        }
        
        function showSearch() {
            $('#product-search').focus();
        }
        
        function showPLU() {
            alert('Fonctionnalité PLU à implémenter');
        }
        
        function showSettings() {
            alert('Paramètres à implémenter');
        }
        
        function showTaux() {
            window.open('TauxServlet?action=lister', '_blank');
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
            updateClock();
            setInterval(updateClock, 1000);
            
            loadRayons();
            loadCart();
            loadExchangeRate();
            
            $('#discountAmount').on('input', function() {
                const value = parseFloat($(this).val());
                if (!isNaN(value) && value >= 0) {
                    discountAmount = value;
                    isDiscountEnabled = value > 0;
                    updateTotalDisplay();
                }
            });
            
            $('#nomClient').on('input', function() {
                validateClient();
            });
            
            setTimeout(() => {
                $('#state-message').hide();
            }, 1000);
        });
    </script>
</body>
</html>