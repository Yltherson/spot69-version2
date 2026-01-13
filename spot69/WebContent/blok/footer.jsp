<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <meta charset="UTF-8">
<!DOCTYPE html>



    </div> <!-- .wrapper -->
    
    <script src="js/jquery.min.js"></script>
    <script src="js/popper.min.js"></script>
    <script src="js/moment.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/simplebar.min.js"></script>
    <script src='js/daterangepicker.js'></script>
    <script src='js/jquery.stickOnScroll.js'></script>
    <script src="js/tinycolor-min.js"></script>
     <script src="js/custom.js"></script>
   <!--  <script src="js/config.js"></script> -->
    <script src="js/d3.min.js"></script>
    <script src="js/topojson.min.js"></script>
    <script src="js/datamaps.all.min.js"></script>
    <script src="js/datamaps-zoomto.js"></script>
    <script src="js/datamaps.custom.js"></script>
    <script src="js/Chart.min.js"></script>
    <script>
      /* defind global options */
      Chart.defaults.global.defaultFontFamily = base.defaultFontFamily;
      Chart.defaults.global.defaultFontColor = colors.mutedColor;
    </script>
    <script src="js/gauge.min.js"></script>
    <script src="js/jquery.sparkline.min.js"></script>
    <script src="js/apexcharts.min.js"></script>
    <script src="js/apexcharts.custom.js"></script>
    <script src='js/jquery.mask.min.js'></script>
    <script src='js/select2.min.js'></script>
    <script src='js/jquery.steps.min.js'></script>
    <script src='js/jquery.validate.min.js'></script>
    <script src='js/jquery.timepicker.js'></script>
    <script src='js/jquery.dataTables.min.js'></script>
    <script src='js/dataTables.bootstrap4.min.js'></script>
    <script src='js/dropzone.min.js'></script>
    <script src='js/uppy.min.js'></script>
    <script src='js/quill.min.js'></script>
    

   
    <script src="js/apps.js"></script>
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-56159088-1"></script>
    <script>
      window.dataLayer = window.dataLayer || [];

      function gtag()
      {
        dataLayer.push(arguments);
      }
      gtag('js', new Date());
      gtag('config', 'UA-56159088-1');
    </script>
    <script>
    function showToastMessage(title, message, type) {
        var container = document.createElement("div");
        container.className = "toast-container";

        var toast = document.createElement("div");
        toast.className = "toast custom-toast fade show";
        toast.setAttribute("role", "alert");
        toast.setAttribute("aria-live", "assertive");
        toast.setAttribute("aria-atomic", "true");

        var headerClass = (type === 'success') ? " text-white" : " text-white";

        toast.innerHTML = 
            "<div class=\"toast-header " + headerClass + "\">" +
                "<strong class=\"mr-auto\">" + title + "</strong>" +
                "<button type=\"button\" class=\"ml-2 mb-1 close text-white\" onclick=\"this.closest('.toast').remove()\" aria-label=\"Fermer\">" +
                    "<span aria-hidden=\"true\">&times;</span>" +
                "</button>" +
            "</div>" +
            "<div class=\"toast-body\">" + message + "</div>";

        container.appendChild(toast);
        document.body.appendChild(container);

        setTimeout(function() {
            toast.classList.remove("show");
            toast.classList.add("hide");
            setTimeout(function() {
                container.remove();
            }, 300);
        }, 5000);
    }

</script>
    
  </body>
</html>
</body>
</html>
 --%>
 
<%--  <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <meta charset="UTF-8">
<!DOCTYPE html>



    </div> <!-- .wrapper --> --%>
    
    <!-- Section Bouton Téléchargement Application (visible dans toutes les pages) -->
    <!-- <footer class="footer mt-auto py-4 bg-light">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6 mb-3 mb-lg-0">
                    <h5 class="mb-2">Notre application mobile</h5>
                    <p class="text-muted mb-0 small">
                        Téléchargez notre application pour une expérience optimale sur mobile
                    </p>
                </div>
                <div class="col-lg-6 text-lg-right">
                    <a href="https://play.google.com/store/apps/details?id=com.votrehotel.application" 
                       target="_blank" 
                       class="btn btn-success d-inline-flex align-items-center px-4 py-2">
                        <i class="fab fa-android fa-lg mr-2"></i>
                        <div class="text-left">
                            <small class="d-block" style="font-size: 0.75rem;">TÉLÉCHARGEZ SUR</small>
                            <span class="font-weight-bold">Google Play</span>
                        </div>
                    </a>
                </div>
            </div>
            <hr class="my-4">
            <div class="row">
                <div class="col-md-6">
                    <p class="text-muted small mb-0">
                        &copy; <span id="current-year"></span> Hotel Management System. Tous droits réservés.
                    </p>
                </div>
                <div class="col-md-6 text-md-right">
                    <a href="privacy-policy.jsp" class="text-muted small mr-3">Politique de confidentialité</a>
                    <a href="terms.jsp" class="text-muted small">Conditions d'utilisation</a>
                </div>
            </div>
        </div>
    </footer> -->
    
    <script src="js/jquery.min.js"></script>
    <script src="js/popper.min.js"></script>
    <script src="js/moment.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/simplebar.min.js"></script>
    <script src='js/daterangepicker.js'></script>
    <script src='js/jquery.stickOnScroll.js'></script>
    <script src="js/tinycolor-min.js"></script>
     <script src="js/custom.js"></script>
   <!--  <script src="js/config.js"></script> -->
    <script src="js/d3.min.js"></script>
    <script src="js/topojson.min.js"></script>
    <script src="js/datamaps.all.min.js"></script>
    <script src="js/datamaps-zoomto.js"></script>
    <script src="js/datamaps.custom.js"></script>
    <script src="js/Chart.min.js"></script>
    <script>
      /* defind global options */
      Chart.defaults.global.defaultFontFamily = base.defaultFontFamily;
      Chart.defaults.global.defaultFontColor = colors.mutedColor;
    </script>
    <script src="js/gauge.min.js"></script>
    <script src="js/jquery.sparkline.min.js"></script>
    <script src="js/apexcharts.min.js"></script>
    <script src="js/apexcharts.custom.js"></script>
    <script src='js/jquery.mask.min.js'></script>
    <script src='js/select2.min.js'></script>
    <script src='js/jquery.steps.min.js'></script>
    <script src='js/jquery.validate.min.js'></script>
    <script src='js/jquery.timepicker.js'></script>
    <script src='js/jquery.dataTables.min.js'></script>
    <script src='js/dataTables.bootstrap4.min.js'></script>
    <script src='js/dropzone.min.js'></script>
    <script src='js/uppy.min.js'></script>
    <script src='js/quill.min.js'></script>
    

   
    <script src="js/apps.js"></script>
    
    <!-- Script pour l'année courante -->
    <script>
      document.getElementById('current-year').textContent = new Date().getFullYear();
    </script>
    
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-56159088-1"></script>
    <script>
      window.dataLayer = window.dataLayer || [];

      function gtag()
      {
        dataLayer.push(arguments);
      }
      gtag('js', new Date());
      gtag('config', 'UA-56159088-1');
    </script>
    
    <!-- Script pour les toasts -->
    <script>
    function showToastMessage(title, message, type) {
        var container = document.createElement("div");
        container.className = "toast-container";

        var toast = document.createElement("div");
        toast.className = "toast custom-toast fade show";
        toast.setAttribute("role", "alert");
        toast.setAttribute("aria-live", "assertive");
        toast.setAttribute("aria-atomic", "true");

        var headerClass = (type === 'success') ? " text-white" : " text-white";

        toast.innerHTML = 
            "<div class=\"toast-header " + headerClass + "\">" +
                "<strong class=\"mr-auto\">" + title + "</strong>" +
                "<button type=\"button\" class=\"ml-2 mb-1 close text-white\" onclick=\"this.closest('.toast').remove()\" aria-label=\"Fermer\">" +
                    "<span aria-hidden=\"true\">&times;</span>" +
                "</button>" +
            "</div>" +
            "<div class=\"toast-body\">" + message + "</div>";

        container.appendChild(toast);
        document.body.appendChild(container);

        setTimeout(function() {
            toast.classList.remove("show");
            toast.classList.add("hide");
            setTimeout(function() {
                container.remove();
            }, 300);
        }, 5000);
    }

    // Script pour le bouton de téléchargement
    document.addEventListener('DOMContentLoaded', function() {
        // Animation au survol des boutons Android
        const androidButtons = document.querySelectorAll('.btn-android');
        androidButtons.forEach(button => {
            button.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-3px)';
                this.style.boxShadow = '0 8px 20px rgba(29, 188, 107, 0.4)';
            });
            
            button.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = '0 4px 12px rgba(29, 188, 107, 0.2)';
            });
        });
        
        // Statistiques de téléchargement (exemple)
        console.log('Application Android disponible - Lien: https://play.google.com/store/apps/details?id=com.votrehotel.application');
    });
    </script>
    
    <script>
    // Force le défilement
    document.addEventListener('DOMContentLoaded', function() {
        // Force le body à avoir un défilement
        document.body.style.overflow = 'auto';
        document.body.style.height = 'auto';
        document.body.style.minHeight = '100vh';
        
        // Force le conteneur principal
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            mainContent.style.overflow = 'auto';
            mainContent.style.height = 'auto';
        }
        
        // Force le conteneur de la table
       /*  const cardBody = document.querySelector('.card-body');
        if (cardBody) {
            cardBody.style.overflow = 'auto';
            cardBody.style.maxHeight = '500px';
        } */
    });
</script>
    
  </body>
</html>
</body>
</html>