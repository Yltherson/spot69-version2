<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<%@ include file="header.jsp"%>




<!-- Section Services -->
<section class="services-section">
	<h2 class="section-title">Nos Services</h2>
	<div class="services-grid">
		<div class="service-card">
			<div class="service-icon">
				<i class="fas fa-concierge-bell"></i>
			</div>
			<h3>Restaurant Gastronomique</h3>
			<p>Cuisine haïtienne authentique et plats internationaux préparés
				avec les meilleurs ingrédients locaux et importés.</p>
		</div>
		<div class="service-card">
			<div class="service-icon">
				<i class="fas fa-hotel"></i>
			</div>
			<h3>Chambres Confortables</h3>
			<p>Chambres et suites modernes avec service en chambre 24h/24,
				WiFi gratuit et vue imprenable sur la ville.</p>
		</div>
		<div class="service-card">
			<div class="service-icon">
				<i class="fas fa-umbrella-beach"></i>
			</div>
			<h3>Rooftop Lounge</h3>
			<p>Espace rooftop exceptionnel avec vue panoramique sur
				Port-au-Prince, idéal pour un verre de rosé ou des cocktails au
				coucher du soleil.</p>
		</div>
	</div>
</section>

<!-- Section À propos de nous -->
<section id="about" class="about-section">
	<div class="about-content">
		<h2 class="section-title">À Propos de Nous</h2>
		<p class="about-intro">
			<strong>Nos engagements pour un modèle durable et
				responsable :</strong>
		</p>

		<div class="about-block">
			<i class="fas fa-leaf icon"></i>
			<div>
				<h3>Soutenir l’agriculture locale</h3>
				<p>Nous nous approvisionnons directement auprès des paysans
					haïtiens, encourageant une production responsable. En privilégiant
					les circuits courts, nous réduisons notre empreinte carbone et
					valorisons les produits frais.</p>
			</div>
		</div>

		<div class="about-block">
			<i class="fas fa-recycle icon"></i>
			<div>
				<h3>Réduction des déchets et gestion durable</h3>
				<ul>
					<li><strong>Zéro plastique</strong> : ustensiles réutilisables
						ou biodégradables</li>
					<li><strong>Compostage</strong> : valorisation des déchets
						organiques</li>
					<li><strong>Tri sélectif</strong> : recyclage systématique</li>
				</ul>
			</div>
		</div>

		<div class="about-block">
			<i class="fas fa-solar-panel icon"></i>
			<div>
				<h3>Énergie et ressources responsables</h3>
				<ul>
					<li><strong>Énergie solaire</strong> : panneaux
						photovoltaïques</li>
					<li><strong>Économie d’eau</strong> : systèmes de récupération
						de pluie</li>
				</ul>
			</div>
		</div>

		<div class="about-block">
			<i class="fas fa-handshake icon"></i>
			<div>
				<h3>Travail équitable et inclusion</h3>
				<p>Nous privilégions l’embauche locale, offrons des formations
					internes et des horaires flexibles pour encourager l’inclusion
					sociale.</p>
			</div>
		</div>

		<div class="about-block">
			<i class="fas fa-seedling icon"></i>
			<div>
				<h3>Made in Haïti</h3>
				<p>Des plats revisités, des boissons artisanales, des
					partenariats avec des producteurs et artisans locaux. 100% haïtien,
					100% authentique.</p>
			</div>
		</div>

		<div class="about-block">
			<i class="fas fa-globe-americas icon"></i>
			<div>
				<h3>Sensibilisation et communauté</h3>
				<p>Nous organisons des ateliers sur l’écologie et l’agriculture
					responsable. Depuis notre lancement en 2025, nous inspirons une
					nouvelle génération d’entreprises durables.</p>
			</div>
		</div>

		<p class="about-closing">
			Chez Spot69, chaque repas, chaque séjour et chaque moment partagé est
			l’occasion de célébrer la richesse d’Haïti tout en protégeant notre
			belle planète. 🌱✨<br /> <strong>Rejoignez-nous dans cette
				aventure responsable !</strong>
		</p>
	</div>
</section>

<!-- Formulaire Contact -->
<div id="contact" class="contact-container">
	<h2 class="section-title">Nous Contactez</h2>
	<div class="contact-grid">
		<div class="form-section">
			<form action="#" method="post">
				<div class="form-group">
					<label for="name">NOM ET PRÉNOM *</label> <input type="text"
						id="name" name="name" required placeholder="Votre nom complet" />
				</div>

				<div class="form-group">
					<label for="phone">NUMÉRO DE TÉLÉPHONE *</label> <input type="tel"
						id="phone" name="phone" required
						placeholder="Votre numéro de téléphone" />
				</div>

				<div class="form-group">
					<label for="email">ADRESSE EMAIL *</label> <input type="email"
						id="email" name="email" required placeholder="Votre adresse email" />
				</div>

				<div class="form-group">
					<label for="message">VOTRE MESSAGE</label>
					<textarea id="message" name="message"
						placeholder="Écrivez votre question ou demande de réservation ici..."></textarea>
				</div>

				<button type="submit" class="submit-btn">
					ENVOYER LE MESSAGE <i class="fas fa-paper-plane"></i>
				</button>
			</form>
		</div>

		<div class="map-section">
			<h3 style="color: var(--gold); margin-bottom: 15px">NOTRE
				ADRESSE</h3>
			<p style="margin-bottom: 20px">
				<i class="fas fa-map-marker-alt" style="color: var(--gold)"></i> <strong>#369
					Autoroute de Delmas, Port-au-Prince, Haïti</strong>
			</p>

			<div style="margin-bottom: 20px">
				<p>
					<i class="fas fa-phone-alt" style="color: var(--gold)"></i> <strong>3901-6969</strong>
				</p>
				<p>
					<i class="fas fa-envelope" style="color: var(--gold)"></i> <strong>info@the69spot.com</strong>
				</p>
			</div>

			<div style="margin-top: 20px">
				<h4 style="color: var(--gold); margin-bottom: 10px">HEURES
					D'OUVERTURE :</h4>
				<p>
					<strong>Notre hôtel :</strong> 24h/24, 7 jours sur 7
				</p>
				<p>
					<strong>Restaurant :</strong> 10:00 AM - 12:00 AM (minuit) | Tous
					les jours
				</p>
				<div
					style="margin-top: 30px; border-radius: 10px; overflow: hidden; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);">
					<iframe
						src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3783.0036348361787!2d-72.29016412532084!3d18.528737882566432!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x8eb9e700038ff335%3A0x2e9c823470188946!2sSpot%2060!5e0!3m2!1sfr!2sht!4v1751985995471!5m2!1sfr!2sht"
						width="600" height="250" style="border: 0" allowfullscreen=""
						loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
				</div>
			</div>
		</div>
	</div>
</div>

<%@ include file="footer.jsp"%>
