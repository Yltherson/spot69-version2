<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<meta charset="UTF-8">
	
<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />
<script>
    // Redirection immédiate
    window.location.href = "MouvementStockServlet?action=lister";
</script>
<main role="main" class="main-content">
	<div class="container-fluid">
		<div class="row justify-content-center">
			<div class="col-12">
				<div class="row">
					<div class="col-md-6 col-xl-3 mb-4">
						<div class="card shadow bg-primary text-white border-0">
							<div class="card-body">
								<div class="row align-items-center">
									<div class="col-3 text-center">
										<span class="circle circle-sm bg-primary-light"> <i
											class="fe fe-16 fe-users text-white mb-0"></i>
										</span>
									</div>
									<div class="col pr-0">
										<p class="small text-muted mb-0">Utilisateurs</p>
										<span class="h3 mb-0 text-white">125</span>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6 col-xl-3 mb-4">
						<div class="card shadow border-0">
							<div class="card-body">
								<div class="row align-items-center">
									<div class="col-3 text-center">
										<span class="circle circle-sm bg-primary"> <i
											class="fe fe-16 fe-home text-white mb-0"></i>
										</span>
									</div>
									<div class="col pr-0">
										<p class="small text-muted mb-0">Chambres</p>
										<span class="h3 mb-0">19</span>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6 col-xl-3 mb-4">
						<div class="card shadow border-0">
							<div class="card-body">
								<div class="row align-items-center">
									<div class="col-3 text-center">
										<span class="circle circle-sm bg-primary"> <i
											class="fe fe-16 fe-filter text-white mb-0"></i>
										</span>
									</div>
									<div class="col">
										<p class="small text-muted mb-0">Check-in</p>
										<div class="row align-items-center no-gutters">
											<div class="col-auto">
												<span class="h3 mr-2 mb-0"> 5 </span>
											</div>
											<div class="col-md-12 col-lg">
												<div class="progress progress-sm mt-2" style="height: 3px">
													<div class="progress-bar bg-success" role="progressbar"
														style="width: 87%" aria-valuenow="87" aria-valuemin="0"
														aria-valuemax="100"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6 col-xl-3 mb-4">
						<div class="card shadow border-0">
							<div class="card-body">
								<div class="row align-items-center">
									<div class="col-3 text-center">
										<span class="circle circle-sm bg-primary"> <i
											class="fe fe-16 fe-activity text-white mb-0"></i>
										</span>
									</div>
									<div class="col">
										<p class="small text-muted mb-0">Montant</p>
										<span class="h3 mb-0">7 060</span> HTG
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="card shadow eq-card mb-4">
							<div class="card-body">

								<!-- Titre principal -->
								<div
									class="card-title d-flex justify-content-between align-items-center">
									<strong> <i class="fe fe-home fe-16 mr-2 text-primary"></i>
										Chambres
									</strong> <a class="small text-muted" href="#!"> <i
										class="fe fe-dollar-sign fe-12 text-muted mr-1"></i> Montant
										gagné
									</a>
								</div>

								<!-- Chambres -->
								<div class="row mt-b">
									<div class="col-6 text-center mt-3 border-right">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-check-circle text-warning mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Disponible</p>
									      <h6 class="mb-0">2680</h6>
									    </div>
									  </div>
									</div>
									
									
									<div class="col-6 text-center mt-3">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-alert-triangle text-warning mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Occupé</p>
									      <h6 class="mb-0">680</h6>
									    </div>
									  </div>
									</div>

								</div>

								<!-- Réservations -->
								<div class="card-title d-flex align-items-center mt-4">
									<strong> <i
										class="fe fe-calendar fe-16 mr-2 text-info"></i> Réservations
									</strong>
								</div>
								<div class="row mt-b">
								<div class="col-6 text-center mt-3 border-right">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-calendar text-info mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Aujourd'hui</p>
									      <h6 class="mb-0">260</h6>
									    </div>
									  </div>
									</div>
									
									
									<div class="col-6 text-center mt-3">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-clock text-secondary mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Hier</p>
									      <h6 class="mb-0">60</h6>
									    </div>
									  </div>
									</div>
									
								</div>

								<!-- Commandes -->
								<div class="card-title d-flex align-items-center mt-4">
									<strong> <i
										class="fe fe-shopping-cart fe-16 mr-2 text-success"></i>
										Commandes
									</strong>
								</div>
								<div class="row mt-b">
								<div class="col-6 text-center mt-3 border-right">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-shopping-bag text-success mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Aujourd'hui</p>
									      <h6 class="mb-0">260</h6>
									    </div>
									  </div>
									</div>
									
									
									<div class="col-6 text-center mt-3">
									  <div class="d-flex align-items-center justify-content-center">
									    <i class="fe fe-clock text-secondary mr-3" style="font-size: 2rem;"></i>
									    <div class="text-left">
									      <p class="text-muted mb-1">Hier</p>
									      <h6 class="mb-0">60</h6>
									    </div>
									  </div>
									</div>
									
									
								</div>

							</div>
						</div>
					</div>


					<div class="col-md-6">
						<div class="card shadow eq-card timeline">
							<div class="card-header">
								<strong class="card-title">Notification</strong> <a
									class="float-right small text-muted" href="#!"></a>
							</div>
							<div class="card-body" data-simplebar
								style="height: 360px; overflow-y: auto; overflow-x: hidden;">
								<div class="pb-3 timeline-item item-warning">
									<div class="pl-5">
										<div class="mb-1 small">
											L'heure pour la </span><strong>chambre #3</strong> est ecoule
										</div>
										<p class="small text-muted">
											System <span class="badge badge-light">3h40</span>
										</p>
									</div>
								</div>
								<div class="pb-3 timeline-item item-primary">
									<div class="pl-5">
										<div class="mb-1 small">
											La </span><strong>chambre #5</strong> est disponible
										</div>
										<p class="small text-muted">
											System <span class="badge badge-light">1h56</span>
										</p>
									</div>
								</div>
								<div class="pb-3 timeline-item item-warning">
									<div class="pl-5">
										<div class="mb-3 small">
											<span class="text-muted mx-2">Plus de <strong>chambre</strong>
												disponible
											</span>
										</div>
										<p class="small text-muted">
											System <span class="badge badge-light">2h24</span>
										</p>
									</div>
								</div>

							</div>
							<!-- / .card-body -->
						</div>
						<!-- / .card -->
					</div>
				</div>

			</div>
		</div>
		<!-- .row -->
	</div>
</main>
<jsp:include page="footer.jsp" />
<!-- Redirection immédiate -->
