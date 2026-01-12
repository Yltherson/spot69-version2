document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll("form");

  forms.forEach(form => {
    form.addEventListener("submit", function (e) {
      if (!form.checkValidity()) return;

      const submitBtn = form.querySelector('button[type="submit"].btn');
      if (submitBtn && !submitBtn.classList.contains('loading')) {
        submitBtn.classList.add("loading");
        const btnText = submitBtn.querySelector(".btn-text");
        const spinner = submitBtn.querySelector(".spinner-border");
        if (btnText) btnText.classList.add("d-none");
        if (spinner) spinner.classList.remove("d-none");
        submitBtn.disabled = true;
      }
    });
  });

  function updateColor(select) {
    // Enlever les anciennes classes
    select.classList.remove(
      'statut-EN_PREPARATION',
      'statut-PRETE',
      'statut-ANNULE',
      'statut-EN_ATTENTE',
      'statut-LIVRE'
    );
    // Ajouter la classe correspondant à la valeur actuelle
    const newClass = 'statut-' + select.value;
    select.classList.add(newClass);
  }

  const selects = document.querySelectorAll('.statut-select');

  selects.forEach(select => {
    updateColor(select); // Applique la couleur au chargement
    select.addEventListener('change', () => updateColor(select)); // Met à jour dynamiquement
  });
});
