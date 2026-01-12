 const loginModal = document.getElementById("loginModal");
  const registerModal = document.getElementById("registerModal");

  const loginBtns = document.querySelectorAll(".login-pc, .login-phone, .login-admin-pc, .login-admin-phone");
  const registerLink = document.querySelector(".register-link a"); // dans login
  const backToLogin = document.getElementById("backToLogin");
  
  const closeLoginBtn = document.querySelector(".close-login");
  const closeRegisterBtn = document.querySelector(".close-register");
  const redirectInput = document.querySelector('input[name="redirectTo"]');

  document.querySelectorAll(".login-admin-pc, .login-admin-phone").forEach(btn => {
    btn.addEventListener("click", e => {
      e.preventDefault();
      if (redirectInput) redirectInput.value = "dashboard";
      loginModal.style.display = "block";
    });
  });

  document.querySelectorAll(".login-pc, .login-phone").forEach(btn => {
    btn.addEventListener("click", e => {
      e.preventDefault();
      if (redirectInput) redirectInput.value = "website";
      loginModal.style.display = "block";
    });
  });


  // Ouvrir login
 /* loginBtns.forEach(btn => {
    btn.addEventListener("click", e => {
      e.preventDefault();
      loginModal.style.display = "block";
    });
  });
*/
  // Ouvrir register depuis login
  registerLink.addEventListener("click", e => {
    e.preventDefault();
    loginModal.style.display = "none";
    registerModal.style.display = "block";
  });

  // Revenir au login depuis register
  backToLogin.addEventListener("click", e => {
    e.preventDefault();
    registerModal.style.display = "none";
    loginModal.style.display = "block";
  });
  
  //Pour fermer les modal
  closeLoginBtn.addEventListener("click", () => {
  loginModal.style.display = "none";
});

closeRegisterBtn.addEventListener("click", () => {
  registerModal.style.display = "none";
});

  // Fermer si clic hors contenu
  window.addEventListener("click", e => {
    if (e.target === loginModal) loginModal.style.display = "none";
    if (e.target === registerModal) registerModal.style.display = "none";
  });
  
  
  /*TOAST POIR LES MESSAGES*/
  function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    const toastText = document.getElementById('toast-text');

    toastText.innerText = message;
    toast.className = `toast-message ${type}`; // success ou error
    setTimeout(() => {
      toast.classList.remove('hidden');
    }, 100); // petit délai pour l’animation

    setTimeout(() => {
      hideToast();
    }, 4000); // disparaît après 4s
  }

  function hideToast() {
    const toast = document.getElementById('toast');
    toast.classList.add('hidden');
  }
  
  
  