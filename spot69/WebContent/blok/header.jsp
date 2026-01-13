<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
   <link rel="icon" type="image/png" href="image/d-logo.png">
<meta name="author" content="">
<link rel="icon" href="favicon.ico">
<title>La Divinité de Dieu</title>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="style2.css">

<!-- Simple bar CSS -->
<link rel="stylesheet" href="css/simplebar.css">
<!-- Fonts CSS -->
<link
	href="https://fonts.googleapis.com/css2?family=Overpass:ital,wght@0,100;0,200;0,300;0,400;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,600;1,700;1,800;1,900&display=swap"
	rel="stylesheet">
<!-- Icons CSS -->
<link rel="stylesheet" href="css/feather.css">
<link rel="stylesheet" href="css/custom.css">
<link rel="stylesheet" href="css/select2.css">
<link rel="stylesheet" href="css/dataTables.bootstrap4.css">
<link rel="stylesheet" href="css/dropzone.css">
<link rel="stylesheet" href="css/uppy.min.css">
<link rel="stylesheet" href="css/jquery.steps.css">
<link rel="stylesheet" href="css/jquery.timepicker.css">
<link rel="stylesheet" href="css/quill.snow.css">
<!-- Date Range Picker CSS -->
<link rel="stylesheet" href="css/daterangepicker.css">
<!-- App CSS -->
<link rel="stylesheet" href="css/app-light.css" id="lightTheme" disabled>
<link rel="stylesheet" href="css/app-dark.css" id="darkTheme">
</head>
<body class="vertical  dark  ">
<style>
.custom-toast {
	background-color: var(--gray);
	color: var(--light);
	border-left: 4px solid var(--gold);
	min-width: 300px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.custom-toast .toast-header {
	background-color: var(--dark);
	color: var(--gold);
	border-bottom: 1px solid var(--gray);
}

.custom-toast .toast-body {
	color: var(--light-gray);
}

.custom-toast .close {
	color: var(--light-gray);
}

.toast-container {
	position: fixed;
	top: 1rem;
	right: 1rem;
	z-index: 9999;
}

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
            padding-bottom: 140px;
            padding-top: 100px;
        }

        /* --- UTILITAIRES --- */
        .no-gutters { margin-right: 0; margin-left: 0; }
        .no-gutters > .col, .no-gutters > [class*="col-"] { padding-right: 0; padding-left: 0; }
        .full-height { height: 100%; }
        .text-xs { font-size: 0.7rem; }
        
        /* --- HEADER (TOP BAR) --- */
        /* .top-bar {
            background-color: #000;
            height: 100px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 15px;
            border-bottom: 1px solid var(--border-color);
        } */
        
        .top-bar {
	    position: fixed;
	    top: 0;
	    left: 0;
	    right: 0;
	    height: 100px;
	    background-color: #000;
	    z-index: 1000;
	    display: flex;
	    align-items: center;
	    justify-content: space-between;
	    padding: 0 20px;
	}
        
        .top-icon {
            color: white;
            text-align: center;
            font-size: 0.8rem;
            margin: 0 10px;
            cursor: pointer;
        }
        .top-icon i { display: block; font-size: 1.2rem; margin-bottom: 2px; }
        .brand-logo { color: #ef5350; font-size: 1.8rem; font-weight: bold; }
        .sold { color: #66bb6a; font-size: 1.8rem; font-weight: bold; }

        /* --- MAIN LAYOUT --- */
        .main-workspace {
            flex: 1;
            display: flex;
            overflow: hidden;
        }

        /* --- LEFT TOOLBAR (Vertical) --- */
        .left-tools {
            width: 120px;
            background-color: #111;
            display: flex;
            flex-direction: column;
            border-right: 1px solid var(--border-color);
            overflow-y: auto;
        }
        .tool-btn {
            height: 70px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: #ccc;
            border-bottom: 1px solid #333;
            cursor: pointer;
            position: relative;
        }
        .tool-btn:hover { background-color: #333; color: white; }
        .tool-btn i { font-size: 1.4rem; margin-bottom: 5px; }
        .tool-btn span { font-size: 0.65rem; text-transform: uppercase; }
        .tool-btn.active { 
            background-color: #444; 
            color: #fdd835;
            border-left: 3px solid #fdd835;
        }
        .tool-btn.rayon-btn {
            border-bottom: 1px solid #444;
        }
        .rayon-badge {
            position: absolute;
            top: 5px;
            right: 5px;
            background-color: #ef5350;
            color: white;
            font-size: 0.6rem;
            padding: 1px 3px;
            border-radius: 3px;
        }

        /* --- PRODUCT AREA --- */
        .product-area {
            flex: 1;
            padding: 5px;
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
            min-width: 150px;
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
            height: 100px;
            margin: 5px;
            border-radius: 4px;
            padding: 8px;
            position: relative;
            cursor: pointer;
            transition: transform 0.1s, opacity 0.2s;
            color: white;
            font-weight: 500;
            font-size: 0.9rem;
            line-height: 1.1;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            width: 150px;
        }
        
        .category-card:hover, .subcategory-card:hover, .product-card:hover { 
            opacity: 0.9; 
            transform: translateY(-2px);
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
            padding: 10px 5px;
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
            bottom: 5px;
            right: 8px;
            font-size: 0.85rem;
            background: rgba(0,0,0,0.7);
            padding: 2px 8px;
            border-radius: 12px;
            font-weight: bold;
            z-index: 3;
        }
        
        .card-code {
            position: absolute;
            bottom: 5px;
            left: 8px;
            font-size: 0.7rem;
            opacity: 0.8;
            text-transform: uppercase;
            background: rgba(0,0,0,0.5);
            padding: 2px 6px;
            border-radius: 3px;
            z-index: 3;
        }
        
        .card-points {
            position: absolute;
            top: 5px;
            right: 8px;
            font-size: 0.7rem;
            background: rgba(255,255,255,0.9);
            color: #333;
            padding: 2px 6px;
            border-radius: 10px;
            font-weight: bold;
            z-index: 3;
        }
        
        .card-title {
            font-weight: bold;
            font-size: 0.9rem;
            margin-bottom: 3px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            text-align: center;
            width: 100%;
        }
        
        /* Style spécifique pour les titres de catégories et sous-catégories */
        .category-card .card-title,
        .subcategory-card .card-title {
            margin-top: 8px;
            margin-bottom: 0;
            font-size: 0.85rem;
        }
        
        .card-description {
            font-size: 0.75rem;
            opacity: 0.9;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            flex-grow: 1;
        }
        
        .card-image-icon {
            position: absolute;
            bottom: 5px;
            right: 5px;
            font-size: 1rem;
            opacity: 0.7;
            z-index: 3;
        }
        
        /* Icône centrée pour catégories et sous-catégories */
        .category-icon,
        .subcategory-icon {
            font-size: 1.8rem;
            margin-bottom: 8px;
            opacity: 0.9;
        }
        
        .selected-card {
            border: 3px solid #fdd835 !important;
            box-shadow: 0 0 10px rgba(253, 216, 53, 0.5);
        }

        /* En-têtes de section */
        .section-header {
            padding: 10px;
            background-color: #333;
            color: #fdd835;
            font-size: 1rem;
            font-weight: bold;
            border-bottom: 1px solid #444;
            text-align: center;
        }
        
        .section-subheader {
            padding: 8px;
            background-color: #2a2a2a;
            color: #ccc;
            font-size: 0.85rem;
            border-bottom: 1px solid #444;
            text-align: center;
        }

        /* --- TICKET AREA (Right Side) --- */
        .ticket-area {
            width: 400px;
            background-color: var(--bg-ticket);
            display: flex;
            flex-direction: column;
            border-left: 1px solid var(--border-color);
        }
        
        .ticket-header {
            padding: 10px;
            background-color: #222;
        }
        
        .ticket-tabs .nav-link {
            background-color: #3e3e3e;
            color: #ccc;
            border: none;
            margin-right: 2px;
            border-radius: 0;
            padding: 8px 15px;
            font-size: 0.85rem;
        }
        .ticket-tabs .nav-link.active {
            background-color: #5d6166;
            color: white;
        }

        .order-list {
            flex: 1;
            overflow-y: auto;
            font-size: 0.9rem;
        }
        .table-dark { background-color: transparent; }
        .table-dark th { border-top: none; border-bottom: 1px solid #444; color: #aaa; font-size: 0.75rem; font-weight: normal; }
        .table-dark td { border: none; padding: 0.5rem 0.75rem; vertical-align: middle; }
        
        .sub-item td:nth-child(3) { padding-left: 20px; color: #bbb; font-size: 0.85rem; font-style: italic; }
        
        

        /* --- BOTTOM NAV --- */
        /* .bottom-nav {
            height: 120px;
            background-color: #111;
            display: flex;
            align-items: center;
            padding: 0 10px;
            border-top: 1px solid #333;
        } */
        
        .bottom-nav {
	    position: fixed;
	    bottom: 0;
	    left: 0;
	    width: 100%;
	    height: 120px;
	    background-color: #111;
	    display: flex;
	    align-items: center;
	    padding: 0 10px;
	    border-top: 1px solid #333;
	    z-index: 1000; /* Pour être au-dessus des autres éléments */
	}
        
        .user-profile {
            width: 70px;
            text-align: center;
            border-right: 1px solid #333;
            margin-right: 10px;
        }
        .avatar-img { width: 30px; height: 30px; border-radius: 50%; object-fit: cover; margin-bottom: 5px; }

        .category-scroll {
            display: flex;
            flex: 1;
            overflow-x: auto;
            gap: 10px;
            padding: 10px 0;
        }
        
        .cat-btn {
            min-width: 110px;
            height: 90px;
            background-color: #333;
            border-radius: 8px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: white;
            cursor: pointer;
        }
        .cat-btn i { font-size: 1.5rem; margin-bottom: 4px; }
        .cat-btn span { font-size: 1rem; }
        .cat-btn.active { background-color: #444; color: #fdd835; }
        .cat-btn.bg-teal { background-color: #009688; }
        
        
        
        /* Responsive */
        @media (max-width: 1400px) {
            .product-item {
                width: calc(33.333% - 5px);
            }
        }
        
        @media (max-width: 1200px) {
            .product-item {
                width: calc(50% - 5px);
            }
        }
        
        .main-content{
        	
        	margin-left: unset !important;
        }
</style>



	<!-- TOAST POUR LA NOTIFICATION -->
	<%
	String toastSuccess = (String) session.getAttribute("ToastAdmSuccesNotif");
	String toastError = (String) session.getAttribute("ToastAdmErrorNotif");
	if (toastSuccess != null || toastError != null) {
	%>
	<!-- TOAST CONTAINER -->
	<div class="toast-container">
		<div id="toast-message" class="toast custom-toast fade" role="alert"
			aria-live="assertive" aria-atomic="true">
			<div class="toast-header">
				<strong class="mr-auto"><%=(toastSuccess != null) ? "Succès" : "Erreur"%></strong>
				<button type="button" class="ml-2 mb-1 close" onclick="closeToast()"
					aria-label="Fermer">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="toast-body">
				<%=(toastSuccess != null) ? toastSuccess : toastError%>
			</div>
		</div>
	</div>

	<script>
    // Affiche et masque le toast sans jQuery
    document.addEventListener('DOMContentLoaded', function () {
        const toast = document.getElementById('toast-message');
        toast.classList.add('show'); // affiche le toast (Bootstrap .show)

        // Masquer automatiquement après 5 secondes (5000 ms)
        setTimeout(() => {
            closeToast();
        }, 5000);
    });

    // Fonction pour fermer le toast
    function closeToast() {
        const toast = document.getElementById('toast-message');
        if (toast) {
            toast.classList.remove('show');
            toast.classList.add('hide');
        }
    }
</script>

	<%
	// Supprime les messages après affichage
	session.removeAttribute("ToastAdmSuccesNotif");
	session.removeAttribute("ToastAdmErrorNotif");
	}
	%>