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
    <link rel="stylesheet" href="css/feather.css">

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
<link rel="stylesheet" href="style2.css">
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