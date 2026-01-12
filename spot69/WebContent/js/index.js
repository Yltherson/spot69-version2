const toggleBtn = document.getElementById("mobileMenuToggle");
    const mobileNav = document.getElementById("mobileNav");
    const closeBtn = document.getElementById("closeMobileNav");
    const overlay = document.getElementById("mobileNavOverlay");

    toggleBtn.addEventListener("click", () => {
      mobileNav.classList.add("show");
      overlay.style.display = "block";
    });

    closeBtn.addEventListener("click", () => {
      mobileNav.classList.remove("show");
      overlay.style.display = "none";
    });

    overlay.addEventListener("click", () => {
      mobileNav.classList.remove("show");
      overlay.style.display = "none";
    });

    mobileNav.querySelectorAll("a").forEach((link) => {
      link.addEventListener("click", () => {
        mobileNav.classList.remove("show");
        overlay.style.display = "none";
      });
    });

    // Fè navbar la chanje lè w scroll
    window.addEventListener("scroll", function () {
      const navbar = document.querySelector(".navbar");
      if (window.scrollY > 50) {
        navbar.style.background = "rgba(10, 10, 10, 0.9)";
        navbar.style.padding = "15px 10%";
        navbar.style.boxShadow = "0 5px 15px rgba(0,0,0,0.3)";
      } else {
        navbar.style.background = "transparent";
        navbar.style.padding = "25px 10%";
        navbar.style.boxShadow = "none";
      }
    });

    // SSCRIPT MODAL
    // Récupérer les éléments
    const modal = document.getElementById("reservationModal");
    const btn = document.getElementById("openModal");
    const span = document.getElementsByClassName("close")[0];
    const form = document.querySelector(".reservation-form");

    // Ouvrir la modale quand on clique sur le bouton
    btn.onclick = function () {
      modal.style.display = "block";
    };

    // Fermer la modale quand on clique sur (x)
    span.onclick = function () {
      modal.style.display = "none";
    };

    // Fermer la modale quand on clique en dehors
    window.onclick = function (event) {
      if (event.target == modal) {
        modal.style.display = "none";
      }
    };

    // Gérer la soumission du formulaire
    form.addEventListener("submit", function (e) {
      e.preventDefault();

      // Récupérer les valeurs du formulaire
      const people = document.getElementById("people").value;
      const date = document.getElementById("date").value;
      const time = document.getElementById("time").value;
      const comments = document.getElementById("comments").value;
      const starter = document.getElementById("starter").value;
      const main = document.getElementById("main").value;

      // Ici vous pourriez envoyer ces données à un serveur
      console.log("Réservation soumise:", {
        people,
        date,
        time,
        comments,
        starter,
        main,
      });

      // Afficher un message de confirmation
      alert("Votre réservation a bien été enregistrée !");

      // Fermer la modale
      modal.style.display = "none";

      // Réinitialiser le formulaire
      form.reset();
    });